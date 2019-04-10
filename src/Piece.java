
public class Piece {
	public enum Type {
		EMPTY, PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
	}

	private Side side;
	private Type type;

	// default to empty piece.
	public Piece() {
		side = Side.EMPTY;
		type = Type.EMPTY;
	}

	public Piece(Side pieceSide) {
		side = pieceSide;
		type = Type.PAWN;
	}

	public Piece(Side pieceSide, Type typeOfPiece) {
		side = pieceSide;
		type = typeOfPiece;

	}

	public Piece(Piece p) {
		side = p.side;
		type = p.type;
	}

	public boolean isEmpty() {
		return side == Side.EMPTY;
	}

	public boolean isWhite() {
		return side == Side.WHITE;
	}

	public boolean isBlack() {
		return side == Side.BLACK;
	}

	public Side getSide() {
		return side;
	}

	public boolean isPawn() {
		return type == Type.PAWN;
	}
	
	public boolean isKnight() {
		return type == Type.KNIGHT;
	}
	
	public boolean isBishop() {
		return type == Type.BISHOP;
	}
	
	public boolean isRook() {
		return type == Type.ROOK;
	}
	
	public boolean isQueen() {
		return type == Type.QUEEN;
	}
	
	public boolean isKing() {
		return type == Type.KING;
	}

	public void makeQueen() {
		type = Type.QUEEN;
	}

	public String toString() {
		switch (side) {
		case WHITE:
			switch (type) {
			case PAWN:
				return "WpW";
			case BISHOP:
				return "WbW";
			case KNIGHT:
				return "WkW";
			case ROOK:
				return "WrW";
			case QUEEN:
				return "WQW";
			case KING:
				return "WKW";
			}
		case BLACK:
			switch (type) {
			case PAWN:
				return "BpB";
			case BISHOP:
				return "BbB";
			case KNIGHT:
				return "BkB";
			case ROOK:
				return "BrB";
			case QUEEN:
				return "BQB";
			case KING:
				return "BKB";
			}
		case EMPTY:
			return "   ";
		}
		return null;
	}
}
