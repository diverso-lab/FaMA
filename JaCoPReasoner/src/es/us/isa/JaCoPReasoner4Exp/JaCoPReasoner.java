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
package es.us.isa.JaCoPReasoner4Exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import JaCoP.constraints.Constraint;
import JaCoP.constraints.IfThenElse;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.Xor;
import JaCoP.core.FDV;


import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.JaCoPReasoner4Exp.JaCoPQuestion;

public class JaCoPReasoner extends es.us.isa.JaCoPReasoner.JaCoPReasoner {

	private Map<GenericRelation,FDV> reifiedVars;
	
	
	public JaCoPReasoner(){
		super();
		reifiedVars = new HashMap<GenericRelation,FDV>();
	}
	
	public void reset(){
		super.reset();
		reifiedVars = new HashMap<GenericRelation,FDV>();
	}
	
	private void reifyRelation(GenericRelation rel, PrimitiveConstraint c){
		FDV var = new FDV(store,rel.getName(),0,1);
		Constraint reifiedConstraint = new Xor(c,var); // c <-> ¬B,  para minimizar el nº de constraints no satisfechas
		reifiedVars.put(rel, var);
		store.impose(reifiedConstraint);
	}
	
	@Override
	public void addMandatory(GenericRelation rel,GenericFeature child, GenericFeature parent) {
		PrimitiveConstraint constraint = createMandatory(rel, child, parent);
		reifyRelation(rel,constraint);
	}


	@Override
	public void addOptional(GenericRelation rel,GenericFeature child, GenericFeature parent) {
		PrimitiveConstraint constraint = createOptional(rel, child, parent);
		reifyRelation(rel,constraint);
	}

	@Override
	public void addCardinality(GenericRelation rel,GenericFeature child, GenericFeature parent,
			Iterator<Cardinality> cardinalities) {
		
		PrimitiveConstraint constraint = createCardinality(rel, child, parent,
				cardinalities);
		reifyRelation(rel,constraint);
		
	}

	@Override
	public void addSet(GenericRelation rel,GenericFeature parent, Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		
		PrimitiveConstraint constraint = createSet(rel, parent, children,
				cardinalities);
		reifyRelation(rel,constraint);
		
	}

	@Override
	public void addExcludes(GenericRelation rel,GenericFeature origin, GenericFeature destination) {
		
		PrimitiveConstraint constraint = createExcludes(rel, origin,
				destination);
		reifyRelation(rel,constraint);
		
	}

	@Override
	public void addRequires(GenericRelation rel,GenericFeature origin, GenericFeature destination) {
		
		PrimitiveConstraint constraint = createRequires(rel, origin,
				destination);
		reifyRelation(rel,constraint);

	}

	public Map<GenericRelation, FDV> getReifiedVars() {
		return reifiedVars;
	}
	
	public PerformanceResult ask(Question q)  {
		if(q == null){
			throw new FAMAParameterException("Question: Not specified");
		}
		PerformanceResult res;
		JaCoPQuestion jq = (JaCoPQuestion)q;
		if (heuristics != null)
			jq.setHeuristics(heuristics);
		jq.preAnswer(this);
		res = jq.answer(this);
		jq.postAnswer(this);
		consistent = true;
		return res;
	}
	
}
