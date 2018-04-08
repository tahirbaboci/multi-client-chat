package chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable{
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();
	
	private int port;
	private DatagramSocket UDPSocket;
	
	private Thread run, manage, send, receive;
	private boolean running = false;
	
	private final int MAX_ATTEMPTS = 5;
	
	private boolean takip = false;
	
	Scanner input = new Scanner(System.in);
	
	//constructer
	public Server(int port) {
		this.port = port;
		
		try {
			UDPSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;   // we should return it because if it will give an exception it will start running without opening socket
		}
		run = new Thread(this, "Server");  
		run.start();
	}

	@Override
	public void run() {
		System.out.println("Server started on port: " + port);
		running = true;
		manageClients();
		receive();
		while(running) {
			String text  = input.nextLine();
			if(!text.startsWith("/")) {
				if(text.equals("")) {
					continue;
				}
				else {
					sendToAll("/m/server: " + text + "/e/");
					continue;
				}
			}
			
			text = text.substring(1);
			if(text.equals("takip")) {
				takip = !takip;
				if(takip == false) {
					System.out.println("takip mode off");
				}
				else {
					System.out.println("takip mode on!");
				}
			}
			else if(text.equals("clients")) {
				System.out.println("Clients : ");
				System.out.println("**********");
				for (int i = 0; i < clients.size(); i++) {
					ServerClient sc = clients.get(i);
					System.out.println(sc.name.trim() + "(" + sc.getID() + ") :" + sc.address + ":" + sc.port);
				}
				System.out.println("**********");
			}
			else if(text.startsWith("kick")) {
			    //kick tahir        [kick][tahir]    [0][1]
				
				String name = text.split(" ")[1]; // gets rid of space
				int id = -1;
				boolean num = false;
				try {
					id = Integer.parseInt(text); // if we try to parse an string it will throw an exception and it is not a number(false)
					num = true;
				} catch (NumberFormatException e) {
					num = false;
				}
				
				if(num) {
					boolean NUMexists = false;
					for (int i = 0; i < clients.size(); i++) {
						if(clients.get(i).getID() == id) {
							NUMexists = true;
						}
					}
					if(NUMexists) {
						disconnect(id, true);
					} 
					else {
						System.out.println("Client " + id + " Doesn't exists!");	
					}
					
				}
				else
				{
					boolean NAMEexists = false;
					int getTheClientID = -1;
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if(name.equals(c.name)) {
							NAMEexists = true;
							getTheClientID = c.getID();
							break;
						}
					}
					if(NAMEexists) {
						disconnect(getTheClientID, true);
					}
					else {
						System.out.println("Client " + name + " Doesn't exists!");	
					}
					
				}
				
			}
			else if (text.equals("help")) {
				printHelp();
			}
			else if(text.equals("quit")) {
				quit();
			}
			else { // if text start with '/' but is an unknown command it will printHelp
				System.out.println("UNKNOWN Command!");
				printHelp();
			}
		}
		input.close();
	}
	
	private void printHelp() {
		System.out.println("the list of available commands:");
		System.out.println("********************************");
		System.out.println("/takip - enable takip mode");
		System.out.println("/clients - shows all connected clients");
		System.out.println("/kick (user ID or username) - kicks a user");
        System.out.println("/help - shows this help message");
        System.out.println("/quit - shuts down the server");
	}
	
	private void manageClients() {
		manage = new Thread("manage") {
			public void run() {
				while(running) {
					sendToAll("/i/server");// ping.... it will send to all servers.  /i/server to ask if they are online or not
					sendStatus();
					try {
						Thread.sleep(2000);       // soruyu gonderdikten sonra cevap icin 2 saniye bekle   BUG: bu thread yuzunden her 2 saniye texbox siliniyor
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);          // i'ninci indisindeki client'i  c'ye atıyor
						if(!clientResponse.contains(c.getID())) { // eger client'ten cevap almadıysak

							if(c.atempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							}
							else {
								c.atempt++;
							}
						}
						else {    //ama eger client'tan cevap aldıysak
							clientResponse.remove(new Integer(c.getID()));         //new Integer dedigimiz zaman bir OBJECT siliyor
							c.atempt = 0;
						}

					}

				}
			}
		};
		manage.start();
	}
	private void sendStatus() {
		if(clients.size() <= 0) {
			return;
		}
		String users = "/u/";
		for (int i = 0; i < clients.size() - 1; i++) {
			users += clients.get(i).name + "/n/"; //    /u/tahir/n/fatih/e/  .....
			
		}
		users += clients.get(clients.size() - 1).name + "/e/";
		sendToAll(users); 
	}
	
	private void receive() {                  //client'ten paket alıyor
		receive = new Thread("Receive") {
			public void run() {
				while(running) {
					byte[] data = new byte[2048];
					DatagramPacket packet = new DatagramPacket(data, data.length);
				    try {
						UDPSocket.receive(packet);  // UDP'nin kendi fonksiyonu
						
					} catch (SocketException e) {
						// blank quit
					} catch (IOException e) {
						e.printStackTrace();
					}
				    process(packet);
				    
				    // it doesnt matter if it is UDP or TCP the header of packet it contains the source and destination ip address and port
				    //clients.add(new ServerClient("tahir", packet.getAddress(), packet.getPort(), 50));
				}
			}
		};
		receive.start();
	}
	
	// it will send all the provided messages to all clients
	private void sendToAll(String message) {
		if(message.startsWith("/m/")) {
			//print the text writed from server to clients
			String text = message.substring(3);
			text =	text.split("/e/")[0];
			System.out.println(text);

		}
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i); // send it to all clients
			send(message.getBytes(), client.address, client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					UDPSocket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message, InetAddress address, int port) {
		message += "/e/";              //end
		send(message.getBytes(), address, port);
	}
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if(takip) {
			System.out.println(string);                       // takip command :   it prints all the processes
		}
		if(string.startsWith("/c/")) {
			
			//UUID id = UUID.randomUUID();
			int id = UniqueIdentifier.getIdentifier();
			String name = string.split("/c/|/e/")[1];
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			System.out.println("\n\nNumber of clients connected to server is : " + clients.size());
			System.out.println(name + " (" + id + ") connected");
			System.out.println(clients.get(0).address.toString() + ":" +  clients.get(0).port);
			
			String ID = "/c/" + id;
			send(ID, packet.getAddress(), packet.getPort());
			
		}
		else if(string.startsWith("/m/")){
			sendToAll(string);
		}
		else if(string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
			
		}
		else if(string.startsWith("/i/")) { 
			// client'ten cevabı alıyoruz
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
			
		}
		else {
			System.out.println(string);
		}
	}
	private void quit() {
		for (int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}
		running = false;
		UDPSocket.close();
	}
	private void disconnect(int id, boolean status) {
		ServerClient sc = null;
        boolean existed = false;
		for (int i = 0; i < clients.size(); i++) {
			if(clients.get(i).getID() == id)
			{
				sc = clients.get(i);
				clients.remove(i);
				existed = true;
				break;
			}
			
		}
		if(!existed) return;
		
		String message = "";
		if(status) {           // eger status true ise cıkmıstır demek
			message = "Client " + sc.name.trim() + " (" + sc.getID() + ") @" + sc.address.toString() + ":" + sc.port + " disconnected";
			
		}
		else {                // ama egere status false ise serverdan gelen mesajları client cevaplamadıgı icin disconnect oluyor   max_attempts = 5;
			message = "Client " + sc.name.trim() + " (" + sc.getID() + ") @" + sc.address.toString() + ":" + sc.port + " timed out";
		}
		System.out.println(message);
		
	}

}
