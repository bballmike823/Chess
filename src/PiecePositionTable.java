
public class PiecePositionTable {
	int [][]values;
	public PiecePositionTable(int[][] VALUES){
		values = VALUES;
	}
	public int get(int row, int col, Side side){
		if(side == Side.WHITE){
			return values[row][col];
		}else  {
			return values[7-row][col];
		}
	}
}
