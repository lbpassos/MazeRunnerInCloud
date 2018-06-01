package loadbalance;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import proxyserver_cnv.DynamoDB.AmazonDynamoDBModule;

public class Loadbalancelistener extends Thread {

	private final static int numberOfThreads = 100;
    
    public Loadbalancelistener(int port) throws IOException{
    	HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new Handler());
        server.createContext("/test", new MyTestHandler());
        
        System.out.println(server.getAddress());    
        System.out.println("LoadBalancer Server listens in port: " + port);
        
        server.setExecutor(Executors.newFixedThreadPool(numberOfThreads)); //Reservar 5 threads
        server.start();
    	
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
             //System.out.println(Queuerequests.size());
             
        }

    }
    
    
}