
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
File: CrossCorr.java
Class: CrossCorr
Date: 02/08/2017
Author: Siyu Chen
Purpose: This file takes in images and adds different types 
of maskson them by both cross correlation and convolve. 

*/
public class CrossCorr {

	/**
	 *
	 * crossCorrelate is a method takes in an image and a mask
	 * and works under three different situations.
	 */
	public static RGBImage crossCorrelate(RGBImage gray, double[][] filter, int flag, int threshold) {
		if (flag == 0 || flag == 1 || flag == 2) {
			RGBImage outImg = new RGBImage(gray);
			int halfWidth, halfHeight;
			halfWidth = filter[0].length / 2; // relates to cols
			halfHeight = filter.length / 2; // relates to rows
			
			//calculate the total value of the filter
			double masktotal = 0;
			for (int rr = 0; rr < filter.length; rr++) {
				for (int cc = 0; cc < filter[0].length; cc++) {

					masktotal += filter[rr][cc];
				}
			}

			for (int r = halfHeight; r < gray.getNumRows() - halfHeight; r++) {
				for (int c = halfWidth; c < gray.getNumCols() - halfWidth; c++) {

					double sum = 0;
					int temp = 0;
					int temp2 = 0;

					//conducting the multiplication of the mask 
					for (int i = -halfHeight; i < filter.length - halfHeight; i++) {
						for (int j = -halfWidth; j < filter[0].length - halfWidth; j++) {
							if (flag == 0) {
								sum += filter[halfHeight + i][halfWidth + j] * gray.getPixel(r + i, c + j).getRed();
							} else {
								if (flag == 1) {
									temp2 += filter[halfHeight + i][halfWidth + j]
											* gray.getPixel(r + i, c + j).getRed();
								} else {
									if (flag == 2) {
										temp += filter[halfHeight + i][halfWidth + j]
												* gray.getPixel(r + i, c + j).getRed();
									}
								}
							}
						}
					}
// when the sum is not equal to 1, divide it by the mask total
					if (flag == 1) {
						sum = temp2 / masktotal;
					} else {
						// when th mask is an edge mask, give pixels only black or white values
						if (flag == 2) {
							if (Math.abs(temp) >= threshold) {
								sum = 255;
							} else {
								sum = 0;
							}
						}
					}

					outImg.setPixel(r, c, (int) sum, (int) sum, (int) sum);
				}
			}

			return outImg;
		}
		return gray;
	}

	public static RGBImage makeGray(RGBImage img) {
		RGBImage gray = new RGBImage(img.getHeight(), img.getWidth());

		for (int r = 0; r < img.getNumRows(); r++) {
			for (int c = 0; c < img.getNumCols(); c++) {
				int grayVal = (int) (img.getPixel(r, c).getRed() * 0.299 + img.getPixel(r, c).getGreen() * 0.587
						+ img.getPixel(r, c).getBlue() * 0.114);
				gray.setPixel(r, c, grayVal, grayVal, grayVal);
			}
		}
		return gray;
	}

	/**
	 * convolve is a method flips the original mask and creates a new mask
	 */
	public static RGBImage convolve(RGBImage gray, double[][] filter, int flag, int threshold) {

		double[][] newfilter = new double[filter.length][filter[0].length];

		for (int rr = 0; rr < filter.length; rr++) {
			for (int cc = 0; cc < filter[0].length; cc++) {

				newfilter[rr][cc] = filter[cc][rr];
			}
		}
		//call crosscorrelate to work on the new mask
		return crossCorrelate(gray, newfilter, flag, threshold);
	}

