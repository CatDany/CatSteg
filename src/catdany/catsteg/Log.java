package catdany.catsteg;

public class Log
{
	public static boolean isDebugLoggingEnabled()
	{
		return false;
	}
	
	public static void i(String format, Object... args)
	{
		System.out.println("[Log]/i " + String.format(format, args));
	}
	
	public static void e(String format, Object... args)
	{
		System.err.println("[Log]/e " + String.format(format, args));
	}
	
	public static void d(String format, Object... args)
	{
		if (isDebugLoggingEnabled())
		{
			System.out.println("[Log]/d " + String.format(format, args));
		}
	}
	
	public static void e(Throwable t, String format, Object... args)
	{
		e(format, args);
		t.printStackTrace(System.err);
	}
}