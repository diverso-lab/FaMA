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
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;


public class ChocoValidQuestion extends ChocoQuestion implements ValidQuestion{

	private boolean valid;
	
	public ChocoValidQuestion() {
		super();
		this.valid = false;
	}

	
	public boolean isValid() {
		return valid;
	}

	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner choco = (ChocoReasoner)r;
		ChocoResult res = new ChocoResult();
		Model chocoProblem=choco.getProblem();
		
		Solver solver = new CPSolver();
		solver.read(chocoProblem);
		try {
			solver.propagate();
			this.heuristic = new MinDomain(solver,solver.getVar(choco.getVars()));			
			valid = solver.solve();
			res.fillFields(solver);
		} catch (ContradictionException e) {
			valid = false;
		}		
		
		return res;
	}

}
