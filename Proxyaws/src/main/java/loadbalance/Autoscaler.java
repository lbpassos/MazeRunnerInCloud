package loadbalance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class Autoscaler implements Runnable{

	
	@Override
	public void run() {
		while( true ) {
			try {
				
				Thread.sleep(5000);
				//check pendencies
				if(Queuerequests.getSizeWaitingQueue()>0) {
					//create
					RunInstancesRequest runInstancesRequest =
				               new RunInstancesRequest();

		            runInstancesRequest.withImageId("ami-308be74f")
		                               .withInstanceType("t2.micro")
		                               .withMinCount(1)
		                               .withMaxCount(1)
		                               .withKeyName("cnv-lab25")
		                               .withSecurityGroups("CNV-ssh+http+v1");
		            //RunInstancesResult runInstancesResult =
		            Proxyserver.ec2.runInstances(runInstancesRequest);
		            
		            
		            Thread.sleep(60000);
		          //insert new instance
		            ArrayList<Instance> activeInst = Proxyserver.getAllInstances(); //active instances
		    		for(int i=0; i<activeInst.size(); ++i) {
		    			if( Instances.isEqual(activeInst.get(i)) ) {
		    				continue;
		    			}
		    			Instances.insert( activeInst.get(i) );
		    			LoadsInInstance.insert( new InstanceLoad(Instances.getInstance(i), Instances.getInstanceID(i)) );//
		    			break;
		    		}
		    		Queuerequests.insert( Queuerequests.getFromWaitingQueue(0).getCaller() ); //insert request back in queue
		            Queuerequests.removeFromWaitingQueue(0);//remove always the first
		            //have to remove
		            for(int i=0; i<Queuerequests.getSizeWaitingQueue(); ++i) {
		            	Queuerequests.insert( Queuerequests.getFromWaitingQueue(0).getCaller() );
		            	Queuerequests.removeFromWaitingQueue(0);//remove always the first
		            }
				}
				else {
					if(Instances.size()==0) {
						RunInstancesRequest runInstancesRequest =
					               new RunInstancesRequest();
	
			            runInstancesRequest.withImageId("ami-308be74f")
			                               .withInstanceType("t2.micro")
			                               .withMinCount(1)
			                               .withMaxCount(1)
			                               .withKeyName("cnv-lab25")
			                               .withSecurityGroups("CNV-ssh+http+v1");
			            Proxyserver.ec2.runInstances(runInstancesRequest);
			            
			            
			            Thread.sleep(60000);
			          //insert new instance
			            ArrayList<Instance> activeInst = Proxyserver.getAllInstances(); //active instances
			    		for(int i=0; i<activeInst.size(); ++i) {
			    			if( Instances.isEqual(activeInst.get(i)) ) {
			    				continue;
			    			}
			    			Instances.insert( activeInst.get(i) );
			    			LoadsInInstance.insert( new InstanceLoad(Instances.getInstance(i), Instances.getInstanceID(i)) );//
			    			break;
			    		}	       						
					}
					else {
						if(Instances.size()==1) {
							continue;
						}
					}
				}
				
				
				//health check
				for(int i=0; i<LoadsInInstance.sz(); ++i) {
					InstanceLoad storedIL = LoadsInInstance.get(i);
					if(storedIL.isFreeLongEnough()==true) {
						//remove the instance
						TerminateInstancesRequest termInstanceReq = new TerminateInstancesRequest();
			            termInstanceReq.withInstanceIds(storedIL.getInstanceID());
			            Proxyserver.ec2.terminateInstances(termInstanceReq);
			            
		    			LoadsInInstance.remove( new InstanceLoad(Instances.getInstance(i), Instances.getInstanceID(i)) ); 
		    			Instances.remove(storedIL.getAddress());//remove from conatainer
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			} 		
		}
	}
}
