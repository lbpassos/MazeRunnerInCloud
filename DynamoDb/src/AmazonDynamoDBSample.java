/*
 * Copyright 2012-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;	

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;




/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class AmazonDynamoDBSample {

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (~/.aws/credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */
	static String[] tableNames; //names of tables dynamodb

    static AmazonDynamoDB dynamoDB;
    static DynamoDBMapper mapperDB; 
    static Map<String,String> expressionAttributesNames;
    static Map<String,AttributeValue> expressionAttributeValues;

    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.ProfilesConfigFile
     * @see com.amazonaws.ClientConfiguration
     */
    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
    	final int tableIdsMazes = 7;
    	final int tableIdsAlg = 3;
    	
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        dynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion("us-east-1")
            .build();
        mapperDB = new DynamoDBMapper(dynamoDB);
        
           
        String tableIds[][] ={
        					{"Maze50","Maze100", "Maze250", "Maze300", "Maze500", "Maze750", "Maze1000"},
        					{"ASTAR","bfs", "dfs"}
        				};
        tableNames = new String[tableIdsMazes*tableIdsAlg];
        int idx = 0;
        for(int i=0; i<tableIdsMazes; ++i) {
        	for(int j=0; j<tableIdsAlg; ++j) {
        		String tmp = tableIds[0][i] + "_" + tableIds[1][j];
        		tableNames[idx++] = tmp;	     		
        	}
        }
        
        expressionAttributesNames = new HashMap<>();
        expressionAttributesNames.put("#velocity","Velocity");
        expressionAttributesNames.put("#norm","Norm");
        
        expressionAttributeValues = new HashMap<>();
        
        
        
        
        
    }
    
    /*-1 nao existe
     * 0 nao existe MAS vel e norma existe
     * 1 ja existe
     */
    public static int searchExistPath(String tableName, int vel, int norm, RequestCoordinate rc ) {
    	expressionAttributeValues.clear();
    	expressionAttributeValues.put(":velocityValue",new AttributeValue().withN( String.valueOf(vel)) );
        expressionAttributeValues.put(":from",new AttributeValue().withN(String.valueOf(norm)));
        
        
        DynamoDBQueryExpression<Request> queryExpression = new DynamoDBQueryExpression<Request>()
                .withKeyConditionExpression("#velocity = :velocityValue and #norm = :from")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues);
        
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder().withTableNameOverride(TableNameOverride.withTableNameReplacement(tableName)).build();
        mapperDB = new DynamoDBMapper(dynamoDB, mapperConfig);
        List<Request> l = mapperDB.query(Request.class,queryExpression);
        
        int returnValue = -1; //nao existe
        for(int i=0; i<l.size(); ++i) {
        	returnValue = 0; //a velocidade e a norma existem
        	Request tmp = l.get(i);
        	Set<RequestCoordinate> tmp_set = tmp.getCoordinates();
        	Iterator iterator = tmp_set.iterator();
        	while(iterator.hasNext() ){
    			RequestCoordinate rc_tmp = (RequestCoordinate)iterator.next();
    			if(rc_tmp.equals(rc)==true) {
    				return 1; //Existe
    			}
        	}
        }
        return returnValue; //nao existe  	
    }
    
    public static List<Request> getAllFilter(String table, int vel, int from, int to) {
    	expressionAttributeValues.clear();
    	expressionAttributeValues.put(":velocityValue",new AttributeValue().withN( String.valueOf(vel)) );
        expressionAttributeValues.put(":from",new AttributeValue().withN(String.valueOf(from)));
        expressionAttributeValues.put(":to",new AttributeValue().withN(String.valueOf(to)));
        
        DynamoDBQueryExpression<Request> queryExpression = new DynamoDBQueryExpression<Request>()
                .withKeyConditionExpression("#velocity = :velocityValue and #norm BETWEEN :from AND :to")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues);
        
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder().withTableNameOverride(TableNameOverride.withTableNameReplacement(table)).build();
        mapperDB = new DynamoDBMapper(dynamoDB, mapperConfig);
        return mapperDB.query(Request.class,queryExpression);
    }

    
    public static void main(String[] args) throws Exception {
        init();
        
        //Request myEntity = new Request();
        //String tableName = .. // Get table name from a property file
        //dynamoMapper.save(myEntity, new DynamoDBMapperConfig(new TableNameOverride(tableName)));
		        
        //******************************* PUT
        Coordinate start = new Coordinate(85,5);
        Coordinate stop = new Coordinate(75,5);
        RequestCoordinate rc = new RequestCoordinate(start, stop);
        
        Request r = new Request(rc, 8, 154); 
        //Ver se já lá está
        int val = searchExistPath(tableNames[0], r.getVelocity(), r.getNorm(), rc );
        System.out.println(val);
        if( val==-1 ) { //nao existe
        	//System.out.println("HERE");
        	mapperDB.save(r, new DynamoDBMapperConfig( new TableNameOverride(tableNames[0]) ) );
        	//mapperDB.save(r);
        }
        else
        	if(val==0) { //inserir no meio
        		//System.out.println("HERE");
        		List<Request> lists = getAllFilter(tableNames[0], r.getVelocity(), r.getNorm(), r.getNorm());
        		mapperDB.delete(lists.get(0));
        		lists.get(0).addPoint(rc, 15);
        		mapperDB.save(lists.get(0), new DynamoDBMapperConfig( new TableNameOverride(tableNames[0]) ) );
        	}
        //1 existe. nao fazer nada	
        
        //Procurar similaridades
        // norma do vector e metrica mais alta
        if(val==-1) {
        	List<Request> lists = getAllFilter(tableNames[0], r.getVelocity(), r.getNorm()-5, r.getNorm()+5);
        	int normMeasure = Integer.MAX_VALUE;
        	int assumeCost=-1;
        	int myNorm = r.getNorm();
        	
        	//procurar a norma mais pequena
        	for(int i=0; i<lists.size(); ++i) {
        		int tmp = Math.abs( lists.get(i).getNorm()-myNorm );
        		if(  tmp<normMeasure) {
        			normMeasure = tmp;
        			assumeCost = i;
        		}
        	}
        	
        	Request moreSimilar = lists.get(assumeCost);
        	Set<RequestCoordinate> rqcoordinates = moreSimilar.getCoordinates();
        	Iterator iterator = rqcoordinates.iterator();
        	int highestMetric = 0;
        	while(iterator.hasNext() ){
    			RequestCoordinate rc_tmp = (RequestCoordinate)iterator.next();
    			int tmp = moreSimilar.getMetric(rc_tmp);
    			if(tmp>highestMetric) {
    				highestMetric = tmp;
    			}
        	}
        		
        		
        }
        
        
        
        
        //mapperDB.save(r, new DynamoDBMapperConfig( new TableNameOverride(tableNames[0]) ) );
        
        //setTable(tableNames[0]);
        //mapperDB.save(r);
        
        
        //System.out.println(r.getPointAndMaze());
        /*System.out.println(r.getAlgoAndMetric());
        
        start = new Coordinate(25,6);
        stop = new Coordinate(15,5);
        rc = new RequestCoordinate(start, stop);
        r.addPoint(rc, 1000);
        String teste = r.getAlgoAndMetric();
        System.out.println(teste);
        */
        
        /*ObjectMapper mapper = new ObjectMapper();
        Map<RequestCoordinate, Integer> map = new HashMap<RequestCoordinate, Integer>();
        JsonNode actualObj = mapper.readTree(teste);
        
        for(int i=0; i<actualObj.size(); ++i) {
        	JsonNode tmp = actualObj.get(i);
        	
        	//Coordinate t = 
        	int t1 = Integer.parseInt(tmp.get("start").get("x").toString());
        	int t2 = Integer.parseInt(tmp.get("start").get("y").toString());
        	Coordinate Retrieved_start = new Coordinate( t1, t2 );
        	t1 = Integer.parseInt(tmp.get("stop").get("x").toString());
        	t2 = Integer.parseInt(tmp.get("stop").get("y").toString());
        	Coordinate Retrieved_stop = new Coordinate( t1, t2 );
        	int metric = Integer.parseInt(tmp.get("metric").toString());
        	
        	RequestCoordinate Retrieved_rc = new RequestCoordinate(Retrieved_start, Retrieved_stop);
        	map.put(Retrieved_rc, metric);
        	
        }*/
        //map = mapper.readValue(teste, new TypeReference<Map<RequestCoordinate, Integer>>(){});
		
        //System.out.println("MAP: " + map.get(rc));
        
        
        
        
        
        
        //Search
        int vel = 10; 
        int from = 9;
        int to = 11;
        
        
  
        /*Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":velocityValue",new AttributeValue().withN( String.valueOf(vel)) );
        expressionAttributeValues.put(":from",new AttributeValue().withN(String.valueOf(from)));
        expressionAttributeValues.put(":to",new AttributeValue().withN(String.valueOf(to)));
  
        DynamoDBQueryExpression<Request> queryExpression = new DynamoDBQueryExpression<Request>()
                .withKeyConditionExpression("#velocity = :velocityValue and #norm BETWEEN :from AND :to")
                .withExpressionAttributeNames(expressionAttributesNames)
                .withExpressionAttributeValues(expressionAttributeValues);
  */
        //mapperDB.save(new Request(), new DynamoDBMapperConfig( new TableNameOverride(tableNames[0]) ) );
        /*DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder().withTableNameOverride(TableNameOverride.withTableNameReplacement(tableNames[0]))
                .build();
        mapperDB = new DynamoDBMapper(dynamoDB, mapperConfig);
        List<Request> l = mapperDB.query(Request.class,queryExpression);
        
        System.out.println(l.size());
        
        for(int i=0; i<l.size(); ++i) {
        	Request tmp = l.get(i);
        	System.out.println(tmp.getNorm() + " " + tmp.getVelocity() + " " + tmp.getCoordinates());
        }*/
        
        
        
        
        
        
        
        //mapperDB.save(r);	
        
        /*r = new Request(rc, "bfs", 1, 154);
        mapperDB.save(r);
        
        r = new Request(rc, "afs", 2, 154);
        mapperDB.save(r);
        
        r = new Request(rc, "dfs", 2, 154);
        mapperDB.save(r);
        
        r = new Request(rc, "afs", 25, 154);
        mapperDB.save(r);*/
        
        //r = new Request(rc, "afs", 10, 154);
        //mapperDB.save(r);
        
        
        //Query
        //Request r = mapperDB.query(Request.class,"afs",2);
        
        //System.out.println( r.getMetric() );
        
        
        
        
        
        
   }
	

}
