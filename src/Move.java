public class Move {
	private int startingPosition;
	private int finalPosition;

	public Move(int start, int end) {
		startingPosition = start;
		finalPosition = end;
	}

	// Takes string in form p1:p2:p3
	public Move(String moveString) {
		String[] temp = moveString.split(":");
		startingPosition = Integer.parseInt(temp[0]);
		finalPosition = Integer.parseInt(temp[1]);
	}

	private boolean isValidPosition(int position) {
		return position >= 0 && position < 64;
	}

	public static boolean isProperFormat(String moveString) {
		for (int index = 0; index < moveString.length(); index++) {
			char temp = moveString.charAt(index);
			boolean valid = (temp == ':') || (temp >= '0' && temp <= '9');
			if (!valid) {
				return false;
			}
		}
		return true;
	}
	public int getInitialPosition() {
		return startingPosition;
	}

	public int getFinalPosition() {
		return finalPosition;
	}

	public String toString() {
		String result = startingPosition + ":" + finalPosition;
		return result;

	}

	public boolean equals(Move move) {
		return move.getInitialPosition() == startingPosition && move.getFinalPosition() == finalPosition;
	}
	
	public boolean isSame(Move move) {
		return move.getInitialPosition() == startingPosition && move.getFinalPosition() == finalPosition;
	}
}
