package ss.project.abalone;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoardUtils {
	protected int numOfColors;
	protected Mark[] values;
	protected ArrayList<Mark> removedItems = new ArrayList<>();
	protected static final char[] YAXIS = "IHGFEDCBA".toCharArray();
	protected static final char[] XAXIS = "123456789".toCharArray();
	protected Map<String, Mark> map = new LinkedHashMap<String, Mark>();

	/**
	 * Depending on the number of colors on the board, fills the values[] with
	 * different Mark values.
	 * 
	 * @requires numOfColors>= 2 && numOfColors<=4
	 */
	public void fillIn() {
		values = new Mark[61];
		for (int i = 0; i < 61; i++) {
			values[i] = Mark.EE;
		}
		switch (numOfColors) {
		case 2:
			for (int i = 0; i < 61; i++) {
				if (i < 16 && i != 11 && i != 12) {
					values[i] = Mark.BB;
				} else if (i > 44 && i != 48 && i != 49) {
					values[i] = Mark.WW;
				}
			}
			break;
		case 3:
			int currentLine = 1;
			int lineLimit = 4;
			for (int i = 0; i < 61; i++) {
				if (i == lineLimit && i < 36) {
					values[i - 1] = Mark.BB;
					values[i] = Mark.BB;
					values[i + 1] = Mark.WW;
					values[i + 2] = Mark.WW;
					lineLimit = lineLimit + 9 - Math.abs(4 - currentLine);
					currentLine++;
				} else if (i > 49) {
					values[i] = Mark.YY;
				}
			}
			values[0] = Mark.WW;
			values[1] = Mark.WW;
			values[36] = Mark.EE;
			values[42] = Mark.BB;
			break;
		case 4:
			currentLine = 1;
			lineLimit = 4;
			for (int i = 0; i < 61; i++) {
				if (i == lineLimit) {
					if (currentLine < 6) {
						for (int j = 0; j < currentLine - 1; j++) {
							values[lineLimit - j] = Mark.BB;
						}
					}
					if (currentLine < 4) {
						for (int k = lineLimit - 4; k < lineLimit - currentLine + 1; k++) {
							values[k] = Mark.RR;
						}
					}
					if (currentLine > 4 && currentLine < 9) {
						for (int l = lineLimit - 9 + Math.abs(4 - currentLine); l < lineLimit + 1 - currentLine + currentLine % 5; l++) {
							values[l] = Mark.WW;
						}
					}
					if (currentLine > 6) {
						for (int s = lineLimit + 2 - currentLine + currentLine % 5; s < lineLimit - 4 + Math.abs(4 - currentLine); s++) {
							values[s] = Mark.YY;
						}
					}
					lineLimit = lineLimit + 9 - Math.abs(4 - currentLine);
					currentLine++;
				}
			}
			values[29] = Mark.EE;
			values[31] = Mark.EE;
			break;
		}
	}

	/**
	 * Fills map with values[]
	 */
	public void reset() {
		int index = 0;
		for (int i = 0; i < 9; i++) {
			int letterNum = (int) ((4 - i) * (Math.signum(4 - i) + 1) / 2);
			for (int j = 0; j < (9 - Math.abs(i - 4)); j++) {
				map.put("" + XAXIS[j + letterNum] + YAXIS[i], values[index++]);
			}
		}
		removedItems = new ArrayList<>();
	}

	/**
	 * Makes a copy of the Board and returns it
	 * 
	 * @return copy
	 */
	public Board deepCopy() {
		Board copy = new Board(numOfColors);
		for (String k : this.map.keySet()) {
			copy.map.put(k, this.map.get(k));
		}
		return copy;
	}

	/**
	 * Checks if a given String belongs to map.keySet()
	 * 
	 * @param String pos
	 * @return true if a coordinate is in map.KeySet()
	 */
	public boolean isValidKey(String pos) {
		for (String k : map.keySet()) {
			if (k.equals(pos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Takes the first letter of a coordinate and returns the line
	 * 
	 * @param char i
	 * @return the line
	 */
	public int line(char i) {
		for (int j = 0; j < 9; j++) {
			if (i == YAXIS[j]) {
				return j + 1;
			}
		}
		return 0;
	}

	/**
	 * Checks if a key has an EE value
	 * 
	 * @param pos
	 * @return map.get(pos) == Mark.EE && isValidKey(pos)
	 */
	public boolean isEEE(String pos) {
		return map.get(pos) == Mark.EE && isValidKey(pos);
	}

	/**
	 * Returns the mark that belongs to a given coordiante
	 * 
	 * @param pos
	 * @return map.get(pos)
	 */
	public Mark getMark(String pos) {
		return map.get(pos);
	}

	/**
	 * Returns the letter after a given letter
	 * 
	 * @param i
	 * @return i++
	 */
	public char nextLetter(char i) {
		i++;
		return i;
	}

	/**
	 * Returns the letter before a given letter
	 * 
	 * @param i
	 * @return i--
	 */
	public char previousLetter(char i) {
		i--;
		return i;
	}

	/**
	 * Returns the direction of a coordinate according to another coordinate
	 * 
	 * @param initalLocation
	 * @param aroundLocation
	 * @return direction
	 */
	public int findDirection(String initalLocation, String aroundLocation) {
		for (int i = 1; i <= 6; i++) {
			if (aroundLocation.equals(movedCoordinates(initalLocation, i))) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Gives the mirror direction
	 * 
	 * @param direction
	 * @return mirrorDirection
	 */
	public int mirrorDirection(int direction) {
		switch (direction) {
		case 1:
			return 4;
		case 2:
			return 5;
		case 3:
			return 6;
		case 4:
			return 1;
		case 5:
			return 2;
		case 6:
			return 3;
		default:
			return 0;
		}
	}

	/**
	 * Returns the moved coordinate with a given initial coordinate and a direction
	 * 
	 * @param pos
	 * @param direction
	 * @return movedCoordinate
	 */
	public String movedCoordinates(String pos, int direction) {
		String newPos = "";
		if (isValidKey(pos)) {
			int num = Character.getNumericValue(pos.charAt(0));
			char letter = pos.charAt(1);
			switch (direction) {
			case 1:
				newPos = "" + num + nextLetter(letter);
				break;
			case 2:
				newPos = "" + (num + 1) + nextLetter(letter);
				break;
			case 3:
				newPos = "" + (num + 1) + letter;
				break;
			case 4:
				newPos = "" + num + previousLetter(letter);
				break;
			case 5:
				newPos = "" + (num - 1) + previousLetter(letter);
				break;
			case 6:
				newPos = "" + (num - 1) + letter;
				break;
			default:
				newPos = null;
			}
		}
		if (isValidKey(newPos)) {
			return newPos;
		}
		return null;
	}

	/**
	 * Prints the indices from 0 to 61 in the same format as the board
	 */
	public void printIndex() {
		int currentLine = 1;
		int lineLimit = 4;
		for (int j = 0; j < 4; j++) {
			System.out.print("  ");
		}
		for (int i = 0; i < 61; i++) {
			if (i == lineLimit) {
				System.out.print(" " + i + " ");
				System.out.println();
				for (int j = 0; j < Math.abs(4 - currentLine); j++) {
					System.out.print("  ");
				}
				lineLimit = lineLimit + 9 - Math.abs(4 - currentLine);
				currentLine++;
			} else {
				if (i < 10) {
					System.out.print("  " + i + " ");
				} else {
					System.out.print(" " + i + " ");
				}
			}
		}
		System.out.println();
	}

	/**
	 * Returns map.keySet() in a formatted way
	 * 
	 * @return map.keySet()
	 */
	public String printIndexKey() {
		String result = "";
		char currentLine = 'I';
		for (int j = 0; j < 4; j++) {
			result += "  ";
		}
		for (String k : map.keySet()) {
			if (k.charAt(1) != currentLine) {
				result += "\n";
				for (int j = 0; j < Math.abs(5 - line(k.charAt(1))); j++) {
					result += "  ";
				}
				result += " " + k + " ";
				currentLine = k.charAt(1);
			} else {
				result += " " + k + " ";
			}
		}
		result += "\n";
		return result;
	}

	/**
	 * Returns map.values() in a formatted way
	 * 
	 * @return map.vlaues()
	 */
	public String printGame() {
		String result = "";
		char currentLine = '1';
		for (int j = 0; j < 4; j++) {
			result += "  ";
		}
		for (String k : map.keySet()) {
			if (k.charAt(1) != currentLine) {
				result += "\n";
				for (int j = 0; j < Math.abs(5 - line(k.charAt(1))); j++) {
					result += "  ";
				}
				result += " " + map.get(k) + " ";
				currentLine = k.charAt(1);
			} else {
				result += " " + map.get(k) + " ";
			}
		}
		result += "\n";
		return result;
	}

	/**
	 * Combines printIndexKey() and printGame()
	 */
	public String toString() {
		return "" + printIndexKey() + printGame() + "Removed: " + removedItems;
	}

	/**
	 * Checks if a given mark is a winner
	 * 
	 * @param mark
	 * @return true if mark is the winner
	 */
	public boolean isWinner(Mark mark) {
		int numOfMarksInTheGraveYard = 0;
		if (removedItems.size() > 0) {
			if (numOfColors != 4) {
				for (int i = 0; i < removedItems.size(); i++) {
					if (removedItems.get(i) != mark && removedItems.get(i) != null && removedItems.get(i) != Mark.EE) {
						numOfMarksInTheGraveYard++;
					}
				}
			} else {
				for (int i = 0; i < removedItems.size(); i++) {
					if (removedItems.get(i) != mark && removedItems.get(i) != Mark.ally(mark) && removedItems.get(i) != null && removedItems.get(i) != Mark.EE) {
						numOfMarksInTheGraveYard++;
					}
				}
			}
		}
		return numOfMarksInTheGraveYard == 6;
	}

	/**
	 * Checks if the board has a winner
	 * 
	 * @return true if the board has a winner
	 */
	public boolean hasWinner() {
		switch (numOfColors) {
		case 2:
			return isWinner(Mark.BB) || isWinner(Mark.WW);
		case 3:
			return isWinner(Mark.BB) || isWinner(Mark.WW) || isWinner(Mark.YY);
		case 4:
			return isWinner(Mark.BB) || isWinner(Mark.WW) || isWinner(Mark.YY) || isWinner(Mark.RR);
		default:
			return false;
		}
	}

	/**
	 * Checks if two coordinates are next to each other
	 * 
	 * @param firstpos
	 * @param secondpos
	 * @return true if they are next to each other
	 */
	public boolean checkNextToEachOther(String firstpos, String secondpos) {
		boolean fstNextToSnd = false;
		if (isValidKey(firstpos) && isValidKey(secondpos)) {
			for (int i = 1; i <= 6; i++) {
				if (secondpos.equals(movedCoordinates(firstpos, i))) {
					fstNextToSnd = true;
				}
			}
		}
		return fstNextToSnd;
	}

	/**
	 * checks if the are they are the next to each other and are the same color or
	 * ally colors if there are 4 colors on the board
	 * 
	 * @param firstpos
	 * @param secondpos
	 * @return checkNextToEachOther(firstpos, secondpos) && isTheSame
	 */
	public boolean isLine(String firstpos, String secondpos) {
		boolean isTheSame;
		if (numOfColors == 4) {
			isTheSame = !isEEE(firstpos) && getMark(firstpos).isAlly(getMark(secondpos));
		} else {
			isTheSame = !isEEE(firstpos) && getMark(firstpos) == getMark(secondpos);
		}
		return checkNextToEachOther(firstpos, secondpos) && isTheSame;
	}

	/**
	 * Checks if three coordinates are next to each other
	 * 
	 * @param firstpos
	 * @param secondpos
	 * @param thirdpos
	 * @return true if three coordinates are next to each other
	 */
	public boolean checkNextToEachOther(String firstpos, String secondpos, String thirdpos) {
		if (isValidKey(firstpos) && isValidKey(secondpos) && isValidKey(thirdpos)) {
			if (isLine(firstpos, secondpos)) {
				if (thirdpos.equals(movedCoordinates(secondpos, findDirection(firstpos, secondpos)))) {
					return true;
				} else if (thirdpos.equals(movedCoordinates(firstpos, findDirection(secondpos, firstpos)))) {
					return true;
				}
			} else if (isLine(firstpos, thirdpos)) {
				if (secondpos.equals(movedCoordinates(thirdpos, findDirection(firstpos, thirdpos)))) {
					return true;
				} else if (secondpos.equals(movedCoordinates(firstpos, findDirection(thirdpos, firstpos)))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if three coordinates are next to each other and if they are the same
	 * mark or ally marks
	 * 
	 * @param firstpos
	 * @param secondpos
	 * @param thirdpos
	 * @return checkNextToEachOther(firstpos, secondpos, thirdpos) &&
	 *         !isEEE(firstpos) && isTheSame
	 */
	public boolean isLine(String firstpos, String secondpos, String thirdpos) {
		boolean isTheSame = false;
		if (numOfColors != 4) {
			isTheSame = getMark(firstpos) == getMark(secondpos) && getMark(firstpos) == getMark(thirdpos);
		} else {
			if (getMark(firstpos).isAlly(getMark(secondpos)) && getMark(secondpos).isAlly(getMark(thirdpos))) {
				if (firstpos.equals(middleOutOfThree(firstpos, secondpos, thirdpos))) {
					isTheSame = getMark(firstpos) == getMark(secondpos) || getMark(firstpos) == getMark(thirdpos);
				} else if (secondpos.equals(middleOutOfThree(firstpos, secondpos, thirdpos))) {
					isTheSame = getMark(secondpos) == getMark(firstpos) || getMark(secondpos) == getMark(thirdpos);
				} else {
					isTheSame = getMark(thirdpos) == getMark(firstpos) || getMark(thirdpos) == getMark(secondpos);
				}
			} else {
				isTheSame = false;
			}
		}
		return checkNextToEachOther(firstpos, secondpos, thirdpos) && !isEEE(firstpos) && isTheSame;
	}

	/**
	 * Returns the coordinate that is the middle out of three
	 * 
	 * @param firstpos
	 * @param secondpos
	 * @param thirdpos
	 * @return middle out of the three coordinates
	 */
	public String middleOutOfThree(String firstpos, String secondpos, String thirdpos) {
		if (checkNextToEachOther(firstpos, secondpos, thirdpos)) {
			String[] allThree = { firstpos, secondpos, thirdpos };
			for (int i = 0; i < 3; i++) {
				int numOfStuffAround = 0;
				for (int j = 1; j <= 6; j++) {
					boolean firstAround = firstpos.equals(movedCoordinates(allThree[i], j));
					boolean secondAround = secondpos.equals(movedCoordinates(allThree[i], j));
					boolean thirdAround = thirdpos.equals(movedCoordinates(allThree[i], j));
					if (firstAround || secondAround || thirdAround) {
						numOfStuffAround++;
					}
				}
				if (numOfStuffAround == 2) {
					return allThree[i];
				}
			}
		}
		return null;
	}

	/**
	 * Checks the number of enemy marbles in front
	 * 
	 * @param pos
	 * @param direction
	 * @return number of marbles in front
	 */
	public int numOfStuffInFront(String pos, int direction) {
		String firstInFront = movedCoordinates(pos, direction);
		String secondInFront = movedCoordinates(firstInFront, direction);
		String thirdInFront = movedCoordinates(secondInFront, direction);
		boolean firstInFrontIsEquals = getMark(firstInFront) != getMark(pos);
		boolean secondInFrontIsEquals = getMark(secondInFront) != getMark(pos);
		if (isEEE(firstInFront) || !isValidKey(firstInFront)) {
			return 0;
		} else if (firstInFrontIsEquals) {
			if (isEEE(secondInFront) || !isValidKey(secondInFront)) {
				return 1;
			} else if (secondInFrontIsEquals && (isEEE(thirdInFront) || !isValidKey(thirdInFront))) {
				return 2;
			} else {
				return 3;
			}
		} else {
			return 3;
		}
	}

	/**
	 * Checks if a String is numeric
	 * 
	 * @param str
	 * @return true if a String is a number
	 */

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if a single marble if can be pushed
	 * 
	 * @param pos
	 * @return true if it is can be pushed
	 */
	public boolean isPushable(String pos) {
		if (!isEEE(pos)) {
			int result;
			for (int i = 1; i <= 6; i++) {
				result = 0;
				if (!isValidKey(movedCoordinates(pos, i))) {
					String inFront = movedCoordinates(pos, mirrorDirection(i));
					if (getMark(inFront) != getMark(pos)) {
						if (getMark(inFront) == getMark(movedCoordinates(inFront, mirrorDirection(i))) && getMark(inFront) != Mark.EE) {
							return true;
						}
					} else if (getMark(inFront) == getMark(pos)) {
						for (int j = 0; j < 3; j++) {
							inFront = movedCoordinates(inFront, mirrorDirection(i));
							if (getMark(pos) != getMark(inFront) && getMark(inFront) != Mark.EE) {
								result++;
							}
						}
					}
					if (result == 3) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns an ArrayList with marbles in the order they are supposed to be
	 * playing
	 * 
	 * @return ArrayList with the mark in order
	 */
	public ArrayList<Mark> differentMark() {
		ArrayList<Mark> returnedArrayList = new ArrayList<>();
		switch (numOfColors) {
		case 2:
			returnedArrayList.add(Mark.WW);
			returnedArrayList.add(Mark.BB);
			break;
		case 3:
			returnedArrayList.add(Mark.WW);
			returnedArrayList.add(Mark.BB);
			returnedArrayList.add(Mark.YY);
			break;
		case 4:
			returnedArrayList.add(Mark.WW);
			returnedArrayList.add(Mark.RR);
			returnedArrayList.add(Mark.BB);
			returnedArrayList.add(Mark.YY);
			break;
		default:
			return null;
		}
		return returnedArrayList;
	}

	/**
	 * Removes all marks with the same color from the field
	 * 
	 * @param m
	 */
	public void removeMarbles(Mark m) {
		for (String s : map.keySet()) {
			if (map.get(s) == m) {
				map.put(s, Mark.EE);
			}
		}
	}

	/**
	 * Returns the map
	 * 
	 * @return map
	 */
	public Map<String, Mark> getMap() {
		return map;
	}
}
