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

//Based on article SPLC presented in 2008, can be found at : http://www.lsi.us.es/~dbc/dbc_archivos/pubs/benavides08-splc.pdf

/**
 * @author malawito
 *
 */
package es.us.isa.ChocoReasoner.questions;

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


public class ChocoValidConfigurationErrorsQuestion extends ChocoQuestion 
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
		
		valid=false;
		ChocoResult res = new ChocoResult();
		ValidProductQuestion vcq = new ChocoValidProductQuestion();

		if(p == null){
			throw new IllegalArgumentException("ValidProduct: Product not specified");
		}
		ChocoReasoner chr=(ChocoReasoner)r;
		Model chmodel=chr.getProblem();
		Collection<GenericFeature> selectedFeats = this.p.getFeatures();
		Collection<GenericFeature> allFeats = chr.getAllFeatures();
		Collection<GenericFeature> deselectedFeats = new ArrayList<GenericFeature>();
		Map<String, IntegerVariable> problemVars =chr.getVariables();
		deselectedFeats.addAll(allFeats);
		deselectedFeats.removeAll(selectedFeats);
		//Check if its necessary to  do anything or not
		vcq.setProduct(p);
		r.ask(vcq);
		valid=vcq.isValid();
		if(isValid()){
			System.out.println("The configuration has not errors");
		}else{
		//Has errors, then do anything...
			
			Iterator<GenericFeature> it=allFeats.iterator();
			Map<GenericFeature,IntegerVariable> Os=new HashMap<GenericFeature,IntegerVariable>();
			Map<GenericFeature,IntegerVariable> Ds=new HashMap<GenericFeature,IntegerVariable>();
			Map<GenericFeature,IntegerVariable> Ss=new HashMap<GenericFeature,IntegerVariable>();
			Map<GenericFeature,IntegerVariable> Fs=new HashMap<GenericFeature,IntegerVariable>();


			while (it.hasNext()){
				GenericFeature feat = it.next();

				IntegerVariable S = null;
				IntegerVariable D = null;
				IntegerVariable F = problemVars.get(feat.getName());
				IntegerVariable O=null;
				Constraint tempconsO=null;
				if(selectedFeats.contains(feat)){
					O = makeIntVar("O"+feat.getName(),0,1);
					tempconsO=eq(O,1);
				}
				if(deselectedFeats.contains(feat)){
					O = makeIntVar("O"+feat.getName(),0,1);
					tempconsO=eq(O,0);
				}
				chmodel.addConstraint(tempconsO);
				S=makeIntVar("S"+feat.getName(),0,1);
				D=makeIntVar("D"+feat.getName(),0,1);
				chmodel.addVariables(O,S,D);
				Os.put(feat,O);
				Ds.put(feat,D);
				Ss.put(feat,S);
				Fs.put(feat,F);
				
				Constraint First= ifOnlyIf(eq(F,0),(and((or((and(eq(O,0),eq(D,0))),and(eq(O,1),eq(D,1)))),eq(S,0))));
				Constraint Second= ifOnlyIf(eq(F,1),(and((or((and(eq(O,1),eq(S,0))),(and(eq(O,0),eq(S,1))))),eq(D,0))));	
				
			
				chmodel.addConstraint(First);
				chmodel.addConstraint(Second);
				
			}
			
			//Constraint to minimize
			IntegerVariable suma=makeIntVar("suma", 0, allFeats.size());
			chmodel.addVariable(suma);
			IntegerVariable[] sumadas = new IntegerVariable[2*allFeats.size()];
			Iterator<IntegerVariable> it2=Ss.values().iterator();
			Iterator<IntegerVariable> it3=Ds.values().iterator();

			int i=0;
			while(it2.hasNext()){
				sumadas[i]=it2.next();
				i++;
				
			}
			while(it3.hasNext()){
				sumadas[i]=it3.next();
				i++;
				
			}
			IntegerExpressionVariable sumatorio = sum(sumadas);
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
				Iterator<Entry<GenericFeature, IntegerVariable>> itSs =Ss.entrySet().iterator();
				while (itSs.hasNext()) {
					Entry<GenericFeature, IntegerVariable> aux = itSs.next();
					IntDomainVar VarSelected = sol2.getVar(aux.getValue());
					if(VarSelected.getVal()==1){
						System.out.println("The feature "+aux.getKey().getName()+" has to be selected");
					}
				}
				Iterator<Entry<GenericFeature, IntegerVariable>> itDs =Ds.entrySet().iterator();
				while (itDs.hasNext()) {
					Entry<GenericFeature, IntegerVariable> aux2 = itDs.next();
					IntDomainVar VarDeSelected = sol2.getVar(aux2.getValue());
					if(VarDeSelected.getVal()==1){
						System.out.println("The feature "+aux2.getKey().getName()+" has to be deselected");
					}
				}
				print=true;
			} while (sol2.nextSolution() == Boolean.TRUE);
			res.fillFields(sol2);
		}
		
		return res;
		
		}
}
	