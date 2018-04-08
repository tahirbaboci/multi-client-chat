package chat.server;

import java.net.InetAddress;

public class ServerClient {

	public String name;
	public InetAddress address;
	public int port;
	private final int ID; // at the same IP and port may be connected two clients so we use an unique ID
	public int atempt = 0; // server it will ask client if it is there or not(disconnected) and it will attempt 5 times 
	                       //server: hey bro are you there,       client: yeah bro I am here

	public int getID() {
		return ID;
	}
	
    public ServerClient(String name, InetAddress address, int port, final int ID) {
    	this.name = name;
    	this.address = address;
    	this.port = port;
    	this.ID = ID;
    }
}
