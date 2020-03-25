package kerberos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import kerberos.Algorithms;

public class AS_Service extends Thread {
	Socket client = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	private String AS_TGS = "KerberosIsTheBest";
	static ArrayList<String> usernames = new ArrayList<String>();
	static ArrayList<String> passwords = new ArrayList<String>();

	public AS_Service(Socket client) {
		super();
		usernames.add("Alice");
		usernames.add("Muath");
		usernames.add("Mohammed");
		usernames.add("Abdulrahman");
		usernames.add("Micheal");
		passwords.add("ABC123456");
		passwords.add("DEF456789");
		passwords.add("GHI987654");
		passwords.add("JKL654123");
		passwords.add("MNO123123");

		this.client = client;
		play();
	}
	public static void addAcount(String username,String password){
		usernames.add(username);
		passwords.add(password);
	}
		void  play() {
		try {
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}

		// get username
		String username = null;
		try {
			username = in.readUTF();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		int index = getIndex(username);
		if (index != -1) {
			Algorithms.setCurrentUser(username);
			System.out.println("User " + username + " has contacted Authintication Server.");
			SecretKey secretKey = Algorithms.generateKey();
			byte[] tmp1, tmp2;
			tmp1 = secretKey.getEncoded();
			tmp2 = Algorithms.encrypt(secretKey.getEncoded(), AS_TGS);

			//organize the data to be sent in one array of bytes
			byte[] data = null;
			try {
				data = new byte[32];
				System.arraycopy(tmp1, 0, data, 0, tmp1.length);
				System.arraycopy(tmp2, 0, data, tmp1.length, tmp2.length);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			byte[] dataToSend = Algorithms.encrypt(data, passwords.get(index));
			
			//send the data
			try {
				out.write(dataToSend);
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			System.out.println("User claiming to be " + username + " has been issued a ticket by AS.");


		} else {
			System.out.println("User " + username + " is not a registered user!");
		}
		try {
			client.close();
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int getIndex(String name) {
		int i;
		for (i = 0; i < usernames.size(); i++) {
			if (usernames.get(i).equals(name))
				return i;
		}
		return -1;
	}
}
