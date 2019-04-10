import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TranspositionTable {
	private List<BoardValuePair> table;
	private boolean sorted;
	public TranspositionTable(){
		table = new ArrayList<BoardValuePair>();
		sorted = false;
	}
	public void put(BoardValuePair boardValue){
		table.add(boardValue);
		sorted = false;
	}
	public boolean contains(BoardValuePair boardValue){
		if(!sorted){
			Collections.sort(table);
		}
		int low = 0;
		int high = table.size() - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			if (boardValue.compareTo(table.get(mid)) == -1) {
				high = mid - 1;
			} else if (boardValue.compareTo(table.get(mid)) == 1) {
				low = mid + 1;
			} else {
				return true;
			}
		}
		return false;
	}
	public int getScore(BoardValuePair boardValue){
		int low = 0;
		int high = table.size() - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			if (boardValue.compareTo(table.get(mid)) == -1) {
				high = mid - 1;
			} else if (boardValue.compareTo(table.get(mid)) == 1) {
				low = mid + 1;
			} else {
				return table.get(mid).getScore();
			}
		}
		return 0;
	}
}
