package kerberos;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.StringUtils;
import java.io.*;

public class TGS_Service extends Thread {
	Socket client = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	ArrayList<String> servers = new ArrayList<String>();
	ArrayList<String> passwords = new ArrayList<String>();
	private String AS_TGS = "KerberosIsTheBest";
	private String requestedServer;
	SecretKey ClientServerSessionKey;
	private int timer;
	byte[] sessionKey;

	public TGS_Service(Socket client) {
		this.client = client;
		servers.add("Bob");
		servers.add("test1");
		servers.add("test2");
		servers.add("test3");
		servers.add("test4");
		passwords.add("PQR456987");
		passwords.add("RST987987");
		passwords.add("UVW654456");
		passwords.add("XYZ321789");
		passwords.add("ABC999999");
	}

	public  void run() {
		try {
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			receiveTicket();
			receiveTimer();
			sendClientTicket();
			sendServerTicket();
			client.close();
			out.close();
			in.close();
		} catch (Exception e) {
			System.out.println("User "+Algorithms.getCurrentUser()+" is invalid\n");
		}

	}

	private void receiveTicket() {
		byte[] ticket;
		try {
			System.out.println("User "+ Algorithms.getCurrentUser()+" sent ticket to TGS");
			ticket = new byte[in.readInt()];
			in.readFully(ticket, 0, ticket.length); // read the message
			sessionKey = Algorithms.decrypt(ticket, AS_TGS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	private void receiveTimer() {
		byte[] tmp;
		try {
			tmp = new byte[in.readInt()];
			in.readFully(tmp, 0, tmp.length); // read the message
			tmp = Algorithms.decrypt(tmp, Base64.getEncoder().encodeToString(sessionKey));
			//System.out.println(Algorithms.undoRightPadInteger(StringUtils.substringAfter(new String(tmp), "-"))+"@@@@");
			timer = Integer.parseInt(Algorithms.undoRightPadInteger(StringUtils.substringAfter(new String(tmp), "-")));
			requestedServer = Algorithms.undoRightPadInteger(StringUtils.substringBefore(new String(tmp), "-"));
			System.out.println("User "+ Algorithms.getCurrentUser()+" with timer "+timer+" requested ticket for server "+requestedServer+" from TGS");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	private void sendClientTicket() {
		try {
			ClientServerSessionKey = Algorithms.generateKey();
			//System.out.println(Arrays.toString(ClientServerSessionKey.getEncoded()));
			out.write(Algorithms.encrypt(ClientServerSessionKey.getEncoded(),
					Base64.getEncoder().encodeToString(sessionKey)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	private void sendServerTicket() {
		try {
			int tmpIndex = getIndex(requestedServer);
			if (tmpIndex!=-1) out.write(
					Algorithms.encrypt(ClientServerSessionKey.getEncoded(), passwords.get(tmpIndex)));
			System.out.println("User "+ Algorithms.getCurrentUser()+" has been issued a ticket for server "+requestedServer+" by TGS");

			//System.out.println("TGS"+Arrays.toString(Algorithms.encrypt(ClientServerSessionKey.getEncoded(), passwords.get(tmpIndex))));		
			} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	private int getIndex(String name) {
		int i;
		for (i = 0; i < servers.size(); i++) {
			if (servers.get(i).equals(name))
				return i;
		}
		return -1;
	}
}