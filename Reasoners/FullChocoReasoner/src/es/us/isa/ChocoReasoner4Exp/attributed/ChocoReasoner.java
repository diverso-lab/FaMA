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
package es.us.isa.ChocoReasoner4Exp.attributed;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static choco.Choco.*;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

import es.us.isa.ChocoReasoner4Exp.attributed.ChocoQuestion;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;

import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;


public class ChocoReasoner extends es.us.isa.ChocoReasoner.attributed.ChocoReasoner{
	
	protected Map<GenericRelation, IntegerVariable> reifiVars;
	
	public ChocoReasoner() {
		super();
		reset();
	}
	
	@Override
	public void reset() {	
		super.reset();
		reifiVars = new HashMap<GenericRelation, IntegerVariable>();
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
			Constraint constraint) {
		
		IntegerVariable mandatoryLookUp = makeIntVar(rel.getName(), 0,1);
		reifiVars.put(rel, mandatoryLookUp);
		Constraint observer =ifOnlyIf(constraint,eq(mandatoryLookUp,1));
		problem.addConstraint(observer);//add only this constraint	
		
	}
	
	@Override
	protected void addMandatory_(GenericRelation rel, GenericAttributedFeature child,
			GenericAttributedFeature parent) {

		Constraint mandatoryConstraint = createMandatory(rel, child, parent);
		reifyRelation(rel, mandatoryConstraint);
		
	}
	
	@Override
	protected void addOptional_(GenericRelation rel, GenericAttributedFeature child,
			GenericAttributedFeature parent) {

		Constraint optionalConstraint = createOptional(rel, child, parent);
		reifyRelation(rel, optionalConstraint);
		
	}
	
	@Override
	protected void addCardinality_(GenericRelation rel, GenericAttributedFeature child,
			GenericAttributedFeature parent, Iterator<Cardinality> cardinalities) {

		Constraint cardConstraint = createCardinality(rel, child, parent, cardinalities);
		reifyRelation(rel, cardConstraint);

	}

	@Override
	protected void addRequires_(GenericRelation rel, GenericAttributedFeature origin,
			GenericAttributedFeature destination) {

		Constraint requiresConstraint = createRequires(rel, origin, destination);
		reifyRelation(rel, requiresConstraint);

	}

	@Override
	protected void addExcludes_(GenericRelation rel, GenericAttributedFeature origin,
			GenericAttributedFeature dest) {

		Constraint excludesConstraint = createExcludes(rel, origin, dest);
		reifyRelation(rel, excludesConstraint);

	}

	@Override
	public void addFeature_(GenericAttributedFeature f, Collection<Cardinality> cards) {
		
		createFeature(f, cards);
		addAttributes(f);
		
	}
	

	private void addAttributes(GenericAttributedFeature f) {
		
		IntegerVariable varFeat = variables.get(f.getName());
		createAttributes(f);
		// una vez procesados todos los atributos, si la feature esta presente
		// tenemos en cuenta las invariantes
		Iterator<? extends es.us.isa.FAMA.models.featureModel.Constraint> itInv = f
				.getInvariants().iterator();
		while (itInv.hasNext()){
			es.us.isa.FAMA.models.featureModel.Constraint inv = itInv.next();
			Constraint c = createInvariant(f, varFeat, inv);
			reifyRelation(inv, c);
		}
	}
	
	@Override
	protected void addSet_(GenericRelation rel, GenericAttributedFeature parent,
			Collection<GenericAttributedFeature> children, Collection<Cardinality> cardinalities) {

		Constraint setConstraint = createSet(rel, parent, children, cardinalities);
		reifyRelation(rel, setConstraint);
		
	}
	
	@Override
	public void addConstraint(es.us.isa.FAMA.models.featureModel.Constraint c) {

		Constraint cons = createConstraint(c);
		reifyRelation(c,cons);
		
	}

	public Map<GenericRelation, IntegerVariable> getReifiedVars() {
		
		return reifiVars;
		
	}
	
}
