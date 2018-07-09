/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.Sat4jReasoner.reified.questions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.pb.OptToPBSATAdapter;
import org.sat4j.pb.core.PBSolver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.Sat4jReasoner.Sat4jResult;
import es.us.isa.Sat4jReasoner.reified.Sat4jReifiedQuestion;
import es.us.isa.Sat4jReasoner.reified.Sat4jReifiedReasoner;

public class Sat4jExplainErrorsQuestion extends Sat4jReifiedQuestion implements
		ExplainErrorsQuestion {

	private Collection<Error> errors;

	@Override
	public Collection<Error> getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Collection<Error> colErrors) {
		errors = colErrors;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		// TODO Auto-generated method stub
		PerformanceResult res = new Sat4jResult();
		Sat4jReifiedReasoner sat4jreified = (Sat4jReifiedReasoner) r;

		Iterator<Error> itErrors = errors.iterator();
		while (itErrors.hasNext()) {
			Error e = itErrors.next();
			// en primer lugar, nos creamos una staged configuration
			// con los elementos de la observacion asociada al error
			Configuration conf = new Configuration();
			Observation obs = e.getObservation();
			Map<? extends VariabilityElement, Object> values = obs
					.getObservation();
			Iterator<?> entryIterator = values.entrySet().iterator();
			while (entryIterator.hasNext()) {
				Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) entryIterator
						.next();
				Integer val = (Integer) entry.getValue();
				conf.addElement(entry.getKey(), val);
			}
			sat4jreified.applyStagedConfiguration(conf);
			PBSolver defaultSolver = org.sat4j.pb.SolverFactory.instance().defaultSolver();
			boolean hasSolution;
			try {
//				WeightedMaxSatDecorator maxsat = new WeightedMaxSatDecorator(defaultSolver);
				WeightedMaxSatDecorator maxsat = new WeightedMaxSatDecorator(defaultSolver);

				Iterator<IVecInt> itClauses = sat4jreified.getClauses()
						.iterator();
				maxsat.newVar(sat4jreified.getClauses().size());
				while (itClauses.hasNext()) {
					IVecInt clause = itClauses.next();
					
					maxsat.addClause(clause);
				}
				IVecInt reifiedVars = sat4jreified.getReifiedVars();
				maxsat.addLiteralsToMinimize(reifiedVars);
				
				OptToPBSATAdapter opt = new OptToPBSATAdapter((IOptimizationProblem) maxsat);

				hasSolution = opt.isSatisfiable();
				if (hasSolution) {
					Explanation exp = new Explanation();
					int[] sol = opt.model();
					Map<Integer, GenericRelation> rels = sat4jreified
							.getReifiedRelations();
					for (int i = 0; i < sol.length; i++){
						int aux = Math.abs(sol[i]);
						if (rels.containsKey(aux) && (sol[i] < 0)){
							GenericRelation rel = rels.get(aux);
							exp.addRelation(rel);
						}
					}
					e.addExplanation(exp);
				}
				
			} catch (ContradictionException ex) {
				ex.printStackTrace();
			} catch (TimeoutException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

		}

		return res;
	}

}