	/**
	 * median is a method that removes salt and pepper noises from images
	 * by replace the pixel at the center by the median value
	 * of the selected area.
	 */
	public static RGBImage median(RGBImage gray, int d) {
		RGBImage outImg = new RGBImage(gray);
		int halfWidth, halfHeight;
		halfWidth = d / 2; // relates to cols
		halfHeight = d / 2; // relates to rows
		int median;

		for (int r = halfHeight; r < gray.getNumRows() - halfHeight; r++) {
			for (int c = halfWidth; c < gray.getNumCols() - halfWidth; c++) {
				
				//put the values of the pixels into a list to sort
				List<Integer> list = new ArrayList<>();

				for (int i = -halfHeight; i < d - halfHeight; i++) {
					for (int j = -halfWidth; j < d - halfWidth; j++) {
						
						list.add(gray.getPixel(r + i, c + j).getRed());
					}
				}
				Collections.sort(list);
				median = list.get((list.size() - 1) / 2);
				outImg.setPixel(r, c, (int) median, (int) median, (int) median);
			}
		}
		return outImg;
	}

	public static void main(String args[]) throws IOException {
		RGBImage inImg = new RGBImage(args[0]);
		RGBImage inImgG, outImg;
		inImgG = makeGray(inImg);
		inImgG.writeImage("gray" + args[0]);

		double[][] mask =

				// blurring mask sum not equal to 1 (testing FLAG 1)
				// {{1, 2, 1},
				// {2, 4, 2},
				// {1, 2, 1}};

				// Horizontal edge mask
				// { { -1, -2, -1, }, { 0, 0, 0, }, { 1, 2, 1, } };

				// Vertical edge mask
				{ { 1, 0, -1, }, { 2, 0, -2, }, { 1, 0, -1, } };

		// sharp mask
		// {{-1.0/9, -1.0/9, -1.0/9,},
		// {-1.0/9, 8.0/9, -1.0/9,},
		// {-1.0/9, -1.0/9, -1.0/9,}};

		// Gaussian mask 13x13 V50
		// { { 0.00202, 0.00259, 0.00318, 0.00373, 0.00418, 0.00448, 0.00458,
		// 0.00448, 0.00418, 0.00373, 0.00318,
		// 0.00259, 0.00202 },
		// { 0.00259, 0.00333, 0.00409, 0.00479, 0.00537, 0.00575, 0.00588,
		// 0.00575, 0.00537, 0.00479,
		// 0.00409, 0.00333, 0.00259 },
		// { 0.00318, 0.00409, 0.00501, 0.00588, 0.00659, 0.00705, 0.00721,
		// 0.00705, 0.00659, 0.00588,
		// 0.00501, 0.00409, 0.00318 },
		// { 0.00373, 0.00479, 0.00588, 0.00689, 0.00772, 0.00827, 0.00846,
		// 0.00827, 0.00772, 0.00689,
		// 0.00588, 0.00479, 0.00373 },
		// { 0.00418, 0.00537, 0.00659, 0.00772, 0.00865, 0.00926, 0.00947,
		// 0.00926, 0.00865, 0.00772,
		// 0.00659, 0.00537, 0.00418 },
		// { 0.00448, 0.00575, 0.00705, 0.00827, 0.00926, 0.00991, 0.01014,
		// 0.00991, 0.00926, 0.00827,
		// 0.00705, 0.00575, 0.00448 },
		// { 0.00458, 0.00588, 0.00721, 0.00846, 0.00947, 0.01014, 0.01038,
		// 0.01014, 0.00947, 0.00846,
		// 0.00721, 0.00588, 0.00458 },
		// { 0.00448, 0.00575, 0.00705, 0.00827, 0.00926, 0.00991, 0.01014,
		// 0.00991, 0.00926, 0.00827,
		// 0.00705, 0.00575, 0.00448 },
		// { 0.00418, 0.00537, 0.00659, 0.00772, 0.00865, 0.00926, 0.00947,
		// 0.00926, 0.00865, 0.00772,
		// 0.00659, 0.00537, 0.00418 },
		// { 0.00373, 0.00479, 0.00588, 0.00689, 0.00772, 0.00827, 0.00846,
		// 0.00827, 0.00772, 0.00689,
		// 0.00588, 0.00479, 0.00373 },
		// { 0.00318, 0.00409, 0.00501, 0.00588, 0.00659, 0.00705, 0.00721,
		// 0.00705, 0.00659, 0.00588,
		// 0.00501, 0.00409, 0.00318 },
		// { 0.00259, 0.00333, 0.00409, 0.00479, 0.00537, 0.00575, 0.00588,
		// 0.00575, 0.00537, 0.00479,
		// 0.00409, 0.00333, 0.00259 },
		// { 0.00202, 0.00259, 0.00318, 0.00373, 0.00418, 0.00448, 0.00458,
		// 0.00448, 0.00418, 0.00373,
		// 0.00318, 0.00259, 0.00202 } };

		// Gaussian mask 13x13 V1
		// {{ 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
		// 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
		// 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00000, 0.00001, 0.00003, 0.00005,
		// 0.00003, 0.00001, 0.00000, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00002, 0.00024, 0.00107, 0.00177,
		// 0.00107, 0.00024, 0.00002, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00001, 0.00024, 0.00292, 0.01306, 0.02154,
		// 0.01306, 0.00292, 0.00024, 0.00001, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00003, 0.00107, 0.01306, 0.05855, 0.09653,
		// 0.05855, 0.01306, 0.00107, 0.00003, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00005, 0.00177, 0.02154, 0.09653, 0.15915,
		// 0.09653, 0.02154, 0.00177, 0.00005, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00003, 0.00107, 0.01306, 0.05855, 0.09653,
		// 0.05855, 0.01306, 0.00107, 0.00003, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00001, 0.00024, 0.00292, 0.01306, 0.02154,
		// 0.01306, 0.00292, 0.00024, 0.00001, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00002, 0.00024, 0.00107, 0.00177,
		// 0.00107, 0.00024, 0.00002, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00000, 0.00001, 0.00003, 0.00005,
		// 0.00003, 0.00001, 0.00000, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
		// 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000},
		// { 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
		// 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000}}

		// Gaussian mask 13x13 V22
		// {{ 0.00375, 0.00419, 0.00459, 0.00492, 0.00517, 0.00533, 0.00538,
		// 0.00533, 0.00517, 0.00492, 0.00459, 0.00419, 0.00375},
		// { 0.00419, 0.00468, 0.00512, 0.00549, 0.00577, 0.00595, 0.00601,
		// 0.00595, 0.00577, 0.00549, 0.00512, 0.00468, 0.00419},
		// { 0.00459, 0.00512, 0.00560, 0.00601, 0.00631, 0.00651, 0.00657,
		// 0.00651, 0.00631, 0.00601, 0.00560, 0.00512, 0.00459},
		// { 0.00492, 0.00549, 0.00601, 0.00644, 0.00677, 0.00698, 0.00705,
		// 0.00698, 0.00677, 0.00644, 0.00601, 0.00549, 0.00492},
		// { 0.00517, 0.00577, 0.00631, 0.00677, 0.00712, 0.00734, 0.00741,
		// 0.00734, 0.00712, 0.00677, 0.00631, 0.00577, 0.00517},
		// { 0.00533, 0.00595, 0.00651, 0.00698, 0.00734, 0.00756, 0.00764,
		// 0.00756, 0.00734, 0.00698, 0.00651, 0.00595, 0.00533},
		// { 0.00538, 0.00601, 0.00657, 0.00705, 0.00741, 0.00764, 0.00771,
		// 0.00764, 0.00741, 0.00705, 0.00657, 0.00601, 0.00538},
		// { 0.00533, 0.00595, 0.00651, 0.00698, 0.00734, 0.00756, 0.00764,
		// 0.00756, 0.00734, 0.00698, 0.00651, 0.00595, 0.00533},
		// { 0.00517, 0.00577, 0.00631, 0.00677, 0.00712, 0.00734, 0.00741,
		// 0.00734, 0.00712, 0.00677, 0.00631, 0.00577, 0.00517},
		// { 0.00492, 0.00549, 0.00601, 0.00644, 0.00677, 0.00698, 0.00705,
		// 0.00698, 0.00677, 0.00644, 0.00601, 0.00549, 0.00492},
		// { 0.00459, 0.00512, 0.00560, 0.00601, 0.00631, 0.00651, 0.00657,
		// 0.00651, 0.00631, 0.00601, 0.00560, 0.00512, 0.00459},
		// { 0.00419, 0.00468, 0.00512, 0.00549, 0.00577, 0.00595, 0.00601,
		// 0.00595, 0.00577, 0.00549, 0.00512, 0.00468, 0.00419},
		// { 0.00375, 0.00419, 0.00459, 0.00492, 0.00517, 0.00533, 0.00538,
		// 0.00533, 0.00517, 0.00492, 0.00459, 0.00419, 0.00375}}

		// Gaussian mask 13x13 V4
		// {{ 0.00000, 0.00002, 0.00006, 0.00014, 0.00027, 0.00039, 0.00044,
		// 0.00039, 0.00027, 0.00014, 0.00006, 0.00002, 0.00000},
		// { 0.00002, 0.00008, 0.00024, 0.00057, 0.00106, 0.00155, 0.00175,
		// 0.00155, 0.00106, 0.00057, 0.00024, 0.00008, 0.00002},
		// { 0.00006, 0.00024, 0.00073, 0.00175, 0.00327, 0.00476, 0.00540,
		// 0.00476, 0.00327, 0.00175, 0.00073, 0.00024, 0.00006},
		// { 0.00014, 0.00057, 0.00175, 0.00420, 0.00785, 0.01142, 0.01294,
		// 0.01142, 0.00785, 0.00420, 0.00175, 0.00057, 0.00014},
		// { 0.00027, 0.00106, 0.00327, 0.00785, 0.01467, 0.02134, 0.02418,
		// 0.02134, 0.01467, 0.00785, 0.00327, 0.00106, 0.00027},
		// { 0.00039, 0.00155, 0.00476, 0.01142, 0.02134, 0.03105, 0.03519,
		// 0.03105, 0.02134, 0.01142, 0.00476, 0.00155, 0.00039},
		// { 0.00044, 0.00175, 0.00540, 0.01294, 0.02418, 0.03519, 0.03987,
		// 0.03519, 0.02418, 0.01294, 0.00540, 0.00175, 0.00044},
		// { 0.00039, 0.00155, 0.00476, 0.01142, 0.02134, 0.03105, 0.03519,
		// 0.03105, 0.02134, 0.01142, 0.00476, 0.00155, 0.00039},
		// { 0.00027, 0.00106, 0.00327, 0.00785, 0.01467, 0.02134, 0.02418,
		// 0.02134, 0.01467, 0.00785, 0.00327, 0.00106, 0.00027},
		// { 0.00014, 0.00057, 0.00175, 0.00420, 0.00785, 0.01142, 0.01294,
		// 0.01142, 0.00785, 0.00420, 0.00175, 0.00057, 0.00014},
		// { 0.00006, 0.00024, 0.00073, 0.00175, 0.00327, 0.00476, 0.00540,
		// 0.00476, 0.00327, 0.00175, 0.00073, 0.00024, 0.00006},
		// { 0.00002, 0.00008, 0.00024, 0.00057, 0.00106, 0.00155, 0.00175,
		// 0.00155, 0.00106, 0.00057, 0.00024, 0.00008, 0.00002},
		// { 0.00000, 0.00002, 0.00006, 0.00014, 0.00027, 0.00039, 0.00044,
		// 0.00039, 0.00027, 0.00014, 0.00006, 0.00002, 0.00000}}

		// outImg = crossCorrelate(inImgG, mask, 0, 100);
		// outImg.writeImage("blurred13x13V22" + args[0]);

		// outImg = crossCorrelate(inImgG, mask, 1, 100);
		// outImg.writeImage("testFlag1" + args[0]);

		// outImg = crossCorrelate(inImgG, mask, 2, 100);
		// outImg.writeImage("sobelHorizontal" + args[0]);
		// outImg.writeImage("sobelVertical" + args[0]);

		// outImg = median(inImgG, 5);
		// outImg.writeImage("median-" + args[0]);

		outImg = convolve(inImgG, mask, 2, 100);
		outImg.writeImage("testConvolveWithVedge" + args[0]);

	}

}