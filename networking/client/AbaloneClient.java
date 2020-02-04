package ss.project.networking.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import ss.project.networking.exceptions.ExitProgram;
import ss.project.networking.exceptions.ServerUnavailableException;
import ss.project.networking.protocols.ProtocolMessages;

public class AbaloneClient {

	private Socket serverSock;
	private BufferedReader in;
	private BufferedWriter out;

	public void start(String nickname) {
		try {
			connect(nickname);
		} catch (ServerUnavailableException e) {
			e.printStackTrace();
			return;
		}
	}

	public void createConnection(InetAddress addr, int port) throws ExitProgram, IOException {
		clearConnection();
		while (serverSock == null) {
			serverSock = new Socket(addr, port);
			in = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));

		}
		System.out.println("Connected");
	}

	public void clearConnection() {
		serverSock = null;
		in = null;
		out = null;
	}

	public synchronized void sendMessage(String msg) throws ServerUnavailableException {
		if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				showMessage(e.getMessage());
				throw new ServerUnavailableException("Could not write to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write to server.");
		}
	}

	public String readLineFromServer() throws ServerUnavailableException {
		if (in != null) {
			try {
				String answer = in.readLine();
				if (answer == null) {
					throw new ServerUnavailableException("Could not read from server.");
				}
				return answer;
			} catch (IOException e) {
				throw new ServerUnavailableException("Could not read from server.");
			}
		} else {
			throw new ServerUnavailableException("Could not read from server.");
		}
	}

	public void closeConnection() throws ServerUnavailableException {
		showMessage("Closing the connection...");
		try {
			in.close();
			out.close();
			serverSock.close();
		} catch (IOException e) {
			throw new ServerUnavailableException("Could not read from server.");
		}
	}

	public void showMessage(String message) {
		System.out.print(message);
	}

	public void connect(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.CONNECT) + String.valueOf(ProtocolMessages.DELIMITER + name));
	}

	public void disconnect() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.DISCONNECT) + String.valueOf(ProtocolMessages.DELIMITER));
		closeConnection();
	}

	public void move(String coordinates, String direction) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.MOVE) + String.valueOf(ProtocolMessages.DELIMITER + coordinates + ProtocolMessages.DELIMITER + direction));
		showMessage(readLineFromServer());
	}

	public void room() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.ROOMS) + String.valueOf(ProtocolMessages.DELIMITER));
		showMessage(readLineFromServer());
	}

	public void leaderBoard() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.LEADERBOARD) + String.valueOf(ProtocolMessages.DELIMITER));
		showMessage(readLineFromServer());
	}

	public void challenge(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.CHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + name);
		showMessage(readLineFromServer());
	}

	public void acceptChallenge(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.ACCEPTCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + name);
		showMessage(readLineFromServer());
	}

	public void denyChallenge(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.DENYCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + name);
		showMessage(readLineFromServer());
	}

	public void join(String roomNum) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.JOIN) + String.valueOf(ProtocolMessages.DELIMITER + roomNum));
		showMessage(readLineFromServer());
	}

	public void leave() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.LEAVE) + String.valueOf(ProtocolMessages.DELIMITER));
		showMessage(readLineFromServer());
	}

	public void text(String text) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.TEXT) + String.valueOf(ProtocolMessages.DELIMITER + text));
		showMessage(readLineFromServer());
	}

	public void ally(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.ALLY) + String.valueOf(ProtocolMessages.DELIMITER + name));
		showMessage(readLineFromServer());
	}

	public void startGame() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.START) + String.valueOf(ProtocolMessages.DELIMITER));
		showMessage(readLineFromServer());
	}
}
