import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MutableBoard extends Board {
	private List<Move> mutableMovesMade;
	private Stack<List<BytePositionPair>> changedPieces;
	private Stack<Integer> lastImportantTurn;
	private CalculatedValue<Boolean> gameOverStatus;
	private CalculatedValue<Boolean> whiteCheckMateStatus;
	private CalculatedValue<Boolean> blackCheckMateStatus;
	private CalculatedValue<Integer> lateGameTurn;
	private Stack<Boolean> captureMade;
	private int turnWhiteKingMoved;
	private int turnBlackKingMoved;
	private int whiteKingLocation;
	private int blackKingLocation;
	boolean kingCaptured;
	byte[] mutableGameBoard;

	public MutableBoard(Player p1, Player p2) throws Exception {
		super(p1, p2);
		mutableMovesMade = new LinkedList<Move>();
		changedPieces = new Stack<List<BytePositionPair>>();
		lastImportantTurn = new Stack<Integer>();
		lastImportantTurn.push(0);
		gameOverStatus = new CalculatedValue<Boolean>();
		gameOverStatus.setValue(false);
		captureMade = new Stack<Boolean>();
		kingCaptured = false;
		turnWhiteKingMoved = 0;
		turnBlackKingMoved = 0;
		mutableGameBoard = new byte[64];
		lateGameTurn = new CalculatedValue<Integer>();
		lateGameTurn.setValue(0);
		whiteKingLocation = getWhiteKingLocation();
		blackKingLocation = getBlackKingLocation();
	}

	public MutableBoard(Board board) {
		super();
		player1 = board.getPlayer1();
		player2 = board.getPlayer2();
		turn = board.getTurn();
		mutableGameBoard = new byte[64];
		for (int i = 0; i < size * size; i++) {
			mutableGameBoard[i] = convertToByte(board.getPiece(i));
		}
		mutableMovesMade = new LinkedList<Move>();
		mutableMovesMade.addAll(board.getMovesMade());
		changedPieces = new Stack<List<BytePositionPair>>();
		lastImportantTurn = new Stack<Integer>();
		lastImportantTurn.push(board.getLastImportantMove());
		captureMade = new Stack<Boolean>();
		kingCaptured = false;
		if (board.whiteKingMoved()) {
			turnWhiteKingMoved = board.getTurn() - 1;
		} else {
			turnWhiteKingMoved = 0;
		}
		if (board.blackKingMoved()) {
			turnBlackKingMoved = board.getTurn() - 1;
		} else {
			turnBlackKingMoved = 0;
		}
		lateGameTurn = new CalculatedValue<Integer>();
		lateGameTurn.setValue(0);
		whiteKingLocation = board.getWhiteKingLocation();
		blackKingLocation = board.getBlackKingLocation();
		gameOverStatus = new CalculatedValue<Boolean>();
		whiteCheckMateStatus = new CalculatedValue<Boolean>();
		blackCheckMateStatus = new CalculatedValue<Boolean>();
	}

	public void makeMove(Move move) throws Exception {
		List<BytePositionPair> changedThisTurn = new LinkedList<BytePositionPair>();
		boolean capMade = false;
		if (move == null) {
			gameOverStatus.setValue(true);
		} else {
			int finalPosition = move.getFinalPosition();
			int initialPosition = move.getInitialPosition();
			if (!isEmpty(finalPosition)) {
				lastImportantTurn.push(turn);
				capMade = true;
				if (isKing(finalPosition)) {
					gameOverStatus.setValue(true);
					kingCaptured = true;
					printToConsole();
					System.out.println(move.toString());
					throw new Exception("should not be able to capture king");
				}
			}
			if(isKing(initialPosition)){
				if(isWhite(initialPosition)){
					whiteKingLocation = finalPosition;
				}else if(isBlack(initialPosition)){
					blackKingLocation = finalPosition;
				}
			}
			changedThisTurn.add(new BytePositionPair(mutableGameBoard[finalPosition], finalPosition));
			changedThisTurn.add(new BytePositionPair(mutableGameBoard[initialPosition], initialPosition));
			mutableGameBoard[finalPosition] = mutableGameBoard[initialPosition];
			mutableGameBoard[initialPosition] = 0;
			if (getRow(finalPosition) == 0 && isPawn(finalPosition)) {
				mutableGameBoard[finalPosition] += 8;
				if (lastImportantTurn.peek() != turn) {
					lastImportantTurn.push(turn);
				}
			} else if (getRow(finalPosition) == 7 && isPawn(finalPosition)) {
				mutableGameBoard[finalPosition] += 8;
				if (lastImportantTurn.peek() != turn) {
					lastImportantTurn.push(turn);
				}
			} else if (isKing(finalPosition)) {
				if (isBlack(move.getFinalPosition())) {
					if (move.getInitialPosition() == 4 && move.getFinalPosition() == 6) {
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[7], 7));
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[5], 5));
						mutableGameBoard[5] = mutableGameBoard[7];
						mutableGameBoard[5] = 0;
					} else if (move.getInitialPosition() == 4 && move.getFinalPosition() == 2) {
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[0], 0));
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[3], 3));
						mutableGameBoard[3] = mutableGameBoard[0];
						mutableGameBoard[0] = 0;
					}
				} else if (isWhite(move.getFinalPosition())) {
					if (move.getInitialPosition() == 60 && move.getFinalPosition() == 62) {
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[63], 63));
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[61], 61));
						mutableGameBoard[61] = mutableGameBoard[63];
						mutableGameBoard[63] = 0;
					} else if (move.getInitialPosition() == 60 && move.getFinalPosition() == 58) {
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[56], 56));
						changedThisTurn.add(new BytePositionPair(mutableGameBoard[59], 59));
						mutableGameBoard[59] = mutableGameBoard[56];
						mutableGameBoard[56] = 0;
					}
					if (turnWhiteKingMoved == 0) {
						turnWhiteKingMoved = turn;
					}
				}
			}
		}
		turn++;
		if (lateGameTurn.calculated() && lateGameTurn.getValue() == 0) {
			lateGameTurn.reset();
		}
		changedPieces.add(changedThisTurn);
		captureMade.push(capMade);
		mutableMovesMade.add(move);
		availableMoves.reset();
		if(gameOverStatus.calculated()&&!gameOverStatus.getValue()){
			gameOverStatus.reset();
			whiteCheckMateStatus.reset();
			blackCheckMateStatus.reset();
		}
	}

	@Override
	public boolean gameOver() {
		if (gameOverStatus.calculated()) {
			return gameOverStatus.getValue();
		} else if (turn - lastImportantTurn.peek() > 100) {
			gameOverStatus.setValue(true);
			return true;
		} else if (whiteCheckMate() || blackCheckMate()) {
			gameOverStatus.setValue(true);
			return true;
		} else if (availableMoves().isEmpty()) {
			return true;
		} else if (turn > 12 && mutableMovesMade.get(turn - 2).equals(mutableMovesMade.get(turn - 6))
				&& mutableMovesMade.get(turn - 3).equals(mutableMovesMade.get(turn - 7))
				&& mutableMovesMade.get(turn - 4).equals(mutableMovesMade.get(turn - 8))
				&& mutableMovesMade.get(turn - 5).equals(mutableMovesMade.get(turn - 9))
				&& mutableMovesMade.get(turn - 6).equals(mutableMovesMade.get(turn - 10))
				&& mutableMovesMade.get(turn - 7).equals(mutableMovesMade.get(turn - 11))) {
			gameOverStatus.setValue(true);
			return true;
		} else {
			gameOverStatus.setValue(false);
			return false;
		}
	}
	
	public boolean whiteCheckMate() {
		if(whiteCheckMateStatus.calculated()){
			return whiteCheckMateStatus.getValue();
		}
		if (!whiteCheck()) {
			whiteCheckMateStatus.setValue(false);
			return false;
		}
		List<Move> potentialMoves = availableMoves();
		// TODO put king moves first
		if(!isEarlyGame()){
			potentialMoves = reorderedMovesForWhiteCheckMate(potentialMoves);
		}
		if (potentialMoves.isEmpty()) {
			whiteCheckMateStatus.setValue(true);
			return true;
		}
		for (Move move : potentialMoves) {
			try {
				makeMove(move);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!whiteCheck()) {
				undoLastMove();
				availableMoves.setValue(potentialMoves);
				whiteCheckMateStatus.setValue(false);
				return false;
			}
			undoLastMove();
		}
		whiteCheckMateStatus.setValue(true);
		return true;
	}
	// TODO
	public boolean blackCheckMate() {
		if(blackCheckMateStatus.calculated()){
			return blackCheckMateStatus.getValue();
		}
		if (!blackCheck()) {
			blackCheckMateStatus.setValue(false);
			return false;
		}
		List<Move> potentialMoves = availableMoves();
		if(!isEarlyGame()){
			potentialMoves = reorderedMovesForBlackCheckMate(potentialMoves);
		}
		if (potentialMoves.isEmpty()) {
			blackCheckMateStatus.setValue(true);
			return true;
		}
		for (Move move : potentialMoves) {
			try {
				makeMove(move);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!blackCheck()) {
				undoLastMove();
				availableMoves.setValue(potentialMoves);
				blackCheckMateStatus.setValue(false);
				return false;
			}
			undoLastMove();
		}
		blackCheckMateStatus.setValue(true);
		return true;
	}

	private List<Move> reorderedMovesForWhiteCheckMate(List<Move> availableMoves){
		List<Move> kingMoves = new LinkedList<Move>();
		List<Move> otherMoves = new LinkedList<Move>();
		for(Move move: availableMoves){
			if(move.getInitialPosition() == getBlackKingLocation()){
				kingMoves.add(move);
			}else{
				otherMoves.add(move);
			}
		}
		kingMoves.addAll(otherMoves);
		return kingMoves;
	}
	private List<Move> reorderedMovesForBlackCheckMate(List<Move> availableMoves){
		List<Move> kingMoves = new LinkedList<Move>();
		List<Move> otherMoves = new LinkedList<Move>();
		for(Move move: availableMoves){
			if(move.getInitialPosition() == getWhiteKingLocation()){
				kingMoves.add(move);
			}else{
				otherMoves.add(move);
			}
		}
		kingMoves.addAll(otherMoves);
		return kingMoves;
	}
	public Move getLastMove() {
		return mutableMovesMade.get(turn - 1);
	}

	public void undoLastMove() {
		for (BytePositionPair oldPiece : changedPieces.pop()) {
			mutableGameBoard[oldPiece.getPosition()] = oldPiece.getPiece();
			if(isKing(oldPiece.getPosition())){
				if(isWhite(oldPiece.getPosition())){
					whiteKingLocation = oldPiece.getPosition();
				}else {
					blackKingLocation = oldPiece.getPosition();
				}
			}
		}
		if (lastImportantTurn.peek() == turn - 1) {
			lastImportantTurn.pop();
		}
		gameOverStatus.setValue(false);
		whiteCheckMateStatus.setValue(false);
		blackCheckMateStatus.setValue(false);
		captureMade.pop();
		availableMoves.reset();
		mutableMovesMade.remove(turn - 2);
		kingCaptured = false;
		if (lateGameTurn.calculated() && lateGameTurn.getValue() == getTurn()) {
			lateGameTurn.setValue(0);
		}
		turn--;
	}

	public boolean captureMade() {
		return captureMade.peek();
	}

	@Override
	public boolean kingCaptured() {
		return kingCaptured;
	}

	// TODO
	public boolean isEarlyGame() {
		if (lateGameTurn.calculated()) {
			return lateGameTurn.getValue() != 0;
		}
		int whiteScore = 0;
		int blackScore = 0;
		int pawnValue = 1;
		int knightValue = 3;
		int bishopValue = 3;
		int rookValue = 5;
		int queenValue = 9;
		for (int i = 0; i < 64; i++) {
			if (!isEmpty(i)) {
				if (isWhite(i)) {
					if (isQueen(i)) {
						whiteScore += queenValue;
					} else if (isRook(i)) {
						whiteScore += rookValue;
					} else if (isBishop(i)) {
						whiteScore += bishopValue;
					} else if (isKnight(i)) {
						whiteScore += knightValue;
					} else if (isPawn(i)) {
						whiteScore += pawnValue;
					}
				} else {
					if (isQueen(i)) {
						blackScore += queenValue;
					} else if (isRook(i)) {
						blackScore += rookValue;
					} else if (isBishop(i)) {
						blackScore += bishopValue;
					} else if (isKnight(i)) {
						blackScore += knightValue;
					} else if (isPawn(i)) {
						blackScore += pawnValue;
					}
				}
			}
			if (whiteScore > 10 && blackScore > 10 && whiteScore + blackScore >= 25) {
				return true;
			}
		}
		if (whiteScore > 10 && blackScore > 10 && whiteScore + blackScore >= 25) {
			lateGameTurn.setValue(getTurn());
			return true;
		} else {
			lateGameTurn.setValue(0);
			return true;
		}

	}

	@Override
	public boolean whiteKingMoved() {
		return turnWhiteKingMoved < turn;
	}

	@Override
	public boolean blackKingMoved() {
		return turnBlackKingMoved < turn;
	}

	private byte convertToByte(Piece piece) {
		byte p = 0;
		if (piece.isEmpty()) {
			return p;
		}
		if (piece.isBlack()) {
			p++;
		}
		if (piece.isPawn()) {
			p += 2;
		} else if (piece.isKnight()) {
			p += 4;
		} else if (piece.isBishop()) {
			p += 6;
		} else if (piece.isRook()) {
			p += 8;
		} else if (piece.isQueen()) {
			p += 10;
		} else if (piece.isKing()) {
			p += 12;
		}
		return p;
	}

	@Override
	public boolean isEmpty(int position) {
		return mutableGameBoard[position] == 0;
	}

	@Override
	public boolean isWhite(int position) {
		return mutableGameBoard[position] % 2 == 0;
	}

	@Override
	public boolean isBlack(int position) {
		return mutableGameBoard[position] % 2 == 1;
	}

	@Override
	public boolean isEmpty(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] == 0;
	}

	@Override
	public boolean isWhite(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] % 2 == 0 && mutableGameBoard[getPosition(row, col)] != 0;
	}

	@Override
	public boolean isBlack(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] % 2 == 1;
	}

	@Override
	public boolean isKing(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] / 2 == 6;
	}

	@Override
	public boolean isKing(int position) {
		return mutableGameBoard[position] / 2 == 6;
	}

	@Override
	public boolean isQueen(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] / 2 == 5;
	}

	@Override
	public boolean isQueen(int position) {
		return mutableGameBoard[position] / 2 == 5;
	}

	@Override
	public boolean isRook(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] / 2 == 4;
	}

	@Override
	public boolean isRook(int position) {
		return mutableGameBoard[position] / 2 == 4;
	}

	@Override
	public boolean isBishop(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] / 2 == 3;
	}

	@Override
	public boolean isBishop(int position) {
		return mutableGameBoard[position] / 2 == 3;
	}

	@Override
	public boolean isKnight(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] / 2 == 2;
	}

	@Override
	public boolean isKnight(int position) {
		return mutableGameBoard[position] / 2 == 2;
	}

	@Override
	public boolean isPawn(int row, int col) {
		return mutableGameBoard[getPosition(row, col)] / 2 == 1;
	}

	@Override
	public boolean isPawn(int position) {
		return mutableGameBoard[position] / 2 == 1;
	}

	@Override
	public Side getSide(int position) {
		if (isEmpty(position)) {
			return Side.EMPTY;
		} else if (isWhite(position)) {
			return Side.WHITE;
		} else {
			return Side.BLACK;
		}
	}

	@Override
	public Side getSide(int row, int col) {
		if (isEmpty(getPosition(row, col))) {
			return Side.EMPTY;
		} else if (isWhite(getPosition(row, col))) {
			return Side.WHITE;
		} else {
			return Side.BLACK;
		}
	}

	@Override
	public boolean areOpponents(int row1, int column1, int row2, int column2) {
		if (isEmpty(row1, column1) || isEmpty(row2, column2)) {
			return false;
		}
		return mutableGameBoard[getPosition(row1, column1)] % 2 != mutableGameBoard[getPosition(row2, column2)] % 2;
	}

	@Override
	public boolean areSame(int row1, int column1, int row2, int column2) {
		if (isEmpty(row1, column1) || isEmpty(row2, column2)) {
			return false;
		}
		return mutableGameBoard[getPosition(row1, column1)] % 2 == mutableGameBoard[getPosition(row2, column2)] % 2;
	}

	@Override
	public Piece getPiece(int position) {
		switch (mutableGameBoard[position]) {
		case 0:
			return new Piece();
		case 2:
			return new Piece(Side.WHITE, Piece.Type.PAWN);
		case 3:
			return new Piece(Side.BLACK, Piece.Type.PAWN);
		case 4:
			return new Piece(Side.WHITE, Piece.Type.KNIGHT);
		case 5:
			return new Piece(Side.BLACK, Piece.Type.KNIGHT);
		case 6:
			return new Piece(Side.WHITE, Piece.Type.BISHOP);
		case 7:
			return new Piece(Side.BLACK, Piece.Type.BISHOP);
		case 8:
			return new Piece(Side.WHITE, Piece.Type.ROOK);
		case 9:
			return new Piece(Side.BLACK, Piece.Type.ROOK);
		case 10:
			return new Piece(Side.WHITE, Piece.Type.QUEEN);
		case 11:
			return new Piece(Side.BLACK, Piece.Type.QUEEN);
		case 12:
			return new Piece(Side.WHITE, Piece.Type.KING);
		case 13:
			return new Piece(Side.BLACK, Piece.Type.KING);
		}
		try {
			throw new Exception("nonexistentPiece");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Piece();
	}

	public List<Move> getMovesMade() {
		return mutableMovesMade;
	}

	public Integer getLastImportantMove() {
		return lastImportantTurn.peek();
	}
	public int getWhiteKingLocation() {
		return whiteKingLocation;
	}

	public int getBlackKingLocation() {
		return blackKingLocation;
	}
}
