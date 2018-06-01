package loadbalance;

import java.util.ArrayList;

public class LoadsInInstance {
	private static ArrayList<InstanceLoad> cont;
	
	public LoadsInInstance() {
		cont = new ArrayList<InstanceLoad>();
	}
	
	public static void insert(InstanceLoad i) {
		if( cont.contains(i) ) {
			cont.remove(i);
		}
		cont.add(i);
	}
	
	public static void remove(InstanceLoad i) {
		if(cont.contains(i)) {
			cont.remove(i);
		}
	}
	
	public static InstanceLoad containsInst(InstanceLoad i) {
		if( cont.contains(i) ) {
			return cont.get( cont.indexOf(i) );
		}
		return null;
	}
	
	public static InstanceLoad get(int pos) {
		return cont.get(pos);
	}
	
	public static int sz() {
		return cont.size();
	}
	

}
