package es.us.isa.FAMA.ws.auxiliar;

import java.util.Collection;
import java.util.LinkedList;

/*
 * This class is not strictly a proxy, but 
 * it represents a product. We use it
 * to provide FaMa WS.
 */
public class ProductProxy {

	private Collection<String> features;

	
	public ProductProxy(){
		features = new LinkedList<String>();
	}
	
	public Collection<String> getFeatures() {
		return features;
	}
	
	public void setFeatures(Collection<String> feats){
		features = feats;
	}
	
	public void addFeature(String s){
		features.add(s);
	}
	
	
	
}
