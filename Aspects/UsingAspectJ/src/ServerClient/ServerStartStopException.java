package ServerClient;

public class ServerStartStopException extends RuntimeException
{
	String str1;
	
	public ServerStartStopException( String str ) 
	{
		str1 = str;	
	}
	
    public String toString()
    { 
		return ("\n" + "Exception Occurred: " + str1 + "\n");
	}
}