import java.io.*;
import java.net.*;

public class TCPClient {

	public static void main(String argv[]) throws Exception {
		//System.out.println("Type close to end connection. Type total close to close client\nand server.");
		try {
			// Establish connection to the server
			Socket clientSocket = new Socket("localhost", 6969);

			// Start the receive thread
			RecieveThread receiveThread = new RecieveThread(clientSocket);
			receiveThread.start();

			// Start the send thread
			SendThread sendThread = new SendThread(clientSocket);
			sendThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

public static class RecieveThread extends Thread {
		private Socket socket;
		public RecieveThread(Socket clientSocket) {
			socket = clientSocket;
		}
		public void run() {
			try {
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String serverMessage;
				boolean protocolCleared = false;
				// handshake protokol
				while ((serverMessage = inFromServer.readLine()) != null && !protocolCleared) {
					if (serverMessage.toLowerCase().contains("ja")) {
						System.out.println("FROM SERVER: " + serverMessage);
						System.out.println("Protokol klaret.\n");
						protocolCleared = true;
					} else if (serverMessage.toLowerCase().contains("nej")) {
						System.out.println("FROM SERVER: " + serverMessage);
						System.out.println("Forbindelse afbrydes.");
						protocolCleared = true;
						socket.close();
					} else {
						System.out.println("FROM SERVER: " + serverMessage);
					}
				}

				while ((serverMessage = inFromServer.readLine()) != null && protocolCleared) {
					System.out.println("FROM SERVER: " + serverMessage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class SendThread extends Thread {
		private Socket socket;
		public SendThread(Socket clientSocket) {
			socket = clientSocket;
		}
		public void run() {
			try {
				BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				String userInput;
				while ((userInput = inFromUser.readLine()) != null) {
					outToServer.writeBytes(userInput + '\n');
					if (userInput.toLowerCase().equals("close")) {
						socket.close();
					}
					outToServer.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}


