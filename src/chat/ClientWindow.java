package chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import paint.Paint;

import java.awt.GridBagLayout;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ClientWindow extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	

	private JTextField txtMessage;
	private JTextArea txtrHistory;
	private DefaultCaret caret;         // a blinky thinks that tells us where we are typing
	
	private Client client;
	private OnlineUsers users;
	private Paint p;
	
	private boolean running = false;
	private boolean connect;
	private boolean CheckCon = false;
	public Thread run, listen;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnSend;
	private JMenuItem mntmOnlineUsers;
	private JMenuItem mntmExit;
	private JMenuItem mntmImage;
	private JMenu mnPainter;
	
	/**
	 * Create the frame.
	 */
	//constructer
	public ClientWindow( String name, String address, int port) {
		
		client = new Client(name, address, port);
		createWindow();
		console("attempting connection to " + address + ":" + port + " user: " + name);
		
		connect = client.openConnection(address);
		if(!connect) {
			System.err.println("connection failed!");
			console("connection to :"+ address + ":" + port + "   FAILED!");
		}
		
		String connection = "/c/" + client.getName() + "/e/";
		client.send(connection.getBytes());
		users = new OnlineUsers();
		running = true;
		run = new Thread(this, "Running");
		run.start();
		
		
	}
	
	public void console(String message) {
		txtrHistory.append(message + "\n");
		//txtrHistory.setCaretPosition(txtrHistory.getDocument().getLength());               //we can do it like this either
	}
	
	private void checkConnection(boolean check) {
		if(check == true) {
			txtMessage.setEditable(true);
		}
	}
    //listen to receive message while running
 	public void run() {
		listen();
	}
	public void send(String message, boolean text) {
		if(!message.equals("")) { 
			if(text == true) {
				message = client.getName() + ": " + message;
				message = "/m/" + message + "/e/";
				//console(message);
				txtMessage.setText("");
				}
			client.send(message.getBytes());
		}
		else {
			return;
		}
	}
	
	
	public void listen() {
		listen = new Thread("listen") {
			public void run() {
				while(running) {
					String message = client.receive();
					if(message.startsWith("/c/")) 
					{
						//System.out.println(message.length()); // length is 2048    thas why it chrashes
						//   /c/8127/e/  .. .                  ..)
						
						//                                     0 /c/ 1 /e/ 3    thats why we choose [1]
						int id = Integer.parseInt(message.split("/c/|/e/")[1]);
						client.setID(id);
						console("Successfully connected to server! \nMy ID to Server is: " + client.getID());
						
						CheckCon = true;
						checkConnection(CheckCon);
					}
					else if(message.startsWith("/m/"))
					{
						//gets the packet from server and Print it to console(message)
						String text = message.substring(3);    // 3. karakterden sonra al
						text =	text.split("/e/")[0];    // /e/'den onceki datayı al
						console(text);	
					}
					else if(message.startsWith("/i/")) 
					{
						String text = "/i/" + client.getID() + "/e/";
						send(text,false);
					}
					else if(message.startsWith("/u/")) {
						String[] u = message.split("/u/|/n/|/e/");    // "","tahir",""
						users.update(Arrays.copyOfRange(u, 1, u.length - 1));  
					}
					
				}
			}
		};
		listen.start();
	}
	
	
	
	private void  createWindow() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		setTitle("Chat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOnlineUsers = new JMenuItem("Online Users");
		mntmOnlineUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				users.setVisible(true);
			}
		});
		mnFile.add(mntmOnlineUsers);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//exit
				client.close();
				dispose();
			}
		});
		mnFile.add(mntmExit);
		
		mnSend = new JMenu("Send");
		menuBar.add(mnSend);
		
		mntmImage = new JMenuItem("Image");
		mntmImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//FileChooser f = new FileChooser();
				//f.file();
				//client.sendImage();
			}
		});
		mnSend.add(mntmImage);
		
		mnPainter = new JMenu("Painter");
		mnPainter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//paint
			}
		});
		menuBar.add(mnPainter);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{45, 825, 30, 10}; // sum = 900
		gbl_contentPane.rowHeights = new int[]{60, 530, 10}; // sum = 600
		contentPane.setLayout(gbl_contentPane);
		
		txtrHistory = new JTextArea();
		txtrHistory.setEditable(false);
		caret = (DefaultCaret)txtrHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);      //it updates when you type new message it will show the message you typed or others typed
		JScrollPane scroll = new JScrollPane(txtrHistory);		//we have set the textArea to ScrollPane
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.weightx = 2;
		scrollConstraints.weighty = 1;
		//(up,left,down,right)
		scrollConstraints.insets = new Insets(0, 0, 5, 0);
		contentPane.add(scroll, scrollConstraints); 		//we have set scroll to GridBagConstraints
		
		
		txtMessage = new JTextField();
		txtMessage.setEditable(false);
		// keypressed action on textMessage
		//when I press ENTER it will send the message
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText(), true);
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		gbc_txtMessage.weightx = 1; //allows us to resıze horizontally
		gbc_txtMessage.weighty = 0; // we dont want it to resize y
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		
		
		
		JButton btnSend = new JButton("Send");
		//button action
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				send(message, true);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.anchor = GridBagConstraints.EAST;
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		gbc_btnSend.weightx = 0;
		gbc_btnSend.weighty = 0;
		contentPane.add(btnSend, gbc_btnSend);
		
		// the action of CLOSE button on the window
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				String disconnect = "/d/" + client.getID() + "/e/";
				send(disconnect, false);
				running = false;
				client.close();
			}
			
		});
		
		setVisible(true);
		txtMessage.requestFocusInWindow();
		setResizable(false);
		
		
	}
	
	
}
