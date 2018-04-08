package chat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

public class Client {

	public static final long serialversionUID = 1L;
	
	private DatagramSocket UDPSocket;   //this is UDP, if you want to do it TCP you have to do "private Socket TCPSocket;"
	private InetAddress ip;
	private String name, address;
	private int port;
	private Thread send;

	FileChooser  f;
	
	private int ID = -1;
	
	
	
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public int getPort() {
		return port;
	}
	public int getID() {
		return ID;
	}
	
	public void setID(int id) {
		this.ID = id;
	}
	
	
	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	public boolean openConnection(String address) {
		try {
			UDPSocket = new DatagramSocket();
			ip = InetAddress.getByName(address);      // converting address from string to InetAddress
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;                 // if it gives an exception return false
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} 

		return true;
	}

	public String receive() {
		byte[] data = new byte[2048];
		DatagramPacket packet = new DatagramPacket(data, data.length);

		try {
			UDPSocket.receive(packet);    // it is a loop, it will wait until it receives a data from server, 
			                              // it will freeze the application, it is a problem but THREADS it will help us
		} catch(SocketException e) {
			//
		} catch(IOException e) {
			e.printStackTrace();
		} 

		String message = new String(packet.getData());
		return message;
	}

	public void close() {
		new Thread("close"){
			public void run() {
				synchronized (UDPSocket) {
					UDPSocket.close();
				}
			}
		}.start();
	}
	public void sendImage() {
		try {
			 BufferedImage img = ImageIO.read(new File(f.path));
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();        
			 ImageIO.write(img, "jpg", baos);
			 baos.flush();
			 byte[] buffer = baos.toByteArray();
			 send(buffer);
		} catch (Exception e) {
			
		}
		 
	}
	public void send(final byte[] data) {
		send = new Thread("send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					UDPSocket.send(packet);
				}catch (SocketException e) {
					//
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}


}
