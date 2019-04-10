import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AiPlayer extends Player {
	protected Random randomGenerator;
	protected int depthOfSearch;
	private int maxDepth;
	private PiecePositionTable pawnPositionValues;
	private PiecePositionTable knightPositionValues;
	private PiecePositionTable bishopPositionValues;
	private PiecePositionTable rookPositionValues;
	private PiecePositionTable queenPositionValues;
	private PiecePositionTable kingEarlyPositionValues;
	private PiecePositionTable kingLatePositionValues;
	private TranspositionTable transpositionTable;

	public AiPlayer(Side SIDE) throws Exception {
		super(SIDE);
		depthOfSearch = 5;
		maxDepth = 5;
		randomGenerator = new Random();
		transpositionTable = new TranspositionTable();
		initializePositionTables();
	}

	public AiPlayer(Side SIDE, int DEPTH) throws Exception {
		super(SIDE);
		randomGenerator = new Random();
		depthOfSearch = DEPTH;
		maxDepth = DEPTH;
		transpositionTable = new TranspositionTable();
		initializePositionTables();
	}

	public AiPlayer(Side SIDE, int DEPTH, int MAXDEPTH) throws Exception {
		super(SIDE);
		randomGenerator = new Random();
		depthOfSearch = DEPTH;
		maxDepth = MAXDEPTH;
		transpositionTable = new TranspositionTable();
		initializePositionTables();
	}

	public void initializePositionTables() {
		int[][] pawnValues = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 50, 50, 50, 50, 50, 50, 50, 50 },
				{ 10, 10, 20, 30, 30, 20, 10, 10 }, { 5, 5, 10, 25, 25, 10, 5, 5 }, { 0, 0, 0, 20, 20, 0, 0, 0 },
				{ 5, -5, -10, 0, 0, -10, -5, 5 }, { 5, 10, 10, -20, -20, 10, 10, 5 }, { 0, 0, 0, 0, 0, 0, 0, 0 } };
		pawnPositionValues = new PiecePositionTable(pawnValues);

		int[][] knightValues = { { -50, -40, -30, -30, -30, -30, -40, -50 }, { -40, -20, 0, 0, 0, 0, -20, -40 },
				{ -30, 0, 10, 15, 15, 10, 0, -30 }, { -30, 5, 15, 20, 20, 15, 5, -30 },
				{ -30, 0, 15, 20, 20, 15, 0, -30 }, { -30, 5, 10, 15, 15, 10, 5, -30 },
				{ -40, -20, 0, 5, 5, 0, -20, -40 }, { -50, -40, -30, -30, -30, -30, -40, -50 } };
		knightPositionValues = new PiecePositionTable(knightValues);

		int[][] bishopValues = { { -20, -10, -10, -10, -10, -10, -10, -20 }, { -10, 0, 0, 0, 0, 0, 0, -10 },
				{ -10, 0, 5, 10, 10, 5, 0, -10 }, { -10, 5, 5, 10, 10, 5, 5, -10 }, { -10, 0, 10, 10, 10, 10, 0, -10 },
				{ -10, 10, 10, 10, 10, 10, 10, -10 }, { -10, 5, 0, 0, 0, 0, 5, -10 },
				{ -20, -10, -10, -10, -10, -10, -10, -20 } };
		bishopPositionValues = new PiecePositionTable(bishopValues);

		int[][] rookValues = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 5, 10, 10, 10, 10, 10, 10, 5 },
				{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 },
				{ -5, 0, 0, 0, 0, 0, 0, -5 }, { -5, 0, 0, 0, 0, 0, 0, -5 }, { 0, 0, 0, 10, 10,10, 0, 0 } };
		rookPositionValues = new PiecePositionTable(rookValues);

		int[][] queenValues = { { -20, -10, -10, -5, -5, -10, -10, -20 }, { -10, 0, 0, 0, 0, 0, 0, -10 },
				{ -10, 0, 5, 5, 5, 5, 0, -10 }, { -5, 0, 5, 5, 5, 5, 0, -5 }, { 0, 0, 5, 5, 5, 5, 0, -5 },
				{ -10, 5, 5, 5, 5, 5, 0, -10 }, { -10, 0, 5, 0, 0, 0, 0, -10 },
				{ -20, -10, -10, -5, -5, -10, -10, -20 } };
		queenPositionValues = new PiecePositionTable(queenValues);

		int[][] kingEarlyValues = { { -30, -40, -40, -50, -50, -40, -40, -30 },
				{ -30, -40, -40, -50, -50, -40, -40, -30 }, { -30, -40, -40, -50, -50, -40, -40, -30 },
				{ -30, -40, -40, -50, -50, -40, -40, -30 }, { -20, -30, -30, -40, -40, -30, -30, -20 },
				{ -10, -20, -20, -20, -20, -20, -20, -10 }, { 20, 20, 0, 0, 0, 0, 20, 20 },
				{ 20, 30, 10, 0, 0, 10, 30, 20 } };
		kingEarlyPositionValues = new PiecePositionTable(kingEarlyValues);

		int[][] kingLateValues = { { -50, -40, -30, -20, -20, -30, -40, -50 }, { -30, -20, -10, 0, 0, -10, -20, -30 },
				{ -30, -10, 20, 30, 30, 20, -10, -30 }, { -30, -10, 30, 40, 40, 30, -10, -30 },
				{ -30, -10, 30, 40, 40, 30, -10, -30 }, { -30, -10, 20, 30, 30, 20, -10, -30 },
				{ -30, -30, 0, 0, 0, 0, -30, -30 }, { -50, -30, -30, -30, -30, -30, -30, -50 } };
		kingLatePositionValues = new PiecePositionTable(kingLateValues);
	}

	public Move getMove() throws Exception {
		MutableBoard b = new MutableBoard(board);
		IntegerMoveScorePair bestMove = miniMax(depthOfSearch, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, b,
				maxDepth - depthOfSearch);
		return bestMove.getMove();
	}

	private IntegerMoveScorePair miniMax(int depth, double alpha, double beta, MutableBoard b, int deepened)
			throws Exception {
		boolean isCurrentPlayer = b.currentPlayer().getSide() == side;
		int bestScore = isCurrentPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		Move bestMove = null;
		if (depth == 0 || b.gameOver()) {
			bestScore = evaluate(b);
		} else {
			List<Move> availableMoves = b.availableMoves();
			Collections.shuffle(availableMoves, randomGenerator);
			availableMoves = reorderedMoves(availableMoves, b);
			for (Move move : availableMoves) {
				if(b.gameOver()){
					throw new Exception("cant make move if game is over");
				}
				b.makeMove(move);
				int score;
				if (deepened != 0){
					if(b.captureMade() && depth == 1) {
					score = miniMax(1, alpha, beta, b, deepened - 1).getScore();
					} else if(!b.isEarlyGame()&& depth == 1) {
					score = miniMax(1, alpha, beta, b, deepened - 1).getScore();
					} else {
						score = miniMax(depth - 1, alpha, beta, b, deepened).getScore();
					}
				} else {
					score = miniMax(depth - 1, alpha, beta, b, deepened).getScore();
				}
				b.undoLastMove();

				if (isCurrentPlayer) {
					if (score > bestScore) {
						bestScore = score;
						bestMove = move;
					}
					if (bestScore > alpha) {
						alpha = bestScore;
					}
				} else {
					if (score < bestScore) {
						bestScore = score;
						bestMove = move;
					}
					if (bestScore < beta) {
						beta = bestScore;
					}
				}
				if (alpha >= beta) {
					break;
				}
			}
		}
		return new IntegerMoveScorePair(bestMove, bestScore);
	}

	protected List<Move> reorderedMoves(List<Move> availableMoves, MutableBoard b) {
		List<Move> topPriority = new ArrayList<Move>();
		List<Move> mediumPriority = new ArrayList<Move>();
		List<Move> lowPriority = new ArrayList<Move>();
		List<Move> reOrdered = new ArrayList<Move>();
		for (Move i : availableMoves) {
			if (isHighPriority(i, b)) {
				topPriority.add(i);
			} else if (isMediumPriority(i, b)) {
				mediumPriority.add(i);
			} else {
				lowPriority.add(i);
			}
		}
		reOrdered.addAll(topPriority);
		reOrdered.addAll(mediumPriority);
		reOrdered.addAll(lowPriority);
		return reOrdered;
	}

	protected boolean isMediumPriority(Move move, MutableBoard b) {
		return !b.isEmpty(move.getFinalPosition());
	}

	protected boolean isHighPriority(Move move, MutableBoard b) {
		return b.isKing(move.getFinalPosition());
	}

	// this is shit
	protected int evaluate(MutableBoard evalBoard) {
//		}
		if (evalBoard.gameOver()) {
			int score = 0;
			if (evalBoard.whiteCheckMate()) {
				score = 1000000000 - evalBoard.getTurn();
			} else if (evalBoard.blackCheckMate()) {
				score = evalBoard.getTurn() - 1000000000;
			} else {
				return 0;
			}
			return getSide() == Side.WHITE ? score : -1 * score;
		}
		int material = getMaterialScore(evalBoard);
		int passedPawns = passedPawns(evalBoard);
		int kingMobility = whiteKingMobility(evalBoard) - blackKingMobility(evalBoard);
		int piecePositionScore = getPiecePositionScore(evalBoard);
		int materialWeight = 0;
		int passedPawnWeight = 0;
		int kingMobilityWeight = 0;
		int piecePositionWeight = 0;
		if (evalBoard.isEarlyGame()) {
			materialWeight = 100;
			passedPawnWeight = 10;
			kingMobilityWeight = 0;
			piecePositionWeight = 1;
		} else {
			materialWeight = 100;
			passedPawnWeight = 0;
			kingMobilityWeight = 1;
			piecePositionWeight = 1;
		}
		int score = 0;
		score += material * materialWeight;
		score += passedPawns * passedPawnWeight;
		score += kingMobility * kingMobilityWeight;
		score += piecePositionScore * piecePositionWeight;
		return getSide() == Side.WHITE ? score : -1 * score;
	}

	protected int getMaterialScore(MutableBoard evalBoard) {
		int score = 0;
		int pawnValue = 1;
		int knightValue = 3;
		int bishopValue = 3;
		int rookValue = 5;
		int queenValue = 9;
		int kingValue = 10000000;
		for (int i = 0; i < 64; i++) {
			if (evalBoard.isBlack(i)) {
				if (evalBoard.isKing(i)) {
					score -= kingValue;
				} else if (evalBoard.isQueen(i)) {
					score -= queenValue;
				} else if (evalBoard.isRook(i)) {
					score -= rookValue;
				} else if (evalBoard.isBishop(i)) {
					score -= bishopValue;
				} else if (evalBoard.isKnight(i)) {
					score -= knightValue;
				} else if (evalBoard.isPawn(i)) {
					score -= pawnValue;
				}
			} else if (evalBoard.isWhite(i)) {
				if (evalBoard.isKing(i)) {
					score += kingValue;
				} else if (evalBoard.isQueen(i)) {
					score += queenValue;
				} else if (evalBoard.isRook(i)) {
					score += rookValue;
				} else if (evalBoard.isBishop(i)) {
					score += bishopValue;
				} else if (evalBoard.isKnight(i)) {
					score += knightValue;
				} else if (evalBoard.isPawn(i)) {
					score += pawnValue;
				}
			}
		}
		return score;
	}

	private int getPiecePositionScore(MutableBoard evalBoard) {
		int score = 0;
		for (int row = 0; row < Board.getSize(); row++) {
			for (int col = 0; col < Board.getSize(); col++) {
				if (!evalBoard.isEmpty(row, col)) {
					Piece p = evalBoard.getPiece(Board.getPosition(row, col));
					int positionScore = 0;
					if (p.isPawn()) {
						positionScore += pawnPositionValues.get(row, col, p.getSide());
					} else if (p.isKnight()) {
						positionScore += knightPositionValues.get(row, col, p.getSide());
					} else if (p.isBishop()) {
						positionScore += bishopPositionValues.get(row, col, p.getSide());
					} else if (p.isRook()) {
						if (evalBoard.isEarlyGame()) {
							positionScore += rookPositionValues.get(row, col, p.getSide());
						} else {
							positionScore += kingLatePositionValues.get(row, col, p.getSide());
						}
					} else if (p.isQueen()) {
						if (evalBoard.isEarlyGame()) {
							positionScore += queenPositionValues.get(row, col, p.getSide());
						} else {
							positionScore += kingLatePositionValues.get(row, col, p.getSide());
						}
					} else if (p.isKing()) {
						if (evalBoard.isEarlyGame()) {
							positionScore += kingEarlyPositionValues.get(row, col, p.getSide());
						} else {
							positionScore += kingLatePositionValues.get(row, col, p.getSide());
						}
					}
					score += p.isWhite() ? positionScore : -1 * positionScore;
				}
			}
		}
		return score;
	}

	private int passedPawns(MutableBoard evalBoard) {
		int score = 0;
		for (int row = 0; row < Board.getSize(); row++) {
			for (int col = 0; col < Board.getSize(); col++) {
				Piece p = evalBoard.getPiece(Board.getPosition(row, col));
				if (p.isPawn()) {
					if (p.isWhite()) {
						boolean passedPawn = true;
						for (int newRow = row - 1; newRow > 0; newRow--) {
							if (evalBoard.isPawn(newRow, col)) {
								passedPawn = false;
								break;
							}
						}
						if (passedPawn) {
							score++;
						}
					} else {
						boolean passedPawn = true;
						for (int newRow = row + 1; newRow < 7; newRow++) {
							if (evalBoard.isPawn(newRow, col)) {
								passedPawn = false;
								break;
							}
						}
						if (passedPawn) {
							score--;
						}
					}
				}
			}
		}
		return score;
	}

	protected int whiteKingMobility(MutableBoard evalBoard) {
		int score = 0;
		int row = Board.getRow(evalBoard.getWhiteKingLocation());
		int col = Board.getColumn(evalBoard.getWhiteKingLocation());
		int newRow = row;
		int newCol = col + 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row;
		newCol = col - 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row + 1;
		newCol = col;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row - 1;
		newCol = col;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row + 1;
		newCol = col + 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row - 1;
		newCol = col + 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row + 1;
		newCol = col - 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row - 1;
		newCol = col - 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isWhite(newRow, newCol)
				&& !evalBoard.blackThreatens(newRow, newCol)) {
			score++;
		}
		return 64-(8-score)*(8-score);
	}
	
	protected int blackKingMobility(MutableBoard evalBoard) {
		int score = 0;
		int row = Board.getRow(evalBoard.getBlackKingLocation());
		int col = Board.getColumn(evalBoard.getBlackKingLocation());
		int newRow = row;
		int newCol = col + 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row;
		newCol = col - 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row + 1;
		newCol = col;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row - 1;
		newCol = col;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row + 1;
		newCol = col + 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row - 1;
		newCol = col + 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row + 1;
		newCol = col - 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		newRow = row - 1;
		newCol = col - 1;
		if (evalBoard.isInBounds(newRow, newCol) && !evalBoard.isBlack(newRow, newCol)
				&& !evalBoard.whiteThreatens(newRow, newCol)) {
			score++;
		}
		return 64-(8-score)*(8-score);
	}

	protected int manhattanDistance(int position1, int position2) {
		return Math.abs(Board.getRow(position1) - Board.getColumn(position2))
				+ Math.abs(Board.getColumn(position1) - Board.getRow(position2));
	}
}
