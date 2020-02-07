package ss.project.networking.server;

import java.util.ArrayList;

import ss.project.abalone.Board;
import ss.project.abalone.Mark;
import ss.project.abalone.Player;
import ss.project.networking.protocols.ProtocolMessages;

public class Room {

	private String name;
	private int turn = 0;
	private Board localBoard;
	private int turnLimit = 96;
	private ArrayList<Mark> marks;
	private static int number = 0;
	private ArrayList<String> winners;
	private boolean allyPicked = false;
	private boolean gameHasEnded = false;
	private boolean gameHasStarted = false;
	private ArrayList<AbaloneClientHandler> room;

	
	
	/**
	 * Gives values to the ArrayList with the players and winners and increments the
	 * room number
	 */
	public Room() {
		number++;
		room = new ArrayList<>();
		winners = new ArrayList<>();
		name = String.valueOf(number);
	}

	/**
	 * Returns the party leader of the room
	 * 
	 * @return room.get(0)
	 */
	public AbaloneClientHandler getLeader() {
		return room.get(0);
	}

	/**
	 * Person leaves the room
	 * 
	 * @param removedPlayer
	 * @return
	 */
	public String leave(AbaloneClientHandler removedPlayer) {
		room.remove(removedPlayer);
		removedPlayer.setRoom(null);
		if (!(gameHasStarted && !gameHasEnded)) {
			removeMarblesBecauseOfDisconnection(removedPlayer.getPlayer().getName());
		}
		return String.valueOf(ProtocolMessages.LEAVE) + String.valueOf(ProtocolMessages.DELIMITER) + removedPlayer.getPlayer().getName() + "\n";
	}

	/**
	 * Add person to the room ArrayList
	 * 
	 * @param addedPlayer
	 * @return the message
	 */
	public String join(AbaloneClientHandler addedPlayer) {
		if (room.size() < 4 && !(gameHasStarted && !gameHasEnded)) {
			room.add(addedPlayer);
			addedPlayer.setRoom(this);
			return String.valueOf(ProtocolMessages.JOIN) + String.valueOf(ProtocolMessages.DELIMITER) + name + String.valueOf(ProtocolMessages.DELIMITER + addedPlayer.getPlayer().getName()) + "\n";
		} else if (room.size() == 4) {
			return error("Full");
		} else if (!!(gameHasStarted && !gameHasEnded)) {
			return error("RoomHasStarted");
		}
		return error("CommandNotRecognized");
	}

	/**
	 * Select an ally
	 * 
	 * @param name
	 * @requires isFour && !gameHasStarted && hasPerson && !isSameName
	 * @return the message
	 */
	public String ally(String name) {
		boolean isFour = room.size() == 4;
		boolean isSameName = getLeader().equals(getPlayerByName(name));
		boolean hasPerson = containsPlayerWithName(name);
		if (isFour && !gameHasStarted && hasPerson && !isSameName) {
			AbaloneClientHandler aux = room.get(2);
			int initIndex = room.indexOf(getPlayerByName(name));
			room.set(2, getPlayerByName(name));
			room.set(initIndex, aux);
			allyPicked = true;
			return String.valueOf(ProtocolMessages.ALLY) + String.valueOf(ProtocolMessages.DELIMITER) + room.get(2).getPlayer().getName() + "\n";
		} else if (!isFour) {
			return error("InvalidPermission");
		} else if (!hasPerson) {
			return error("AllyNotInRoom");
		} else if (isSameName) {
			return error("CommandNotRecognized");
		} else if (gameHasStarted) {
			return error("RoomHasStarted");
		}
		return error("InvalidPermission");
	}

