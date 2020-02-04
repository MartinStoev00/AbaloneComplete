package ss.project.networking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ss.project.abalone.Player;
import ss.project.networking.protocols.ProtocolMessages;

public class AbaloneClientHandler implements Runnable {

	private Socket sock;
	private Player player;
	private Room belongsToRoom;
	private static AbaloneServer srv;
	private BufferedReader in;
	private BufferedWriter out;
	private boolean hasNotShutDown = true;
	private boolean playing = false;
	private boolean playerHasConnected = false;
	private boolean joinesARoomForTheFirstTime = true;
	private ArrayList<String> challengedBy;
	private ArrayList<String> challenging;

	public AbaloneClientHandler(Socket sock, AbaloneServer srv, String name) {
		challengedBy = new ArrayList<>();
		challenging = new ArrayList<>();
		try {
			AbaloneClientHandler.srv = srv;
			this.sock = sock;
			player = new Player(name);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			shutdown();
			leave();
		}
	}

	@Override
	public void run() {
		String msg;
		try {
			while (hasNotShutDown) {
				msg = in.readLine();
				if (msg != null) {
					System.out.println(">Incoming: " + msg + " by " + this.toString());
					handleCommand(msg);
					out.newLine();
					out.flush();
				}
			}
		} catch (IOException e) {
			System.out.println(player.getName() + " left");
		} finally {
			shutdown();
			leave();
		}
	}

	private void handleCommand(String msg) throws IOException {
		String[] msgs = msg.split("" + ProtocolMessages.DELIMITER);
		if (msgs.length == 1) {
			switch (msgs[0]) {
			case "S":
				sendToAll(start());
				break;
			case "R":
				out.write(roomsDisplay());
				out.flush();
				break;
			case "P":
				out.write(leaderBoardDisplay());
				out.flush();
				break;
			case "L":
				sendToAll(leave());
				break;
			case "D":
				srv.removeClient(this);
				sendToAll(disconnect());
				leave();
				out.flush();
				break;
			default:
				sendToAll(Room.error("CommandNotRecognized"));
			}
		} else if (msgs.length == 2) {
			switch (msgs[0]) {
			case "B":
				sendToAll(text(msgs[1]));
				break;
			case "A":
				sendToAll(ally(msgs[1]));
				break;
			case "J":
				sendToAll(join(msgs[1]));
				removeChallengeFromEveryone();
				break;
			case "C":
				sendToAll(connect(msgs[1]));
				break;
			case "W":
				sendToSpecificPerson(challenge(msgs[1]), msgs[1]);
				break;
			case "N":
				sendToSpecificPerson(denyChallengeFromPerson(msgs[1]), msgs[1]);
				break;
			case "Y":
				sendToSpecificPerson(acceptChallenge(msgs[1]), msgs[1]);
				break;
			default:
				sendToAll(Room.error("CommandNotRecognized"));
			}
		} else if (msgs.length == 3) {
			switch (msgs[0]) {
			case "M":
				sendToAll(move(msgs[1], msgs[2]));
				break;
			default:
				sendToAll(Room.error("CommandNotRecognized"));
			}
		} else {
			sendToAll(Room.error("CommandNotRecognized"));
		}
	}

	public String text(String text) {
		if (!text.matches("^\\s\\w*$")) {
			return String.valueOf(ProtocolMessages.TEXT) + String.valueOf(ProtocolMessages.DELIMITER) + text + String.valueOf(ProtocolMessages.DELIMITER) + player.getName();
		} else {
			return Room.error("CommandNotRecognized");
		}
	}

	public boolean getPlaying() {
		return playing;
	}

	public String connect(String name) {
		if (!playerHasConnected && !srv.containsClientWithName(name) && name.matches("^[a-zA-Z0-9]*$")) {
			player.setName(name);
			playerHasConnected = true;
			srv.getLeaderBoard().put(player.getName(), 0);
			return String.valueOf(ProtocolMessages.CONNECT) + String.valueOf(ProtocolMessages.DELIMITER) + player.getName() + "\n";
		} else if (srv.containsClientWithName(name)) {
			return Room.error("NameTaken");
		} else if (!name.matches("^[a-zA-Z0-9]*$") || name.length() > 15) {
			return Room.error("CommandNotRecognized");
		}
		return Room.error("CommandNotRecognized");
	}

	public String roomsDisplay() {
		String result = String.valueOf(ProtocolMessages.ROOMS);
		for (Room r : srv.getRooms()) {
			result += r.toString();
		}
		return result + "\n";
	}

