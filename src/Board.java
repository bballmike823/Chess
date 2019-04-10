import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Board {
	protected CalculatedValue<List<Move>> availableMoves;
	protected List<Move> movesMade;
	private List<Piece> gameBoard;
	protected Player player1;
	protected Player player2;
	protected int turn;
	protected int turnOfLastCaptureOrAdvance;
	protected boolean whiteKingMoved;
	protected boolean blackKingMoved;
	protected static final int size = 8;
	public static final int seed = 1;

	public Board() {
		initialize();
	}

	public Board(Player p1, Player p2) throws Exception {
		initialize();
		if (!p1.getSide().equals(p2.getSide())) {
			player1 = p1;
			player2 = p2;
		} else {
			throw new Exception("Players are on the same side");
		}

	}

	private void initialize() {
		turn = 1;
		turnOfLastCaptureOrAdvance = 0;
		whiteKingMoved = false;
		blackKingMoved = false;
		movesMade = new ArrayList<Move>();
		try {
			availableMoves = new CalculatedValue<List<Move>>();
		} catch (Exception e) {
			System.err.println("cannot initialize availablemoves ");
			System.exit(0);
		}
		gameBoard = new ArrayList<Piece>(getSize() * getSize());
		for (int i = 0; i < size * size; i++) {
			gameBoard.add(new Piece(Side.EMPTY));
		}
		gameBoard.set(8, new Piece(Side.BLACK));
		gameBoard.set(9, new Piece(Side.BLACK));
		gameBoard.set(10, new Piece(Side.BLACK));
		gameBoard.set(11, new Piece(Side.BLACK));
		gameBoard.set(12, new Piece(Side.BLACK));
		gameBoard.set(13, new Piece(Side.BLACK));
		gameBoard.set(14, new Piece(Side.BLACK));
		gameBoard.set(15, new Piece(Side.BLACK));
		gameBoard.set(0, new Piece(Side.BLACK, Piece.Type.ROOK));
		gameBoard.set(1, new Piece(Side.BLACK, Piece.Type.KNIGHT));
		gameBoard.set(2, new Piece(Side.BLACK, Piece.Type.BISHOP));
		gameBoard.set(3, new Piece(Side.BLACK, Piece.Type.QUEEN));
		gameBoard.set(4, new Piece(Side.BLACK, Piece.Type.KING));
		gameBoard.set(5, new Piece(Side.BLACK, Piece.Type.BISHOP));
		gameBoard.set(6, new Piece(Side.BLACK, Piece.Type.KNIGHT));
		gameBoard.set(7, new Piece(Side.BLACK, Piece.Type.ROOK));

		gameBoard.set(48, new Piece(Side.WHITE));
		gameBoard.set(49, new Piece(Side.WHITE));
		gameBoard.set(50, new Piece(Side.WHITE));
		gameBoard.set(51, new Piece(Side.WHITE));
		gameBoard.set(52, new Piece(Side.WHITE));
		gameBoard.set(53, new Piece(Side.WHITE));
		gameBoard.set(54, new Piece(Side.WHITE));
		gameBoard.set(55, new Piece(Side.WHITE));
		gameBoard.set(56, new Piece(Side.WHITE, Piece.Type.ROOK));
		gameBoard.set(57, new Piece(Side.WHITE, Piece.Type.KNIGHT));
		gameBoard.set(58, new Piece(Side.WHITE, Piece.Type.BISHOP));
		gameBoard.set(59, new Piece(Side.WHITE, Piece.Type.QUEEN));
		gameBoard.set(60, new Piece(Side.WHITE, Piece.Type.KING));
		gameBoard.set(61, new Piece(Side.WHITE, Piece.Type.BISHOP));
		gameBoard.set(62, new Piece(Side.WHITE, Piece.Type.KNIGHT));
		gameBoard.set(63, new Piece(Side.WHITE, Piece.Type.ROOK));
	}

	public void makeMove(Move move) throws Exception {
		Player currentPlayer = currentPlayer();
		if (move == null) {
			if (!availableMoves().isEmpty()) {
				printToConsole();
				System.out.println(currentPlayer.getSide());
				for (Move m : availableMoves.getValue()) {
					System.out.println(m.toString());
				}
				throw new Exception("Player has available moves");
			}
			printToConsole();
			movesMade.add(move);
			turn++;
		} else if (isValidMove(move)) {
			if (!isEmpty(move.getFinalPosition())) {
				turnOfLastCaptureOrAdvance = turn;
			}
			gameBoard.set(move.getFinalPosition(), gameBoard.get(move.getInitialPosition()));
			gameBoard.set(move.getInitialPosition(), new Piece());
			if (getRow(move.getFinalPosition()) == 0 && gameBoard.get(move.getFinalPosition()).isPawn()) {
				gameBoard.get(move.getFinalPosition()).makeQueen();
				turnOfLastCaptureOrAdvance = turn;
			} else if (getRow(move.getFinalPosition()) == 7 && gameBoard.get(move.getFinalPosition()).isPawn()) {
				gameBoard.get(move.getFinalPosition()).makeQueen();
				turnOfLastCaptureOrAdvance = turn;
			} else if (isKing(move.getFinalPosition())) {
				if (isBlack(move.getFinalPosition())) {
					if (move.getInitialPosition() == 4 && move.getFinalPosition() == 6) {
						gameBoard.set(5, gameBoard.get(7));
						gameBoard.set(7, new Piece());
					} else if (move.getInitialPosition() == 4 && move.getFinalPosition() == 2) {
						gameBoard.set(3, gameBoard.get(0));
						gameBoard.set(0, new Piece());
					}
					blackKingMoved = true;
				} else if (isWhite(move.getFinalPosition())) {
					if (move.getInitialPosition() == 60 && move.getFinalPosition() == 62) {
						gameBoard.set(61, gameBoard.get(63));
						gameBoard.set(63, new Piece());
					} else if (move.getInitialPosition() == 60 && move.getFinalPosition() == 58) {
						gameBoard.set(59, gameBoard.get(56));
						gameBoard.set(56, new Piece());
					}
					whiteKingMoved = true;
				}
			}
			turn++;
			availableMoves.reset();
			movesMade.add(move);
		}

	}

	public void nextMove() throws Exception {
		makeMove(currentPlayer().getMove());
	}

	public Player currentPlayer() {
		if (turn % 2 == 1) {
			return player1;
		} else {
			return player2;
		}
	}

	public void print() throws Exception {
		printToConsole();
	}

	public void printToConsole() throws Exception {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (col != 0) {
					System.out.print("|");
				}
				System.out.print(readable(row * size + col));
			}
			System.out.println();
			if (row != size - 1) {
				for (int i = 0; i < size * 2 - 1; i++) {
					System.out.print("--");
				}
				System.out.println();
			}
		}
		int whiteKingLocation = 0;
		for (int i = 0; i < 64; i++) {
			if (isKing(i) && isWhite(i)) {
				whiteKingLocation = i;
			}
		}
		int blackKingLocation = 0;
		for (int i = 0; i < 64; i++) {
			if (isKing(i) && isBlack(i)) {
				blackKingLocation = i;
			}
		}
		if (blackThreatens(whiteKingLocation)) {
			System.out.println("white is in check");
		}
		if (whiteThreatens(blackKingLocation)) {
			System.out.println("black is in check");
		}

	}

	private String readable(int position) throws Exception {
		return getPiece(position).toString();
	}

	// TODO implement checkmate detection
	public boolean gameOver() {
		if (whiteCheckMate() || blackCheckMate()) {
			return true;
		} else if (turn - turnOfLastCaptureOrAdvance > 100) {
			return true;
			// dont like this way of finding no availableMoves;
		} else if (availableMoves().isEmpty()) {
			//debugging
			try {
				printToConsole();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		} else if (turn > 6 && movesMade.get(turn - 2).equals(movesMade.get(turn - 6))
				&& movesMade.get(turn - 3).equals(movesMade.get(turn - 7))
				&& movesMade.get(turn - 4).equals(movesMade.get(turn - 8))
				&& movesMade.get(turn - 5).equals(movesMade.get(turn - 9))
				&& movesMade.get(turn - 6).equals(movesMade.get(turn - 10))
				&& movesMade.get(turn - 7).equals(movesMade.get(turn - 11))) {
			return true;
		} else if (kingCaptured()) {
			try {
				throw new Exception("king should not be able to be captured");
			} catch (Exception e) {
				System.out.println(movesMade.get(turn - 2));
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean kingCaptured() {
		int kings = 0;
		for (int i = 0; i < 64; i++) {
			if (isKing(i)) {
				kings++;
			}
			if (kings == 2) {
				return false;
			}
		}
		return true;
	}

	public boolean Threatened(int position) {
		if (isEmpty(position)) {
			return false;
		}
		if (isWhite(position)) {
			return blackThreatens(position);
		} else {
			return whiteThreatens(position);
		}
	}

	public boolean isThreatenedBy(Side opponent, int row, int col) {
		if (opponent.isWhite()) {
			return whiteThreatens(getPosition(row, col));
		} else if (opponent.isBlack()) {
			return blackThreatens(getPosition(row, col));
		} else {
			return false;
		}
	}

	public boolean blackThreatens(int position) {
		return blackThreatens(getRow(position),getColumn(position));
	}
	public boolean blackThreatens(int row,int col) {
		int newRow = row - 1;
		int newCol = col - 1;
		// pawn threats
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isPawn(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isPawn(newRow, newCol)) {
			return true;
		}
		// knight threats
		newRow = row + 2;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row - 2;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row + 2;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row - 2;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col + 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col + 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col - 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col - 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isBlack(newRow, newCol)) {
			return true;
		}
		// bishop threats
		for (int i = 1; i < size; i++) {
			newRow = row + i;
			newCol = col + i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row - i;
			newCol = col + i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row + i;
			newCol = col - i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row - i;
			newCol = col - i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}
		// rook threats
		for (int i = 1; i < size; i++) {
			newRow = row + i;
			if (!isInBounds(newRow, col)) {
				break;
			} else if (isWhite(newRow, col)) {
				break;
			} else if (isBlack(newRow, col)) {
				if (isRook(newRow, col) || isQueen(newRow, col)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row - i;
			if (!isInBounds(newRow, col)) {
				break;
			} else if (isWhite(newRow, col)) {
				break;
			} else if (isBlack(newRow, col)) {
				if (isRook(newRow, col) || isQueen(newRow, col)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newCol = col + i;
			if (!isInBounds(row, newCol)) {
				break;
			} else if (isWhite(row, newCol)) {
				break;
			} else if (isBlack(row, newCol)) {
				if (isRook(row, newCol) || isQueen(row, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newCol = col - i;
			if (!isInBounds(row, newCol)) {
				break;
			} else if (isWhite(row, newCol)) {
				break;
			} else if (isBlack(row, newCol)) {
				if (isRook(row, newCol) || isQueen(row, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}
		// king threats
		newRow = row;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isBlack(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		return false;
	}
	
	public boolean whiteThreatens(int position) {
		return whiteThreatens(getRow(position),getColumn(position));
	}

	public boolean whiteThreatens(int row,int col) {
		int newRow = row + 1;
		int newCol = col - 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isPawn(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isPawn(newRow, newCol)) {
			return true;
		}
		// knight threats
		newRow = row + 2;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row - 2;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row + 2;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row - 2;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col + 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col + 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col - 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col - 2;
		if (isInBounds(newRow, newCol) && isKnight(newRow, newCol) && isWhite(newRow, newCol)) {
			return true;
		}
		// bishop threats
		for (int i = 1; i < size; i++) {
			newRow = row + i;
			newCol = col + i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row - i;
			newCol = col + i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row + i;
			newCol = col - i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row - i;
			newCol = col - i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (isBlack(newRow, newCol)) {
				break;
			} else if (isWhite(newRow, newCol)) {
				if (isBishop(newRow, newCol) || isQueen(newRow, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}
		// rook threats
		for (int i = 1; i < size; i++) {
			newRow = row + i;
			if (!isInBounds(newRow, col)) {
				break;
			} else if (isBlack(newRow, col)) {
				break;
			} else if (isWhite(newRow, col)) {
				if (isRook(newRow, col) || isQueen(newRow, col)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newRow = row - i;
			if (!isInBounds(newRow, col)) {
				break;
			} else if (isBlack(newRow, col)) {
				break;
			} else if (isWhite(newRow, col)) {
				if (isRook(newRow, col) || isQueen(newRow, col)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newCol = col + i;
			if (!isInBounds(row, newCol)) {
				break;
			} else if (isBlack(row, newCol)) {
				break;
			} else if (isWhite(row, newCol)) {
				if (isRook(row, newCol) || isQueen(row, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}

		for (int i = 1; i < size; i++) {
			newCol = col - i;
			if (!isInBounds(row, newCol)) {
				break;
			} else if (isBlack(row, newCol)) {
				break;
			} else if (isWhite(row, newCol)) {
				if (isRook(row, newCol) || isQueen(row, newCol)) {
					return true;
				} else {
					break;
				}
			}
		}
		// king threats
		newRow = row;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row + 1;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		newRow = row - 1;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && isWhite(newRow, newCol) && isKing(newRow, newCol)) {
			return true;
		}
		return false;
	}

	public boolean whiteKingCaptured() {
		for (int i = 0; i < 64; i++) {
			if (isKing(i) && isWhite(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean blackKingCaptured() {
		for (int i = 0; i < 64; i++) {
			if (isKing(i) && isBlack(i)) {
				return false;
			}
		}
		return true;
	}

	// TODO
	public boolean whiteCheckMate() {
		if (!whiteCheck()) {
			return false;
		}
		MutableBoard eval = new MutableBoard(this);
		List<Move> potentialMoves = eval.availableMoves();
		// TODO put king moves first
		// availableMoves = reorderedMoves(availableMoves, eval);
		if (potentialMoves.isEmpty()) {
			return true;
		}
		for (Move move : potentialMoves) {
			try {
				eval.makeMove(move);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!eval.whiteCheck()) {
				return false;
			}
			eval.undoLastMove();
		}
		return true;
	}

	public boolean whiteCheck() {
		return whiteThreatens(getBlackKingLocation());
	}

	// TODO
	public boolean blackCheckMate() {
		if (!blackCheck()) {
			return false;
		}
		MutableBoard eval = new MutableBoard(this);
		List<Move> potentialMoves = eval.availableMoves();
		// TODO put king moves first
		// availableMoves = reorderedMoves(availableMoves, eval);
		if (potentialMoves.isEmpty()) {
			return true;
		}
		for (Move move : potentialMoves) {
			try {
				eval.makeMove(move);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!eval.blackCheck()) {
				return false;
			}
			eval.undoLastMove();
		}
		return true;
	}

	public boolean blackCheck() {
		return blackThreatens(getWhiteKingLocation());
	}

	public int getWhiteKingLocation() {
		for (int i = 0; i < 64; i++) {
			if (isKing(i) && isWhite(i)) {
				return i;
			}
		}
		return 64;
	}

	public int getBlackKingLocation() {
		for (int i = 0; i < 64; i++) {
			if (isKing(i) && isBlack(i)) {
				return i;
			}
		}
		return 64;
	}

	// TODO
	public boolean whiteWins() {
		if (!gameOver()) {
			return false;
		} else {
			return whiteCheckMate();
		}
	}

	// TODO
	public boolean blackWins() {
		if (!gameOver()) {
			return false;
		} else {
			return blackCheckMate();
		}
	}

	// TODO
	public boolean tie() {
		return (gameOver() && !whiteCheckMate() && !blackCheckMate());
	}

	public String winner() {
		if (whiteWins()) {
			return "WHITE";
		} else if (blackWins()) {
			return "BLACK";
		} else if (tie()) {
			return "TIE";
		} else {
			return "game not over";
		}
	}

	public List<Move> availableMoves(Side side) {
		if (side == Side.EMPTY) {
			try {
				throw new Exception("cant get moves for null side");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (availableMoves.calculated() && side.equals(currentPlayer().getSide())) {
			return availableMoves.getValue();
		}
		List<Move> moves = new ArrayList<Move>();
		List<Integer> startingPoints = new LinkedList<Integer>();
		for (int position = 0; position < getSize() * getSize(); position++) {
			if ((!isEmpty(position))
					&& ((isWhite(position) && turn % 2 == 1) || (isBlack(position) && turn % 2 == 0))) {
				startingPoints.add(position);
			}
		}
		for (int position : startingPoints) {
			moves.addAll(getMovesForPiece(position));
		}
		List<Move> legalMoves = new LinkedList<Move>();
		if (currentPlayer().getSide().isWhite()) {
			MutableBoard eval = new MutableBoard(this);
			for (Move move : moves) {
				try {
					eval.makeMove(move);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!eval.blackCheck()) {
					legalMoves.add(move);
				}
				eval.undoLastMove();
			}
		} else if (currentPlayer().getSide().isBlack()) {
			MutableBoard eval = new MutableBoard(this);
			for (Move move : moves) {
				try {
					eval.makeMove(move);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!eval.whiteCheck()) {
					legalMoves.add(move);
				}
				eval.undoLastMove();
			}
		}
		if (side.equals(currentPlayer().getSide())) {
			availableMoves.setValue(legalMoves);
		}
		return legalMoves;
	}

	// TODO add en pasent and castle
	protected List<Move> getMovesForPiece(int startingPosition) {
		if (isPawn(startingPosition)) {
			if (isWhite(startingPosition)) {
				return whitePawnMoves(startingPosition);
			} else if (isBlack(startingPosition)) {
				return blackPawnMoves(startingPosition);
			}
		} else if (isKnight(startingPosition)) {
			return knightMoves(startingPosition);
		} else if (isBishop(startingPosition)) {
			return bishopMoves(startingPosition);
		} else if (isRook(startingPosition)) {
			return rookMoves(startingPosition);
		} else if (isQueen(startingPosition)) {
			return queenMoves(startingPosition);
		} else if (isKing(startingPosition)) {
			return kingMoves(startingPosition);
		}
		try {
			throw new Exception("THIS should be unreachable");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected List<Move> whitePawnMoves(int startingPosition) {
		int row = getRow(startingPosition);
		int col = getColumn(startingPosition);
		List<Move> moves = new LinkedList<Move>();
		if (isEmpty(row - 1, col)) {
			moves.add(new Move(getPosition(row, col), getPosition(row - 1, col)));
			if (row == 6 && isEmpty(4, col)) {
				moves.add(new Move(getPosition(6, col), getPosition(4, col)));
			}
		}
		if (col != 0 && isBlack(row - 1, col - 1)) {
			moves.add(new Move(getPosition(row, col), getPosition(row - 1, col - 1)));
		}
		if (col != 7 && isBlack(row - 1, col + 1)) {
			moves.add(new Move(getPosition(row, col), getPosition(row - 1, col + 1)));
		}
		return moves;
	}

	protected List<Move> blackPawnMoves(int startingPosition) {
		int row = getRow(startingPosition);
		int col = getColumn(startingPosition);
		List<Move> moves = new LinkedList<Move>();
		if (isEmpty(row + 1, col)) {
			moves.add(new Move(getPosition(row, col), getPosition(row + 1, col)));
			if (row == 1 && isEmpty(3, col)) {
				moves.add(new Move(getPosition(1, col), getPosition(3, col)));
			}
		}
		if (col != 0 && isWhite(row + 1, col - 1)) {
			moves.add(new Move(getPosition(row, col), getPosition(row + 1, col - 1)));
		}
		if (col != 7 && isWhite(row + 1, col + 1)) {
			moves.add(new Move(getPosition(row, col), getPosition(row + 1, col + 1)));
		}
		return moves;
	}

	protected List<Move> knightMoves(int startingPosition) {
		int row = getRow(startingPosition);
		int col = getColumn(startingPosition);
		List<Move> moves = new LinkedList<Move>();
		int newRow = row + 2;
		int newCol = col + 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 2;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row + 2;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 2;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row + 1;
		newCol = col + 2;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 1;
		newCol = col + 2;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row + 1;
		newCol = col - 2;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 1;
		newCol = col - 2;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		return moves;
	}

	protected List<Move> bishopMoves(int startingPosition) {
		int row = getRow(startingPosition);
		int col = getColumn(startingPosition);
		List<Move> moves = new LinkedList<Move>();
		for (int i = 1; i < size; i++) {
			int newRow = row + i;
			int newCol = col + i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (areSame(row, col, newRow, newCol)) {
				break;
			} else if (areOpponents(row, col, newRow, newCol)) {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
			}
		}

		for (int i = 1; i < size; i++) {
			int newRow = row - i;
			int newCol = col + i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (areSame(row, col, newRow, newCol)) {
				break;
			} else if (areOpponents(row, col, newRow, newCol)) {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
			}
		}

		for (int i = 1; i < size; i++) {
			int newRow = row + i;
			int newCol = col - i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (areSame(row, col, newRow, newCol)) {
				break;
			} else if (areOpponents(row, col, newRow, newCol)) {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
			}
		}

		for (int i = 1; i < size; i++) {
			int newRow = row - i;
			int newCol = col - i;
			if (!isInBounds(newRow, newCol)) {
				break;
			} else if (areSame(row, col, newRow, newCol)) {
				break;
			} else if (areOpponents(row, col, newRow, newCol)) {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
			}
		}
		return moves;
	}

	protected List<Move> rookMoves(int startingPosition) {
		int row = getRow(startingPosition);
		int col = getColumn(startingPosition);
		List<Move> moves = new LinkedList<Move>();
		for (int i = 1; i < size; i++) {
			int newRow = row + i;
			if (!isInBounds(newRow, col)) {
				break;
			} else if (areSame(row, col, newRow, col)) {
				break;
			} else if (areOpponents(row, col, newRow, col)) {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, col)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, col)));
			}
		}

		for (int i = 1; i < size; i++) {
			int newRow = row - i;
			if (!isInBounds(newRow, col)) {
				break;
			} else if (areSame(row, col, newRow, col)) {
				break;
			} else if (areOpponents(row, col, newRow, col)) {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, col)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(newRow, col)));
			}
		}

		for (int i = 1; i < size; i++) {
			int newCol = col + i;
			if (!isInBounds(row, newCol)) {
				break;
			} else if (areSame(row, col, row, newCol)) {
				break;
			} else if (areOpponents(row, col, row, newCol)) {
				moves.add(new Move(getPosition(row, col), getPosition(row, newCol)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(row, newCol)));
			}
		}

		for (int i = 1; i < size; i++) {
			int newCol = col - i;
			if (!isInBounds(row, newCol)) {
				break;
			} else if (areSame(row, col, row, newCol)) {
				break;
			} else if (areOpponents(row, col, row, newCol)) {
				moves.add(new Move(getPosition(row, col), getPosition(row, newCol)));
				break;
			} else {
				moves.add(new Move(getPosition(row, col), getPosition(row, newCol)));
			}
		}
		return moves;
	}

	protected List<Move> queenMoves(int startingPosition) {
		List<Move> moves = new LinkedList<Move>();
		moves.addAll(bishopMoves(startingPosition));
		moves.addAll(rookMoves(startingPosition));
		return moves;
	}

	protected List<Move> kingMoves(int startingPosition) {
		Side side = getSide(startingPosition);
		List<Move> moves = new LinkedList<Move>();
		moves.addAll(basicKingMoves(startingPosition));
		if (side.isWhite()) {
			if (whiteKingMoved()) {
				return moves;
			} else {
				moves.addAll(basicKingMoves(startingPosition));
				if (isEmpty(7, 5) && isEmpty(7, 6) && isWhite(7, 7) && isRook(7, 7)) {
					moves.add(new Move(60, 62));
				}
				if (isEmpty(7, 3) && isEmpty(7, 2) && isEmpty(7, 1) && isWhite(7, 0) && isRook(7, 0)) {
					moves.add(new Move(60, 58));
				}
			}
		} else if (side.isBlack()) {
			if (blackKingMoved()) {
				return moves;
			} else {
				moves.addAll(basicKingMoves(startingPosition));
				if (isEmpty(0, 5) && isEmpty(0, 6) && isBlack(0, 7) && isRook(0, 7)) {
					moves.add(new Move(4, 6));
				}
				if (isEmpty(0, 3) && isEmpty(0, 2) && isEmpty(0, 1) && isBlack(0, 0) && isRook(0, 0)) {
					moves.add(new Move(4, 2));
				}
			}
		}
		return moves;
	}

	protected List<Move> basicKingMoves(int startingPosition) {
		int row = getRow(startingPosition);
		int col = getColumn(startingPosition);
		Side opponent = getSide(startingPosition).opponent();
		List<Move> moves = new LinkedList<Move>();
		int newRow = row;
		int newCol = col + 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row + 1;
		newCol = col;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 1;
		newCol = col;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row + 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 1;
		newCol = col + 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row + 1;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		newRow = row - 1;
		newCol = col - 1;
		if (isInBounds(newRow, newCol) && !areSame(row, col, newRow, newCol)
				&& !isThreatenedBy(opponent, newRow, newCol)) {
			moves.add(new Move(getPosition(row, col), getPosition(newRow, newCol)));
		}
		return moves;
	}

	public boolean areOpponents(Side side, int row, int column) {
		if (side.isEmpty() || isEmpty(row, column)) {
			return false;
		}
		return !side.equals(getSide(row, column));
	}

	public boolean areOpponents(int row1, int column1, int row2, int column2) {
		if (isEmpty(row1, column1) || isEmpty(row2, column2)) {
			return false;
		}
		return !getSide(row1, column1).equals(getSide(row2, column2));
	}

	public boolean areSame(Side side, int row, int column) {
		if (side.isEmpty() || isEmpty(row, column)) {
			return false;
		}
		return side.equals(getSide(row, column));
	}

	public boolean areSame(int row1, int column1, int row2, int column2) {
		if (isEmpty(row1, column1) || isEmpty(row2, column2)) {
			return false;
		}
		return getSide(row1, column1).equals(getSide(row2, column2));
	}

	public boolean areSame(Side side, int position) {
		if (side.isEmpty() || isEmpty(position)) {
			return false;
		}
		return side.equals(getSide(position));
	}

	public boolean areSame(int position1, int position2) {
		if (isEmpty(position1) || isEmpty(position2)) {
			return false;
		}
		return getSide(position1).equals(getSide(position2));
	}

	public List<Move> availableMoves() {
		return availableMoves(currentPlayer().getSide());
	}

	public boolean isValidMove(Move move, Side side) {
		if (!areSame(side, move.getInitialPosition())) {
			return false;
		} else if (!areSame(side, move.getFinalPosition())) {
			try {
				List<Move> availableMoves = availableMoves();
				for (Move availableMove : availableMoves) {
					if (move.equals(availableMove)) {
						return true;
					}
				}
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isValidMove(Move move) {
		return isValidMove(move, currentPlayer().getSide());
	}

	protected Player getPlayer1() {
		return player1;
	}

	protected Player getPlayer2() {
		return player2;
	}

	protected int getTurn() {
		return turn;
	}

	public boolean isEmpty(int position) {
		return gameBoard.get(position).isEmpty();
	}

	public boolean isWhite(int position) {
		return gameBoard.get(position).isWhite();
	}

	public boolean isBlack(int position) {
		return gameBoard.get(position).isBlack();
	}

	public boolean isSame(int position, Side side) {
		return gameBoard.get(position).getSide().equals(side);
	}

	public boolean isEmpty(int row, int col) {
		return gameBoard.get(row * getSize() + col).isEmpty();
	}

	public boolean isWhite(int row, int col) {
		return gameBoard.get(row * getSize() + col).isWhite();
	}

	public boolean isBlack(int row, int col) {
		return gameBoard.get(row * getSize() + col).isBlack();
	}

	public boolean isKing(int row, int col) {
		return gameBoard.get(getPosition(row, col)).isKing();
	}

	public boolean isKing(int position) {
		return gameBoard.get(position).isKing();
	}

	public boolean isQueen(int row, int col) {
		return gameBoard.get(getPosition(row, col)).isQueen();
	}

	public boolean isQueen(int position) {
		return gameBoard.get(position).isQueen();
	}

	public boolean isRook(int row, int col) {
		return gameBoard.get(getPosition(row, col)).isRook();
	}

	public boolean isRook(int position) {
		return gameBoard.get(position).isRook();
	}

	public boolean isBishop(int row, int col) {
		return gameBoard.get(getPosition(row, col)).isBishop();
	}

	public boolean isBishop(int position) {
		return gameBoard.get(position).isBishop();
	}

	public boolean isKnight(int row, int col) {
		return gameBoard.get(getPosition(row, col)).isKnight();
	}

	public boolean isKnight(int position) {
		return gameBoard.get(position).isKnight();
	}

	public boolean isPawn(int row, int col) {
		return gameBoard.get(getPosition(row, col)).isPawn();
	}

	public boolean isPawn(int position) {
		return gameBoard.get(position).isPawn();
	}

	public Side getSide(int position) {
		return gameBoard.get(position).getSide();
	}

	public Side getSide(int row, int col) {
		return gameBoard.get(getPosition(row, col)).getSide();
	}

	protected boolean isInBounds(int row, int column) {
		return (row >= 0 && row < getSize()) && (column >= 0 && column < getSize());
	}

	public static int getRow(int position) {
		return position / getSize();
	}

	public static int getColumn(int position) {
		return position % getSize();
	}

	public static int getPosition(int row, int column) {
		return row * getSize() + column;
	}

	public static int getSize() {
		return size;
	}

	public boolean whiteKingMoved() {
		return whiteKingMoved;
	}

	public boolean blackKingMoved() {
		return blackKingMoved;
	}

	public Move getHumanMove() throws Exception {
		System.out.println("Please enter your move");
		print();
		Scanner scanner = new Scanner(System.in);
		String moveString = scanner.nextLine();
		while (true) {
			if (Move.isProperFormat(moveString) && isValidMove(new Move(moveString))) {
				scanner.close();
				return new Move(moveString);
			} else {
				System.out.println("Invalid Move. Please enter another move. Available moves:");
				List<Move> availableMoves = availableMoves();
				for (Move m : availableMoves) {
					System.out.println(m.toString());
				}
				moveString = scanner.nextLine();
			}
		}
	}

	public Piece getPiece(int position) {
		return gameBoard.get(position);
	}

	public Move getLastMove() {
		return movesMade.get(movesMade.size() - 1);
	}

	public int getSeed() {
		return seed;
	}

	public void close() {

	}

	public List<Move> getMovesMade() {
		return movesMade;
	}

	public Integer getLastImportantMove() {
		return turnOfLastCaptureOrAdvance;
	}
}
