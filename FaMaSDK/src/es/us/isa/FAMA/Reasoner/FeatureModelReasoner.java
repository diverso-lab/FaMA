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
package es.us.isa.FAMA.Reasoner;

import java.util.Collection;
import java.util.Iterator;


import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;


/**
 * It's imposes the methods to work with Feature models
 */
public abstract class FeatureModelReasoner extends Reasoner {
	abstract public void reset();
	abstract public void addFeature(GenericFeature feature,Collection<Cardinality> cardIt);
	abstract public void addRoot(GenericFeature feature);
	abstract public void addMandatory(GenericRelation rel,GenericFeature child, GenericFeature parent);
	abstract public void addOptional(GenericRelation rel,GenericFeature child, GenericFeature parent);
	abstract public void addCardinality(GenericRelation rel,GenericFeature child, GenericFeature parent,Iterator<Cardinality> cardinalities);
	abstract public void addSet(GenericRelation rel,GenericFeature parent, Collection<GenericFeature> children,Collection<Cardinality> cardinalities);
	abstract public void addExcludes(GenericRelation rel,GenericFeature origin, GenericFeature destination);
	abstract public void addRequires(GenericRelation rel,GenericFeature origin, GenericFeature destination);
	
}
