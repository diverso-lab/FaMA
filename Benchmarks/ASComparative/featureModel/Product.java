/*
 * Created on 10-Jan-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package featureModel;

import java.util.List;
import java.util.ArrayList;

/**
 * @author trinidad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Product {
	private List listOfFeatures;
	
	public Product () {
		listOfFeatures = new ArrayList();
	}
	
	public int getNumberOfFeatures() {
		return listOfFeatures.size();
	}
	
	public Feature getFeature(int index) throws IndexOutOfBoundsException{
		if ( index >= 0 && index < listOfFeatures.size())
			return (Feature)(listOfFeatures.get(index));
		else
			throw new IndexOutOfBoundsException("Feature does not exist in this product");
	}
	
	public void addFeature (Feature f) {
		listOfFeatures.add(f);
	}
}
