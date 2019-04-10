
public class BoardValuePair implements Comparable{
	private byte board[];
	private int score;

	public BoardValuePair(byte[] BOARD, int SCORE) {
		score = SCORE;
		board = BOARD.clone();
	}
	public boolean equals(BoardValuePair b){
		for(int i = 0; i < 64; i++){
			if(board[i]!=b.getByteAt(i)){
				return false;
			}
		}
		return true;
	}
	public byte getByteAt(int i){
		return board[i];
	}
	public int compareTo(BoardValuePair b){
		for(int i = 0; i < 64; i++){
			if(board[i]<b.getByteAt(i)){
				return -1;
			} else if(board[i]>b.getByteAt(i)){
				return 1;
			}
		}
		return 0;
	}
	public int getScore(){
		return score;
	}
	public void setScore(int SCORE){
		score =  SCORE;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
