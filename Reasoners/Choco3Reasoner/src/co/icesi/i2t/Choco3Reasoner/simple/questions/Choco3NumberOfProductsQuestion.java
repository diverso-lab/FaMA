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

import java.util.LinkedList;
import java.util.List;

import solver.Solver;
import solver.search.solution.AllSolutionsRecorder;
import solver.search.solution.ISolutionRecorder;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.Variable;
import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

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
		// 
		// NOTE: The Choco 3 solver is repeating solutions, hence, solutions must recorded and 
		// filtered one by one.
//		this.numberOfProducts = solver.findAllSolutions();
		
		// Use a solution recorder that records all solutions that are found.
		// Asking this question may cause a memory explosion, thus the reasoner may fail.
		ISolutionRecorder defaultSolutionRecorder = solver.getSolutionRecorder();
		solver.set(new AllSolutionsRecorder(solver));
		
		List<Product> products = new LinkedList<Product>();
		
		// Reset the search so the CSP can be solved again, if it was previously solved.
		solver.getSearchLoop().reset();
		
		long solutionsFound = solver.findAllSolutions();
		if (solutionsFound > 0) {
			// If at least one solution is found
			// Solutions cannot be retrieved directly from the solver
			// They are stored by a solution recorder
			// Since we're interested in all the solutions we retrieve all solutions
			List<Solution> solutions = solver.getSolutionRecorder().getSolutions();
			for (Solution solution : solutions) {
				// Find the features that will be present in the product represented by the solution
				Product product = new Product();
				for (int i = 0; i < solver.getNbVars(); i++) {
					Variable variable = solver.getVar(i);
					// If the current variable is of type IntVar
					// We're interested in this type of variables since they are the ones
					// that represent features in the CSP
					if (variable instanceof IntVar) {
						// Check if the variable's value is greater than zero
						// This means the feature represented by this variable was selected to be
						// present in the product found
						if (solution.getIntVal((IntVar) variable) > 0) {
							// Search for the feature in the reasoner
							GenericFeature feature = choco3Reasoner.searchFeatureByName(variable.getName());
							if (feature != null) {
								// If the feature was found
								// Add the feature to the product
								product.addFeature(feature);
							}
						}
					}
				}
				// Add the product to the list of products if the product 
				// is not already in the list.
				if (!products.contains(product)) {
					products.add(product);
				}
			}
			// Set the number of products found.
			this.numberOfProducts = products.size();
		}
		
		// Create and return performance result
		Choco3PerformanceResult performanceResult = new Choco3PerformanceResult();
		performanceResult.addFields(solver);
		// Reset to the default solution recorder.
		solver.set(defaultSolutionRecorder);
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
