package com.amazonaws.samples;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import proxyserver_cnv.DynamoDB.AmazonDynamoDBModule;
import proxyserver_cnv.DynamoDB.Coordinate;
import proxyserver_cnv.DynamoDB.RequestCoordinate;




public class Queuerequests {
    static ArrayList<WeightsLoad> queue;

    
    public Queuerequests(){
    	queue = new ArrayList<WeightsLoad>();
    }
    
    public static void insert(HttpExchange val){
        
        //decompose request
    	String s = val.getRequestURI().getQuery();
    	Map<String, String> result = new HashMap<String, String>();
        for (String param : s.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        
        //Criar argumentos para passar para o maze na mesma ordem que o esperado
    	String[] arg_maze = new String[8];
    	arg_maze[0] = result.get("x0");
    	arg_maze[1] = result.get("y0");
    	arg_maze[2] = result.get("x1");
    	arg_maze[3] = result.get("y1");
    	arg_maze[4] = result.get("v");
    	arg_maze[5] = result.get("s");
    	arg_maze[6] = result.get("m");
    	
    	/*try {
    	String response = "ok Marilu";
        val.sendResponseHeaders(200, response.length());
        OutputStream os = val.getResponseBody();
        os.write(response.getBytes());
        os.close();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}*/
        
    	/*System.out.println("x0: " + arg_maze[0]);
    	System.out.println("y0: " + arg_maze[1]);
    	System.out.println("x1: " + arg_maze[2]);
    	System.out.println("y1: " + arg_maze[3]);
    	System.out.println("Velocity: " + arg_maze[4]);
    	System.out.println("Strategy " + arg_maze[5]);
    	System.out.println("Maze " + arg_maze[6]);
    	*/
    	
    	//tests if already exists in dynamoDB
    	//Coordinate start = new Coordinate(Integer.parseInt(arg_maze[0]), Integer.parseInt(arg_maze[1]));
    	//Coordinate stop = new Coordinate(Integer.parseInt(arg_maze[2]), Integer.parseInt(arg_maze[3]));
    	//RequestCoordinate rc = new RequestCoordinate(start, stop);
    	//long idx = AmazonDynamoDBModule.getMetric(arg_maze[6], arg_maze[5], arg_maze[4], rc);
    	//if(idx==-1) {//it does not exists in database (UNKNOWN)
    	//	queue.add( new WeightsLoad(-1, val, arg_maze) );
    		
    	//}
    	//else {//it exists in database
    	//	queue.add( new WeightsLoad(idx, val, arg_maze) );
    	//}
    	
    }
    
    public static int size() {
    	return queue.size();
    }
    
    public static WeightsLoad getRquest(int pos) {
    	return queue.get(pos);
    }
    
    public static void remove(int pos){
        queue.remove(pos);
    }
    
}
