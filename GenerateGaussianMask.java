/*
	GenerateGaussianMask.java

        written by: Michael Eckmann
	Skidmore College

	A program to generate gaussian masks of dimension
	size x size
	with a specified gaussian variance.
*/
import java.text.DecimalFormat;

public class GenerateGaussianMask
{

	public static void main(String args[])
	{
		int size = 3; // number of rows/columns of the square mask
		double variance = 5;  // variance of the Gaussian

		// if arguments are passed in they are:
		// width/height of mask followed by the variance		
		if (args.length == 2)
		{
			size = Integer.parseInt(args[0]);
			variance = Double.parseDouble(args[1]);
		}
		double sigma=Math.sqrt(variance);
		double twoSigmaSquared = 2*variance;
		//double c1 = 1/(sigma*Math.sqrt(2*Math.PI)); 
		double c = 1/(variance*2*Math.PI);
		System.out.println("c = " + c);
		double mask[][] = new double[size][size];
		int sizeOver2 = size/2;

		DecimalFormat threeDigs = new DecimalFormat("000");
		DecimalFormat doubleFormat = new DecimalFormat("0.00000");

		for (int i=-sizeOver2; i<=sizeOver2; i++)
		{
			for (int j=-sizeOver2; j<=sizeOver2; j++)
			{
				mask[i+sizeOver2][j+sizeOver2] = c * Math.pow(Math.E,-1*(i*i+j*j)/(twoSigmaSquared));
			}
		}

		double lastElement = mask[size-1][size-1];

		System.out.println("The mask rounded to integers");
		double total = 0;
		for (int i=0; i<mask.length; i++)
		{
			for (int j=0; j<mask[i].length; j++)
			{
				System.out.print(" " + threeDigs.format((int)(mask[i][j]/lastElement)));
				//System.out.print(" " + (mask[i][j]/lastElement));
				total += (mask[i][j]/lastElement);
			}
			System.out.println();
		}
		System.out.println("total = " + total);
		double sum=0;
		System.out.print("{");
		for (int i=0; i<mask.length; i++)
		{
			System.out.print("{");
			for (int j=0; j<mask[i].length-1; j++)
			{
				System.out.print(" " + doubleFormat.format((mask[i][j]/lastElement)/total) + ",");
				sum += ((mask[i][j]/lastElement)/total);
			}
			System.out.print(" " + doubleFormat.format((mask[i][mask[i].length-1]/lastElement)/total));
			sum += ((mask[i][mask[i].length-1]/lastElement)/total);
			System.out.print("}");
			if (i != mask.length-1) System.out.println(",");
		}
		System.out.println("}");

		System.out.println("sum = " + sum);
	}
}