	/**
	 * Removes the marbles of a specific person and add people to winner ArrayList
	 * 
	 * @param name
	 */
	public void removeMarblesBecauseOfDisconnection(String name) {
		if (gameHasStarted && room.size() == 3) {
			if (name.equals(turn())) {
				turn++;
				turnLimit++;
			}
			localBoard.removeMarbles(getMarkByPlayerName(name));
			marks.remove(getMarkByPlayerName(name));
		} else if (gameHasStarted) {
			gameHasEnded = true;
			if (room.size() == 2) {
				if (room.get(0).getPlayer().getName().equals(name)) {
					winners.add(room.get(1).getPlayer().getName());
					AbaloneClientHandler.addPointToLeaderBoard(room.get(1).getPlayer().getName());
				} else {
					winners.add(room.get(0).getPlayer().getName());
					AbaloneClientHandler.addPointToLeaderBoard(room.get(0).getPlayer().getName());
				}
			} else if (room.size() == 4) {
				if (room.get(0).getPlayer().getName().equals(name) || room.get(2).getPlayer().getName().equals(name)) {
					winners.add(room.get(1).getPlayer().getName());
					AbaloneClientHandler.addPointToLeaderBoard(room.get(1).getPlayer().getName());
					winners.add(room.get(3).getPlayer().getName());
					AbaloneClientHandler.addPointToLeaderBoard(room.get(3).getPlayer().getName());
				} else {
					winners.add(room.get(0).getPlayer().getName());
					AbaloneClientHandler.addPointToLeaderBoard(room.get(0).getPlayer().getName());
					winners.add(room.get(2).getPlayer().getName());
					AbaloneClientHandler.addPointToLeaderBoard(room.get(2).getPlayer().getName());
				}
			}
		}
	}

	/**
	 * Changes the room name if it is a challenge room
	 */
	public void startChallenge() {
		name = "C" + name;
	}

	/**
	 * Starts the room
	 * 
	 * @requires room.size() > 1 && !(gameHasStarted && !gameHasEnded) &&
	 *           !(room.size() == 4 ^ allyPicked)
	 * @return message for starting the game
	 */
	public String start() {
		if (room.size() > 1 && !(gameHasStarted && !gameHasEnded) && !(room.size() == 4 ^ allyPicked)) {
			ArrayList<Player> players = new ArrayList<>();
			String resultPart = "";
			for (AbaloneClientHandler clients : room) {
				players.add(clients.getPlayer());
				resultPart += ";" + clients.getPlayer().getName();
			}
			localBoard = new Board(players);
			turn = 0;
			winners.clear();
			if (marks != null) {
				marks.clear();
			}
			gameHasStarted = true;
			gameHasEnded = false;
			allyPicked = false;
			turnLimit = 96;
			marks = localBoard.differentMark();
			return String.valueOf(ProtocolMessages.START) + String.valueOf(ProtocolMessages.DELIMITER) + room.size() + resultPart + String.valueOf(ProtocolMessages.DELIMITER) + "\n";
		} else if (room.size() == 1) {
			return error("Empty");
		} else if (room.size() == 4 ^ allyPicked) {
			return error("NoAllyPicked");
		}
		return error("GameHasStarted");
	}

	/**
	 * Constructs an error message
	 * 
	 * @param error
	 * @return error message
	 */
	public static String error(String error) {
		return String.valueOf(ProtocolMessages.ERROR) + String.valueOf(ProtocolMessages.DELIMITER) + error + "\n";
	}

	/**
	 * Returns the current player
	 * 
	 * @requires gameHasStarted && !gameHasEnded
	 * @return current player name
	 */
	public String turn() {
		if (gameHasStarted && !gameHasEnded) {
			return room.get(turn % marks.size()).getPlayer().getName();
		} else {
			return error("CommandNotRecognized");
		}
	}

	/**
	 * A getter for the winners
	 * 
	 * @return winners
	 */
	public ArrayList<String> winners() {
		return winners;
	}

