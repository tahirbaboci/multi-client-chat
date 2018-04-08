package chat.server;

public class ServerMain {

	private int port;
	private Server server;
	
	public ServerMain(int port) {  //Debug Configurations... /arguments/ program arguments: 8888
		this.port = port;          // yani port 8888 diye yukarıdaki arguments'te tanımlanmıstır
		server = new Server(port);
	}
	
	public static void main(String[] args) {
		int port;
		
		if(args.length != 1) {
			System.out.println("java -jar Chat.jar [port]");
		    return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);

	}

}
