package ss.project.networking.server;

import java.util.Scanner;

import ss.project.abalone.BoardUtils;

public class AbaloneServerTUI {
	private Scanner scanner;
/**
 * Gives a value to the Scanner
 */
	public AbaloneServerTUI() {
		scanner = new Scanner(System.in);
	}
/**
 * Shows the message
 * @param message
 */
	public void showMessage(String message) {
		System.out.println(message);
	}
/**
 * Returns the String inserted
 * @param question
 * @return the inserted String
 */
	public String getString(String question) {
		System.out.print(question);
		return scanner.nextLine();
	}
/**
 * Returns the inserted number
 * @param question
 * @return the inserted number
 */
	public int getInt(String question) {
		String result = getString(question);
		while(!BoardUtils.isNumeric(result)) {
			result = getString(question);
		}
		return Integer.parseInt(result);
	}
}
