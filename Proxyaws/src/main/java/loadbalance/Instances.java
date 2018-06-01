package loadbalance;

import java.util.ArrayList;
import com.amazonaws.services.ec2.model.Instance;

class InstanceDescription{
	private String id;
	private String addr;
	
	public InstanceDescription(String i, String a) {
		this.id = i;
		this.addr = a;
	}
	
	public String getID() {
		return id;
	}
	
	public String getAddr() {
		return addr;
	}
	
	@Override
	public boolean equals(Object obj) {

	    if (obj == null) return false;

	    if (!(obj instanceof InstanceDescription)) {
	       return false;
	    }

	    if (obj == this) {
	        return true;
	    }

	    return this.addr.equals( ((InstanceDescription)obj).getAddr()) && this.id.equals( ((InstanceDescription)obj).getID());
	}
	
	@Override
	public int hashCode() {
	    return addr.hashCode() + id.hashCode();
	}
}


//available instances
public class Instances {
	private static ArrayList<InstanceDescription> cont;
	private static int idxRobin;
	
	public Instances() {
		cont = new ArrayList<InstanceDescription>();
		idxRobin = 0;
	}
	
	public static void insert(Instance i) {
				
		cont.add( new InstanceDescription(i.getInstanceId(), i.getPublicDnsName()) );
		
	}
	
	public static void remove(String addr) { //remove the instance by address
		for(int i=0; i<cont.size(); ++i) {
			if(cont.get(i).getAddr().equals(addr)) {
				cont.remove(i);
				break;
			}
		}
	}
	
	public static int size() { //number of instances
		return cont.size();
	}
	
	public static String getInstanceRoundRobin() { //inatance in round robin
		String tmp = cont.get(idxRobin).getAddr();
		++idxRobin;
		idxRobin = idxRobin%cont.size();
		return tmp;		
	}

	public static String getInstance(int pos) { //instance by position
		String tmp = cont.get(pos).getAddr();
		return tmp;		
	}
	
	public static String getInstanceID(int pos) { //instance by position
		String tmp = cont.get(pos).getID();
		return tmp;		
	}
	
	public static boolean isEqual(Instance i) {
		InstanceDescription tmp = new InstanceDescription(i.getInstanceId(), i.getPublicDnsName());
		if(cont.contains(tmp)) {
			return true;
		}
		return false;
		
	}
}
