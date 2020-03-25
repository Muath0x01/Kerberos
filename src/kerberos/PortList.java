package kerberos;

import java.util.ArrayList;

public class PortList {
	ArrayList<String> servers = new ArrayList<String>();
	ArrayList<Integer> ports = new ArrayList<Integer>();

	public PortList(){
		servers.add("Bob");
		servers.add("test1");
		servers.add("test2");
		servers.add("test3");
		servers.add("test4");
		ports.add(11000);
		ports.add(12000);
		ports.add(13000);
		ports.add(14000);
		ports.add(15000);
	}
	
	
	public int findPort(String serverName){
			int i;
			for (i = 0; i < servers.size(); i++) {
				if (servers.get(i).equals(serverName))
					return ports.get(i);
			}
			return -1;
		
	}
}
