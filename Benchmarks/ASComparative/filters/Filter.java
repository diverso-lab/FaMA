package filters;

import java.util.ArrayList;
import java.util.Iterator;
public class Filter {

	private ArrayList included; // Features with value=1
	private ArrayList excluded; // Features with value=0
	
	public Filter() {
		included=new ArrayList();
		excluded=new ArrayList();
	}
	
	public void includeFeature(int feature) {
		included.add(feature);
	}
	
	public void excludeFeature(int feature) {
		excluded.add(feature);
	}
	
	public ArrayList getIncludedFeatures() {
		return included;
	}
	
	public ArrayList getExcludedFeatures() {
		return excluded;
	}
	
	// Print filter
	public String print() {
		
		String res="";
		
		// Included features
		if (!this.included.isEmpty()) {
			res+="**************** FILTER *******************\n";
			res+="INCLUDED FEATURES: [ ";
			Iterator it=included.iterator();
			while (it.hasNext())
				res+="F" + (Integer)it.next() + " ";
			
			res+="]\n";
		}
		
		// Excluded features
		if (!this.excluded.isEmpty()) {;
			res+="EXCLUDED FEATURES: [ ";
			Iterator it=excluded.iterator();
			while (it.hasNext())
				res+="F" + (Integer)it.next() + " ";
			
			res+="]\n";
		}	
		
		res+="\n";
		
		return res;
	}
}
