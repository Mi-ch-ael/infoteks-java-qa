package com.example.qa;

import java.net.MalformedURLException;

public class Main {
	public static void main(String[] args){
		if(args.length != 3) {
			System.err.println("Pass exactly three command-line arguments: <username> <password> " +
					"<URL of remote file>");
			return;
		}
		ConnectionManager connectionManager;
		try {
			connectionManager = new ConnectionManager(args[0], args[1], args[2]);
		}
		catch (MalformedURLException e) {
			System.err.println("Given URL does not seem right. Unable to connect, exiting.");
			e.printStackTrace();
			return;
		}
		new IOProcessor(System.in, new ConnCommandRunner(connectionManager), args[0], args[2]).process();
	}
}
