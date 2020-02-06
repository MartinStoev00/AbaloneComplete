package ss.project.networking.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

import ss.project.abalone.Board;
import ss.project.abalone.BoardUtils;
import ss.project.abalone.ComputerPlayer;
import ss.project.abalone.Player;
import ss.project.networking.exceptions.ExitProgram;
import ss.project.networking.exceptions.ServerUnavailableException;
import ss.project.networking.protocols.ProtocolMessages;

public class AbaloneClientTUI implements Runnable {
	private static Scanner scanner;
	private AbaloneClient ac;
	private ComputerPlayer cp;
	private Board clientBoard;
	private ArrayList<Player> players;
	private String thinkingLevel;
	private String leader;
	private String currentPlayer;
	private String thisPersonName = "";
	private String response = "empty";
	private boolean isBot = false;
	private boolean hasStarted = false;
	private boolean isConnected = false;
	private boolean reconnected = false;
/**
 * Gives values to the Client and the Scanner
 */
	public AbaloneClientTUI() {
		ac = new AbaloneClient();
		scanner = new Scanner(System.in);
	}
/**
 * Starts the client, asks for a name, select a bot 
 * option and then chooses to reconnect
 * @throws ServerUnavailableException
 */
	public void start() throws ServerUnavailableException {
		InetAddress ip = getIp();
		int port = getInt("Port: ");
		showMessage("Attempting to connect to " + String.valueOf(ip).substring(1) + ":" + port + "\n");
		try {
			ac.createConnection(ip, port);
		} catch (ExitProgram e1) {
			System.out.println("Could not connect to Server");
		} catch (IOException e) {
			showMessage("ERROR: could not create a socket on " + ip.toString() + " and port " + port + "\n");
		}
		isConnected = true;
		while (response.contains(String.valueOf(ProtocolMessages.ERROR) + String.valueOf(ProtocolMessages.DELIMITER)) || response.equals("empty")) {
			response = "empty";
			showMessage("> Enter nickname here: ");
			String name = scanner.nextLine();
			ac.start(name);
			thisPersonName = name;
			response = ac.readLineFromServer();
			while (response.equals("")) {
				response = ac.readLineFromServer();
			}
			handleIncomingCommand(response);
		}
		if(reconnected) {
			ac.start(thisPersonName);
		}
		cp = new ComputerPlayer(thisPersonName, "1");
		System.out.println("You have been connected");
		showMessage("> Type y to select the bot option: ");
		String botChoice = scanner.nextLine();
		if (botChoice.equals("y")) {
			isBot = true;
			System.out.println("Bot option selected");
			System.out.print("Chose a thinking level 1 or 2 \nInput different than this will\nautomatically select 1: ");
			thinkingLevel = scanner.nextLine();
			if (thinkingLevel.equals("2")) {
				cp.setThinkingLevel("2");
			}
		} else {
			System.out.println("Bot option not selected");
		}
		help();
		showMessage("> Enter command here: ");
		new Thread(this).start();
		while (true) {
			try {
				handleUserInput(scanner.nextLine());
			} catch (ExitProgram e) {
				isConnected = false;
				break;
			}
		}
		ac.disconnect();
		System.out.print("\nIf you want to connect again enter 'y': ");
		String answer = scanner.nextLine();
		if (answer.equals("y")) {
			reconnected = true;
			start();
		}
	}
/**
 * Reads the incoming messages and acts based on them
 */
	@Override
	public void run() {
		while (isConnected) {
			String msg;
			try {
				msg = ac.readLineFromServer();
				if (msg != null && !msg.equals("")) {
					handleIncomingCommand(msg);
					System.out.print("> Enter command here: ");
				}
			} catch (ServerUnavailableException e) {
				System.out.println("\nNo access to server");
				break;
			}
		}
	}
/**
 * Handles the incoming command
 * @param msg
 * @throws ServerUnavailableException
 */
	public void handleIncomingCommand(String msg) throws ServerUnavailableException {
		String[] msgs = msg.split(";");
		switch (msgs[0]) {
		case "C":
			if (!msgs[1].equals(thisPersonName)) {
				System.out.println("\n" + msgs[1] + " has been connected");
			}
			break;
		case "R":
			System.out.println(msg.replace(";;", ";\n").replace("<" + thisPersonName + ">", "<you(" + thisPersonName + ")>").replace("R;", "Rooms:\n"));
			break;
		case "P":
			System.out.println(msg.replace(";;", ";\n").replace("<" + thisPersonName + ">", "<you(" + thisPersonName + ")>").replace("P;", "Leadership Board:\n"));
			break;
		case "J":
			if (!msgs[2].equals(thisPersonName)) {
				System.out.println("\n" + msgs[2] + " has joined Room#" + msgs[1]);
			} else {
				System.out.println("You have joined Room#" + msgs[1]);
			}
			break;
		case "L":
			if (!msgs[1].equals(thisPersonName)) {
				System.out.println("\n" + msgs[1] + " has left");
			} else {
				System.out.println("You have left");
			}
			break;
		case "B":
			if (!msgs[2].equals(thisPersonName)) {
				System.out.println("\n" + msgs[2] + ": \"" + msgs[1] + "\"");
			} else {
				System.out.println("You: \"" + msgs[1] + "\"");
			}
			break;
		case "A":
			if (!msgs[1].equals(thisPersonName)) {
				System.out.println("\n" + msgs[1] + " has been chosen as an ally");
			} else {
				System.out.println("\nYou has been chosen as an ally");
			}
			break;
		case "S":
			leader = msgs[2];
			int thisPersonIndex = 0;
			hasStarted = true;
			players = new ArrayList<>();
			for (int i = 0; i < Integer.parseInt(msgs[1]); i++) {
				if (msgs[i + 2].equals(thisPersonName)) {
					thisPersonIndex = i;
				}
				if (isBot && msgs[i + 2].equals(thisPersonName)) {
					players.add(cp);
				} else {
					players.add(new Player(msgs[i + 2]));
				}
			}
			clientBoard = new Board(players);
			cp.setBoard(clientBoard);
			cp.setMark(clientBoard.differentMark().get(thisPersonIndex));
			System.out.println("\n" + msgs[1] + "-player game has started");
			System.out.println("\n" + clientBoard);
			break;
		case "T":
			currentPlayer = msgs[1];
			if (!msgs[1].equals(thisPersonName)) {
				System.out.println("\nNow is " + currentPlayer + "s turn");
			} else {
				System.out.println("\nNow is your turn");
			}
			if (isBot && currentPlayer.equals(thisPersonName)) {
				for (int i = 0; i < players.size(); i++) {
					if (players.get(i).getName().equals(thisPersonName)) {
						String firstStr = cp.recommendedMove()[0];
						String secondStr = cp.recommendedMove()[1];
						ac.move(firstStr, secondStr);
						clientBoard.move(firstStr, secondStr, players.get(i).getMark());
						System.out.println("\n" + clientBoard);
					}
				}
			}
			break;
		case "W":
			if (msgs[1].equals(thisPersonName)) {
				System.out.println("You have challenged " + msgs[2]);
			} else {
				System.out.println("\nYou have been challenged by " + msgs[1]);
			}
			break;
		case "N":
			if (msgs[1].equals(thisPersonName)) {
				System.out.println("\nYou have denied " + msgs[2]);
			} else {
				System.out.println("\nYou have been denied by " + msgs[1]);
			}
			break;
		case "Y":
			if (msgs[1].equals(thisPersonName)) {
				System.out.println("You have accepted " + msgs[2] + " challenge");
			} else {
				System.out.println("\n" + msgs[1] + "has accepted your challenge");
			}
			break;
		case "M":
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getName().equals(currentPlayer)) {
					clientBoard.move(msgs[1], msgs[2], players.get(i).getMark());
					System.out.println("\n" + clientBoard);
				}
			}
			break;
		case "F":
			if (msgs.length == 2) {
				if (!msgs[1].equals(thisPersonName)) {
					System.out.println("\n" + msgs[1] + " is the winner!");
				} else {
					System.out.println("\nYou are the winner!");
				}
			} else if (msgs.length == 3) {
				String firstPerson = !msgs[1].equals(thisPersonName) ? msgs[1] : "you";
				String secondPerson = !msgs[2].equals(thisPersonName) ? msgs[2] : "you";
				System.out.println("\n" + firstPerson + " and " + secondPerson + " are both the winners!");
			} else {
				System.out.println("\nTie");
			}
			if (leader.equals(thisPersonName)) {
				System.out.print("If you want to play again enter S \n");
			} else {
				System.out.print("If you want to play again stay \n");
			}
			break;
		case "D":
			System.out.println("\n" + msgs[1] + " has disconnected");
			int disconnectedNum = 0;
			if (hasStarted) {
				for (Player p : players) {
					if (p.getName().equals(msgs[1])) {
						disconnectedNum = players.indexOf(p);
					}
				}
				if (disconnectedNum != 0) {
					clientBoard.removeMarbles(clientBoard.differentMark().get(disconnectedNum));
				}
				if (clientBoard.differentMark().size() == 3) {
					System.out.println("\n" + clientBoard);
				}
			}
			break;
		case "E":
			System.out.println("ERROR:" + msgs[1]);
			break;
		}
	}
