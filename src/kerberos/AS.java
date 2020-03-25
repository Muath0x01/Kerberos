package kerberos;

import java.net.*;
import java.io.*;

public class AS implements Runnable {
	ServerSocket AS_serverSocket = null;
	int port;

	public AS(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			AS_serverSocket = new ServerSocket(port);
			while (true) {
				Socket client = AS_serverSocket.accept();
				new AS_Service(client).start();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		} finally {
			if (AS_serverSocket != null)
				try {
					AS_serverSocket.close();
				} catch (Exception e) {
					System.err.println(e);
				}
		}

	}

}
