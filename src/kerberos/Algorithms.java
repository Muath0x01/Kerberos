package kerberos;
//This class contains some methods used in all other classes
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

public class Algorithms {
	static ArrayList<String> servers = new ArrayList<String>();
	static ArrayList<Integer> ports = new ArrayList<Integer>();
	static int sleepTime;
	static String currentUser;
	public synchronized static int getSleepTime() {
		int tmp = sleepTime;
		sleepTime += 750;
		return tmp;
	}

	public static void initialize() {
		sleepTime = 0;
		currentUser=null;
/*		servers.add("Bob");
		servers.add("test1");
		servers.add("test2");
		servers.add("test3");
		servers.add("test4");
		ports.add(11000);
		ports.add(12000);
		ports.add(13000);
		ports.add(14000);
		ports.add(15000);*/
	}

	public static String getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(String currentUser) {
		Algorithms.currentUser = currentUser;
	}

	public static int findPort(String name) {
		int i;
		for (i = 0; i < servers.size(); i++) {
			if (servers.get(i).equals(name))
				return ports.get(i);
		}
		return -1;

	}

	public static SecretKeySpec setKey(String myKey) {
		try {
			byte[] key = rightPadString(myKey).getBytes("UTF-8");
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			return secretKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encrypt(byte[] arrayToEncrypt, String myKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, setKey(myKey));
			return cipher.doFinal(arrayToEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decrypt(byte[] arrayToDecrypt, String myKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, setKey(myKey));
			return cipher.doFinal(arrayToDecrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String rightPadString(String str) {
		if (str.length() > 16)
			str = str.substring(0, 16);
		return StringUtils.rightPad(str, 16, '1');
	}

	public static String rightPadInteger(int number, String serverToContat) {
		String str = serverToContat + "-" + number;
		if (str.length() > 16)
			str = str.substring(0, 16);
		return StringUtils.rightPad(str, 16, '#');
	}

	public static String undoRightPadInteger(String str) {
		return StringUtils.remove(str, '#');
	}

	public static SecretKey generateKey() {
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyGen.init(128); // for example
		return keyGen.generateKey();
	}

}
