import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;


@DynamoDBTable(tableName = "TestTable")
public class Requests {
	private Integer id;  //partition key
    private Pictures pictures;
    /* ...other attributes omitted... */
    
    @DynamoDBHashKey(attributeName="Id")  
    public Integer getId() { return id;}
    public void setId(Integer id) {this.id = id;}
        
    @DynamoDBAttribute(attributeName="Pictures")  
    public Pictures getPictures() { return pictures;}
    public void setPictures(Pictures pictures) {this.pictures = pictures;}
    
    // Additional properties go here. 
  

}
