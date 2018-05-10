package proxyserver_cnv;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;





public class LoadBalancer extends Thread {

	private final static int numberOfThreads = 100;
	
	
	
	public static void main(String[] args) throws IOException {
		
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/", new Handler());
		server.createContext("/test", new MyTestHandler());
		
		System.out.println(server.getAddress());	
		System.out.println("LoadBalancer Server listens in port 8080");
		
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
			 
			
			String target = "http://127.0.0.1:8000"; //created by aws
			String targetUri = t.getRequestURI().getPath();
			String targetQuery = t.getRequestURI().getQuery();
			
			String toCreateURL = target + targetUri + "?" + targetQuery;
			
			//System.out.println( toCreateURL );
			
						
			URL reqURL = new URL(toCreateURL);
			
			//Partir, transformar, ler do dynamo, armazenar
			

						
			//connect to server
			try {
				HttpURLConnection httpcon = (HttpURLConnection) reqURL.openConnection();
				httpcon.setDoOutput(false); //GET
				httpcon.setConnectTimeout(0);//Infinite timeout
				httpcon.connect(); //connect
				
							
				int code = httpcon.getResponseCode();
				if(code==200) {
					InputStream input = httpcon.getInputStream();
					//System.out.println(input.toString().length());
					
					OutputStream os = t.getResponseBody();
					t.sendResponseHeaders(code,0);
					
					final byte[] buffer = new byte[0x10000];
	        	    int count = 0;
	        	    while ((count = input.read(buffer)) >= 0) {
	        	    	os.write(buffer,0,count);
	        	    }
	        	    os.flush();
	    			os.close();
					input.close();
					
				}
				else {
					String response = "Bad Request";
		            t.sendResponseHeaders(code, response.length());
		            OutputStream os = t.getResponseBody();
		            os.write(response.getBytes());
		            os.close();
				}
				System.out.println(code);
				
			}
			catch(Exception e) {
				e.printStackTrace();
				String response = "nok";
	            t.sendResponseHeaders(400, response.length());
	            OutputStream os = t.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
				
			}			
		}

	}
}