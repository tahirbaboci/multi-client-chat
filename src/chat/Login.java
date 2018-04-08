package chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.Iterable;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtPort;
	private JTextField txtIPAddress;
	private JTextField txtName;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private void login(String name, String address, int port) {
		dispose();
		new ClientWindow(name, address, port);
	}
	
	// bu metodu yapamadım :(
	
	/*
	public boolean isAddress(String address) {
		String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(address);
		if (matcher.find()) {
		    return true;
		} else{
		    return false;
		}
	}
	*/
	
	private boolean isDigit(String a) {
		int sayac = 0;
		for(int i = 0;i < a.length();i++) {
			if(Character.isDigit(a.charAt(i))) {
				sayac++;
			}
		}
		if(sayac == a.length()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//constructer
	public Login() {
		
		createWindowLogin();
	}
	
	
	/**
	 * Create the frame.
	 */
	private void createWindowLogin(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		setResizable(false);
		setTitle("Login");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300,380);
        setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(60, 53, 162, 22);
		txtName.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(txtName);
		txtName.setColumns(5);
		
		txtPort = new JTextField();
		txtPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String name = txtName.getText();
					String address = txtIPAddress.getText();
					
					if(!isDigit(txtPort.getText())){
						JOptionPane.showMessageDialog(null, "port must be a digit!","port error",JOptionPane.ERROR_MESSAGE);
					}
					/*
					else if(!address.equals("localhost") || !isAddress(address)) {
						JOptionPane.showMessageDialog(null,"address : " + address + " is Wronge!", "ip exception!" , JOptionPane.ERROR_MESSAGE);
					}
					*/
					else {
						int port = Integer.parseInt(txtPort.getText());
						login(name, address, port);
					}
				}
			}
		});
		txtPort.setHorizontalAlignment(SwingConstants.CENTER);
		txtPort.setColumns(5);
		txtPort.setBounds(60, 196, 162, 22);
		contentPane.add(txtPort);
		
		txtIPAddress = new JTextField();
		txtIPAddress.setHorizontalAlignment(SwingConstants.CENTER);
		txtIPAddress.setColumns(5);
		txtIPAddress.setBounds(60, 117, 162, 22);
		contentPane.add(txtIPAddress);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(115, 37, 56, 16);
		contentPane.add(lblName);
		
		JLabel labelPort = new JLabel("Port:");
		labelPort.setBounds(124, 177, 28, 16);
		contentPane.add(labelPort);
		
		JLabel lblAdress = new JLabel("IP Address:");
		lblAdress.setBounds(102, 100, 67, 16);
		contentPane.add(lblAdress);
		
		JLabel lbl_IPaddress_Description = new JLabel("(eg. 192.169.1.30)");
		lbl_IPaddress_Description.setBounds(90, 139, 107, 16);
		contentPane.add(lbl_IPaddress_Description);
		
		JLabel lblPort_description = new JLabel("(eg. 8080)");
		lblPort_description.setBounds(90, 217, 107, 16);
		contentPane.add(lblPort_description);
		
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String name = txtName.getText();
				String address = txtIPAddress.getText();
				
				if(!isDigit(txtPort.getText())){
					JOptionPane.showMessageDialog(null, "port must be a digit!","port error",JOptionPane.ERROR_MESSAGE);
				}
				/*
				else if(!address.equals("localhost") || !isAddress(address)) {
					JOptionPane.showMessageDialog(null,"address : " + address + " is Wronge!", "ip exception!" , JOptionPane.ERROR_MESSAGE);
				}
				*/
				else {
					int port = Integer.parseInt(txtPort.getText());
					login(name, address, port);
				}
				
			}

			
		});
		btnLogin.setBounds(60, 286, 162, 34);
		contentPane.add(btnLogin);
	}
	
	
	
}
