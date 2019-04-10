import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimplePointsAI extends AiPlayer {

	public SimplePointsAI(Side SIDE) throws Exception {
		super(SIDE);
	}

	public SimplePointsAI(Side SIDE, int DEPTH) throws Exception {
		super(SIDE, DEPTH);
	}

	public SimplePointsAI(Side SIDE, int DEPTH, int MAXDEPTH) throws Exception {
		super(SIDE, DEPTH, MAXDEPTH);
	}
	@Override
	public Move getMove() throws Exception {
		MutableBoard b = new MutableBoard(board);
		IntegerMoveScorePair bestMove = miniMax(depthOfSearch, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, b);
		// System.out.println(side.toString()+" turn "+b.getTurn()+" board value
		// "+evaluate(b)+" move score "+bestMove.getScore());
		return bestMove.getMove();
	}

	private IntegerMoveScorePair miniMax(int depth, double alpha, double beta, MutableBoard b)
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
			if (availableMoves.isEmpty()) {
				bestScore = miniMax(depth - 1, alpha, beta, b).getScore();
			}
			for (Move move : availableMoves) {
				b.makeMove(move);
				int score;
				score = miniMax(depth - 1, alpha, beta, b).getScore();
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
	@Override
	protected int evaluate(MutableBoard evalBoard) {
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
		return getSide() == side.WHITE ? getMaterialScore(evalBoard) : -1 * getMaterialScore(evalBoard);
	}
	@Override
	protected List<Move> reorderedMoves(List<Move> availableMoves, MutableBoard b) {
		List<Move> topPriority = new ArrayList<Move>();
		List<Move> lowPriority = new ArrayList<Move>();
		List<Move> reOrdered = new ArrayList<Move>();
		for (Move i : availableMoves) {
			if (isHighPriority(i, b)) {
				topPriority.add(i);
			} else {
				lowPriority.add(i);
			}
		}
		reOrdered.addAll(topPriority);
		reOrdered.addAll(lowPriority);
		return reOrdered;
	}
	@Override
	protected boolean isHighPriority(Move move, MutableBoard b) {
		return !b.isEmpty(move.getFinalPosition());
	}
}
