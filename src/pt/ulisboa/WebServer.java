package pt.ulisboa;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.meic.cnv.mazerunner.maze.Main;

public class WebServer {

	private final static int numberOfThreads = 10;
	
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        //server.setExecutor(null); // creates a default executor
        
        //server.setExecutor(Executors.newCachedThreadPool());
        server.setExecutor(Executors.newFixedThreadPool(numberOfThreads)); //Reservar 5 threads
        
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            
        	String s = t.getRequestURI().getQuery();
        	long threadId = Thread.currentThread().getId(); //Debug
        	
        	String teste = t.getRequestURI().getPath();
        	//System.out.println(teste);
        	
        	if( !teste.equals("/mzrun.html") ) { //Garantir pagina
        		String response = "404 (Not Found)\n";
        	    t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close(); 
        	}
        	else {
        		//Retirar argumentos passados por url        	
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
	        	arg_maze[6] = "src/maze_files/" + result.get("m");
	        	arg_maze[7] = "src/output_maze_files/" + "mazesolution" + String.valueOf(threadId) + ".html";
	        	
	        			
	        	//long threadId = Thread.currentThread().getId();
	            System.out.println(threadId);		
	        			
	        	//String response = "";
	            
	        	try {
	        		Main.main(arg_maze); //Correr o maze

	        		FileInputStream fs = new FileInputStream(arg_maze[7]); //Ficheiro solucao
	        		OutputStream os = t.getResponseBody();
	        		t.sendResponseHeaders(200, 0);
	        		
	                final byte[] buffer = new byte[0x10000];
	        	    int count = 0;
	        	    while ((count = fs.read(buffer)) >= 0) {
	        	    	os.write(buffer,0,count);
	        	    }
	        	    fs.close();
	        	    os.close();
	     
	        		
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        		
	        		String response = e.getMessage();
	        		t.sendResponseHeaders(200, response.length());
	                OutputStream os = t.getResponseBody();
	                os.write(response.getBytes());
	                os.close();       		
	        	}
	        	
	        	
	        	//int x_start = Integer.parseInt( result.get("x0") );
	        	
	        	
	        	// Teste das threads
	            /*
	            try        
	            {
	                Thread.sleep(60000);
	            } 
	            catch(InterruptedException ex) 
	            {
	                Thread.currentThread().interrupt();
	            }
	            
	        	*/
	        	//String response = "This was the query:" + t.getRequestURI().getQuery() 
	            //                   + "##";
	            
	    	      
	            //os.write(response.getBytes());
        	}
            
            System.out.println("Finished " + threadId);

            
        }
    }

}

