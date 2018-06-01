package loadbalance;

public class InstanceLoad{
	private int numReq; //number of consecutive minutes in 100 %
	private final int MAX;
	private long timeElapsed;
	private long freedomtime;
	
	private String addr;
	private String instanceID;
	
	public InstanceLoad(String add, String id) {
		this.numReq = 0;
		this.MAX = 3;
		this.timeElapsed = -1;
		this.freedomtime = 60000000000l;
		this.addr = add;
		this.instanceID = id;
	}
	
	public void addRequest() {
		++numReq;
	}
	
	public void removeRequest() {
		--numReq;
		if(numReq==0) {
			timeElapsed = System.nanoTime();
		}
	}
	
	public boolean isFull() {
		return numReq==MAX;
	}
	
	public boolean isFreeLongEnough() {
		if(numReq==0 && timeElapsed!=-1) {
			long actual = System.nanoTime();
			if(actual-timeElapsed>=freedomtime) {
				return true;
			}
		}
		return false;
	}
	
	public String getAddress() {
		return addr;
	}
	
	public String getInstanceID() {
		return instanceID;
	}
	
	@Override
	public boolean equals(Object obj) {

	    if (obj == null) return false;

	    if (!(obj instanceof InstanceLoad)) {
	       return false;
	    }

	    if (obj == this) {
	        return true;
	    }

	    return this.addr.equals( ((InstanceLoad)obj).getAddress());
	}
	
	@Override
	public int hashCode() {
	    return Integer.parseInt(addr);
	}
	
}
