import java.util.Scanner;

public class Game {
	private Player player1;
	private Player player2;
	private Board board;
	private int seed = 1;
	private Timer timer;
	private long player1Time;
	private long player2Time;

	public Game() throws Exception {
		player1 = new Player(Side.WHITE);
		player2 = new Player(Side.BLACK);
		board = new GUIBoard(player1, player2);
		player1.setBoard(board);
		player2.setBoard(board);
		timer = new Timer();
		player1Time = 0;
		player2Time = 0;
	}
	public Game(Player p1,Player p2) throws Exception {
		player1 = p1;
		player2 = p2;
		board = new GUIBoard(player1, player2);
		player1.setBoard(board);
		player2.setBoard(board);
		timer = new Timer();
		player1Time = 0;
		player2Time = 0;
	}

	public void play() {
		while (!board.gameOver()) {
			boolean isPlayer1 = board.currentPlayer().equals(player1);
			timer.start();
			try {
				board.nextMove();
			} catch (Exception e) {
				System.err.println("cannot make next move");
				e.printStackTrace();
				System.exit(1);
			}
			timer.stop();
			if (isPlayer1) {
				player1Time += timer.getTime();
			} else {
				player2Time += timer.getTime();
			}

		}
		if (board.tie()) {
			System.out.println("TIE");
		} else {
			System.out.println(board.winner() + " WINS");
		}
		board.close();
	}

	public long getPlayer1Time() {
		return player1Time;
	}

	public long getPlayer2Time() {
		return player2Time;
	}

	public boolean whiteWins(){
		return board.whiteWins();
	}
	public boolean blackWins(){
		return board.blackWins();
	}
	public boolean endedOnTurns(){
		return board.getTurn() - board.turnOfLastCaptureOrAdvance > 100;
	}
}
