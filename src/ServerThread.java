
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread {
	Socket connSocket;
	BufferedReader outToClientReader = new BufferedReader(new InputStreamReader(System.in));
	DataOutputStream outToClient;
	BufferedReader inFromClient;
	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
		try {
			this.outToClient = new DataOutputStream(connSocket.getOutputStream());
			this.inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void run() {
		try {
			// Do the work and the communication with the client here
			String clientSentence;
			Boolean connection = false;
			// handshake protokol
			String protocol = "For at starte en chat, skal protokollen følges. Din første besked SKAL\n" +
					"starte med Snakke efterfulgt af et mellemrum og dit navn.\n";
			outToClient.writeBytes(protocol);
			outToClient.flush();
			while (!connection && connSocket.isConnected()) {
				try {
					clientSentence = inFromClient.readLine();
					if (clientSentence.substring(0, 6).equals("Snakke")) {
						outToClient.writeBytes("Vent på svar...\n");
						outToClient.flush();
						System.out.println(clientSentence.substring(7) + " vil snakke med dig.\n" +
								"Vil du snakke med ham? Type ja eller nej....");
						while (!connection) {
							String answer = outToClientReader.readLine();
							if (answer.equals("ja") || answer.equals("nej")) { // det er klientens ansvar at bryde forbindelsen, hvis svaret er nej
								if (answer.equals("ja")) {
									answer = answer + ". Handshake protokol klaret.\n";
									outToClient.writeBytes(answer);
									outToClient.flush();
									connection = true;
								} else if (answer.equals("nej")) {
									answer = answer + ". Server vil ikke snakke med dig.\n";
									outToClient.writeBytes(answer);
									outToClient.flush();
									connection = true;
								}
								connection = true;
								System.out.println("Handshake protokol klaret.");
							} else {
								System.out.println("Du skrev ikke ja eller nej (lowercase). Prøv igen.");
							}
						}
					} else {
						outToClient.writeBytes("Din besked overholdte ikke protokollen. Prøv igen.\n");
					}
				} catch (StringIndexOutOfBoundsException e) {
					// Tilfælde hvor stringen er for lille
					outToClient.writeBytes("Din besked er for kort og overholder ikke protokollen. Prøv igen.\n");
				} catch (IOException e) {
					// And tilfælde
					e.printStackTrace();
				}
            }
			outToClient.flush();
			clientThread client = new clientThread();
			client.start();

			while (connSocket.isConnected()) {
				clientSentence = inFromClient.readLine();
				if (clientSentence == null || clientSentence.equals("close")) {
					break;
				}
				System.out.println(clientSentence);
			}
			connSocket.close();
			System.out.println("Connection closed.\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// thread der repræsenterer den client, der er forbundet
	public class clientThread extends Thread {
		public clientThread() {
		}
		public void run() {
			try {
				String message;
				while (connSocket.isConnected()) {
					message = outToClientReader.readLine();
					outToClient.writeBytes(message + '\n');
					outToClient.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
