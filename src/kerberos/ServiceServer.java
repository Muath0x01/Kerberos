package kerberos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;

public class ServiceServer extends Thread {
	Socket client = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	
	private String name, password, connectedClientName;
	private int timer;
	byte[] sessionKey;

	public ServiceServer(Socket client,String name, String password) {
		this.client = client;
		this.name=name;
		this.password=password;
		sessionKey = new byte[16];
	}
	
	public void run(){
		try {
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
	
			receiveTicket();
			receiveTimer();
			sendNewTimer();
			client.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void receiveTicket() {
		byte[] ticket;
		try {
			ticket = new byte[in.readInt()];
			in.readFully(ticket, 0, ticket.length); // read the message
			sessionKey = Algorithms.decrypt(ticket, password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void receiveTimer() {
		byte[] tmp;
		try {
			tmp = new byte[in.readInt()];
			in.readFully(tmp, 0, tmp.length); // read the message
			tmp = Algorithms.decrypt(tmp, Base64.getEncoder().encodeToString(sessionKey));
			timer = Integer.parseInt(Algorithms.undoRightPadInteger(StringUtils.substringAfter(new String(tmp), "-")));
			connectedClientName = Algorithms.undoRightPadInteger(StringUtils.substringBefore(new String(tmp), "-"));
			System.out.println("Server "+name+" has received timer "+timer+" from user "+connectedClientName+".");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendNewTimer() {
		timer -=1;
		try {
			String toSend = Algorithms.rightPadInteger(timer,"Nonsense");
			byte[] tmp = Algorithms.encrypt(toSend.getBytes(), Base64.getEncoder().encodeToString(sessionKey));
			System.out.println("User "+connectedClientName+" has been sent new timer "+timer+" from server "+name+".");
			out.writeInt(tmp.length); // write length of the message
			out.write(tmp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
