package kerberos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import kerberos.Algorithms;
import kerberos.PortList;
import org.apache.commons.lang3.StringUtils;

public class Client implements Runnable {
	private static byte[] sessionKey;

	private String username, password, serverToContat;
	private int timer;
	public Client(String username, String password, String serverToContat) {
		this.username = username;
		this.password = password;
		this.serverToContat = serverToContat;
	}
	@Override
	public  void run() {
		try {
			Thread.sleep(Algorithms.getSleepTime());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contactAS();
	}

	public void contactAS() {
		
		Socket sock = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		
		try {
			sock = new Socket("localhost", 30000);
			out = new DataOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
			//send username to AS
			out.writeUTF(username); // write length of the message
		} catch (Exception e) {
			e.printStackTrace();
		}

		byte[] data = new byte[32];
		try {
			in.readFully(data, 0, 32);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		data= Algorithms.decrypt(data, password);
		sessionKey = Arrays.copyOfRange(data, 0, 16);
		byte[] ticket = Arrays.copyOfRange(data, 16, 32);
		contactTGS(ticket);
		try {
			sock.close();
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

	}

	public void  contactTGS(byte[] ticket) {
		Socket sock = null;
		DataInputStream in = null;
		DataOutputStream out = null;

		try {
			sock = new Socket("localhost", 20000);
			out = new DataOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
			
			//send ticket to TGS
			out.writeInt(ticket.length); // write length of the message
			out.write(ticket); // write the message
			
			//Send timer and server name to TGS
			Random rand = new Random();
			timer = (rand.nextInt(30) + 10);
			//System.out.println(username+" Timer "+timer);
			String toSend = Algorithms.rightPadInteger(timer,serverToContat);
			byte[] tmp = Algorithms.encrypt(toSend.getBytes(), Base64.getEncoder().encodeToString(sessionKey));
			out.writeInt(tmp.length); // write length of the message
			out.write(tmp);
			
			//receive encrypted serverSessionKey from TGS
			byte[] serverSessionKey = new byte[16];
			in.read(serverSessionKey);
			serverSessionKey=Algorithms.decrypt(serverSessionKey,Base64.getEncoder().encodeToString(sessionKey));
			
			//receive encrypted server ticket from TGS
			byte[] encryptedServerSessionKey = new byte[16];
			in.read(encryptedServerSessionKey);

			contactServer(serverSessionKey,encryptedServerSessionKey);
			sock.close();
			out.close();
			in.close();

		} catch (Exception ioe) {
			System.err.println(ioe);
		}
	}

	public void contactServer(byte[] serverSessionKey,byte[] encryptedServerSessionKey) {
		Socket sock = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			sock = new Socket("localhost", Algorithms.findPort(serverToContat));
			out = new DataOutputStream(sock.getOutputStream());
			in = new DataInputStream(sock.getInputStream());
			
			//send ticket to Server
			out.writeInt(encryptedServerSessionKey.length); // write length of the message
			out.write(encryptedServerSessionKey); // write the message
			
			//Send timer and client name to server
			String toSend = Algorithms.rightPadInteger(timer,username);
			byte[] tmp = Algorithms.encrypt(toSend.getBytes(), Base64.getEncoder().encodeToString(serverSessionKey));
			out.writeInt(tmp.length); // write length of the message
			out.write(tmp);
			
			
			//receive netimer from server 
			tmp = new byte[in.readInt()];
			in.readFully(tmp, 0, tmp.length); // read the message
			tmp = Algorithms.decrypt(tmp, Base64.getEncoder().encodeToString(serverSessionKey));
			int newTimer = Integer.parseInt(Algorithms.undoRightPadInteger(StringUtils.substringAfter(new String(tmp), "-")));
			System.out.println("User "+username+" has received new timer "+newTimer+" from server "+serverToContat+"\n");
			sock.close();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
