package proxyserver_cnv;

import java.util.ArrayList;

public class InstanceLoad {
	private ArrayList<ClassOfService> container;
	private long MAX_LOAD;
	private long CURRENT_LOAD;
	
	public InstanceLoad(long ml) {
		container = new ArrayList<ClassOfService>();
		this.MAX_LOAD = ml;
		this.CURRENT_LOAD = 0;
	}
	
	public int canInsert(ClassOfService cos) {
		if(container.size()==0) {
			container.add(cos);
			CURRENT_LOAD = cos.getMultiplier();
			return 0; //first position
		}
		if( CURRENT_LOAD>=MAX_LOAD ) {
			return -1; //full
		}
		if( CURRENT_LOAD+cos.getMultiplier() <= MAX_LOAD ) {
			CURRENT_LOAD += cos.getMultiplier();
			container.add(cos);
			return container.size()-1;//current position
		}
		return -1; //full
	}

	public void remove(int pos) {
		ClassOfService tmp = container.remove(pos);
		CURRENT_LOAD -= tmp.getMultiplier(); 
	}
	
}
