package ss.project.abalone;

import java.util.ArrayList;

public class Board extends BoardUtils {

//Constructor
	public Board(ArrayList<Player> players) {
		this(players.size());
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) instanceof ComputerPlayer) {
				((ComputerPlayer) players.get(i)).setBoard(this);
			}
			players.get(i).setMark(differentMark().get(i));
		}
	}

	public Board(int numOfColors) {
		super.numOfColors = numOfColors;
		fillIn();
		reset();
	}

//moves one without pushing
	public boolean moveWithoutPush(String firstpos, int direction, Mark mark) {
		if (isEEE(movedCoordinates(firstpos, direction)) && getMark(firstpos) == mark) {
			super.map.put(movedCoordinates(firstpos, direction), mark);
			super.map.put(firstpos, Mark.EE);
			return true;
		}
		return false;
	}

//moves two without pushing
	public boolean moveWithoutPush(String firstpos, String secondpos, int direction, Mark mark) {
		boolean isEEEInFrontOf1 = isEEE(movedCoordinates(firstpos, direction));
		boolean isEEEInFrontOf2 = isEEE(movedCoordinates(secondpos, direction));
		if (isLine(firstpos, secondpos) && (getMark(firstpos) == mark || getMark(secondpos) == mark)) {
			if (isEEEInFrontOf1 && isEEEInFrontOf2) {
				if (moveWithoutPush(secondpos, direction, getMark(secondpos)) && moveWithoutPush(firstpos, direction, getMark(firstpos))) {
					return true;
				}
			} else if (isEEEInFrontOf1 || isEEEInFrontOf2) {
				if (firstpos.equals(movedCoordinates(secondpos, direction))) {
					if (moveWithoutPush(firstpos, direction, mark)) {
						if (moveWithoutPush(secondpos, direction, getMark(secondpos))) {
							return true;
						}
					}
				} else if (secondpos.equals(movedCoordinates(firstpos, direction))) {
					if (moveWithoutPush(secondpos, direction, mark)) {
						if (moveWithoutPush(firstpos, direction, getMark(firstpos))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

//moves two with pushing 
	public boolean moveWithPush(String firstpos, String secondpos, int direction, Mark mark) {
		if (isLine(firstpos, secondpos) && (getMark(firstpos) == mark || (numOfColors == 4 && mark.isAlly(getMark(firstpos))))) {
			if (direction == findDirection(firstpos, secondpos)) {
				String nextStep = movedCoordinates(secondpos, direction);
				String evenFurther = movedCoordinates(nextStep, direction);
				if (moveWithoutPush(firstpos, secondpos, direction, mark)) {
					return true;
				} else if (isValidKey(nextStep)) {
					if (((mark != getMark(nextStep) && numOfColors != 4 && isEEE(evenFurther)) || (numOfColors == 4 && !mark.isAlly(getMark(nextStep)))) && isEEE(evenFurther)) {
						super.map.put(evenFurther, getMark(nextStep));
						super.map.put(nextStep, getMark(secondpos));
						super.map.put(secondpos, getMark(firstpos));
						super.map.put(firstpos, Mark.EE);
						return true;
					} else if ((mark != getMark(nextStep) && numOfColors != 4 && evenFurther == null) || (numOfColors == 4 && !mark.isAlly(getMark(nextStep))) && evenFurther == null) {
						removedItems.add(getMark(nextStep));
						super.map.put(nextStep, getMark(secondpos));
						super.map.put(secondpos, getMark(firstpos));
						super.map.put(firstpos, Mark.EE);
						return true;
					}
				}
			} else if (direction == findDirection(secondpos, firstpos)) {
				String nextStep = movedCoordinates(firstpos, direction);
				String evenFurther = movedCoordinates(nextStep, direction);
				if (moveWithoutPush(secondpos, firstpos, direction, mark)) {
					return true;
				} else if (isValidKey(nextStep)) {
					if (((mark != getMark(nextStep) && numOfColors != 4 && isEEE(evenFurther)) || (numOfColors == 4 && !mark.isAlly(getMark(nextStep)))) && isEEE(evenFurther)) {
						super.map.put(evenFurther, getMark(nextStep));
						super.map.put(nextStep, getMark(firstpos));
						super.map.put(firstpos, getMark(secondpos));
						super.map.put(secondpos, Mark.EE);
						return true;
					} else if ((mark != getMark(nextStep) && numOfColors != 4 && evenFurther == null) || (numOfColors == 4 && !mark.isAlly(getMark(nextStep))) && evenFurther == null) {
						removedItems.add(getMark(nextStep));
						super.map.put(nextStep, getMark(firstpos));
						super.map.put(firstpos, getMark(secondpos));
						super.map.put(secondpos, Mark.EE);
						return true;
					}
				}
			} else {
				if (moveWithoutPush(firstpos, secondpos, direction, mark)) {
					return true;
				}
			}
		}
		return false;
	}

//moves three without pushing
	public boolean moveWithoutPush(String firstpos, String secondpos, String thirdpos, int direction, Mark mark) {
		if (isLine(firstpos, secondpos, thirdpos) && (getMark(firstpos) == mark || getMark(secondpos) == mark || getMark(thirdpos) == mark)) {
			String middle = middleOutOfThree(firstpos, secondpos, thirdpos);
			String head = movedCoordinates(middle, direction);
			String tail = movedCoordinates(middle, mirrorDirection(direction));
			boolean isEEEInFront1 = isEEE(movedCoordinates(firstpos, direction));
			boolean isEEEInFront2 = isEEE(movedCoordinates(secondpos, direction));
			boolean isEEEInFront3 = isEEE(movedCoordinates(thirdpos, direction));
			boolean isInFrontOf2 = secondpos.equals(movedCoordinates(middle, direction));
			boolean isInBehindf2 = secondpos.equals(movedCoordinates(middle, mirrorDirection(direction)));
			boolean isInFrontOf3 = thirdpos.equals(movedCoordinates(middle, direction));
			boolean isInBehindf3 = thirdpos.equals(movedCoordinates(middle, mirrorDirection(direction)));
			if (isEEEInFront1 && isEEEInFront2 && isEEEInFront3) {
				if ((moveWithoutPush(firstpos, secondpos, direction, mark) || moveWithoutPush(firstpos, secondpos, direction, Mark.ally(mark)))
						&& (moveWithoutPush(thirdpos, direction, mark) || moveWithoutPush(thirdpos, direction, Mark.ally(mark)))) {
					return true;
				}
			} else if (isInFrontOf3 || isInBehindf3 || isInFrontOf2 || isInBehindf2) {
				if (numOfColors == 4) {
					if (getMark(tail) == mark && getMark(middle) == mark && getMark(head) == mark) {
						if (moveWithoutPush(head, middle, direction, mark) && moveWithoutPush(tail, direction, mark)) {
							return true;
						}
					} else if (getMark(middle) == mark && getMark(head) == mark) {
						if (moveWithoutPush(head, middle, direction, mark) && moveWithoutPush(tail, direction, Mark.ally(mark))) {
							return true;
						}
					} else if (getMark(middle) == Mark.ally(mark) && getMark(tail) == Mark.ally(mark)) {
						if (moveWithoutPush(head, direction, mark) && moveWithoutPush(tail, middle, direction, Mark.ally(mark))) {
							return true;
						}
					}
				} else {
					if (moveWithoutPush(head, middle, direction, mark) && moveWithoutPush(tail, direction, mark)) {
						return true;
					}
				}
			}
		}
		return false;
	}

//moves three with pushing
	public boolean moveWithPush(String firstpos, String secondpos, String thirdpos, int direction, Mark mark) {
		if (isLine(firstpos, secondpos, thirdpos) && (getMark(firstpos) == mark || getMark(secondpos) == mark || getMark(thirdpos) == mark)) {
			String middle = middleOutOfThree(firstpos, secondpos, thirdpos);
			String head = movedCoordinates(middle, direction);
			String tail = movedCoordinates(middle, mirrorDirection(direction));
			String firstInFront = movedCoordinates(head, direction);
			String secondInFront = movedCoordinates(firstInFront, direction);
			boolean isInTheWay = direction == findDirection(firstpos, secondpos) || direction == mirrorDirection(findDirection(firstpos, secondpos));
			if (isInTheWay) {
				switch (numOfStuffInFront(head, direction)) {
				case 0:
					if (moveWithoutPush(firstpos, secondpos, thirdpos, direction, mark)) {
						return true;
					}
					break;
				case 1:
					if (numOfColors == 4) {
						if (getMark(tail) == mark && getMark(middle) == mark && getMark(head) == mark) {
							if (moveWithPush(head, middle, direction, mark)) {
								if (moveWithoutPush(tail, direction, mark)) {
									return true;
								}
							}
						} else if ((getMark(middle) == mark && getMark(head) == mark) || (getMark(middle) == Mark.ally(mark) && getMark(tail) == Mark.ally(mark))) {
							if (moveWithPush(head, middle, direction, mark)) {
								if (moveWithoutPush(tail, direction, getMark(tail))) {
									return true;
								}
							}
						}
					} else {
						if (moveWithPush(head, middle, direction, mark)) {
							if (moveWithoutPush(tail, direction, getMark(tail))) {
								return true;
							}
						}
					}
					break;
				case 2:
					if (!isValidKey(movedCoordinates(secondInFront, direction))) {
						if (numOfColors != 4 || (numOfColors == 4 && !getMark(secondInFront).isAlly(mark)) && !getMark(firstInFront).isAlly(mark)) {
							removedItems.add(getMark(secondInFront));
							super.map.put(secondInFront, Mark.EE);
							moveWithPush(firstpos, secondpos, thirdpos, direction, mark);
							return true;
						}
					} else if (moveWithoutPush(firstInFront, secondInFront, direction, getMark(secondInFront))) {
						if (moveWithoutPush(firstpos, secondpos, thirdpos, direction, mark)) {
							return true;
						}
					}
					break;
				default:
					return false;
				}
			} else {
				if (moveWithoutPush(firstpos, secondpos, thirdpos, direction, mark)) {
					return true;
				}
			}
		}
		return false;
	}

//moving
	public boolean move(String command, String direction, Mark mark) {
		String firstpos = "", secondpos = "", thirdpos = "";
		if (command.length() > 1) {
			firstpos = "" + command.charAt(0) + command.charAt(1);
		}
		if (command.length() > 3) {
			secondpos = "" + command.charAt(2) + command.charAt(3);
		}
		if (command.length() > 5) {
			thirdpos = "" + command.charAt(4) + command.charAt(5);
		}
		if (isValidKey(firstpos) && isNumeric(direction)) {
			int dir = Integer.parseInt(direction);
			if (dir > 0 && dir < 7) {
				if (secondpos == "" && thirdpos == "") {
					if (moveWithoutPush(firstpos, dir, mark)) {
						return true;
					}
				} else if (thirdpos == "" && isValidKey(secondpos)) {
					if (moveWithPush(firstpos, secondpos, dir, mark)) {
						return true;
					}
				} else if (isValidKey(thirdpos) && isValidKey(secondpos)) {
					if (moveWithPush(firstpos, secondpos, thirdpos, dir, mark)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean pushPossible(String firstpos, String secondpos, int direction, Mark mark) {
		if (this.deepCopy().moveWithPush(firstpos, secondpos, direction, mark) && !this.deepCopy().moveWithoutPush(firstpos, secondpos, direction, mark)) {
			return true;
		}
		return false;
	}

	public boolean pushPossible(String firstpos, String secondpos, String thirdpos, int direction, Mark mark) {
		if (this.deepCopy().moveWithPush(firstpos, secondpos, thirdpos, direction, mark) && !this.deepCopy().moveWithoutPush(firstpos, secondpos, thirdpos, direction, mark)) {
			return true;
		}
		return false;
	}

	public boolean isValidMove(String command, String direction, Mark mark) {
		return deepCopy().move(command, direction, mark);
	}

//game test
//main
	public static void main(String[] args) {
		Board board = new Board(2);
		board.printIndex();
		board.move("5H6H", "5", Mark.BB);
		System.out.println(board);
	}
}