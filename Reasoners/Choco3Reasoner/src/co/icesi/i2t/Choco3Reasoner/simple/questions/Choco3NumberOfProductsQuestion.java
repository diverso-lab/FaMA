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
import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;

/**
 * Implementation to solve the number of products question using the Choco 3 reasoner.
 * This operation calculates the number of products that can be 
 * derived from the feature model with the specified constraints.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoNumberOfProductsQuestion Choco 2 implementation for the number of products question.
 * @version 1.0, June 2014
 */
public class Choco3NumberOfProductsQuestion extends Choco3Question implements
		NumberOfProductsQuestion {

	/**
	 * The number of products that can be derived from the feature model with the specified constraints.
	 * Since there can be many solutions we use a long integer.
	 */
	private long numberOfProducts;
	
	/**
	 * Builds a new instance of the number of products question for the Choco 3 reasoner.
	 */
	public Choco3NumberOfProductsQuestion() {
		super();
		this.numberOfProducts = 0;
	}
	
	/**
	 * Builds a new instance of the number of products question for the Choco 3 reasoner
	 * using the given number of products.
	 */
	public Choco3NumberOfProductsQuestion(long numberOfProducts) {
		super();
		this.numberOfProducts = numberOfProducts;
	}

	/**
	 * Returns the number of products found.
	 * 
	 * @return The number of products found
	 * 
	 * @see es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion#getNumberOfProducts()
	 */
	public double getNumberOfProducts() {
		return this.numberOfProducts;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		// Not needed
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
		
		// Set the heuristic or strategy to be used by the reasoner.
		// TODO Set heuristic MinDomain
		
		// The findAllSolutions method attempts to find all the possible solutions to the CSP
		// and returns the number of solutions obtained.
		// In this case it will represent the number of products that can be derived from the feature model
		// with the applied constraints.
		this.numberOfProducts = solver.findAllSolutions();
		
		// Create and return performance result
		Choco3PerformanceResult performanceResult = new Choco3PerformanceResult();
		performanceResult.addFields(solver);
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
