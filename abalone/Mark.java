package ss.project.abalone;

public enum Mark {

	EE, WW, BB, YY, RR;

	public String toString() {
		if (this == WW) {
			return "WW";
		} else if (this == BB) {
			return "BB";
		} else if (this == YY) {
			return "YY";
		} else if (this == RR) {
			return "RR";
		} else {
			return "EE";
		}
	}

	public static Mark ally(Mark m) {
		switch (m) {
		case WW:
			return BB;
		case BB:
			return WW;
		case YY:
			return RR;
		case RR:
			return YY;
		default:
			return null;
		}
	}

	public boolean isEnemyFor2(Mark m) {
		return this != m && m != EE && this != EE;
	}

	public boolean isAlly(Mark m) {
		return this == m || this == ally(m);
	}

	public boolean isEnemyFor4(Mark m) {
		return this != m && this != ally(m) && this != null && this != EE;
	}
}
