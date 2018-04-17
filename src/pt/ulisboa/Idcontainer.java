package pt.ulisboa;

import java.util.HashMap;
import java.util.Map;

public class Idcontainer {
	static Map<Integer,Integer> t = new HashMap<Integer,Integer>();
	
	public static synchronized void inc(int t_id) {
		if( t.containsKey(t_id)==true ) {
			Integer tmp = t.get(t_id);
			++tmp;
			t.put(t_id, tmp);
		}
		else {
			t.put(t_id, 1);
		}
	}
	
	public static synchronized void dynInstrCount(int incr) {
		inc(0); //chamar o threadId
	}
	
	public static void erase(int t_id) {
		if(t.containsKey(t_id)==true) {
			t.remove(t_id);
		}
	}
	
	public static Integer getValue(int t_id) {
		return t.get(t_id);
	}

	public static void main(String[] args) {
		Idcontainer.inc(12);
		System.out.println(Idcontainer.getValue(12));
		
		Idcontainer.inc(12);
		System.out.println(Idcontainer.getValue(12));
		
		Idcontainer.inc(13);
		System.out.println(Idcontainer.getValue(13));
		
		Idcontainer.inc(14);
		System.out.println(Idcontainer.getValue(14));
		
		//System.out.println(x);
		Idcontainer.erase(12);
		System.out.println(t.size());
	}
}
