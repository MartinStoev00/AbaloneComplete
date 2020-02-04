package ss.project.abalone;

import java.util.ArrayList;

public class ComputerPlayer extends Player {
	private Board board;
	private String thinkingLevel;

	public ComputerPlayer(String name, String thinkingLevel) {
		super(name);
		this.thinkingLevel = thinkingLevel;
	}

	public void setBoard(Board board) {
		this.board = board;
	}
	
	public void setThinkingLevel(String tl) {
		if(tl.equals("2")) {
			thinkingLevel = "2";
		} else if(tl.equals("1")) {
			thinkingLevel = "1";
		}
	}

	public String[] naive() {
		String result[] = new String[2];
		for (String s : board.getMap().keySet()) {
			for (int i = 1; i <= 6; i++) {
				if (board.isValidMove(s, String.valueOf(i), this.getMark())) {
					result[0] = s;
					result[1] = String.valueOf(i);
					return result;
				}
			}
		}
		return null;
	}

	public String[] smart() {
		String result[] = new String[2];
		// first checks if it is pushable
		for (String s : board.getMap().keySet()) {
			if (board.getMark(s) == this.getMark() && board.isPushable(s)) {
				for (int i = 1; i <= 6; i++) {
					if (board.isValidMove(s, String.valueOf(i), this.getMark())) {
						result[0] = s;
						result[1] = String.valueOf(i);
						return result;
					}
				}
			}
		}

		// checks if it can push three
		for (String s : board.getMap().keySet()) {
			if (board.getMark(s) == this.getMark()) {
				for (int i = 1; i <= 6; i++) {
					String firstMoved = board.movedCoordinates(s, i);
					String secondMoved = board.movedCoordinates(firstMoved, i);
					if (board.getMark(firstMoved) == this.getMark() && board.getMark(secondMoved) == this.getMark()) {
						if (board.pushPossible(s, firstMoved, secondMoved, i, this.getMark())) {
							result[0] = s + firstMoved + secondMoved;
							result[1] = String.valueOf(i);
							return result;
						} else if (board.pushPossible(s, firstMoved, secondMoved, board.mirrorDirection(i), this.getMark())) {
							result[0] = s + firstMoved + secondMoved;
							result[1] = String.valueOf(board.mirrorDirection(i));
							return result;
						}
					}
				}
			}
		}

		// checks if it can push two
		for (String s : board.getMap().keySet()) {
			if (board.getMark(s) == this.getMark()) {
				for (int i = 1; i <= 6; i++) {
					String firstMoved = board.movedCoordinates(s, i);
					if (board.getMark(firstMoved) == this.getMark()) {
						if (board.pushPossible(s, firstMoved, i, this.getMark())) {
							result[0] = s + firstMoved;
							result[1] = String.valueOf(i);
							return result;
						} else if (board.pushPossible(s, firstMoved, board.mirrorDirection(i), this.getMark())) {
							result[0] = s + firstMoved;
							result[1] = String.valueOf(board.mirrorDirection(i));
							return result;
						}
					}
				}
			}
		}

		// checks if it can move 3 if anything others fail
		for (String s : board.getMap().keySet()) {
			if (board.getMark(s) == this.getMark()) {
				for (int i = 1; i <= 6; i++) {
					String allyBehindMeCoordinates = board.movedCoordinates(s, i);
					String allyBehindMyAllyCoordinates = board.movedCoordinates(allyBehindMeCoordinates, i);
					if (board.isLine(s, allyBehindMeCoordinates, allyBehindMyAllyCoordinates)) {
						for (int j = 1; j <= 6; j++) {
							if (board.isValidMove(s + allyBehindMeCoordinates + allyBehindMyAllyCoordinates, String.valueOf(j), this.getMark())) {
								result[0] = s + allyBehindMeCoordinates + allyBehindMyAllyCoordinates;
								result[1] = String.valueOf(j);
								return result;
							}
						}
					}
				}
			}
		}
		//check if can move two if anything else fails
		for (String s : board.getMap().keySet()) {
			if (board.getMark(s) == this.getMark()) {
				for (int i = 1; i <= 6; i++) {
					String allyBehindMeCoordinates = board.movedCoordinates(s, i);
					if (board.isLine(s, allyBehindMeCoordinates)) {
						for (int j = 1; j <= 6; j++) {
							if (board.isValidMove(s + allyBehindMeCoordinates, String.valueOf(j), this.getMark())) {
								result[0] = s + allyBehindMeCoordinates;
								result[1] = String.valueOf(j);
								return result;
							}
						}
					}
				}
			}
		}
		return naive();
	}

	public String[] recommendedMove() {
		if (thinkingLevel.equals("2")) {
			return smart();
		} else {
			return naive();
		}
	}

	public static void main(String args[]) {
		ArrayList<Player> p = new ArrayList<>();
		ComputerPlayer cp = new ComputerPlayer("asdf", "2");
		p.add(cp);
		ComputerPlayer cp1 = new ComputerPlayer("asdsdaff", "2");
		p.add(cp1);
		Board board = new Board(p);
		board.printIndex();
		for (int i = 0; i < 50; i++) {
			board.move(cp.recommendedMove()[0], cp.recommendedMove()[1], cp.getMark());
			System.out.println(board);
			board.move(cp1.recommendedMove()[0], cp1.recommendedMove()[1], cp1.getMark());
			System.out.println(board);
		}
	}
}
