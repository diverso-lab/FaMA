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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.AttributedFeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.extended.OptimalProducts;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public class ChocoOptimalProducts extends ChocoQuestion implements OptimalProducts {
	
	Collection<Product> products= new ArrayList<Product>();
	String attname="";
	
	public PerformanceResult answer(Reasoner r) throws FAMAException {
		ChocoReasoner reasoner = (ChocoReasoner) r;
		ChocoResult res = new ChocoResult();
		Solver sol = new CPSolver();
		Model p = reasoner.getProblem();
		Map<String, IntegerVariable> atributesVar = reasoner
				.getAttributesVariables();
		
		
		// primero cramos la coleccion con los atributos que nos interesan
		// dependiendo de la cadena de entrada

		Collection<IntegerVariable> selectedAtts = new ArrayList<IntegerVariable>();
		Iterator<Entry<String, IntegerVariable>> atributesIt = atributesVar
				.entrySet().iterator();
		while (atributesIt.hasNext()) {
			Entry<String, IntegerVariable> entry = atributesIt.next();
			if (entry.getKey().contains("."+attname)) {
				selectedAtts.add(entry.getValue());
			}
		}
		
		//Ahora necesitamos crear una variable suma de todos los atributos anteriores"
		IntegerVariable[] reifieds = new IntegerVariable[selectedAtts.size()];

		IntegerVariable suma = Choco
				.makeIntVar("_suma", 0, selectedAtts.size());
		IntegerExpressionVariable sumatorio = Choco.sum(selectedAtts
				.toArray(reifieds));
		Constraint sumReifieds = Choco.eq(suma, sumatorio);

		p.addConstraint(sumReifieds);

		sol.read(p);
		try {
			sol.propagate();
		} catch (ContradictionException e1) {
			e1.printStackTrace();
		}
		IntDomainVar maxVar = sol.getVar(suma);
		sol.minimize(maxVar, false);
		//Buscamos el minimo valor de la suma. es la misma chapuza de explain errors :S
		Solver sol2 = new CPSolver();
		Constraint cons2 = Choco.eq(suma, sol.getVar(suma).getVal());
		p.addConstraint(cons2);

		sol2.read(p);

		try {
			sol2.propagate();
		} catch (ContradictionException e1) {
			e1.printStackTrace();
		}
		//Obtener todo los valores que tengan ese valor
		if (sol2.solve() == Boolean.TRUE && sol2.isFeasible()) {
			do {
				Product prod = new Product();
				for (int i = 0; i < p.getNbIntVars(); i++) {
					IntDomainVar aux = sol2.getVar(p.getIntVar(i));
					if (aux.getVal() > 0) {
						GenericFeature f = getFeature(aux, reasoner);
						if (f != null) {
							prod.addFeature(f);
						}
					}
				}
				products.add(prod);
			} while (sol2.nextSolution() == Boolean.TRUE);
		}
		res.fillFields(sol2);
		return res;

	}

	private GenericFeature getFeature(IntDomainVar aux, ChocoReasoner reasoner) {
		String temp = new String(aux.toString().substring(0,
				aux.toString().indexOf(":")));
		GenericFeature f = reasoner.searchFeatureByName(temp);
		return f;
	}

	@Override
	public Collection<Product> getProducts() {
		return products;
	}

	@Override
	public void setAttributeName(String name) {
		this.attname=name;
		
	}

	public Class<? extends AttributedFeatureModelReasoner> getReasonerClass(){
		return ChocoReasoner.class;
	}
}
