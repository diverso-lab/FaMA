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
package es.us.isa.ChocoReasoner.questions;
/**
 * @author malawito
 *
 */
import static choco.Choco.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;


import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;


import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;


import es.us.isa.FAMA.models.featureModel.GenericFeature;

import es.us.isa.FAMA.models.featureModel.Product;




public class ChocoValidConfigurationErrorsQuestionAW extends ChocoQuestion 
implements ValidConfigurationErrorsQuestion{

		
	private Product p;
	
	boolean valid;
	
	public boolean isValid() {
		return valid;
	}

	public void setProduct(Product p) {
		this.p = p;
	}
	public PerformanceResult answer(Reasoner r) throws FAMAException{
		if(r == null){
			throw new FAMAException("Reasoner: Reasoner not specified");
		}
		valid=false;
		ChocoResult res = new ChocoResult();
		ValidProductQuestion vcq = new ChocoValidProductQuestion();

		if(p == null){
			throw new FAMAException("ValidProduct: Product not specified");
		}
		ChocoReasoner chr=(ChocoReasoner)r;
		Model chmodel=chr.getProblem();
		Collection<GenericFeature> selectedFeats = this.p.getFeatures();
		Collection<GenericFeature> allFeats = chr.getAllFeatures();
		Collection<GenericFeature> deselectedFeats = new ArrayList<GenericFeature>();
		Map<String, IntegerVariable> problemVars =chr.getVariables();
		deselectedFeats.addAll(allFeats);
		deselectedFeats.removeAll(selectedFeats);
		vcq.setProduct(p);
		r.ask(vcq);
		valid=vcq.isValid();
		if(isValid()){
			System.err.println("The configuration has not errors");
		}else{
			
			Iterator<GenericFeature> it=allFeats.iterator();
			Map<GenericFeature,IntegerVariable> ObserverVars=new HashMap<GenericFeature,IntegerVariable>();
			Map<GenericFeature,IntegerVariable> ValueVars=new HashMap<GenericFeature,IntegerVariable>();

			while (it.hasNext()){
				GenericFeature feat = it.next();
				IntegerVariable ValueVar = null;
				IntegerVariable ObsVar;
				if(selectedFeats.contains(feat)){
					ValueVar=makeIntVar("ValueVar"+feat.getName(),1,1);
				}
				if(deselectedFeats.contains(feat)){
					ValueVar=makeIntVar("ValueVar"+feat.getName(),0,0);

				}
				ObsVar=makeIntVar("Observer"+feat.getName(),0,1);
				ObserverVars.put(feat,ObsVar);
				ValueVars.put(feat,ValueVar);
				chmodel.addVariable(ValueVar);
				chmodel.addVariable(ObsVar);
				IntegerVariable temp =problemVars.get(feat.getName());
				Constraint cons=ifOnlyIf(neq(ValueVar,temp),eq(ObsVar,1));
				chmodel.addConstraint(cons);
				
			}
			//Constraint to minimize
			IntegerVariable suma=makeIntVar("suma", 0, ObserverVars.size());
			chmodel.addVariable(suma);
			IntegerVariable[] Observadas = new IntegerVariable[ObserverVars.size()];
			Iterator<IntegerVariable> it2=ObserverVars.values().iterator();
			int i=0;
			while(it2.hasNext()){
				Observadas[i]=it2.next();
				i++;
			}
			IntegerExpressionVariable sumatorio = sum(Observadas);
			Constraint sumObservers = eq(suma,sumatorio);
			chmodel.addConstraint(sumObservers);
			Solver sol = new CPSolver();
			

			Solver sol2 = new CPSolver();
			sol.read(chmodel);
			this.heuristic = new MinDomain(sol,sol.getVar(chr.getVars()));
//			this.heuristic.setVars(sol.getVar(chr.getVars()));
//			sol.setVarIntSelector(heuristic);
			sol.minimize(sol.getVar(suma), false);
			Constraint cons2 = eq(suma, sol.getVar(suma).getVal());
			chmodel.addConstraint(cons2);
			sol2.read(chmodel);
			this.heuristic = new MinDomain(sol2,sol2.getVar(chr.getVars()));
//			this.heuristic.setVars(sol2.getVar(chr.getVars()));
//			sol2.setVarIntSelector(heuristic);
			sol2.solve();
			int index=1;
			boolean print=false;
			System.out.println("Solution "+index);
			index++;
			do {
				if(print==true){
				System.out.println("Solution "+index);
				index++;
				print=false;
				}
				Iterator<Entry<GenericFeature, IntegerVariable>> itRel =ObserverVars.entrySet().iterator();
				while (itRel.hasNext()) {
					Entry<GenericFeature, IntegerVariable> aux = itRel.next();
					IntDomainVar obsSolVar = sol2.getVar(aux.getValue());
					IntegerVariable problemValueVar = ValueVars.get(aux.getKey());
					IntDomainVar valueSolVar = sol2.getVar(problemValueVar);
					if(valueSolVar==null){
						throw new IllegalArgumentException("por tratar");
					}
					if ((obsSolVar.getVal() == 1)&&(valueSolVar.getVal()==1)) {
						System.out.println("The feature "+aux.getKey().getName()+" has to be deselected");
						print=true;
					}
					if((obsSolVar.getVal() == 1)&&(valueSolVar.getVal()==0)){
						System.out.println("The feature "+aux.getKey().getName()+" has to be selected");
						print=true;

					}

					
				}
				
			} while (sol2.nextSolution() == Boolean.TRUE);
			res.fillFields(sol);
		}
		
		return res;
		
		}
}
	
