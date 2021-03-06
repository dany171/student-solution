package com.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	// CONSTANTS
	private static final int PORT = 6789;
	
	private static final String REMOVE_UNWANTED_SYMBOLS = "[\\[\\],]";

	// PROPERTIES
	private static String host = "localhost";

	private static boolean runClient = true;

	// METHODS
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome");

		// Loop for client console
		while (runClient) {

			String inputString = scanner.nextLine();

			switch (inputString) {

			case "exit":
				
				runClient = false;
				sendCommand(inputString);
				break;

			case "host":
				host = inputString;
				break;

			default:
				sendCommand(inputString);
			}
		}

		scanner.close();

	}

	public static void sendCommand(String command) {
		try {
			Socket clientSocket = new Socket(host, PORT);

			OutputStream os = clientSocket.getOutputStream();
			DataOutputStream outToServer = new DataOutputStream(os);
			outToServer.writeBytes(command + '\n');

			InputStream is = clientSocket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader inFromServer = new BufferedReader(isr);

			String line = inFromServer.readLine();
			
			line = line.replaceAll(REMOVE_UNWANTED_SYMBOLS, "");
			line = line.replace("@", "\n");
			
			System.out.println(line);
			
			clientSocket.close();
			
		} catch (Exception e) {
			
			System.out.println(e);
			
		}
	}
}