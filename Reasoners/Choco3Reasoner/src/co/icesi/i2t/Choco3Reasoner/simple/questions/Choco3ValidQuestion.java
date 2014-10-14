/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.Choco3Reasoner.simple.questions;

import solver.Solver;
import solver.exception.ContradictionException;
import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;

/**
 * Implementation to solve the valid question using the Choco 3 reasoner.
 * This operation analyzes if a CSP representing a feature model is valid.
 * A CSP is valid if after propagating the specified constraints at least one solution is found.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoValidQuestion Choco 2 implementation for the valid question.
 * @version 1.0, June 2014
 */
public class Choco3ValidQuestion extends Choco3Question implements
		ValidQuestion {

	/**
	 * Indicates if the CSP is valid or not.
	 */
	private boolean isValid;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidQuestion#isValid()
	 */
	public boolean isValid() {
		return isValid;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.isValid = false;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		// Cast the reasoner into a Choco 3 Reasoner instance
		Choco3Reasoner choco3Reasoner = (Choco3Reasoner) reasoner;
		// Get the solver
		Solver solver = choco3Reasoner.getSolver();
		Choco3PerformanceResult performanceResult = new Choco3PerformanceResult();
		try {
			// Propagate the constraints
			solver.propagate();
			// Set the heuristic or strategy to be used by the reasoner
			// TODO Set heuristic MinDomain
			// Attempt to find a solution
			this.isValid = solver.findSolution();
			performanceResult.addFields(solver);
		} catch (ContradictionException e) {
			// If a contradiction is detected it is not valid
			this.isValid = false;
		}
		return performanceResult;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
		// Not needed
	}

}
