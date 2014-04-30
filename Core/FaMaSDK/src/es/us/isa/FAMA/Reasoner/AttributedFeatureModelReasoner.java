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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.extended.ConstantIntConverter;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.StringDomainIntConverter;

/**
 * This class imposes the methods that an Attributed Feature model have to have to work properly.
 */
public abstract class AttributedFeatureModelReasoner extends FeatureModelReasoner {

	protected ConstantIntConverter constantIntConverter;
	
	public AttributedFeatureModelReasoner(){
		constantIntConverter = new ConstantIntConverter();
		constantIntConverter.addIntConverter(new StringDomainIntConverter());
	}
	
	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {
		
		GenericAttributedFeature attChild = (GenericAttributedFeature) child;
		GenericAttributedFeature attParent = (GenericAttributedFeature) parent;
		addCardinality_(rel,attChild,attParent,cardinalities);
		
	}

	protected abstract void addCardinality_(GenericRelation rel, GenericAttributedFeature child,
			GenericAttributedFeature parent, Iterator<Cardinality> cardinalities);
	
	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {
		
		GenericAttributedFeature attOrigin = (GenericAttributedFeature) origin;
		GenericAttributedFeature attDestination = (GenericAttributedFeature) destination;
		addExcludes_(rel, attOrigin, attDestination);
		
	}
	
	protected abstract void addExcludes_(GenericRelation rel, GenericAttributedFeature origin,
			GenericAttributedFeature destination);

	@Override
	public void addFeature(GenericFeature feature,
			Collection<Cardinality> cardIt) {

		GenericAttributedFeature f = (GenericAttributedFeature) feature;
		addFeature_(f,cardIt);

	}

	protected abstract void addFeature_(GenericAttributedFeature feature, Collection<Cardinality> cardIt);
	
	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {

		GenericAttributedFeature attChild = (GenericAttributedFeature) child;
		GenericAttributedFeature attParent = (GenericAttributedFeature) parent;
		addMandatory_(rel, attChild, attParent);
		
	}
	
	protected abstract void addMandatory_(GenericRelation rel, 
			GenericAttributedFeature child, GenericAttributedFeature parent);

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		
		GenericAttributedFeature attChild = (GenericAttributedFeature) child;
		GenericAttributedFeature attParent = (GenericAttributedFeature) parent;
		addOptional_(rel, attChild, attParent);

	}

	protected abstract void addOptional_(GenericRelation rel, GenericAttributedFeature child,
			GenericAttributedFeature parent);
	
	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		GenericAttributedFeature attOrigin = (GenericAttributedFeature) origin;
		GenericAttributedFeature attDestination = (GenericAttributedFeature) destination;
		addRequires_(rel, attOrigin, attDestination);

	}

	protected abstract void addRequires_(GenericRelation rel, GenericAttributedFeature origin,
			GenericAttributedFeature destination);
	
	@Override
	public void addRoot(GenericFeature feature) {
		
		GenericAttributedFeature f = (GenericAttributedFeature) feature;
		addRoot_(f);

	}
	
	protected abstract void addRoot_(GenericAttributedFeature feature); 

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		
		GenericAttributedFeature p = (GenericAttributedFeature) parent;
		Collection<GenericAttributedFeature> c = new ArrayList<GenericAttributedFeature>();
		Iterator<GenericFeature> it = children.iterator();
		while (it.hasNext()){
			GenericFeature aux = it.next();
			if (aux instanceof GenericAttributedFeature){
				c.add((GenericAttributedFeature)aux);
			}
			else{
				throw new IllegalArgumentException("No AttributedFeature detected");
			}
		}
		addSet_(rel,p,c,cardinalities);

	}
	
	protected abstract void addSet_(GenericRelation rel, GenericAttributedFeature parent,
			Collection<GenericAttributedFeature> children, 
			Collection<Cardinality> cardinalities);
	
	abstract public void addConstraint(Constraint c);

	public ConstantIntConverter getConstantIntConverter() {
		return constantIntConverter;
	}

	public void setConstantIntConverter(ConstantIntConverter constantIntConverter) {
		this.constantIntConverter = constantIntConverter;
	}
	
	
	
}
