
public enum Side {
	WHITE,BLACK,EMPTY;
	public boolean isEmpty(){
		return this.equals(EMPTY);
	}
	public boolean isWhite(){
		return this.equals(WHITE);
	}
	public boolean isBlack(){
		return this.equals(BLACK);
	}
	public Side opponent(){
		if(isEmpty()){
			return EMPTY;
		} else if(isWhite()){
			return BLACK;
		} else{
			return WHITE;
		}
	}
}
