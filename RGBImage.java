/*

	RGBImage is a class that represents an RGB image as a 2d array of RGBPixel

	This class provides a way to create an empty image (call RGBImage(int height, int width))
         and then expects setPixel to be called height*width times for each pixel coordinate

	This class also provides a constructor that will read an image from disk into the 2d array
	 (call RGBImage(String jpgFileName) )

	This class also provides a method to write the 2d array to disk (call writeImage(String fName))

	Author: Michael Eckmann
	Skidmore College
	for Spring 2017
	Digital Image Processing Course

*/
import java.io.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.stream.*;

public class RGBImage
{

	private RGBPixel[][] pic;

	// constructor to copy an image into new image
	public RGBImage(RGBImage img)
	{
		pic = new RGBPixel[img.getNumRows()][img.getNumCols()];
		// height = # rows
		// width = # cols
		for (int r = 0; r < img.getNumRows(); r++)
		{
			for (int c = 0; c < img.getNumCols(); c++)
			{
				pic[r][c] = new RGBPixel(img.getPixel(r,c).getRed(),
						  img.getPixel(r,c).getGreen(),
						  img.getPixel(r,c).getBlue());
			}
		}
	}


	// constructor that creates the 2d array of the appropriate size
	public RGBImage(int height, int width)
	{
		// height = # rows
		// width = # cols

		pic = new RGBPixel[height][width];
	}

	// constructor that reads a jpeg from disk and loads it into the 2d array
	// pic instance variable
	public RGBImage(String jpgFileName)
	{
		readImage(jpgFileName);
	}

	public int getNumRows()
	{
		return getHeight();
	}

	public int getNumCols()
	{
		return getWidth();
	}

	public int getHeight()
	{
		return pic.length;
	}

	public int getWidth()
	{
		return pic[0].length;
	}

	public RGBPixel getPixel(int r, int c)
	{
		return pic[r][c];
	}

	public void setPixel(int r, int c, RGBPixel p)
	{
		pic[r][c] = p;
	}

	public void setPixel(int r, int c, int red, int green, int blue)
	{
		pic[r][c] = new RGBPixel(red, green, blue);
	}


	public void makeBlack()
	{
		for (int r = 0; r < getNumRows(); r++)
		{
			for (int c = 0; c < getNumCols(); c++)
			{
				setPixel(r,c,0,0,0);
			}
		}

	}

	/*

		method: readImage

		Reads a jpeg image from a file on disk into an RGBImage (which contains a 2d array of RGBPixel).

		input parameter: String jpgFileName --- the name of the jpeg file

		stores to 2d array pic
			this is the jpeg image stored in a 2d array of RGBPixel

	*/

	private void readImage(String jpgFileName)
	{
 
		BufferedImage img = null;
		try {
    			img = ImageIO.read(new File(jpgFileName));
		} catch (IOException e) {
			System.out.println("Could not read " + jpgFileName);
		}
		int w = img.getWidth();
		int h = img.getHeight();

		int allPixels[] = img.getRGB(0, 0, w, h, null, 0, w); 
		
		int rows = h, cols = w;

		pic = new RGBPixel[h][w];
				
		for (int i = 0; i < allPixels.length; i++)
		{
			Color c = new Color(allPixels[i]);

			int row = i / w;
			int col = i % w;
			pic[row][col] = new RGBPixel(c.getRed(), c.getGreen(), c.getBlue());
		}

	}

	/*

		method: writeImage

		Write a jpeg image to disk from this RGBImage (which contains a 2d array of RGBPixel).

		input parameters: 
                      String fName --- the name of the jpeg file to write

		returns: nothing

	*/


	public void writeImage(String fName) throws IOException
	{
		int i = 0;
		int pixelArray[] = new int[getNumRows()*getNumCols()];
		BufferedImage img = new BufferedImage(getNumCols(), getNumRows(), BufferedImage.TYPE_INT_RGB);

		for (int r = 0; r < getNumRows(); r++)
		{
			for (int c = 0; c < getNumCols(); c++)
			{
				Color col = new Color(getPixel(r,c).getRed(), getPixel(r,c).getGreen(), getPixel(r,c).getBlue());
				img.setRGB(c,r,col.getRGB());
			}
		}
		// modified code from: http://www.javased.com/?api=javax.imageio.IIOImage
		Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = (ImageWriter)iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(1);
		File outputFile = new File(fName);
		FileImageOutputStream output = new FileImageOutputStream(outputFile);
		writer.setOutput(output);
		IIOImage image = new IIOImage(img, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
		
	}


}