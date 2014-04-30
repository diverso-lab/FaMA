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
/*
 * Created on 10-Jan-2005
 *
 */
package es.us.isa.FAMA.models.featureModel;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;



/**
 * A product is a set of features.
 */
public class Product extends GenericProduct{
	
	protected String name;
	
	protected List<GenericFeature> listOfFeatures;
	
	public Product () {
		listOfFeatures = new ArrayList<GenericFeature>();
	}
	
	public int getNumberOfFeatures() {
		return listOfFeatures.size();
	}
	

	
	public void addFeature (GenericFeature f) {
		listOfFeatures.add(f);
		listOfElements.add(f);
	}
	
	public Collection<GenericFeature> getFeatures(){
		return listOfFeatures;
	}
	
	public boolean equals(Object p){
		boolean eq=false;
		if (p instanceof Product){
			Collection<? extends VariabilityElement> listOfFeat1=((Product) p).getFeatures();
			if(listOfFeat1.containsAll(listOfFeatures)&&listOfFeatures.containsAll(listOfFeat1))
				eq=true;
		}
		
		return eq;
	}
	
	@Override
	public String toString(){
		Iterator<GenericFeature> it = listOfFeatures.iterator();
		String str="";
		while  (it.hasNext()){
			VariabilityElement feat = it.next();
			String str2=feat.getName();
			str+=str2+";";
		
		}
		return str;

		
	}

	public boolean removeFeature(GenericFeature f) {
		listOfElements.remove(f);
		return listOfFeatures.remove(f);
	}

	public void addAllFeatures(Collection<GenericFeature> allFeatures) {
		listOfFeatures.addAll(allFeatures);
		listOfElements.addAll(allFeatures);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
