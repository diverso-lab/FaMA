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


import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

/**
 * An abstract class to represent a generic relation.
 */
public abstract class GenericRelation extends VariabilityElement {
	protected String name;
	
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public boolean equals(Object o){
		boolean res = false;
		if (o instanceof GenericRelation) {
			GenericRelation f = (GenericRelation)o;
			return this.name.equalsIgnoreCase(f.getName());
		}
		return res;
	}
}
