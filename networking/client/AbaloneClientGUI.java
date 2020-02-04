package ss.project.networking.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ss.project.abalone.Board;
import ss.project.abalone.ComputerPlayer;
import ss.project.abalone.Player;
import ss.project.networking.exceptions.ExitProgram;
import ss.project.networking.exceptions.ServerUnavailableException;
import ss.project.networking.protocols.ProtocolMessages;

public class AbaloneClientGUI extends Application implements Runnable {

	Stage window;
	private AbaloneClient ac = new AbaloneClient();
	private ComputerPlayer cp;
	private Board clientBoard;
	private ArrayList<Player> players;
	private String thinkingLevel;
	private String leader;
	private String currentPlayer;
	private String thisPersonName = "";
	private String response = "empty";
	private String roomStyle = "-fx-background-color: rgba(255, 255, 255, 0.5);"+ 
			"-fx-padding: 5px;" + 
			"-fx-background-color: white;\r\n" + 
			"-fx-background-radius: 50px;\r\n" + 
			"-fx-border-radius: 50px;\r\n" + 
			"-fx-border-width: 2px;\r\n" + 
			"-fx-border-width: 2px;\r\n" + 
			"-fx-border-color: black;\r\n" + 
			"-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 5, 0, 0, 0);\r\n" + 
			"-fx-min-width: 200px;\r\n" + 
			"-fx-min-height: 200px;";
	private String leaderBoardStyle = "-fx-background-color: rgba(255, 255, 255, 0.5);"+ 
			"-fx-padding: 5px;" +
			"-fx-background-color: white;\r\n" + 
			"-fx-background-radius: 50px;\r\n" + 
			"-fx-border-radius: 50px;\r\n" + 
			"-fx-border-width: 2px;\r\n" + 
			"-fx-border-color: black;\r\n" + 
			"-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 5, 0, 0, 0);\r\n" + 
			"-fx-min-width: 300px;\r\n" + 
			"-fx-min-height: 400px;";
	private String buttonStyle = "-fx-padding: 5px;" +
			"-fx-background-color: white;\r\n" + 
			"-fx-background-radius: 50px;\r\n" + 
			"-fx-border-radius: 50px;\r\n" + 
			"-fx-min-width: 120px;\r\n" + 
			"-fx-border-width: 1px;\r\n" + 
			"-fx-border-color: black;\r\n" + 
			"-fx-font-size: 18px;";
	private String hoveredButtonStyle = "-fx-padding: 5px;" +
			"-fx-background-color: #cccccc;\r\n" + 
			"-fx-background-radius: 50px;\r\n" + 
			"-fx-border-radius: 50px;\r\n" + 
			"-fx-min-width: 120px;\r\n" + 
			"-fx-border-width: 1px;\r\n" + 
			"-fx-border-color: black;\r\n" + 
			"-fx-font-size: 18px;";
	private String inputStyle = "-fx-padding: 9px;" +
			"-fx-background-color: white;\r\n" + 
			"-fx-background-radius: 50px;\r\n" + 
			"-fx-border-radius: 50px;\r\n" + 
			"-fx-max-width: 250px;\r\n" + 
			"-fx-border-width: 1px;\r\n" + 
			"-fx-border-color: black;\r\n" + 
			"-fx-font-size: 18px;";
	private String hoveredInputStyle = "-fx-padding: 9px;" +
			"-fx-background-color: #cccccc;\r\n" + 
			"-fx-background-radius: 50px;\r\n" + 
			"-fx-border-radius: 50px;\r\n" + 
			"-fx-max-width: 250px;\r\n" + 
			"-fx-border-width: 1px;\r\n" + 
			"-fx-border-color: black;\r\n" + 
			"-fx-font-size: 18px;";
	private String labelStyle = "-fx-font-size: 18px;";
	private boolean isBot = false;
	private boolean hasStarted = false;
	private boolean isConnected = false;
	private boolean reconnected = false;

	public void start(Stage s) {
		serverConnect(s);
	}

