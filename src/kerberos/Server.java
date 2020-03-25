package kerberos;

import java.net.*;
import java.io.*;
import kerberos.Algorithms;

public class Server implements Runnable {
	ServerSocket serverSocket = null;
	int port;
	String name;
	String password;
	public Server(String name, String password,int port) {
		Algorithms.servers.add(name);
		Algorithms.ports.add(port);
		this.name=name;
		this.port = port;
		this.password=password;
	}

	@Override
	public void run()  {
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				Socket client = serverSocket.accept();
				new ServiceServer(client,name,password).start();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		} finally {
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (Exception e) {
					System.err.println(e);
				}
		}
		
	}

}
