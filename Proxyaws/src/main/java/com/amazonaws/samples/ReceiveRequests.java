package com.amazonaws.samples;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import proxyserver_cnv.DynamoDB.AmazonDynamoDBModule;




class AutoScaleThread extends Thread {

    public void run(){
       //System.out.println("MyThread running");
    	int numInstances;
    	while(true) {
    		if(Queuerequests.size()>0) {
    			//check if there is running instances
    			numInstances = EC2_launchCheck.checkInstance();
    			
    			System.out.println(numInstances);
    		}
    		try {
    			Thread.sleep(1000);
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
}


	


public class ReceiveRequests extends Thread {

    private final static int numberOfThreads = 100;
    
    
    
    public static void main(String[] args) throws IOException {
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new Handler());
        server.createContext("/test", new MyTestHandler());
        
        System.out.println(server.getAddress());    
        System.out.println("LoadBalancer Server listens in port 8080");
        
        server.setExecutor(Executors.newFixedThreadPool(numberOfThreads)); //Reservar 5 threads
        server.start();
        
        new Queuerequests();
        
        try {
        	AmazonDynamoDBModule.init(); //Start dynamoDB
        }
        catch(Exception e) {
        	e.printStackTrace();
        	server.stop(0);
        }
        
        try {
        	EC2_launchCheck.init(); //
        }
        catch(Exception e) {
        	e.printStackTrace();
        	return;
        }
        
        AutoScaleThread myThread = new AutoScaleThread();
        myThread.start();
        /*Timer timer = new Timer();
        TimerTask task = new TimerTask() {
      	  @Override
      	  public void run() {
      	    //do some processing
      		  System.out.println("MyThread running");
      	  }
      };
    	timer.schedule(task, 0l, 1000l); //call the run() method at 1 second intervals
*/
    }

    

    //For ping purposes of the load balancer
    static class MyTestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "ok";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class Handler implements HttpHandler {
            
        public void handle(HttpExchange t) throws IOException {
             Queuerequests.insert(t); //insert request in queue          
        }

    }
}