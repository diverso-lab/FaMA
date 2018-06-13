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


import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.OneProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public class ChocoOneProductQuestion extends ChocoQuestion implements
		OneProductQuestion {

	private Product prod;

	public ChocoOneProductQuestion() {
	}

	
	

	public void preAnswer(Reasoner r) {
		prod = new Product();;
	}

	public PerformanceResult answer(Reasoner choco) {

		ChocoReasoner r = (ChocoReasoner) choco;
		ChocoResult res = new ChocoResult();
		Model chocoProblem = r.getProblem();
		Solver solver = new CPSolver();
		solver.read(chocoProblem);
		this.heuristic = new MinDomain(solver,solver.getVar(r.getVars()));
//		this.heuristic.setVars(solver.getVar(r.getVars()));
//		solver.setVarIntSelector(heuristic);
		
		
		if (solver.solve() == Boolean.TRUE && solver.isFeasible()) {

			
			for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
				IntDomainVar aux = solver.getVar(chocoProblem.getIntVar(i));
				if (aux.getVal() > 0) {
					GenericFeature f = getFeature(aux, r);
					if (f != null) {
						prod.addFeature(f);
					}
				}
			}
			

		}
		res.fillFields(solver);
		return res;
	}

	private GenericFeature getFeature(IntDomainVar aux, ChocoReasoner reasoner) {
		String temp = new String(aux.toString().substring(0,
				aux.toString().indexOf(":")));
		GenericFeature f = reasoner.searchFeatureByName(temp);
		return f;
	}

	
	public Product getProduct() {
		return prod;
	}

}
