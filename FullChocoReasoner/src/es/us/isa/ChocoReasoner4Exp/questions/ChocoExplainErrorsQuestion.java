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
package es.us.isa.ChocoReasoner4Exp.questions;

import static choco.Choco.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner4Exp.ChocoQuestion;
import es.us.isa.ChocoReasoner4Exp.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class ChocoExplainErrorsQuestion extends ChocoQuestion implements
		ExplainErrorsQuestion {

	private Collection<Error> errors;

	
	public Collection<Error> getErrors() {
		return this.errors;
	}

	
	public void setErrors(Collection<Error> colErrors) {
		this.errors = colErrors;

	}

	
	public Class<? extends Reasoner> getReasonerClass() {
		return ChocoReasoner.class;
	}

	public PerformanceResult answer(Reasoner r) throws FAMAException {

		ChocoResult res = new ChocoResult();
		ChocoReasoner chReasoner = (ChocoReasoner) r;
		

		if ((errors == null) || errors.isEmpty()) {
			errors = new LinkedList<Error>();
			return res;
		}
		Model p = chReasoner.getProblem();
		Map<String, IntegerVariable> vars = chReasoner.getVariables();
		Map<String, IntegerExpressionVariable> setVars = chReasoner
				.getSetRelations();
		Iterator<Error> itE = this.errors.iterator();

		// mientras haya errores
		while (itE.hasNext()) {
			// crear una lista de constraints, que impondremos segun las
			// observaciones
			Solver sol = new CPSolver();
			List<Constraint> cons4obs = new ArrayList<Constraint>();
			Error e = itE.next();
			Observation obs = e.getObservation();
			Map<? extends VariabilityElement, Object> values = obs
					.getObservation();
			Iterator<?> its = values.entrySet().iterator();

			// mientras haya observations
			// las imponemos al problema como restricciones
			while (its.hasNext()) {
				try {
					Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) its
							.next();
					Constraint cn;
					int value = (Integer) entry.getValue();
					VariabilityElement ve = entry.getKey();
					if (ve instanceof GenericFeature) {
						IntegerVariable arg0 = vars.get(ve.getName());
						cn = eq(arg0, value);
					} else {
						IntegerExpressionVariable arg0 = setVars.get(ve
								.getName());
						cn = eq(arg0, value);
					}
					p.addConstraint(cn);
					cons4obs.add(cn);
				} catch (ClassCastException exc) {
				}
			}

			// la funcion a maximizar sera el sumatorio de las vbles
			// reificadas
			IntegerVariable[] reifieds = new IntegerVariable[chReasoner
					.getReifiedVars().size()];
			Iterator<IntegerVariable> it = chReasoner.getReifiedVars().values()
					.iterator();
			int i = 0;
			while (it.hasNext()) {
				reifieds[i] = it.next();
				i++;
			}

			IntegerVariable suma = Choco.makeIntVar("suma", 0, chReasoner
					.getRelations().size());
			IntegerExpressionVariable sumatorio = Choco.sum(reifieds);
			Constraint sumReifieds = Choco.eq(suma, sumatorio);
			p.addConstraint(sumReifieds);
			cons4obs.add(sumReifieds);
			

			sol.read(p);
			// IntDomainVar result=sol.getVar(suma);
			sol.maximize(sol.getVar(suma), false);

			Solver sol2 = new CPSolver();
			Constraint cons2 = Choco.eq(suma, sol.getVar(suma).getVal());
			cons4obs.add(cons2);
			p.addConstraint(cons2);

			sol2.read(p);

			sol2.solve();
			Collection<Explanation> explanations = new LinkedList<Explanation>();

			do {
				Iterator<Entry<GenericRelation, IntegerVariable>> itRel = chReasoner
						.getReifiedVars().entrySet().iterator();
				Explanation exp = new Explanation();
				while (itRel.hasNext()) {
					Entry<GenericRelation, IntegerVariable> aux = itRel.next();
					IntDomainVar solVar = sol2.getVar(aux.getValue());
					if (solVar.getVal() == 0) {
						exp.addRelation(aux.getKey());
					}
				}
				if (!explanations.contains(exp)){
					explanations.add(exp);
				}
			} while (sol2.nextSolution() == Boolean.TRUE);
			
			for (Explanation exp:explanations){
				e.addExplanation(exp);
			}
			
			// finalmente, eliminamos las constraints
			Iterator<Constraint> removeCons = cons4obs.iterator();
			while (removeCons.hasNext()) {
				p.removeConstraint(removeCons.next());
			}
			p.removeVariable(suma);
//			res.fillFields(sol);
			res.addFields(sol);
		}

		
		return res;

	}

}
