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
package proxyserver_cnv.DynamoDB;

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
public class AmazonDynamoDBModule {

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
    public static void init() throws Exception {
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
        					{"astar","bfs", "dfs"}
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
    private static synchronized int searchExistPath(String tableName, int vel, int norm, RequestCoordinate rc ) {
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
    
    //Get all paths in table with velocity vel and norm in the interval from <= norm <= to
    private static synchronized List<Request> getAllFilter(String table, int vel, int from, int to) {
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
    
    
    /* 
     * Tira a metrica
    */
    public static synchronized long getMetric(String maze, String algorithm, String vel, RequestCoordinate rc) {
    	int positionOfPoint = maze.indexOf('.');
    	String table = maze.substring(0, positionOfPoint) + "_" + algorithm; //Maze100_alg
    	
    	//System.out.println("AWS" + table);
    	
    	//check existance of coordinate
    	Request r = new Request(rc, Integer.parseInt(vel), -1); //dummy request
    	int val = searchExistPath(table, r.getVelocity(), r.getNorm(), rc );
    	//Procurar similaridades
        // norma do vector e metrica mais alta
    	//System.out.println("AAAAAAAA" + val);
        if(val==-1) { //do not exist
        	List<Request> lists = getAllFilter(table, r.getVelocity(), r.getNorm()-5, r.getNorm()+5);
        	int normMeasure = Integer.MAX_VALUE;
        	int assumeCost=-1;
        	int myNorm = r.getNorm();
        	
        	//System.out.println("Lists: " + lists.size());
        	
        	//procurar a norma mais pequena
        	for(int i=0; i<lists.size(); ++i) {
        		int tmp = Math.abs( lists.get(i).getNorm()-myNorm );
        		if(  tmp<normMeasure) {
        			normMeasure = tmp;
        			assumeCost = i;
        		}
        	}
        	
        	if(assumeCost>=0) {
	        	Request moreSimilar = lists.get(assumeCost);
	        	Set<RequestCoordinate> rqcoordinates = moreSimilar.getCoordinates();
	        	Iterator iterator = rqcoordinates.iterator();
	        	long highestMetric = 0;
	        	while(iterator.hasNext() ){
	    			RequestCoordinate rc_tmp = (RequestCoordinate)iterator.next();
	    			long tmp = moreSimilar.getMetric(rc_tmp);
	    			if(tmp>highestMetric) {
	    				highestMetric = tmp;
	    			}
	        	}
	        	return highestMetric;
        	}
        	return -1;
        }
        else {
        	if(val==0) { //nao existe mas vel e norma existem
        		//System.out.println("HERE");
        		List<Request> lists = getAllFilter(table, r.getVelocity(), r.getNorm(), r.getNorm());
        		
        		Set<RequestCoordinate> rqcoordinates = lists.get(0).getCoordinates();
            	Iterator iterator = rqcoordinates.iterator();
            	long highestMetric = 0;
            	while(iterator.hasNext() ){
        			RequestCoordinate rc_tmp = (RequestCoordinate)iterator.next();
        			long tmp = lists.get(0).getMetric(rc_tmp);
        			if(tmp>highestMetric) {
        				highestMetric = tmp;
        			}
            	}
            	return highestMetric;
        	}
        	else {//val = 1. Existe
        		List<Request> lists = getAllFilter(table, r.getVelocity(), r.getNorm(), r.getNorm());
        		Set<RequestCoordinate> rqcoordinates = lists.get(0).getCoordinates();
        		Iterator iterator = rqcoordinates.iterator();
        		while(iterator.hasNext() ){
        			RequestCoordinate rc_tmp = (RequestCoordinate)iterator.next();
        			if( rc_tmp.equals(rc) ) {
        				return lists.get(0).getMetric(rc_tmp); 
        			}
            	}
        	}
        	
        }
        return -1;
    }
     
    /* Transform to RequestCoordinate */
    public static synchronized RequestCoordinate fromStringToRequestCoordinate(String x0, String y0, String x1, String y1) {
    	Coordinate start = new Coordinate(Integer.parseInt(x0), Integer.parseInt(y0));
    	//System.out.println("OLA" + start.getX());
    	Coordinate stop = new Coordinate(Integer.parseInt(x1), Integer.parseInt(y1));
    	//System.out.println("OLA=====" + stop.getX());
    	RequestCoordinate rc = new RequestCoordinate(start, stop);
    	//System.out.println("KAISER" + rc.getStart().getX() + " ----->" + rc.getStop().getY());
    	return rc;
    }
    
    public static synchronized void insertRequestCoordinateInDynamoDB(String maze, String algorithm, String vel, RequestCoordinate rc, long metric) {
    	int positionOfPoint = maze.indexOf('.');
    	String table = maze.substring(0, positionOfPoint) + "_" + algorithm; //Maze100_alg
    	//check existance of coordinate
    	Request r = new Request(rc, Integer.parseInt(vel), metric);
    	int val = searchExistPath(table, r.getVelocity(), r.getNorm(), rc );
    	
    	if( val==-1 ) { //nao existe. Por
        	//System.out.println("HERE");
        	mapperDB.save(r, new DynamoDBMapperConfig( new TableNameOverride(table) ) );
        }
    	else {
        	if(val==0) { //inserir no meio. Nor e velocidade existem
        		//System.out.println("HERE");
        		List<Request> lists = getAllFilter(table, r.getVelocity(), r.getNorm(), r.getNorm());
        		mapperDB.delete(lists.get(0));
        		lists.get(0).addPoint(rc, metric);
        		mapperDB.save(lists.get(0), new DynamoDBMapperConfig( new TableNameOverride(table) ) );
        	}
    	}
    	//val = 1. ja existe nao faz nada
    }
    
   
    
    
    
    
    
    
    
    
    
    
    
	

}
