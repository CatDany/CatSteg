package catdany.catsteg;

import java.io.File;
import java.util.Scanner;

public class Utils
{
	private static final Scanner scan = new Scanner(System.in);
	
	public static String promptString()
	{
		String in = scan.nextLine();
		return in;
	}
	
	public static File promptFile(String comment)
	{
		Log.i("Prompting file name (%s):", comment);
		String fileName = Utils.promptString();
		return new File(fileName);
	}
	
	public static boolean getBit(int bits, int pos)
	{
		return ((bits >> pos) & 1) == 1;
	}
}