package loadbalance;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sun.net.httpserver.HttpExchange;

public class ProxyAction implements Runnable{
	private String target;
	private String instID;
	private HttpExchange h;
	
	public ProxyAction(String target, String iID, RequestDescription r) {
		this.target = target;
		this.instID = iID;
		this.h= r.getCaller();
	}

	@Override
	public void run() {
		
		System.out.println( target );//
		
		String Maze_target = target + ":8000";//concat string
		String targetUri = h.getRequestURI().getPath();
		String targetQuery = h.getRequestURI().getQuery();
		
		String toCreateURL = "http://"+Maze_target + targetUri + "?" + targetQuery;
		
		System.out.println( Maze_target );//
		
					
		
		
		//Partir, transformar, ler do dynamo, armazenar
		

					
		//connect to server
		try {
			URL reqURL = new URL(toCreateURL);
			HttpURLConnection httpcon = (HttpURLConnection) reqURL.openConnection();
			httpcon.setDoOutput(false); //GET
			httpcon.setConnectTimeout(0);//Infinite timeout
			httpcon.connect(); //connect
			
						
			int code = httpcon.getResponseCode();
			if(code==200) {
				InputStream input = httpcon.getInputStream();
				//System.out.println(input.toString().length());
				
				OutputStream os = h.getResponseBody();
				h.sendResponseHeaders(code,0);
				
				final byte[] buffer = new byte[0x10000];
        	    int count = 0;
        	    while ((count = input.read(buffer)) >= 0) {
        	    	os.write(buffer,0,count);
        	    }
        	    os.flush();
    			os.close();
				input.close();
				
				//Finnish
				InstanceLoad il_tmp = new InstanceLoad(target, instID);
				InstanceLoad storedIL = LoadsInInstance.containsInst(il_tmp);
				storedIL.removeRequest();
				LoadsInInstance.insert(storedIL);//update list
							
			}
			else {
				String response = "Bad Request";
				h.sendResponseHeaders(code, response.length());
	            OutputStream os = h.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
			}
			System.out.println(code);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			//String response = "nok";
			//h.sendResponseHeaders(400, response.length());
            //OutputStream os = h.getResponseBody();
            //os.write(response.getBytes());
            //os.close();
			
		}			
	}
}
