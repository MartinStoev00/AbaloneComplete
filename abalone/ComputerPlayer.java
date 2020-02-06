package ss.project.abalone;

public class ComputerPlayer extends Player {
	private Board board;
	private String thinkingLevel;

	/**
	 * Constructs a ComputerPlayer with a given name and a thinking level and set
	 * the to a local variable
	 * 
	 * @param name
	 * @param thinkingLevel
	 */
	public ComputerPlayer(String name, String thinkingLevel) {
		super(name);
		this.thinkingLevel = thinkingLevel;
	}

	/**
	 * Set the local board to a given input
	 * 
	 * @param board
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Set the local variable thinkingLevel to a given input
	 * 
	 * @param tl
	 */
	public void setThinkingLevel(String tl) {
		if (tl.equals("2")) {
			thinkingLevel = "2";
		} else if (tl.equals("1")) {
			thinkingLevel = "1";
		}
	}

	/**
	 * Returns the first possible move
	 * 
	 * @return the first possible move
	 */
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

	/**
	 * Checks what move to make based on if it will be pushed, will push or else can
	 * move three or two at a time
	 * 
	 * @return the smart possible move
	 */
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
		// check if can move two if anything else fails
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

	/**
	 * Returns a recommended move based on the thinking level
	 * 
	 * @return smart() or naive()
	 */
	public String[] recommendedMove() {
		if (thinkingLevel.equals("2")) {
			return smart();
		} else {
			return naive();
		}
	}
}
