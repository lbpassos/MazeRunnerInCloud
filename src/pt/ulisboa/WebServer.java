package pt.ulisboa;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.meic.cnv.mazerunner.maze.Main;

public class WebServer {

	private final static int numberOfThreads = 10;
	private static ThreadPrint local;
	private static final PrintStream console = System.out; 
	private static FileWriter logfile;
	private static BufferedWriter bw;
	private static PrintWriter out;
	
	public static synchronized void writeLog(String str) {
		out.println(str);
		out.flush();
	}
	
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        //server.setExecutor(null); // creates a default executor
        
        //server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Server listen in port 8000");
        server.setExecutor(Executors.newFixedThreadPool(numberOfThreads)); //Reservar 5 threads
        
        local = new ThreadPrint( console ); //console is the default
        
        //Open log file
        try {
        	logfile = new FileWriter("src/Log/logfile.txt", true);
        	bw = new BufferedWriter(logfile);
        	out = new PrintWriter(bw);
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        
        server.start();
        
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            
        	String s = t.getRequestURI().getQuery();
        	long threadId = Thread.currentThread().getId(); //Debug
        	
        	String teste = t.getRequestURI().getPath();
        	//System.out.println(teste);
        	//PrintStream console = System.out;
        	//System.setOut(console);
        	//System.out.println("aqui");
        	
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
	        		        		
	        		//Create file to write instrumentation results
	        		String out_file = "src/output_instrumented/" + "out" + String.valueOf(threadId);
	        		File file = new File(out_file);
	        		FileOutputStream fos = new FileOutputStream(file);
	        		PrintStream ps = new PrintStream(fos);
	        		local.setCurrent(ps);
	        		
	        		System.setOut(local);//Redirect to
	        		
	        		long timeMillis = System.currentTimeMillis();  //counting time
	        		Main.main(arg_maze); //Correr o maze
	        		long end = System.currentTimeMillis();
	        		long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(end - timeMillis);
	        		
	        		
	        		//System.out.println(threadId);
	        		local.flush();
	        		fos.close();
	        		ps.close();
	        		//local.close();
	        		BufferedReader reader = new BufferedReader(new FileReader(file));
	        		Long numBlocks = Long.parseLong( reader.readLine() );
	        		reader.close();
	        		
	        		    	        		
	        		String lineOfText = threadId + "," 
	        		           + arg_maze[0] + ","
	        		           + arg_maze[1] + ","
	        		           + arg_maze[2] + ","
	        		           + arg_maze[3] + ","
	        		           + arg_maze[4] + ","
	        		           + arg_maze[5] + ","
	        		           + result.get("m") + ","
	        		           + numBlocks + ","
	        		           + timeSeconds;
	        		           
	        		writeLog(lineOfText);
	        		
	        		FileInputStream fs = new FileInputStream(arg_maze[7]); //Ficheiro solucao
	        		OutputStream os = t.getResponseBody();
	        		t.sendResponseHeaders(200, 0);
	        		
	                final byte[] buffer = new byte[0x10000];
	        	    int count = 0;
	        	    while ((count = fs.read(buffer)) >= 0) {
	        	    	os.write(buffer,0,count);
	        	    }
	        	    //System.setOut(console);
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
        	//System.setOut(console);
        	local.setCurrent(console);
            System.out.println("Finished " + threadId);

            
        }
    }

}