	public void lobbyDisplay(Stage s) {
		window = s;
		window.setTitle("Lobby");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20, 20, 20, 20));
		grid.setVgap(20);
		grid.setHgap(20);
		

		VBox room1 = new VBox();
		room1.setStyle(roomStyle);
		Label l = new Label("Room 1");
		l.setStyle(labelStyle);
		room1.setAlignment(Pos.TOP_CENTER);
		room1.getChildren().add(l);
		GridPane.setConstraints(room1, 0, 0);
		
		VBox room2 = new VBox();
		room2.setStyle(roomStyle);
		Label l1 = new Label("Room 2");
		l1.setStyle(labelStyle);
		room2.setAlignment(Pos.TOP_CENTER);
		room2.getChildren().add(l1);
		GridPane.setConstraints(room2, 1, 0);

		VBox room3 = new VBox();
		room3.setStyle(roomStyle);
		Label l2 = new Label("Room 3");
		l2.setStyle(labelStyle);
		room3.setAlignment(Pos.TOP_CENTER);
		room3.getChildren().add(l2);
		GridPane.setConstraints(room3, 2, 0);

		VBox room4 = new VBox();
		room4.setStyle(roomStyle);
		Label l3 = new Label("Room 4");
		l3.setStyle(labelStyle);
		room4.setAlignment(Pos.TOP_CENTER);
		room4.getChildren().add(l3);
		GridPane.setConstraints(room4, 0, 1);
		
		VBox room5 = new VBox();
		room5.setStyle(roomStyle);
		Label l4 = new Label("Room 5");
		l4.setStyle(labelStyle);
		room5.setAlignment(Pos.TOP_CENTER);
		room5.getChildren().add(l4);
		GridPane.setConstraints(room5, 1, 1);
		
		VBox room6 = new VBox();
		room6.setStyle(roomStyle);
		Label l5 = new Label("Room 6");
		l5.setStyle(labelStyle);
		room6.setAlignment(Pos.TOP_CENTER);
		room6.getChildren().add(l5);
		GridPane.setConstraints(room6, 2, 1);
		
		VBox room7 = new VBox();
		room7.setStyle(roomStyle);
		Label l6 = new Label("Room 7");
		l6.setStyle(labelStyle);
		room7.setAlignment(Pos.TOP_CENTER);
		room7.getChildren().add(l6);
		GridPane.setConstraints(room7, 0, 2);
		
		VBox room8 = new VBox();
		room8.setStyle(roomStyle);
		Label l7 = new Label("Room 8");
		l7.setStyle(labelStyle);
		room8.setAlignment(Pos.TOP_CENTER);
		room8.getChildren().add(l7);
		GridPane.setConstraints(room8, 1, 2);
		
		VBox room9 = new VBox();
		room9.setStyle(roomStyle);
		Label l8 = new Label("Room 9");
		l8.setStyle(labelStyle);
		room9.setAlignment(Pos.TOP_CENTER);
		room9.getChildren().add(l8);
		GridPane.setConstraints(room9, 2, 2);
		
		VBox leaderBoard = new VBox();
		leaderBoard.setStyle(leaderBoardStyle);
		ScrollPane s1 = new ScrollPane();
        s1.setPannable(true);
        s1.setPrefSize(370, 400);
        s1.setContent(leaderBoard);
		Label l9 = new Label("Leader Board");
		l9.setStyle(labelStyle);
		leaderBoard.setAlignment(Pos.TOP_CENTER);
		leaderBoard.getChildren().add(l9);
		GridPane.setConstraints(s1, 3, 0,1,2);
		
		GridPane utils = new GridPane();
		utils.setPadding(new Insets(10, 10, 10, 10));
		utils.setVgap(10);
		utils.setHgap(10);
		
		Label challengeLabel = new Label("Challenge someone:");
		challengeLabel.setStyle(labelStyle);
		GridPane.setHalignment(challengeLabel, HPos.CENTER);
		GridPane.setConstraints(challengeLabel,0,0,2,1);
		
		TextField personChallenged = new TextField("");
		personChallenged.setStyle(inputStyle);
		personChallenged.setOnMouseEntered(e -> personChallenged.setStyle(hoveredInputStyle));
		personChallenged.setOnMouseExited(e -> personChallenged.setStyle(inputStyle));
		personChallenged.setPromptText("Joe");
		GridPane.setConstraints(personChallenged,0,1,2,1);
		
		Button leave = new Button("Leave");
		leave.setStyle(buttonStyle);
		leave.setOnMouseEntered(e -> leave.setStyle(hoveredButtonStyle));
		leave.setOnMouseExited(e -> leave.setStyle(buttonStyle));
		GridPane.setConstraints(leave,0,3);
		
		Button help = new Button("Help");
		help.setStyle(buttonStyle);
		help.setOnMouseEntered(e -> help.setStyle(hoveredButtonStyle));
		help.setOnMouseExited(e -> help.setStyle(buttonStyle));
		GridPane.setConstraints(help,1,3);
		
		utils.getChildren().addAll(challengeLabel,personChallenged,leave,help);
		GridPane.setConstraints(utils, 3, 2);
		
		grid.getChildren().addAll(room1, room2, room3, room4, room5, room6, room7, room8, room9,s1,utils);
		Scene scene = new Scene(grid, 1020, 680);
		window.setScene(scene);
		window.setResizable(false);
		window.show();
	}

	public void choseBot(Stage s) {
		window = s;
		window.setTitle("Chose a bot option:");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(9);
		grid.setHgap(10);

		Label l = new Label("Chose a bot:");
		GridPane.setConstraints(l, 0, 0);

		Button b1 = new Button("Bot lvl 1");
		GridPane.setConstraints(b1, 0, 1);
		b1.setMinWidth(75);
		b1.setOnAction(e -> {
			cp.setThinkingLevel("1");
			window.close();
		});

		Button b2 = new Button("Bot lvl 2");
		GridPane.setConstraints(b2, 1, 1);
		b2.setMinWidth(75);
		b2.setOnAction(e -> {
			cp.setThinkingLevel("2");
			window.close();
			lobbyDisplay(s);
		});

		Button b3 = new Button("Not bot");
		GridPane.setConstraints(b3, 2, 1);
		b3.setMinWidth(75);
		b3.setOnAction(e -> {
			window.close();
			lobbyDisplay(s);
		});

		grid.getChildren().addAll(l, b1, b2, b3);
		Scene scene = new Scene(grid, 270, 80);
		window.setScene(scene);
		window.show();
	}

	public void serverConnect(Stage s) {
		window = s;
		window.setTitle("Connecting to Server");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label l = new Label("IP Address:");
		GridPane.setConstraints(l, 0, 0);

		TextField input = new TextField("192.168.1.230");
		input.setPromptText("192.168.1.230");
		GridPane.setConstraints(input, 1, 0);

		Label l1 = new Label("Port:");
		GridPane.setConstraints(l1, 0, 1);

		TextField input1 = new TextField("9999");
		input1.setPromptText("9999");
		GridPane.setConstraints(input1, 1, 1);

		Button b = new Button("Connect");
		GridPane.setConstraints(b, 1, 2);
		b.setOnAction(e -> {
			int port;
			InetAddress ip;
			try {
				ip = InetAddress.getByName(input.getText());
				port = Integer.parseInt(input1.getText());
				ac.createConnection(ip, port);
				isConnected = true;
				window.close();
				nameConnect(s);
			} catch (ExitProgram e1) {
				display("No access to server");
				input.setText("");
				input1.setText("");
			} catch (NumberFormatException | UnknownHostException e3) {
				display("Wrong Parameters");
				input.setText("");
				input1.setText("");
			} catch (IOException e2) {
				display("Server Unavailable");
				input.setText("");
				input1.setText("");
			}
		});

		grid.getChildren().addAll(l, input, l1, input1, b);
		Scene scene = new Scene(grid, 240, 120);
		window.setScene(scene);
		window.show();
	}

	public void nameConnect(Stage s) {
		window = s;
		window.setTitle("Connecting to Server");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		Label l = new Label("Username:");
		GridPane.setConstraints(l, 0, 0);

		TextField input = new TextField("Steve");
		input.setPromptText("Username");
		GridPane.setConstraints(input, 1, 0);
		Button b = new Button("Connect");
		GridPane.setConstraints(b, 1, 2);
		b.setOnAction(e -> {
			try {
				response = "empty";
				ac.start(input.getText());
				thisPersonName = input.getText();
				response = ac.readLineFromServer();
				while (response.equals("")) {
					response = ac.readLineFromServer();
				}
				handleIncomingCommand(response);
			} catch (ServerUnavailableException e1) {
				display("Server Unavailable");
			}
			input.setText("");
			if (!response.contains(String.valueOf(ProtocolMessages.ERROR) + String.valueOf(ProtocolMessages.DELIMITER)) && !response.equals("empty")) {
				cp = new ComputerPlayer(thisPersonName, "1");
				window.close();
				choseBot(s);
			}
		});
		grid.getChildren().addAll(l, input, b);
		Scene scene = new Scene(grid, 250, 90);
		window.setScene(scene);
		window.show();
	}

	public void display(String message) {
		Stage window = new Stage();

		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(message);
		window.setMinWidth(250);
		window.setMinHeight(130);

		Label label = new Label();
		label.setText(message);
		Button closeButton = new Button("Close this window");
		closeButton.setOnAction(e -> window.close());

		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton);
		layout.setAlignment(Pos.CENTER);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}

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
			System.out.println(98);
			display("ERROR:" + msgs[1]);
			break;
		}
	}

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

	public static void main(String args[]) {
		launch(args);
	}
}