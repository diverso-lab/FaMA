/*
 * Created on 10-Jan-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package featureModel;

/**
 * @author trinidad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Feature {
	protected String featureName;
	
	public Feature () {
		this.featureName = "";
	}
	
	public Feature (String name) {
		this.featureName = name;
	}
	
	public void setName( String name ){
		this.featureName = name;
	}
	
	public String getName(){
		return featureName;
	}
	
	public String toString() {
		return featureName;
	}
}
