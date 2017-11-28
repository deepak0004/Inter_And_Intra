package ServerClient;

import java.util.ArrayList;
import java.util.HashMap;

public aspect ServerStartStop 
{
	ArrayList<MyServer> servers = new ArrayList<MyServer>();
	//HashMap<MyServer, Integer> hmap = new HashMap<MyServer, Integer>();
	
	before(MyServer s):
		call(* MyServer.start()) && target(s)
		{
		    if( servers.contains(s) )    //hmap.containsKey(s) ) 
		    {
		    	throw new ServerStartStopException("Server started when already it was earlier started");				
		    }
		}
	
	after(MyServer s) returning:
		call(* MyServer.start()) && target(s)
		{
	   		servers.add(s);			
	   		//hmap.put(s, 1);
		}
	
	before(MyServer s):
		call(* MyServer.stop()) && target(s)
		{
			if(servers.contains(s))
			{
				servers.remove(s);
			}
			else
			{
				throw new ServerStartStopException("Stop called when server was not started");
			}
		}
	
//	////////////////////
//    after(MyServer s):
//		call(* MyServer.stop()) && target(s)
//		{
//            hmap.remove(s); 
//		}
//    ////////////////////
	
    before(MyServer s, int time):
		call(* MyServer.process(int)) && target(s) && args(time)
		{
		    if( !servers.contains(s) )
		    {
		    	throw new ServerStartStopException("Process called but server not started");
		    }
		}
	
    before(MyServer s, int time):
		call(* MyServer.process(int)) && target(s) && args(time)
		{
		    if( time>1 )
		    { 
		    	throw new ServerStartStopException("Timeout");
		    }
		}
    
//    ////////////////////
//    after(MyServer s, int time):
//		call(* MyServer.process(int)) && target(s) && args(time)
//		{
//            hmap.remove(s); 
//		}
//    ////////////////////
    
    pointcut mainMethod() : execution(public static void main(String[]));
    after() returning: mainMethod()
    {
    	 if( servers.size()>0 )
    	 {
    		 throw new ServerStartStopException("Main end reached but servers not stopped");
    	 }
    }
}
