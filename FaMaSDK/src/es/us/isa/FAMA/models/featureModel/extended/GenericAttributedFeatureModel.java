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

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;

public abstract class GenericAttributedFeatureModel extends GenericFeatureModel{
	
	protected ConstantIntConverter converter;
	
	public abstract Collection<? extends GenericAttributedFeature> getAttributedFeatures();
		
	public abstract GenericAttributedFeature searchFeatureByName(String featurename);
	
	public Collection<? extends GenericFeature> getFeatures(){
		return this.getAttributedFeatures();
	}
	
	public void setConstantIntConverter(ConstantIntConverter conv){
		converter = conv;
	}
	
	public ConstantIntConverter getConstantIntConverter(){
		return converter;
	}
	
}
