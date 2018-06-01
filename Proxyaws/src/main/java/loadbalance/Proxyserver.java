package loadbalance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.waiters.WaiterParameters;
//import com.amazonaws.services.ec2.waiters.*;


public class Proxyserver {
	public static Semaphore semaphore;
	public static Semaphore semaphoreAS;
	public static AmazonEC2      ec2;
	

	public static ArrayList<Instance> getAllInstances(){
		
		ArrayList<Instance> toReturn = new ArrayList<Instance>();
		
		try {
	        DescribeAvailabilityZonesResult availabilityZonesResult = Proxyserver.ec2.describeAvailabilityZones();
	        System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
	                " Availability Zones.");
	        /* using AWS Ireland. 
	         * TODO: Pick the zone where you have your AMI, sec group and keys */
	        DescribeInstancesResult describeInstancesRequest = Proxyserver.ec2.describeInstances();
	        List<Reservation> reservations = describeInstancesRequest.getReservations();
	        Set<Instance> instances = new HashSet<Instance>();
	
	        for (Reservation reservation : reservations) {
	        	instances.addAll(reservation.getInstances());
	        }
	
	        ArrayList<String> instancesIDs = new ArrayList<String>();
	        for(Instance inst : instances) {
	        	if( inst.getImageId().equals("ami-308be74f") ) { //running instances
	        		//++count;
	        		instancesIDs.add(inst.getInstanceId());
	        	}
	        	
	        }
	        
	        DescribeInstanceStatusRequest d = new DescribeInstanceStatusRequest().withInstanceIds(instancesIDs);
	        
	        ec2.waiters().instanceStatusOk().run(
	        		  new WaiterParameters().withRequest(d)
	        		    // Optionally, you can tune the PollingStrategy:
	        		    // .withPollingStrategy(...)
	        		  );
	        		

	        
	        describeInstancesRequest = Proxyserver.ec2.describeInstances();
	        reservations = describeInstancesRequest.getReservations();
	        instances = new HashSet<Instance>();
	        for (Reservation reservation : reservations) {
	        	instances.addAll(reservation.getInstances());
	        }
	        for(Instance inst : instances) {
	        	if( inst.getImageId().equals("ami-308be74f") && inst.getState().getCode()==16 ) { //running instances
	        		//++count;
	        		toReturn.add(inst);
	        	}
	        }
	        
	        
	        
	        System.out.println("You have " + toReturn.size() + " Amazon EC2 instance(s) running.");
	        //return toReturn;
	        
	    } catch (AmazonServiceException ase) {
	            System.out.println("Caught Exception: " + ase.getMessage());
	            System.out.println("Reponse Status Code: " + ase.getStatusCode());
	            System.out.println("Error Code: " + ase.getErrorCode());
	            System.out.println("Request ID: " + ase.getRequestId());
	    }
		return toReturn;
	}
	
		
	
	
	
	
	public static ArrayList<Instance> getInstances(){
		ArrayList<Instance> toReturn = new ArrayList<Instance>();
		
		try {
	        DescribeAvailabilityZonesResult availabilityZonesResult = Proxyserver.ec2.describeAvailabilityZones();
	        System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
	                " Availability Zones.");
	        /* using AWS Ireland. 
	         * TODO: Pick the zone where you have your AMI, sec group and keys */
	        DescribeInstancesResult describeInstancesRequest = Proxyserver.ec2.describeInstances();
	        List<Reservation> reservations = describeInstancesRequest.getReservations();
	        Set<Instance> instances = new HashSet<Instance>();
	
	        for (Reservation reservation : reservations) {
	        	instances.addAll(reservation.getInstances());
	        }
	
	        //int count = 0;
	        
	        
	        
	        for(Instance inst : instances) {
	        	if( inst.getImageId().equals("ami-308be74f") && inst.getState().getCode()==16 ) { //running instances
	        		//++count;
	        		toReturn.add(inst);
	        	}
	        }
	        
	        
	        
	        System.out.println("You have " + toReturn.size() + " Amazon EC2 instance(s) running.");
	        //return toReturn;
	        
	    } catch (AmazonServiceException ase) {
	            System.out.println("Caught Exception: " + ase.getMessage());
	            System.out.println("Reponse Status Code: " + ase.getStatusCode());
	            System.out.println("Error Code: " + ase.getErrorCode());
	            System.out.println("Request ID: " + ase.getRequestId());
	    }
		return toReturn;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		try {
			init();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		semaphore = new Semaphore(1); //loadbalancer
		semaphoreAS = new Semaphore(1); //loadbalancer
		
		try {
			semaphore.acquire();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		// Construct listener and select port here
		//Loadbalancelistener listener;
		try {
			new Loadbalancelistener(8080);
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		new Queuerequests();
		new Instances();
		new LoadsInInstance();
		
		ArrayList<Instance> activeInst = getInstances(); //active instances
		for(int i=0; i<activeInst.size(); ++i) {
			Instances.insert( activeInst.get(i) );//instances in the system
			LoadsInInstance.insert( new InstanceLoad(Instances.getInstance(i), Instances.getInstanceID(i)) );//
		}
		
		
		new Thread( new Loadbalance() ).start();
		new Thread( new Autoscaler() ).start();
		
		
			
		
		
			

	}
	
	public static void init() throws Exception {

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
      ec2 = AmazonEC2ClientBuilder.standard().withRegion("us-east-1").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }


}

