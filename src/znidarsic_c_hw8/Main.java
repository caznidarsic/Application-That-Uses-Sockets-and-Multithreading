package znidarsic_c_hw8;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class Main {
	
	private static int PORT = 20010;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
		}
		
		Socket clientSocket = null;
				
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				ClientThread thread = new ClientThread(clientSocket);
				thread.start();
			} catch (IOException e) {
				System.err.println("Accept failed.");
			}
		}

	}

}
