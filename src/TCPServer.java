
import java.io.*;
import java.net.*;
public class TCPServer {

	public static void main(String[] args)throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6969);
		System.out.println("This address: " + InetAddress.getLocalHost().toString() + "\n" +
				"Port: " + welcomeSocket.getLocalPort());
		WaitingThread waitingThread = new WaitingThread();
		waitingThread.start();
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			waitingThread.stopWaiting();
			(new ServerThread(connectionSocket)).start();
		}
	}
}