/**
 * Sends the user commands to server
 * @param input
 * @throws ExitProgram
 * @throws ServerUnavailableException
 */
	public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException {
		try {
			String[] msgs = input.split(" ");
			switch (msgs[0].toUpperCase()) {
			case "R":
				ac.room();
				break;
			case "P":
				ac.leaderBoard();;
				break;
			case "J":
				ac.join(msgs[1]);
				break;
			case "L":
				ac.leave();
				break;
			case "A":
				ac.ally(msgs[1]);
				break;
			case "S":
				ac.startGame();
				break;
			case "W":
				ac.challenge(msgs[1]);
				break;
			case "Y":
				ac.acceptChallenge(msgs[1]);
				break;
			case "N":
				ac.denyChallenge(msgs[1]);
				break;
			case "H":
				help();
				System.out.print("> Enter command here: ");
				break;
			case "M":
				ac.move(msgs[1], msgs[2]);
				break;
			case "Q":
				if (hasStarted) {
					System.out.print("> Secret hint : " + cp.recommendedMove()[0] + " " + cp.recommendedMove()[1] + "\n");
					System.out.print("> Enter command here: ");
				} else {
					System.out.println("> Game has not started");
					System.out.print("> Enter command here: ");
				}
				break;
			case "B":
				ac.text(input.substring(2));
				break;
			case "D":
				throw new ExitProgram("Program Exited");
			default:
				showMessage("ERROR:CommandNotRecognized\n");
				System.out.print("> Enter command here: ");
			}
		} catch (IndexOutOfBoundsException e) {
			showMessage("ERROR:CommandNotRecognized\n");
			System.out.print("> Enter command here: ");
		}
	}
