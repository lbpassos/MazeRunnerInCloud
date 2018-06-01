package proxyserver_cnv.DynamoDB;

public class RequestCoordinate {
	private Coordinate start;
	private Coordinate end;
	
	public RequestCoordinate(Coordinate s, Coordinate e) {
		this.start = s;
		this.end = e;
	}
	
	
	public Coordinate getStart() { return start; }
	
	public Coordinate getStop() { return end; }
	
	public double getNorm() {
		return Math.sqrt( (start.getX()-end.getX())*(start.getX()-end.getX()) + (start.getY()-end.getY())*(start.getY()-end.getY()) );
	}
	
		
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof RequestCoordinate))return false;
	    RequestCoordinate otherMyClass = (RequestCoordinate)other;
	    if( start.equals(otherMyClass.getStart()) && end.equals(otherMyClass.getStop()) ){
	    	return true;
	    }
	    return false;    
	}
	
	@Override
	public int hashCode() {
		return start.hashCode() + end.hashCode();
	}
}
