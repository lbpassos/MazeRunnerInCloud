package proxyserver_cnv;

import java.util.ArrayList;

public class ServicesContainer {
	private ArrayList<ClassOfService> container;
		
	public ServicesContainer() {
		container = new ArrayList<ClassOfService>();
	}

	public void insert(ClassOfService cos) {
		container.add(cos);	
	}
	
	public ClassOfService getMyClass(long metric) {
		ClassOfService tmp;
		for(int i=0; i<container.size(); ++i) {
			tmp = container.get(i);
			if( tmp.isWithin(metric) ) {
				return tmp;
			}
		}
		//no class found
		return container.get(container.size()-1); //belongs to highest load class
	}
}