	public void removeChallengeFromEveryone() throws IOException {
		String sent = "";
		for (AbaloneClientHandler ach : srv.getClientArray()) {
			if (ach.getChallenging().contains(player.getName())) {
				sent = String.valueOf(ProtocolMessages.DENYCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + player.getName() + String.valueOf(ProtocolMessages.DELIMITER) + ach.getPlayer().getName() + "\n";
				ach.getChallenging().remove(player.getName());
				ach.getChallengedBy().remove(player.getName());
				ach.out.write(sent);
				ach.out.newLine();
				ach.out.flush();
				out.write(sent);
				out.newLine();
				out.flush();
				srv.getView().showMessage(sent);
			}
		}
		challengedBy.clear();
		challenging.clear();
	}

	public String denyChallengeFromPerson(String name) {
		challengedBy.remove(name);
		if (srv.containsClientWithName(name)) {
			for (AbaloneClientHandler ach : srv.getClientArray()) {
				ach.getChallenging().remove(name);
			}
			return String.valueOf(ProtocolMessages.DENYCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + player.getName() + String.valueOf(ProtocolMessages.DELIMITER) + name + "\n";
		}
		return Room.error("CommandNotRecognized");
	}

	public String join(String room) throws IOException {
		if (playerHasConnected && !playing) {
			try {
				int roomNumber;
				if (room.contains("C")) {
					roomNumber = Integer.parseInt(room.substring(1));
				} else {
					roomNumber = Integer.parseInt(room);
				}
				if ((roomNumber > 0 && roomNumber < 10) || room.contains("C")) {
					Room roomToBeJoined = srv.getRooms().get(roomNumber - 1);
					if (belongsToRoom != null) {
						joinesARoomForTheFirstTime = false;
					}
					if (!joinesARoomForTheFirstTime) {
						belongsToRoom.leave(this);
					}
					return roomToBeJoined.join(this);
				}

			} catch (NumberFormatException e) {
				return Room.error("CommandNotRecognized");
			}
			return Room.error("CommandNotRecognized");
		} else {
			return Room.error("NotConnected");
		}
	}

	public String turn() {
		return String.valueOf(ProtocolMessages.TURN) + String.valueOf(ProtocolMessages.DELIMITER) + belongsToRoom.turn() + "\n";
	}

	public String leave() {
		if (playerHasConnected && belongsToRoom != null && !playing) {
			joinesARoomForTheFirstTime = true;
			return belongsToRoom.leave(this);
		} else {
			return Room.error("CannotLeaveInGame");
		}
	}

	public String disconnect() {
		if (belongsToRoom != null) {
			belongsToRoom.removeMarblesBecauseOfDisconnection(player.getName());
		}
		srv.getLeaderBoard().remove(player.getName());
		srv.removeClient(this);
		try {
			removeChallengeFromEveryone();
		} catch (IOException e) {
			System.out.println("All players denied");
		}
		return String.valueOf(ProtocolMessages.DISCONNECT) + String.valueOf(ProtocolMessages.DELIMITER) + player.getName();
	}

	public static LinkedHashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public String leaderBoardDisplay() {
		LinkedHashMap<String, Integer> sortedMap = sortByValue(srv.getLeaderBoard());
		String returnedString = String.valueOf(ProtocolMessages.LEADERBOARD);

		for (String s : sortedMap.keySet()) {
			returnedString += String.valueOf(ProtocolMessages.DELIMITER) + s + String.valueOf(ProtocolMessages.DELIMITER) + sortedMap.get(s) + String.valueOf(ProtocolMessages.DELIMITER);
		}
		return returnedString + "\n";
	}

	public String start() {
		if (playerHasConnected && belongsToRoom.getLeader().equals(this) && belongsToRoom != null) {
			playing = true;
			return belongsToRoom.start();
		} else if (!belongsToRoom.getLeader().equals(this)) {
			return Room.error("InvalidPermission");
		} else if (belongsToRoom == null) {
			return Room.error("CommandNotRecognized");
		} else {
			return Room.error("NotConnected");
		}
	}

	public String ally(String name) {
		if (playerHasConnected && belongsToRoom.getLeader().equals(this) && belongsToRoom != null) {
			return belongsToRoom.ally(name);
		} else if (!belongsToRoom.getLeader().equals(this)) {
			return Room.error("InvalidPermission");
		} else if (belongsToRoom == null) {
			return Room.error("CommandNotRecognized");
		} else {
			return Room.error("NotConnected");
		}
	}

	public String finish() {
		playing = false;
		String result = String.valueOf(ProtocolMessages.FINISH) + String.valueOf(ProtocolMessages.DELIMITER);
		for (String s : belongsToRoom.winners()) {
			result += s + String.valueOf(ProtocolMessages.DELIMITER);
		}
		return result;
	}

	public String move(String coordinates, String direction) {
		if (playerHasConnected && belongsToRoom != null) {
			return belongsToRoom.move(coordinates, direction, this.getPlayer());
		} else if (belongsToRoom == null) {
			return Room.error("CommandNotRecognized");
		} else {
			return Room.error("NotConnected");
		}
	}

	public String challenge(String name) {
		if (srv.containsClientWithName(name) && !playing && belongsToRoom == null && !name.equals(player.getName())) {
			challenging.add(name);
			for (AbaloneClientHandler ach : srv.getClientArray()) {
				if (ach.getPlayer().getName().equals(name) && !ach.getPlaying() && ach.getRoom() == null) {
					ach.addChallengers(player.getName());
					return String.valueOf(ProtocolMessages.CHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + player.getName() + String.valueOf(ProtocolMessages.DELIMITER) + name + "\n";
				}
			}
		}
		return Room.error("CommandNotRecognized");
	}

	public String acceptChallenge(String name) throws IOException {
		if (srv.containsClientWithName(name) && !playing && belongsToRoom == null && challengedBy.contains(name)) {
			for (AbaloneClientHandler ach : srv.getClientArray()) {
				if (ach.getPlayer().getName().equals(name) && !ach.getPlaying() && ach.getRoom() == null && ach.getChallenging().contains(player.getName())) {
					challengedBy.remove(name);
					ach.removeChallengers(player.getName());
					belongsToRoom = new Room();
					belongsToRoom.startChallenge();
					srv.getRooms().add(belongsToRoom);
					join(belongsToRoom.getNumber());
					ach.join(belongsToRoom.getNumber());
					sendToAll(start());
					removeChallengeFromEveryone();
					return String.valueOf(ProtocolMessages.ACCEPTCHALLENGE) + String.valueOf(ProtocolMessages.DELIMITER) + player.getName() + String.valueOf(ProtocolMessages.DELIMITER) + name + "\n";
				}
			}
		}
		return Room.error("CommandNotRecognized");
	}

	public void addChallengers(String name) {
		challengedBy.add(name);
	}

	public void removeChallengers(String name) {
		challengedBy.remove(name);
	}

	public ArrayList<String> getChallenging() {
		return challenging;
	}

	public ArrayList<String> getChallengedBy() {
		return challengedBy;
	}

	public void sendToSpecificPerson(String message, String name) throws IOException {
		if (message.charAt(0) != 'E' && srv.containsClientWithName(name)) {
			out.write(message);
			out.newLine();
			out.flush();
			for (AbaloneClientHandler ach : srv.getClientArray()) {
				if (ach.getPlayer().getName().equals(name)) {
					ach.out.write(message);
					ach.out.newLine();
					ach.out.flush();
				}
			}
		} else {
			out.write(Room.error("CommandNotRecognized"));
			out.newLine();
			out.flush();
		}
	}

	public static void addPointToLeaderBoard(String name) {
		for (String s : srv.getLeaderBoard().keySet()) {
			if (s.equals(name)) {
				int newResult = srv.getLeaderBoard().get(s) + 1;
				srv.getLeaderBoard().put(s, newResult);
			}
		}
	}

	public void sendToAll(String message) throws IOException {
		char firstLetter = message.charAt(0);
		if (firstLetter == ProtocolMessages.ERROR) {
			out.write(message);
			out.flush();
		} else {
			if (belongsToRoom != null || ProtocolMessages.roomChar.contains(Character.valueOf(firstLetter))) {
				if (message.charAt(0) == 'M' || message.charAt(0) == 'S' || message.charAt(0) == 'D') {
					for (AbaloneClientHandler ach : belongsToRoom.getRoom()) {
						if (message.charAt(0) != 'D' || ach != this) {
							ach.out.write(message);
							ach.out.newLine();
							ach.out.flush();
							if (belongsToRoom.gameHasEnded()) {
								ach.out.write(finish());
								ach.out.newLine();
								ach.out.flush();
							} else if (belongsToRoom.gameHasStarted() && !belongsToRoom.gameHasEnded()) {
								ach.out.write(turn());
								ach.out.newLine();
								ach.out.flush();
							}
						}
					}
				} else {
					if (belongsToRoom != null) {
						for (AbaloneClientHandler ach : belongsToRoom.getRoom()) {
							ach.out.write(message);
							ach.out.newLine();
							ach.out.flush();
						}
					} else {
						out.write(Room.error("CommandNotRecognized"));
						out.newLine();
						out.flush();
					}
				}
			} else if (ProtocolMessages.serverChar.contains(Character.valueOf(firstLetter))) {
				for (AbaloneClientHandler ach : srv.getClientArray()) {
					ach.out.write(message);
					ach.out.newLine();
					ach.out.flush();
				}
			} else {
				out.write(Room.error("CommandNotRecognized"));
				out.newLine();
				out.flush();
			}
		}
		if (belongsToRoom != null && belongsToRoom.getNumber().charAt(0) == 'C' && belongsToRoom.gameHasEnded()) {
			srv.getRooms().remove(belongsToRoom);
			belongsToRoom = null;
		}
	}

	public void setRoom(Room r) {
		belongsToRoom = r;
	}

	public Room getRoom() {
		return belongsToRoom;
	}

	public Player getPlayer() {
		return player;
	}

	private void shutdown() {
		try {
			sendToAll(disconnect());
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			System.out.println(player.getName() + " has disconnected!");
		}
	}

	public String toString() {
		return player.toString();
	}

	public boolean equals(AbaloneClientHandler input) {
		if (input instanceof AbaloneClientHandler) {
			return this.toString().equals(input.toString());
		}
		return false;
	}
}
