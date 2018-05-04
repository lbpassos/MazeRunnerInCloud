//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;


//@DynamoDBDocument
public class RequestCoordinate {
	private Coordinate start;
	private Coordinate end;
	
	public RequestCoordinate(Coordinate s, Coordinate e) {
		this.start = s;
		this.end = e;
	}
	
	
	//@DynamoDBAttribute(attributeName = "start")
	public Coordinate getStart() { return start; }
	//@DynamoDBAttribute(attributeName = "stop")
	public Coordinate getStop() { return end; }
	
	@Override
	public int hashCode() {
		return start.hashCode() + end.hashCode();
	}
	
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof RequestCoordinate))return false;
	    RequestCoordinate otherMyClass = (RequestCoordinate)other;
	    
	    if( start.equals(otherMyClass.getStart()) && end.equals(otherMyClass.getStop()) ) {
	    	return true;
	    }
	    return false;
	}

}