/**
 * Show a message
 * @param message
 */
	public static void showMessage(String message) {
		System.out.print(message);
	}
/**
 * Wants an address
 * @return ip address
 */
	public InetAddress getIp() {
		System.out.print("Please enter IP address here: ");
		String ips = scanner.nextLine();
		InetAddress ip;
		try {
			ip = InetAddress.getByName(ips);
		} catch (Exception e) {
			System.out.println("Invalid IP. Please try again.");
			return getIp();
		}
		return ip;
	}
/**
 * Prints help menu
 */
	public void help() {
		System.out.println("HELP MANUAL\n" + "d..................................... exit\n" + "m <coordinates> <direction>............move\n" + "a <name>......................picks an ally\n"
				+ "l............................leave the room\n" + "n <name>.....................deny challenge\n" + "s...........................starts the game\n" + "h..........................help (this menu)\n"
				+ "w <name>..................challenge someone\n" + "q........................you receive a hint\n" + "p.......................display leaderboard\n" + "y <name>...............accept the challenge\n"
				+ "j <room>............joins the room inserted\n" + "b <text>...sends text to people in the room\n" + "r...display the rooms and the peopel inside");
	}
/**
 * Asks for int 
 * @param question
 * @return the input
 */
	public static int getInt(String question) {
		System.out.print(question);
		String result = scanner.nextLine();
		while (!BoardUtils.isNumeric(result)) {
			System.out.print(question);
			result = scanner.nextLine();
		}
		return Integer.parseInt(result);
	}

	public static void main(String[] args) {
		try {
			(new AbaloneClientTUI()).start();
		} catch (ServerUnavailableException e) {
			System.out.println("\nConnection lost");
		}
	}
}
