package pt.ulisboa.DynamoDB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

//
//
@DynamoDBTable(tableName = "Maze50_astar")
public class Request {
	private Map<RequestCoordinate, Long> map; //requestedcoordinate - metric
	private int norm;
	private int velocity;
	//private int metric;
	
	
	public Request() {
		map = new HashMap<RequestCoordinate, Long>();
	}
	
	public Request(RequestCoordinate coordinate, int vel, long metric) {
		
		map = new HashMap<RequestCoordinate, Long>();
		map.put(coordinate, metric);
		this.velocity = vel;
		
		Double d = coordinate.getNorm();
		this.norm = d.intValue();
		//this.metric = metric;
	}
	
	
	@DynamoDBHashKey(attributeName="Velocity")
    public int getVelocity() { return velocity;}
	public void setVelocity(int Velocity) { this.velocity = Velocity; }
	
	@DynamoDBRangeKey(attributeName="Norm") 
    public int getNorm() {
		return norm;
	}
	public void setNorm(int Norm) { this.norm = Norm; }
		
	
	
	
	/*
	{
		  "coord and metric":[
		    {"start":{"x":"xx", "y":"yy"},
		      "stop":{"x":"xx", "y":"yy"},
		     "metric":"XX"
		    }
		  ]
	}
	*/
	@DynamoDBAttribute(attributeName = "Coordinate and Metric")
	public String getAlgoAndMetric() {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		
		Set<RequestCoordinate> s = map.keySet();
		ObjectNode[] objectNode_start = new ObjectNode[s.size()];
		ObjectNode[] objectNode_stop = new ObjectNode[s.size()];
		ObjectNode[] objectNode_full = new ObjectNode[s.size()];
		
		
		Iterator iterator = s.iterator();
		int i = 0;
		while(iterator.hasNext() ){
			RequestCoordinate rc = (RequestCoordinate)iterator.next();
			
			objectNode_start[i] = mapper.createObjectNode();
			objectNode_start[i].put("x", rc.getStart().getX());
			objectNode_start[i].put("y", rc.getStart().getY());
			
			objectNode_stop[i] = mapper.createObjectNode();
			objectNode_stop[i].put("x", rc.getStop().getX());
			objectNode_stop[i].put("y", rc.getStop().getY());
			
			objectNode_full[i] = mapper.createObjectNode();
			objectNode_full[i].put("start", objectNode_start[i]);
			objectNode_full[i].put("stop", objectNode_stop[i]);
			objectNode_full[i].put("metric", map.get(rc));
			
			arrayNode.add(  objectNode_full[i] );
			++i;			
		}
		return arrayNode.toString();
	}
	public void setAlgoAndMetric(String s) {
		try {
			ObjectMapper mapper = new ObjectMapper();
	        JsonNode actualObj = mapper.readTree(s);
	        
	        for(int i=0; i<actualObj.size(); ++i) {
	        	JsonNode tmp = actualObj.get(i);
	        	
	        	int t1 = Integer.parseInt(tmp.get("start").get("x").toString());
	        	int t2 = Integer.parseInt(tmp.get("start").get("y").toString());
	        	Coordinate Retrieved_start = new Coordinate( t1, t2 );
	        	
	        	t1 = Integer.parseInt(tmp.get("stop").get("x").toString());
	        	t2 = Integer.parseInt(tmp.get("stop").get("y").toString());
	        	Coordinate Retrieved_stop = new Coordinate( t1, t2 );
	        	long metric = Integer.parseInt(tmp.get("metric").toString());
	        	
	        	RequestCoordinate Retrieved_rc = new RequestCoordinate(Retrieved_start, Retrieved_stop);
	        	map.put(Retrieved_rc, metric);
	        }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//List of paths
	@DynamoDBIgnore
	public Set<RequestCoordinate> getCoordinates() {
		 return map.keySet();
	}
	
	//Mteric for a specific path
	public long getMetric(RequestCoordinate rc) {
		 return map.get(rc);
	}	
	
	//Add Point
	public void addPoint(RequestCoordinate rc, long metr) {
		if(map.containsKey(rc)==false) {
			map.put(rc, metr);
		}
	}
	
	
}
