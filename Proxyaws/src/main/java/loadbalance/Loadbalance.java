package loadbalance;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Loadbalance implements Runnable{
	
	
	@Override
	public void run() {
		while( true ) {
			try {
				System.out.println("LOAD BALANCE before semaphore");
				Proxyserver.semaphore.acquire();
				System.out.println("LOAD BALANCE I HAVE the semaphore");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			//codigo
			//Just Round Robin
			Proxyserver.semaphore.release();
			RequestDescription rq = Queuerequests.getRquest(0);
			Queuerequests.remove(0);

			String target_address = "";
			String target_id = "";
			for(int i=0; i<Instances.size(); ++i) {
				String tmp = Instances.getInstance(i); //get the address
				InstanceLoad il_tmp = new InstanceLoad(tmp, Instances.getInstanceID(i));
				InstanceLoad storedIL = LoadsInInstance.containsInst(il_tmp);
				if( storedIL.isFull() ) {
					continue;
				}
				else {
					target_address = storedIL.getAddress(); //address asigned
					target_id = storedIL.getInstanceID();
					storedIL.addRequest();
					LoadsInInstance.insert(storedIL);//update list
					break;
				}
			}
			if( target_address.equals("") ) { //no free instance
				//inform autoscaler
				Queuerequests.insertInWaitingQueue(rq);
				//Queuerequests.insert(rq.getCaller()); //insert request back in queue
				
			}
			else {
				//String target = "http://127.0.0.1:8000"; //created by aws
				//String target = Instances.getInstanceRoundRobin();
				ProxyAction client = new ProxyAction(target_address, target_id, rq);
				Thread cw = new Thread(client);
				cw.start();
			}
		}
	}
	
	
	

}
