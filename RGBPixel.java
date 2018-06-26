/*

	A class that represents 1 pixel of a color image

	The ranges of the red, green and blue values are 0 .. 255 inclusive

	Author: Michael Eckmann
	Skidmore College
	for Spring 2017
	Digital Image Processing Course

*/
public class RGBPixel
{
	private int r;
	private int g;
	private int b;

	public RGBPixel(int r, int g, int b)
	{
		if (r >= 0 && r <= 255)
			this.r = r;
		else
			this.r = 0;
		if (g >= 0 && g <= 255)
			this.g = g;
		else
			this.g = 0;
		if (b >= 0 && b <= 255)
			this.b = b;
		else
			this.b = 0;
	}

	public int getRed()
	{
		return r;
	}

	public int getGreen()
	{
		return g;
	}

	public int getBlue()
	{
		return b;
	}

	public void setRed(int r)
	{
		if (r >= 0 && r <= 255)
			this.r = r;
	}
	public void setGreen(int g)
	{
		if (g >= 0 && g <= 255)
			this.g = g;
	}
	public void setBlue(int b)
	{
		if (b >= 0 && b <= 255)
			this.b = b;
	}

}