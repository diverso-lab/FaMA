/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.FAMA.models.featureModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
/**
 * Represents a product of attibuted featuress
 */
public class AttributedProduct extends Product {
	
	protected List<GenericAttributedFeature> listOfAttFeatures;
		
	public  AttributedProduct() {
		super();
		listOfAttFeatures = new ArrayList<GenericAttributedFeature>();;
		listOfFeatures = new ArrayList<GenericFeature>();
	}
	
	public void addFeature (GenericAttributedFeature f) {
		listOfAttFeatures.add(f);
		listOfFeatures.add(f);
	}
	

	public Collection<GenericAttributedFeature> getAttFeatures(){
		return listOfAttFeatures;
	}
	
	@Override
	public Collection<GenericFeature> getFeatures(){	
		return listOfFeatures;
	}
	
}
