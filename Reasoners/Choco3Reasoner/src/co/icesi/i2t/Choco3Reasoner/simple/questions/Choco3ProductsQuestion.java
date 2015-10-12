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

import java.util.Collection;
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
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

/**
 * Implementation to solve the all products question using the Choco 3 reasoner.
 * This operation calculates all valid products that can be 
 * derived from the feature model with the specified constraints.
 * 
 * Asking this question may cause a memory explosion, thus the reasoner may fail.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoProductsQuestion Choco 2 implementation for the all products question.
 * @version 1.0, June 2014
 */
public class Choco3ProductsQuestion extends Choco3Question implements
		ProductsQuestion {

	/**
	 * The products that can be derived with the imposed constraints, if found.
	 * If no product is found, the list will be empty.
	 */
	private List<Product> products;
	
	/**
	 * Returns the number of products found.
	 * 
	 * @return The number of products found
	 * 
	 * @see es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion#getNumberOfProducts()
	 */
	public long getNumberOfProducts() {
		if (this.products != null) {
			return this.products.size();
		}
		return 0;
	}

	/**
	 * Returns a collection of products that can be derived with the imposed constraints, if found.
	 * Returns an empty collection if no product was found.
	 * 
	 * @return A collection of products that can be derived with the imposed constraints, if found.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.questions.OneProductQuestion#getProduct()
	 */
	public Collection<Product> getAllProducts() {
		return this.products;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.products = new LinkedList<Product>();
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
		
		// Set the heuristic or strategy to be used by the reasoner
		// TODO Set heuristic MinDomain
		
		// Use a solution recorder that records all solutions that are found.
		// Asking this question may cause a memory explosion, thus the reasoner may fail.
		ISolutionRecorder defaultSolutionRecorder = solver.getSolutionRecorder();
		solver.set(new AllSolutionsRecorder(solver));
		
		// Reset the search so the CSP can be solved again, if it was previously solved.
		solver.getSearchLoop().reset();
		
		// The findAllSolutions method attempts to find all possible solutions to the CSP
		// and it returns the number of solutions obtained
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
				// is not already in the list
				if (!this.products.contains(product)) {
					this.products.add(product);
				}
			}
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
