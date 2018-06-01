package proxyserver_cnv.DynamoDB;

public class Coordinate {
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Coordinate))return false;
	    Coordinate otherMyClass = (Coordinate)other;
	    if(x==otherMyClass.getX() && y==otherMyClass.getY()) {
	    	return true;
	    }
	    return false;    
	}
	
	@Override
	public int hashCode() {
		return x + y;
	}
}
