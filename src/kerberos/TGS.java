package kerberos;

import java.net.*;
import java.io.*;

public class TGS implements Runnable {
	ServerSocket TGS_serverSocket = null;
	int port;

	public TGS(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			TGS_serverSocket = new ServerSocket(port);
			while (true) {
				Socket client = TGS_serverSocket.accept();
				new TGS_Service(client).start();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
		} finally {
			if (TGS_serverSocket != null)
				try {
					TGS_serverSocket.close();
				} catch (Exception e) {
					System.err.println(e);
				}
		}
		
	}

}
