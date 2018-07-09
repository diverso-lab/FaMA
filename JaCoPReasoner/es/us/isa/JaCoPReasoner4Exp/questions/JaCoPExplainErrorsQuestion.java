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
package es.us.isa.JaCoPReasoner4Exp.questions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import JaCoP.constraints.Constraint;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.MostConstrainedDynamic;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.JaCoPReasoner4Exp.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.JaCoPResult;

public class JaCoPExplainErrorsQuestion extends
		es.us.isa.JaCoPReasoner4Exp.JaCoPQuestion implements
		ExplainErrorsQuestion {

	private Collection<Error> errors;

	
	public Collection<Error> getErrors() {
		return errors;
	}

	
	public void setErrors(Collection<Error> colErrors) {
		errors = colErrors;
	}

	
	public PerformanceResult answer(JaCoPReasoner r) {

		JaCoPResult res = new JaCoPResult();
		if ((errors == null) || errors.isEmpty()) {
			errors = new LinkedList<Error>();
			return res;
		}
		
		FDstore store = r.getStore();
		Collection<FDV> reifiedVars = r.getReifiedVars().values();
		FDV[] array = reifiedVars.toArray(new FDV[1]);
		FDV sum = new FDV(store,"_reifiedSum",0,array.length);
		Constraint ctr = new Sum(array, sum); 
		store.impose(ctr);
		store.consistency();
//		Search label = new DepthFirstSearch();
//		ComparatorVariable comp = new MostConstrainedDynamic();
//		SelectChoicePoint select = new SimpleSelect(array, comp, new IndomainMin());
//		label.getSolutionListener().searchAll(true);
//		label.getSolutionListener().recordSolutions(true);
		
		Iterator<Error> itE = this.errors.iterator();
		// mientras haya errores
		while (itE.hasNext()) {
//			store.consistency();
			Error e = itE.next();
			Observation obs = e.getObservation();
			Map<? extends VariabilityElement, Object> values = obs
					.getObservation();
			Iterator<?> its = values.entrySet().iterator();
			Configuration stagedConf = new Configuration();
	
			while (its.hasNext()) {
				Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) its.next();
				int value = (Integer) entry.getValue();
				VariabilityElement ve = entry.getKey();
				stagedConf.addElement(ve, value);
			}
			
			r.applyStagedConfiguration(stagedConf);
			
//			FDstore store = r.getStore();
//			int storeLevel = r.getStoreLevel();
//			store.setLevel(storeLevel + 1);
//			Collection<FDV> reifiedVars = r.getReifiedVars().values();
//			FDV[] array = reifiedVars.toArray(new FDV[1]);
//			FDV sum = new FDV(store,"_reifiedSum",0,array.length);
			
			
//			Constraint ctr = new Sum(array, sum); 
//			store.impose(ctr);
			
			store.consistency();
			Search label = new DepthFirstSearch();
			ComparatorVariable comp = new MostConstrainedDynamic();
			SelectChoicePoint select = new SimpleSelect(array, comp, new IndomainMin());
			label.getSolutionListener().searchAll(true);
			label.getSolutionListener().recordSolutions(true);
			boolean enc = false;
			for (int i = 0; i < array.length && !enc; i++){
				int newStoreLevel = store.level;
				store.setLevel(newStoreLevel + 1);
				Constraint aux = new XeqC(sum,i);
				store.impose(aux);
				enc = label.labeling(store, select);
				store.removeLevel(newStoreLevel + 1);
				store.setLevel(newStoreLevel);
			}
			//boolean result = label.labeling(store, select,sum);
			Variable[] variables = label.getSolutionListener().getVariables();
			int nSol = label.getSolutionListener().solutionsNo();
			int[][] resValues = label.getSolutionListener().getSolutions();
			for (int i = 0; i < nSol; i++){
				Explanation expl = new Explanation();
				for (int j = 0; j < resValues[i].length; j++){
					if (resValues[i][j] == 1){
						//relacion desactivada
						String nameRel = variables[j].id();
						Iterator<GenericRelation> itRel = r.getReifiedVars().keySet().iterator();
						while (itRel.hasNext()){
							GenericRelation rel = itRel.next();
							if (nameRel.equalsIgnoreCase(rel.getName())){
								expl.addRelation(rel);
							}
						}
					}
				}
				e.addExplanation(expl);
			}
//			storeLevel = store.level;
//			store.removeLevel(storeLevel);
//			store.setLevel(storeLevel - 1);
			
			
			res.addFields(label);
			
			r.unapplyStagedConfigurations();
		}

		return res;
	}

}
