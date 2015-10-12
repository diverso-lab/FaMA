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
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.Variable;
import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.OneProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;

/**
 * Implementation to solve the one product question using the Choco 3 reasoner.
 * This operation calculates a valid product of a feature model that can be 
 * derived from the feature model with the specified constraints.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoOneProductQuestion Choco 2 implementation for the one product question.
 * @version 1.0, June 2014
 */
public class Choco3OneProductQuestion extends Choco3Question implements
		OneProductQuestion {

	/** 
	 * A product that can be derived with the imposed constraints, if found.
	 * If it is not found the product will have no features associated.
	 */
	private Product product;
	/**
	 * Seed value to pick a random variable and assign it a random value.
	 */
	private long seed;
	
	/**
	 * Returns a product that can be derived with the imposed constraints, if found.
	 * Returns <code>null</code> if no product was found.
	 * 
	 * @return A product that can be derived with the imposed constraints, if found.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.questions.OneProductQuestion#getProduct()
	 */
	public GenericProduct getProduct() {
		return this.product;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		// Create a product instance.
		this.product = new Product();
		// Generate the seed value.
		this.seed = new java.util.Random().nextLong();
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
		// TODO Set heuristic using RandomIntValSelector and RandomIntVarSelector
//		this.heuristic = IntStrategyFactory.random_value(solver.retrieveIntVars(), seed);
//		solver.set(this.heuristic);
		
		// The findSolution method attempts to find the first possible solution to the CSP
		// and it returns true if it found one, false otherwise
		if (solver.findSolution()) {
			// If a solution is found
			// Solutions cannot be retrieved directly from the solver
			// They are stored by a solution recorder
			// Since we're interested in only one solution we can retrieve the last solution
			Solution solution = solver.getSolutionRecorder().getLastSolution();
			// Find the features that will be present in the product represented by the solution found
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
		}
		
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
