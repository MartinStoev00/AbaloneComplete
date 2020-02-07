package ss.project.networking.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import ss.project.networking.exceptions.ExitProgram;
import ss.project.networking.exceptions.PortNotAvailableException;
import ss.project.networking.exceptions.ServerUnavailableException;
import ss.project.networking.protocols.ProtocolMessages;

public class AbaloneClient {

	public Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * Starts a connection using the nickname of the player
	 * 
	 * @requires Server to be available
	 * @param nickname
	 */
	public void start(String nickname) {
		try {
			connect(nickname);
		} catch (ServerUnavailableException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Gives values to the local variable
	 * 
	 * @param addr
	 * @param port
	 * @throws ExitProgram
	 * @throws PortNotAvailableException 
	 * @throws IOException
	 */
	public void createConnection(InetAddress addr, int port) throws ExitProgram, PortNotAvailableException {
		clearConnection();
		while (sock == null) {
			try {
				sock = new Socket(addr, port);
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			} catch (IOException e) {
				throw new PortNotAvailableException(String.valueOf(port));
			}

		}
		System.out.println("Connected");
	}

	/**
	 * Sets local variable to be null
	 */
	public void clearConnection() {
		sock = null;
		in = null;
		out = null;
	}

	/**
	 * Sends a message to the sever
	 * 
	 * @param msg
	 * @throws ServerUnavailableException
	 */
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

	/**
	 * Reads lie from server
	 * 
	 * @return the retuned line
	 * @throws ServerUnavailableException
	 */
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

	/**
	 * Closes the connection with the server
	 * 
	 * @throws ServerUnavailableException
	 */
	public void closeConnection() throws ServerUnavailableException {
		showMessage("Closing the connection...");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			throw new ServerUnavailableException("Could not read from server.");
		}
	}

	/**
	 * Displays the given message
	 * 
	 * @param message
	 */
	public void showMessage(String message) {
		System.out.print(message);
	}

	/**
	 * Sends a connect message
	 * 
	 * @param name
	 * @throws ServerUnavailableException
	 */
	public void connect(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.CONNECT) + String.valueOf(ProtocolMessages.DELIMITER + name));
	}

	/**
	 * Sends s disconnect message
	 * 
	 * @throws ServerUnavailableException
	 */
	public void disconnect() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.DISCONNECT) + String.valueOf(ProtocolMessages.DELIMITER));
		closeConnection();
	}

	/**
	 * Sends a move message
	 * 
	 * @param coordinates
	 * @param direction
	 * @throws ServerUnavailableException
	 */
	public void move(String coordinates, String direction) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.MOVE) + String.valueOf(ProtocolMessages.DELIMITER + coordinates + ProtocolMessages.DELIMITER + direction));
	}

	/**
	 * Sends a room request
	 * 
	 * @throws ServerUnavailableException
	 */
	public void room() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.ROOMS) + String.valueOf(ProtocolMessages.DELIMITER));
	}

	/**
	 * Sends a leaderBoard request
	 * 
	 * @throws ServerUnavailableException
	 */
	public void leaderBoard() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.LEADERBOARD) + String.valueOf(ProtocolMessages.DELIMITER));
	}

	/**
	 * Sends a challenge request
	 * 
	 * @param name
	 * @throws ServerUnavailableException
	 */
	public void challenge(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.CHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + name);
	}

	/**
	 * Accepts the given challenge
	 * 
	 * @param name
	 * @throws ServerUnavailableException
	 */
	public void acceptChallenge(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.ACCEPTCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + name);
	}

	/**
	 * Denies a given challenge
	 * 
	 * @param name
	 * @throws ServerUnavailableException
	 */
	public void denyChallenge(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.DENYCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + name);
	}

	/**
	 * Joins a given room
	 * 
	 * @param roomNum
	 * @throws ServerUnavailableException
	 */
	public void join(String roomNum) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.JOIN) + String.valueOf(ProtocolMessages.DELIMITER + roomNum));
	}

	/**
	 * Leaves the current room
	 * 
	 * @throws ServerUnavailableException
	 */
	public void leave() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.LEAVE) + String.valueOf(ProtocolMessages.DELIMITER));
	}

	/**
	 * Sends a text to the current room
	 * 
	 * @param text
	 * @throws ServerUnavailableException
	 */
	public void text(String text) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.TEXT) + String.valueOf(ProtocolMessages.DELIMITER + text));
	}

	/**
	 * Select an ally in the room
	 * 
	 * @param name
	 * @throws ServerUnavailableException
	 */
	public void ally(String name) throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.ALLY) + String.valueOf(ProtocolMessages.DELIMITER + name));
	}

	/**
	 * Sends a request to start the game
	 * 
	 * @throws ServerUnavailableException
	 */
	public void startGame() throws ServerUnavailableException {
		sendMessage(String.valueOf(ProtocolMessages.START) + String.valueOf(ProtocolMessages.DELIMITER));
	}
}
