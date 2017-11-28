package ServerClient;

import java.util.Random;

public class MyServer
{
	public void start()
	{
		System.out.println("Server starting");
	}
	
	public void stop()
	{
		System.out.println("Server stopping");
	}
	
	public void process(int time)
	{
//		Random r = new Random();
//		int processTime = r.nextInt(5);
//
		System.out.println("Server processing");
	}
}