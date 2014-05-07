/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.FAMA.models.variabilityModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public class GenericProduct {

protected String name;
	
	protected List<VariabilityElement> listOfElements;
	
	public GenericProduct () {
		listOfElements = new ArrayList<VariabilityElement>();
	}
	
	public int getNumberOfElements() {
		return listOfElements.size();
	}
	

	
	public void addElement (VariabilityElement f) {
		listOfElements.add(f);
	}
	
	public Collection<VariabilityElement> getElements(){
		return listOfElements;
	}
	
	public boolean equals(Object p){
		boolean eq=false;
		if (p instanceof Product){
			Collection<? extends VariabilityElement> listOfFeat1=((Product) p).getFeatures();
			if(listOfFeat1.containsAll(listOfElements)&&listOfElements.containsAll(listOfFeat1))
				eq=true;
		}
		
		return eq;
	}
	
	@Override
	public String toString(){
		Iterator<VariabilityElement> it = listOfElements.iterator();
		String str="";
		while  (it.hasNext()){
			VariabilityElement feat = it.next();
			String str2=feat.getName();
			str+=str2+";";
		
		}
		return str.substring(0, str.length()-1);

		
	}

	public boolean removeElement(VariabilityElement f) {
		return listOfElements.remove(f);
	}

	public void addAllElements(Collection<VariabilityElement> allFeatures) {
		this.listOfElements.addAll(allFeatures);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
