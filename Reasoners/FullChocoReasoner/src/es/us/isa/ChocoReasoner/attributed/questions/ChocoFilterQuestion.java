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
package es.us.isa.ChocoReasoner.attributed.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static choco.Choco.*;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class ChocoFilterQuestion extends ChocoQuestion implements
		FilterQuestion {

	private Map<VariabilityElement,Integer> elemsSet;
	private List<Constraint> filterConstraints;
	
	public ChocoFilterQuestion(){
		super();
		elemsSet = new HashMap<VariabilityElement,Integer>();
		filterConstraints = new ArrayList<Constraint>();
	}
	
	
	public void addValue(VariabilityElement ve, int value) {
		if (!elemsSet.containsKey(ve))
			elemsSet.put(ve,value);
	}

	
	public void removeValue(VariabilityElement ve) {
		Iterator<Entry<VariabilityElement,Integer>> it = elemsSet.entrySet().iterator();
		while (it.hasNext()) {
			Entry<VariabilityElement,Integer> e = it.next();
			if (e.getKey().getName().equalsIgnoreCase(ve.getName()))
				it.remove();
		}
	}
	
	
	public void preAnswer(Reasoner choco){
		ChocoReasoner r = (ChocoReasoner)choco;
		Iterator<Entry<VariabilityElement,Integer>> it = elemsSet.entrySet().iterator();
		Map<String,IntegerVariable> vars = r.getVariables();
		Map<String,IntegerExpressionVariable> rels = r.getSetRelations();
		Map<String,IntegerVariable> atts = r.getAttributesVariables();
		while (it.hasNext()) {
			Entry<VariabilityElement,Integer> e = it.next();
			VariabilityElement v = e.getKey();//FIXME f == null :(
			Model p = r.getProblem();
			int arg1 = e.getValue().intValue();
			Constraint aux;
			//the constraint is created to not have a solution for the problem
			IntegerVariable errorVar = makeIntVar("error",0,0,"cp:no_decision");
			Constraint error = eq(1,errorVar);
			if (v instanceof GenericAttributedFeature){
				IntegerVariable arg0 = vars.get(v.getName());			
				if(!r.getAllFeatures().contains((GenericAttributedFeature)v)){
					if (e.getValue() == 0){
						System.err.println("The feature "+v.getName()+" do not exist on the model");
					}
					else{
						p.addConstraint(error);
						this.filterConstraints.add(error);
						System.err.println("The feature "+v.getName()+" do not exist, and can not be added");
					}
				}
				else{
					aux = eq(arg0, arg1);
					p.addConstraint(aux);
					this.filterConstraints.add(aux);
				}
				
			}
			else if (v instanceof GenericRelation){
				IntegerExpressionVariable arg0 = rels.get(v.getName());			
				if(!r.getSetRelations().keySet().contains(v.getName())){
					if (e.getValue() == 0){
						System.err.println("The relation "+v.getName()+"do not exist already in to the model");
					}
					else{
						p.addConstraint(error);
						this.filterConstraints.add(error);
						System.err.println("The relation "+v.getName()+"do not exist, and can not be added");
					}
				}
				else{
					aux = eq(arg0, arg1);
					p.addConstraint(aux);
					this.filterConstraints.add(aux);
				}
			}
			else if (v instanceof GenericAttribute){
				GenericAttribute attAux = (GenericAttribute)v;
				String attName = attAux.getFeature().getName()+"."+v.getName();
				IntegerVariable arg0 = atts.get(attName);				
				if(!r.getAllAttributes().contains((GenericAttribute)v)){
					if (e.getValue() == 0){
						System.err.println("The attribute "+v.getName()+" do not exist on the model");
					}
					else{
						p.addConstraint(error);
						this.filterConstraints.add(error);
						System.err.println("The attribute "+v.getName()+" do not exist, and can not be added");
					}
				}
				else{
					aux = eq(arg0, arg1);
					p.addConstraint(aux);
					this.filterConstraints.add(aux);
				}
			}
			else{
				System.err.println("Type of the Variability element "+v.getName()+" not recognized");
			}
			
		}		
		
	}
	
	
	public void postAnswer(Reasoner r) {
		Iterator<Constraint> it = this.filterConstraints.iterator();
		Model p = ((ChocoReasoner) r).getProblem();
		while (it.hasNext()){
			Constraint cons = it.next();
			p.removeConstraint(cons);
			it.remove();
		}
	}

}
