package proxyserver_cnv;

public class ClassOfService{
	private long minThreshold;
	private long maxThreshold; //if -1 means infinite
	private int multiplier; //should be multiple of 2 until further notice
	                        //should be normalized. Ex: one container with capacity 1 can hold (for 3 classes)
	                        //Class A - 1             ------ 100
	                        //Class B - 0.5           ------  50
							//Class C - 0.25          ------  25
	 
	public ClassOfService(long min, long max, int mul) {
		this.minThreshold = min;
		this.maxThreshold = max;
		this.multiplier = mul;
	}
	
	public long getminThreshold() {
		return minThreshold;
	}
	public long getmaxThreshold() {
		return maxThreshold;
	}
	public int getMultiplier() {
		return multiplier;
	}
	
	public boolean isWithin(long val) {
		if(minThreshold<=val && val<=maxThreshold) {
			return true;
		}
		return false;
	}
	
}
