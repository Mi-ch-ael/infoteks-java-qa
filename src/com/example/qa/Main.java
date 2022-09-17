package com.example.qa;

import java.net.MalformedURLException;

public class Main {
	public static void main(String[] args){
		if(args.length != 3) {
			System.out.println("Pass exactly three command-line arguments: <username> <password> " +
					"<URL of remote file>");
			return;
		}
		ConnectionManager connectionManager;
		try {
			connectionManager = new ConnectionManager(args[0], args[1], args[2]);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		Client client = new Client(connectionManager);
		/*try {
			String students = connectionManager.getStudents();
			System.out.println(students);
			List<Student> list = JSONParser.getInstance().parse(students);
			System.out.println(JSONParser.getInstance().stringify(list));
		}
		catch (IOException e) {
			System.err.println("Could not retrieve student list because of error:");
			e.printStackTrace();
		}
		catch (ScriptException e) {
			System.err.println("Could not parse student list because of error:");
			e.printStackTrace();
		}
		String data = "{\"students\": []}";
		try {
			connectionManager.putStudents(data);
		}
		catch (IOException e) {
			System.err.println("Could not send student list because of error:");
			e.printStackTrace();
		}
		try {
			System.out.println(connectionManager.getStudents());
		}
		catch (IOException e) {
			System.err.println("Could not retrieve student list because of error:");
			e.printStackTrace();
		}*/
		new IOProcessor(System.in, client, args[0], args[2]).process();
	}
}
