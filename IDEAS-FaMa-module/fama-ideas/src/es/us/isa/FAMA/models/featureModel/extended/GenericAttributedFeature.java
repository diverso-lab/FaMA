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
package es.us.isa.FAMA.models.featureModel.extended;

import java.util.Collection;

import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

public abstract class GenericAttributedFeature extends GenericFeature {

	public abstract Collection<? extends GenericAttribute> getAttributes();
	
	public abstract Collection<? extends Constraint> getInvariants();
	
	public abstract GenericAttribute searchAttributeByName(String name);
	
	public void addAttribute(GenericAttribute att){
		//con esto logramos vincular al atributo con su feature
		att.feature = this;
		newAttribute(att);
	}
	
	protected abstract void newAttribute(GenericAttribute att);
}
