package ss.project.networking.protocols;

import java.util.Arrays;
import java.util.List;

//192.168.1.230
public abstract class ProtocolMessages {
	public static final char DELIMITER = ';';
	public static final char CHALLENGE = 'W';
	public static final char ACCEPTCHALLENGE = 'Y';
	public static final char DENYCHALLENGE = 'N';
	public static final char MOVE = 'M';
	public static final char START = 'S';
	public static final char ALLY = 'A';
	public static final char TEXT = 'B';
	public static final char TURN = 'T';
	public static final char FINISH = 'F';
	public static final char CONNECT = 'C';
	public static final char DISCONNECT = 'D';
	public static final char JOIN = 'J';
	public static final char ROOMS = 'R';
	public static final char LEADERBOARD = 'P';
	public static final char LEAVE = 'L';
	public static final char ERROR = 'E';
	static final Character[] firstarray = { Character.valueOf(MOVE),
											Character.valueOf(START),
											Character.valueOf(ALLY),
											Character.valueOf(TURN),
											Character.valueOf(TEXT),
											Character.valueOf(FINISH)};
	static final Character[] secondarray = {Character.valueOf(CONNECT),
											Character.valueOf(DISCONNECT),
											Character.valueOf(JOIN),
											Character.valueOf(LEAVE)};
	public static final List<Character> roomChar = Arrays.asList(firstarray);
	public static final List<Character> serverChar = Arrays.asList(secondarray);
}
