
public class Main {
	public static void main(String[] args){
		Timer timer = new Timer();
		timer.start();
		try {
			playGames(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer.stop();
		System.out.println("Total Time: "+timer.getTime());
		System.exit(0);
	}
	public static void playGames(int numberOfGames) throws Exception{
		int whiteWins = 0;
		int blackWins = 0;
		int ties = 0;
		int decidedOnTurns = 0;
		long player1Time = 0;
		long player2Time = 0;
		for(int i = 0; i < numberOfGames; i++){
			System.out.println(i+1);
			Player player1 = new  AiPlayer(Side.WHITE,4);
			Player player2 = new SimplePointsAI(Side.BLACK,4);
			Game game = new Game(player1,player2);
			game.play();
			player1Time += game.getPlayer1Time();
			player2Time += game.getPlayer2Time();
			if(game.whiteWins()){
				whiteWins++;
			} else if (game.blackWins()){
				blackWins++;
			} else {
				ties++;
			}
			if(game.endedOnTurns()){
				decidedOnTurns++;
			}
			System.out.println(percent(whiteWins,blackWins,ties)+"%");
			System.out.println("White time: "+player1Time);
			System.out.println("Black time: "+player2Time);
		}
		System.out.println("WHITE: "+whiteWins);
		System.out.println("BLACK: "+blackWins);
		System.out.println("TIES:  "+ties);
		System.out.println("ENDED BY TURNS: "+decidedOnTurns);
	}
	private static int percent(int win, int loss,int tie){
		return (100*win +50* tie)/(win+loss+tie);
	}
}
