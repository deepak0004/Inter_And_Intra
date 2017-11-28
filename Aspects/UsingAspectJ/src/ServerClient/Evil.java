package ServerClient;

//Test Case 5: Works fine
public class Evil
{


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyServer s = new MyServer();
		s.start();
		s.process(4); //Property 5 violated
		s.process(1);
		s.stop();
	}

}