	/**
	 * Makes a move on the local board
	 * 
	 * @param coordinates
	 * @param direction
	 * @param player
	 * @requires !localBoard.hasWinner() && isValidMove && isCurrentMark && turn <=
	 *           turnLimit && gameHasStarted && !gameHasEnded
	 * @return move message
	 */
	public String move(String coordinates, String direction, Player player) {
		boolean isValidMove = localBoard.isValidMove(coordinates, direction, getMarkByPlayerName(turn()));
		boolean isCurrentMark = getMarkByPlayerName(turn()) == player.getMark();
		if (!localBoard.hasWinner() && isValidMove && isCurrentMark && turn < turnLimit && gameHasStarted && !gameHasEnded) {
			localBoard.move(coordinates, direction, getMarkByPlayerName(turn()));
			turn++;
			if (localBoard.hasWinner() || turn == turnLimit) {
				gameHasEnded = true;
				for (AbaloneClientHandler j : room) {
					if (localBoard.isWinner(j.getPlayer().getMark())) {
						winners.add(j.getPlayer().getName());
						AbaloneClientHandler.addPointToLeaderBoard(j.getPlayer().getName());
					}
				}
			}
			return String.valueOf(ProtocolMessages.MOVE) + String.valueOf(ProtocolMessages.DELIMITER) + coordinates + String.valueOf(ProtocolMessages.DELIMITER) + direction + String.valueOf(ProtocolMessages.DELIMITER + player.getName())
					+ "\n";
		} else if (!isCurrentMark) {
			return error("OutOfTurns");
		} else if (gameHasEnded) {
			return error("RoomHasEnded");
		} else if (!isValidMove) {
			return error("MoveNotAllowed");
		} else if (turn > turnLimit) {
			gameHasEnded = true;
			return error("CommandNotRecognized");
		} else if (localBoard.hasWinner()) {
			return error("CommandNotRecognized");
		} else if (!!(gameHasStarted && !gameHasEnded)) {
			return error("CommandNotRecognized");
		}
		return error("OutOfTurns");
	}

	/**
	 * Return gameHasEnded variable
	 * 
	 * @return gameHasEnded
	 */
	public boolean gameHasEnded() {
		return gameHasEnded;
	}

	/**
	 * Returns the AbaloneClientHandler based on name
	 * 
	 * @param name
	 * @requires person to be in a room
	 * @return AbaloneClientHandler based on name
	 */
	public AbaloneClientHandler getPlayerByName(String name) {
		for (AbaloneClientHandler ach : room) {
			if (ach.getPlayer().getName().equals(name)) {
				return ach;
			}
		}
		return null;
	}

	/**
	 * Checks if a person is in the room
	 * 
	 * @param name
	 * @return true if person is in the room ArrayList
	 */
	public boolean containsPlayerWithName(String name) {
		if (getPlayerByName(name) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the board
	 * 
	 * @return localBoard
	 */
	public Board getBoard() {
		return localBoard;
	}

	/**
	 * Returns the gameHasStarted
	 * 
	 * @return gameHasStarted
	 */
	public boolean gameHasStarted() {
		return gameHasStarted;
	}

	/**
	 * Returns the room ArrayList
	 * 
	 * @return room
	 */
	public ArrayList<AbaloneClientHandler> getRoom() {
		return room;
	}

	/**
	 * Gets the Mark based on the given name
	 * 
	 * @param name
	 * @return Mark based on the given name
	 */
	public Mark getMarkByPlayerName(String name) {
		for (AbaloneClientHandler ach : room) {
			if (ach.getPlayer().getName().equals(name)) {
				return ach.getPlayer().getMark();
			}
		}
		return null;
	}

	/**
	 * Gets the name of the room
	 * 
	 * @return name;
	 */
	public String getNumber() {
		return name;
	}
	/**
	 * Returns the game status
	 * @return gameHasStarted && !gameHasEnded
	 */
	public boolean getGameStatus() {
		return gameHasStarted && !gameHasEnded;
	}

	/**
	 * Makes the toString with the game state, people inside and the people in it
	 */
	public String toString() {
		String gamestance = "";
		gamestance = !(gameHasStarted && !gameHasEnded) ? "notStarted" : "Started";
		String result = "";
		result = ";" + name + ";" + gamestance + ";";
		result += String.valueOf(room.size()) + ";";
		for (AbaloneClientHandler ach : room) {
			result += "<" + ach.getPlayer().getName() + ">;";
		}
		return result;
	}
}
