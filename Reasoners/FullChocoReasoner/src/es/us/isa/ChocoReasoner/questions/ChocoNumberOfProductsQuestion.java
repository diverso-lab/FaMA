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
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;


public class ChocoNumberOfProductsQuestion extends ChocoQuestion implements NumberOfProductsQuestion{
	
	private long numberOfProducts;

	public ChocoNumberOfProductsQuestion(long numberOfProducts) {
		super();
		this.numberOfProducts = numberOfProducts;
	}

	public ChocoNumberOfProductsQuestion() {
		super();
		this.numberOfProducts=0;
	}

	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner choco = (ChocoReasoner)r;
		ChocoResult res = new ChocoResult();
		Model pb=choco.getProblem();
		Solver s = new CPSolver();
		s.read(pb);
		this.heuristic = new MinDomain(s,s.getVar(choco.getVars()));
//		this.heuristic.setVars(s.getVar(choco.getVars()));
//		s.setVarIntSelector(heuristic);
		
		
		s.solveAll();
		
		if (s.isFeasible()){
			this.numberOfProducts=s.getNbSolutions();
		}else{
			this.numberOfProducts=0;
		}
		return res;
	}

	public double getNumberOfProducts() {
		return numberOfProducts;
	}

}
