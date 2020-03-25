package kerberos;

import kerberos.Algorithms;


public class TestClass {

	public static void main(String[] args) {
		Algorithms.initialize();
		//run key distribution center
		new Thread(new AS(30000)).start();
		new Thread(new TGS(20000)).start();
		
		//run two servers
		new Thread(new Server("Bob", "PQR456987",1500)).start();
		new Thread(new Server("test1", "RST987987",2000)).start();
		
		//run test clients
		new Thread(new Client("Alice", "ABC123456", "Bob")).start();//Normal registeres lient
		new Thread(new Client("Muath", "qqqqqqq", "test1")).start();//Invalid password
		new Thread(new Client("Ahmed", "aaaaaa", "test1")).start();//unregistered user
		
		
		
		//adding client example
		AS_Service.addAcount("Ali", "bbbbbb");
		
		//running new client
		new Thread(new Client("Ali", "bbbbbb", "test1")).start();//unregistered user


	}
	
	/*current registered users and servers
	Alice - ABC123456
	Muath - DEF456789
	Mohammed - GHI987654
	Abdulrahman - JKL654123
	Micheal - MNO123123


	Server-TGS stored in TGS
	username - password - port
	Bob - PQR456987 - 11000
	test1 - RST987987 - 12000
	test2 - UVW654456 - 13000
	test3 - XYZ321789 - 14000
	test4 - ABC999999 - 15000
	*/

}
