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
package es.us.isa.FAMA.models.FAMAAttributedfeatureModel;

import es.us.isa.FAMA.models.featureModel.Constraint;

/**
 * @author   trinidad, Manuel Nieto Ucl�s
 */
public abstract class Dependency extends Constraint {	
	protected AttributedFeature origin;
	protected AttributedFeature destination;
	
	protected Dependency(String name){
		this.name = name;
	}
	
	protected Dependency(AttributedFeature origin, AttributedFeature destination){
		this.origin = origin;
		this.destination = destination;
	}
	
	protected Dependency(String name, AttributedFeature origin, AttributedFeature destination){
		this.name = name;
		this.origin = origin;
		this.destination = destination;
	}
	
	/**
	 * @return
	 * @uml.property  name="origin"
	 */
	public AttributedFeature getOrigin(){
		return origin;
	}
	
	/**
	 * @param f
	 * @uml.property  name="origin"
	 */
	public void setOrigin(AttributedFeature f){
		if ( f != null) {
			origin = f;
		}
	}
	
	/**
	 * @return
	 * @uml.property  name="destination"
	 */
	public AttributedFeature getDestination(){
		return destination;
	}
	
	/**
	 * @param f
	 * @uml.property  name="destination"
	 */
	public void setDestination(AttributedFeature f){
		if ( f != null) {
			destination = f;
		}
	}
}
