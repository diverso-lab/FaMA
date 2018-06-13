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
package es.us.isa.ChocoReasoner4Exp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static choco.Choco.*;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

import es.us.isa.ChocoReasoner4Exp.ChocoQuestion;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;


public class ChocoReasoner extends es.us.isa.ChocoReasoner.ChocoReasoner{
	
	protected Map<GenericRelation,IntegerVariable> reifiVars;
	
	public ChocoReasoner() {
		super();
		reset();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.reifiVars= new HashMap<GenericRelation, IntegerVariable>();
	}

	@Override
	public PerformanceResult ask(Question q) {
		if (q == null) {
			throw new FAMAException("Question: Not specified");
		}
		PerformanceResult res;
		ChocoQuestion chq = (ChocoQuestion) q;
		chq.preAnswer(this);
		res = chq.answer(this);
		chq.postAnswer(this);
		return res;

	}
	
	private void reifyRelation(GenericRelation rel,
			Constraint mandatoryConstraint) {
		
		IntegerVariable mandatoryLookUp = makeIntVar(rel.getName(), 0,1);
		reifiVars.put(rel, mandatoryLookUp);
		Constraint observer =ifOnlyIf(mandatoryConstraint,eq(mandatoryLookUp,1));
		problem.addConstraint(observer);//add only this constraint	
		
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {

//		super.addMandatory(rel, child, parent);
//		Constraint mandatoryConstraint = dependencies.get(rel.getName());
		Constraint mandatoryConstraint = createMandatory(rel, child, parent);
		reifyRelation(rel, mandatoryConstraint);
		
	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {

//		super.addOptional(rel, child, parent);
//		Constraint optionalConstraint = dependencies.get(rel.getName());
		Constraint optionalConstraint = createOptional(rel, child, parent);
		reifyRelation(rel, optionalConstraint);
		
	}
	
	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {
		
//		super.addCardinality(rel, child, parent, cardinalities);
//		Constraint cardConstraint = dependencies.get(rel.getName());
		Constraint cardConstraint = createCardinality(rel, child, parent, cardinalities);
		reifyRelation(rel, cardConstraint);

	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

//		super.addRequires(rel, origin, destination);
//		Constraint requiresConstraint = dependencies.get(rel.getName());
		Constraint requiresConstraint = createRequires(rel, origin, destination);
		reifyRelation(rel, requiresConstraint);
	}

	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature dest) {
		
//		super.addExcludes(rel, origin, dest);
//		Constraint excludesConstraint = dependencies.get(rel.getName());
		Constraint excludesConstraint = createExcludes(rel, origin, dest);
		reifyRelation(rel, excludesConstraint);
		
	}

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children, Collection<Cardinality> cardinalities) {
		
//		super.addSet(rel, parent, children, cardinalities);
//		Constraint setConstraint = dependencies.get(rel.getName());
		Constraint setConstraint = createSet(rel, parent, children, cardinalities);
		reifyRelation(rel, setConstraint);
		
	}

	public Map<GenericRelation,IntegerVariable> getReifiedVars(){
		return this.reifiVars;
	}

}
