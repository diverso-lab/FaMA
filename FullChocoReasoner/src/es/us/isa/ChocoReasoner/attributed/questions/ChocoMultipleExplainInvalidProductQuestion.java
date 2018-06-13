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

package es.us.isa.ChocoReasoner.attributed.questions;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.integer.IntVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.MultipleExplainInvalidProductQuestion;
import es.us.isa.FAMA.errors.ConfigurationExplaining;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;

public class ChocoMultipleExplainInvalidProductQuestion extends ChocoQuestion
		implements MultipleExplainInvalidProductQuestion {

	private Collection<ConfigurationExplaining> res;
	private Product invalidProduct; 
	
	@Override
	public Collection<ConfigurationExplaining> getProductExplaining() {
		return res;
	}

	public void setInvalidProduct(Product p){
		invalidProduct = p;
	}
	
	public PerformanceResult answer(Reasoner r){
		 
		/*
		 * previo: analizamos la validez del producto 
		 * 1. si es invalido
		 * 	  1.1. Creamos un conjunto de potentialsSelects
		 *         con todas las features no seleccionadas 
		 *         en invalidProduct.
		 *         Por cada feature, creamos una variable Si,
		 *         con las restriccion Sk = 1 <=> k = 1
		 *    1.2. Creamos un conjunto de potentialDeselects
		 *         con todas las features seleccionadas en 
		 *         invalidProduct 
		 *         Por cada feature, creamos una variable Di,
		 *         con las restriccion Dk = 1 <=> k = 0
		 *    1.3. Minimizamos Sk + Dk
		 *    1.4. Tomamos como configuracion las features "k"
		 *         que esten seleccionadas (k=1)
		 *         Tomamos como features seleccionadas los Sk = 1
		 *         Tomamos como features deseleccionadas los Dk = 1
		 *    1.5. Devolvemos el resultado
		 * 2. si es valido, devolvemos ese mismo, y 
		 *    los conjuntos de cambios vacios
		 */
		
		//inicialmente vamos a saltarnos el paso previo de comprobar la
		//validez
		
		//XXX en esta primera version, no vamos a tener en cuenta los atributos
		
		res = new LinkedList<ConfigurationExplaining>();
		
		ChocoReasoner choco = (ChocoReasoner) r;
		Map<GenericAttributedFeature,IntegerVariable> feats = new HashMap<GenericAttributedFeature, IntegerVariable>();
		Map<String,IntegerVariable> featsByName = choco.getVariables();
		Collection<GenericAttributedFeature> allFeats = choco.getAllFeatures();
		for (GenericAttributedFeature f:allFeats){
			feats.put(f, featsByName.get(f.getName()));
		}
		
		Set<Entry<GenericAttributedFeature,IntegerVariable>> entries = feats.entrySet();
		Collection<GenericFeature> invalidProductFeats = invalidProduct.getFeatures();
		Map<IntegerVariable,GenericFeature> selections = new HashMap<IntegerVariable, GenericFeature>();
		Map<IntegerVariable,GenericFeature> deselections = new HashMap<IntegerVariable, GenericFeature>();
		
		Model model = choco.getProblem();
		
		for (Entry<GenericAttributedFeature,IntegerVariable> e:entries){
			if (invalidProductFeats.contains(e.getKey())){
				//si aparece en la config invalida
				//la metemos como candidata a ser deseleccionada
				IntegerVariable v = Choco.makeBooleanVar("D-"+e.getValue().getName());
				Constraint c = Choco.ifOnlyIf(Choco.eq(v, 1), Choco.eq(e.getValue(), 0));
				model.addConstraint(c);
				deselections.put(v, e.getKey());
			}
			else{
				//si no aparece en la config invalida
				//la metemos como candidata a ser seleccionada
				IntegerVariable v = Choco.makeBooleanVar("S-"+e.getValue().getName());
				Constraint c = Choco.ifOnlyIf(Choco.eq(v, 1), Choco.eq(e.getValue(), 1));
				model.addConstraint(c);
				selections.put(v, e.getKey());
			}
		}
		
		Collection<IntegerVariable> varsToMin = new LinkedList<IntegerVariable>();
		varsToMin.addAll(deselections.keySet());
		varsToMin.addAll(selections.keySet());
		IntegerVariable[] varsToMinArray = varsToMin.toArray(new IntegerVariable[0]);
		IntegerVariable sumatorio = Choco.makeIntVar("sumatorio", 0, varsToMinArray.length);
		Constraint constraintSum = Choco.eq(sumatorio, Choco.sum(varsToMinArray));
		model.addConstraint(constraintSum);
		
		Solver solver = new CPSolver();
		solver.read(model);
		solver.minimize(solver.getVar(sumatorio), false);
		
		//XXX con esto vamos a sacar todas las soluciones
		Solver solver2 = new CPSolver();
		int changes = solver.getVar(sumatorio).getVal();
		model.addConstraint(Choco.eq(sumatorio, changes));
		
		solver2.read(model);
		boolean solution = solver2.solve();
		
		while (solution){
			List<GenericFeature> toSelect = new LinkedList<GenericFeature>();
			List<GenericFeature> toDeselect = new LinkedList<GenericFeature>();
			Product fixedProduct = new Product();
			
			fixedProduct.addAllFeatures(invalidProduct.getFeatures());
			Set<Entry<IntegerVariable,GenericFeature>> selectionEntries = selections.entrySet();
			Set<Entry<IntegerVariable,GenericFeature>> deselectionEntries = deselections.entrySet();
			
			for (Entry<IntegerVariable,GenericFeature> e:deselectionEntries){
				IntDomainVar var = solver2.getVar(e.getKey());
				if (var.getVal() == 1){
					//la feature ha sido deseleccionada
					toDeselect.add(e.getValue());
					//ademas, tenemos que eliminarla de la config original
					fixedProduct.removeFeature(e.getValue());
				}
			}
			
			for (Entry<IntegerVariable,GenericFeature> e:selectionEntries){
				IntDomainVar var = solver2.getVar(e.getKey());
				if (var.getVal() == 1){
					//la feature ha sido seleccionada
					toSelect.add(e.getValue());
					//ademas, tenemos que añadirla a la config original
					fixedProduct.addFeature(e.getValue());
				}
			}
			
			ConfigurationExplaining aux = new ConfigurationExplaining(toSelect, toDeselect, invalidProduct, fixedProduct);
			res.add(aux);
			solution = solver2.nextSolution();
		}
		
		ChocoResult result = new ChocoResult();
		result.fillFields(solver);
		return result;
	}

}
