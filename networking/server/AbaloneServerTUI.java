package ss.project.networking.server;

import java.util.Scanner;

import ss.project.abalone.BoardUtils;

public class AbaloneServerTUI {
	private Scanner scanner;

	public AbaloneServerTUI() {
		scanner = new Scanner(System.in);
	}

	public void showMessage(String message) {
		System.out.println(message);
	}

	public String getString(String question) {
		System.out.print(question);
		return scanner.nextLine();
	}

	public int getInt(String question) {
		String result = getString(question);
		while(!BoardUtils.isNumeric(result)) {
			result = getString(question);
		}
		return Integer.parseInt(result);
	}
}
