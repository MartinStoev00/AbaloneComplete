package ss.project.networking.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import ss.project.networking.exceptions.ExitProgram;

public class AbaloneServer implements Runnable {
	private int clientNumber;
	private ServerSocket ssock;
	private AbaloneServerTUI view;
	private ArrayList<AbaloneClientHandler> clientArray;
	private ArrayList<Room> rooms;
	private HashMap<String, Integer> leaderBoard;

	/**
	 * Gives values to different variables
	 */
	public AbaloneServer() {
		clientNumber = 1;
		rooms = new ArrayList<>();
		leaderBoard = new HashMap<>();
		clientArray = new ArrayList<>();
		view = new AbaloneServerTUI();
		for (int i = 0; i < 9; i++) {
			rooms.add(new Room());
		}
	}

	/**
	 * Adds people to an arrayList and show if the are connected
	 */
	@Override
	public void run() {
		boolean openNewSocket = true;
		while (openNewSocket) {
			try {
				setup();
				while (true) {
					Socket sock = ssock.accept();
					String name = "Client" + String.format("%02d", clientNumber++);
					view.showMessage("[" + name + "] connected");
					AbaloneClientHandler handler = new AbaloneClientHandler(sock, this, name);
					new Thread(handler).start();
					clientArray.add(handler);
				}
			} catch (ExitProgram e1) {
				openNewSocket = false;
			} catch (IOException e) {
				System.out.println("Server IO error occurred: " + e.getMessage());
			}
		}
		view.showMessage("See you later!");
	}

	/**
	 * Creates the server the a give port number
	 * 
	 * @throws ExitProgram
	 */
	public void setup() throws ExitProgram {
		ssock = null;
		String ip = "";
		while (ssock == null) {
			int port = view.getInt("Please enter the server port: ");
			try {
				ip = "192.168.1.230";////////////////////////////////////////////////////////////////
				view.showMessage("Attempting to open a socket at port " + port + "...");
				ssock = new ServerSocket(port, 0, InetAddress.getByName(ip));
				view.showMessage("Server started at port " + port);
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on " + ip + " and port " + port + ".");
			}
		}
	}

	/**
	 * A getter for the rooms ArrayList
	 * 
	 * @return rooms
	 */
	public ArrayList<Room> getRooms() {
		return rooms;
	}

	/**
	 * Removes a client from clientArray
	 * 
	 * @param client
	 */
	public void removeClient(AbaloneClientHandler client) {
		clientArray.remove(client);
	}

	/**
	 * Returns the clientArray
	 * 
	 * @return
	 */
	public ArrayList<AbaloneClientHandler> getClientArray() {
		return clientArray;
	}

	/**
	 * Checks if the name is contained in the server
	 * 
	 * @param name
	 * @return
	 */
	public boolean containsClientWithName(String name) {
		for (AbaloneClientHandler ach : clientArray) {
			if (ach.getPlayer().getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the AbaloneServerTUI
	 * 
	 * @return view
	 */
	public AbaloneServerTUI getView() {
		return view;
	}

	/**
	 * Returns the leaderBoard ArrayList
	 * 
	 * @return
	 */
	public HashMap<String, Integer> getLeaderBoard() {
		return leaderBoard;
	}

	public static void main(String[] args) {
		System.out.println("Welcome to the Server! Starting...");
		new Thread(new AbaloneServer()).start();
	}
}
