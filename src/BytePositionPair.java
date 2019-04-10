
public class BytePositionPair {
	private byte piece;
	private int location;

	public BytePositionPair(byte PIECE, int position) {
		location = position;
		piece = PIECE;
	}

	public byte getPiece() {
		return piece;
	}

	public int getPosition() {
		return location;
	}
}
