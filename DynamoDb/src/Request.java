
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;


@DynamoDBTable(tableName = "TestTable_v1")
public class Request {
	private RequestCoordinate coordinate;
	private String mazeSize;
	private String metric;
	private String algo;
	private String velocity;
	
	public Request(RequestCoordinate coordinate, String mazeSize, String algo, String vel, String metric) {
		this.coordinate = coordinate;
		this.mazeSize = mazeSize;
		this.metric = metric;
		this.algo = algo;
		this.velocity = vel;
		
	}
	
	@DynamoDBHashKey(attributeName="Id") 
    public long getId() { return coordinate.hashCode() + mazeSize.hashCode() + velocity.hashCode() + algo.hashCode();}
	
	@DynamoDBAttribute(attributeName = "Xi")
    public String getXi() { return String.valueOf( coordinate.getStart().getX() ); }
	
	@DynamoDBAttribute(attributeName = "Yi")
    public String getYi() { return String.valueOf( coordinate.getStart().getY() );  }
	
	@DynamoDBAttribute(attributeName = "Xf")
    public String getXf() { return String.valueOf( coordinate.getStop().getX() );  }
	
	@DynamoDBAttribute(attributeName = "Yf")
    public String getYf() { return String.valueOf( coordinate.getStart().getY() );  }
	
	@DynamoDBAttribute(attributeName = "mazeSize")
    public String getMazeSize() { return mazeSize; }
	
    @DynamoDBAttribute(attributeName = "velocity")
	public String getVelocity() { return velocity; }
    
    @DynamoDBAttribute(attributeName = "algorithm")
	public String getAlg() { return algo; }
	
    @DynamoDBAttribute(attributeName = "metric")
	public String getMetric() { return metric; }
    
    
	
}
