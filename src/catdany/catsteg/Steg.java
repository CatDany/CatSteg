package catdany.catsteg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class Steg
{
	public static final String VERSION_NAME	= "1.0-d1";
	public static final long VERSION_CODE = 1;
	
	private static final int BLUE_OR_MASK	= 0b00000000000000000000000000000001;
	private static final int BLUE_AND_MASK	= 0b11111111111111111111111111111110;
	private static final int GREEN_OR_MASK	= 0b00000000000000000000000100000000;
	private static final int GREEN_AND_MASK	= 0b11111111111111111111111011111111;
	private static final int RED_OR_MASK	= 0b00000000000000010000000000000000;
	private static final int RED_AND_MASK	= 0b11111111111111101111111111111111;
	
	public static void main(String[] args)
	{
		Log.i("Welcome to CatSteg %s!", VERSION_NAME);
		
		if (args.length == 0)
		{
			Log.e("No runtime arguments. Use 'read' or 'write'.");
		}
		else if (args[0].equals("read"))
		{
			Steg steg = new Steg();
			steg.loadImage(Utils.promptFile("steg png image"));
			byte[] buf = steg.read();
			Log.i("Concealed string (utf-8):");
			Log.i(new String(buf, StandardCharsets.UTF_8));
			Log.i("Concealed binary (hex):");
			Log.i(DatatypeConverter.printHexBinary(buf));
			Log.i("Done");
		}
		else if (args[0].equals("write"))
		{
			Steg steg = new Steg();
			steg.loadImage(Utils.promptFile("original png image"));
			Log.i("Prompting text to conceal (utf-8):");
			steg.write(Utils.promptString().getBytes(StandardCharsets.UTF_8));
			steg.save(Utils.promptFile("output png image"));
			Log.i("Successfully saved");
		}
		else
		{
			Log.e("Invalid runtime arguments. Use 'read' or 'write'.");
		}
	}
	
	private BufferedImage image;
	private BufferedImage output;
	
	public void write(byte[] bytes)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		output = new BufferedImage(width, height, image.getType());
		int maxi = bytes.length*8;
		int i = 0;
		for (int w = 0; w < width; w++)
		{
			for (int h = 0; h < height; h++)
			{
				int rgb = image.getRGB(w, h);
				Log.d("RGB at %s;%s is %s", w, h, Integer.toHexString(rgb));
				
				if (i < maxi && Utils.getBit(bytes[i/8], i%8))
					//c = new Color(c.getRed(), c.getGreen(), c.getBlue() | 0x1);
					rgb |= BLUE_OR_MASK;
				else
					//c = new Color(c.getRed(), c.getGreen(), c.getBlue());
					rgb &= BLUE_AND_MASK;
				i++;
				if (i < maxi && Utils.getBit(bytes[i/8], i%8))
					//c = new Color(c.getRed(), c.getGreen() | 0x1, c.getBlue());
					rgb |= GREEN_OR_MASK;
				else
					//c = new Color(c.getRed(), c.getGreen() & 0xfe, c.getBlue());
					rgb &= GREEN_AND_MASK;
				i++;
				if (i < maxi && Utils.getBit(bytes[i/8], i%8))
					//c = new Color(c.getRed() | 0x1, c.getGreen(), c.getBlue());
					rgb |= RED_OR_MASK;
				else
					//c = new Color(c.getRed() & 0xfe, c.getGreen(), c.getBlue());
					rgb &= RED_AND_MASK;
				i++;

				Log.d("RGB (after) at %s;%s is %s", w, h, Integer.toHexString(rgb));
				output.setRGB(w, h, rgb);
			}
		}
	}
	
	public byte[] read()
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int i = 0;
		byte[] buf = new byte[width*height/8/3];
		itDown: for (int w = 0; w < width; w++)
		{
			for (int h = 0; h < height; h++)
			{
				int rgb = image.getRGB(w, h);
				
				if (i/8 < buf.length && Utils.getBit(rgb, 0))
					buf[i/8] |= 1 << i%8;
				i++;
				
				if (i/8 < buf.length && Utils.getBit(rgb, 8))
					buf[i/8] |= 1 << i%8;
				i++;
				
				if (i/8 < buf.length && Utils.getBit(rgb, 16))
					buf[i/8] |= 1 << i%8;
				i++;
			}
		}
		return buf;
	}
	
	public void save(File file)
	{
		try
		{
			ImageIO.write(output, "png", file);
		} catch (IOException e) {
			Log.e(e, "Unable to save image");
		}
	}
	
	public void loadImage(File file)
	{
		try
		{
			this.image = ImageIO.read(file);
		} catch (IOException | IllegalArgumentException t) {
			Log.e(t, "Unable to load image");
		}
	}
}