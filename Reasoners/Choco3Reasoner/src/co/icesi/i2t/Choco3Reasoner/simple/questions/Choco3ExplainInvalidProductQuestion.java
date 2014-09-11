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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

/**
 * Implementation to solve the explain invalid product question using the Choco 3 reasoner.
 * This operation provides options to repair an invalid product for a given feature model.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoExplainInvalidProductQuestion Choco 2 implementation for the explain invalid product question.
 * @version 0.1, June 2014
 */
public class Choco3ExplainInvalidProductQuestion extends Choco3Question
		implements ExplainInvalidProductQuestion {

	/**
	 * The collection of features that were selected for the fixed product.
	 */
	private Collection<GenericFeature> featuresToSelect;
	/**
	 * The collections of features deselected for the fixed product.
	 */
	private Collection<GenericFeature> featuresToDeselect;
	/**
	 * The invalid product.
	 */
	private Product invalidProduct;
	/**
	 * The fixed product.
	 */
	private Product fixedProduct;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion#setInvalidProduct(es.us.isa.FAMA.models.featureModel.Product)
	 */
	public void setInvalidProduct(Product invalidProduct) {
		this.invalidProduct = invalidProduct;
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion#getSelectedFeatures()
	 */
	public Collection<GenericFeature> getSelectedFeatures() {
		return this.featuresToSelect;
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion#getDeselectedFeatures()
	 */
	public Collection<GenericFeature> getDeselectedFeatures() {
		return this.featuresToDeselect;
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion#getFixedProduct()
	 */
	public Product getFixedProduct() {
		return this.fixedProduct;
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		// Initialize collections and fixed product
		this.featuresToSelect = new LinkedList<GenericFeature>();
		this.featuresToDeselect = new LinkedList<GenericFeature>();
		this.fixedProduct = new Product();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		// Cast the reasoner into a Choco 3 Reasoner instance
		Choco3Reasoner choco3Reasoner = (Choco3Reasoner) reasoner;
		Solver solver = choco3Reasoner.getSolver();
		
		// Map features to variables
		Map<GenericFeature, IntVar> featuresAndVariables = new HashMap<GenericFeature, IntVar>();
		// Get all the features from the feature model
		Collection<GenericFeature> features = choco3Reasoner.getAllFeatures();
		// Get the variables representing the features in the CSP
		Map<String, IntVar> featureVariables = choco3Reasoner.getVariables();
		for (GenericFeature feature : features) {
			featuresAndVariables.put(feature, featureVariables.get(feature.getName()));
		}
		Set<Entry<GenericFeature,IntVar>> featuresAndVariablesSet = featuresAndVariables.entrySet();
		
		// Get the features from the invalid product
		Collection<GenericFeature> invalidProductFeatures = this.invalidProduct.getFeatures();
		
		// Create a set of potential features to be selected from all the
		// features not selected in the invalid product
		// For every feature Fi, create a variable Si with a constraint
		// Si = 1 <=> Fi = 1
		Map<GenericFeature, IntVar> selections = new HashMap<GenericFeature, IntVar>();
		// Create a set of potential features to be deselected from all
		// the features selected in the invalid product
		// For every feature k, create a variable Dk with a constraint
		// Dk = 1 <=> Fk = 0
		Map<GenericFeature, IntVar> deselections = new HashMap<GenericFeature, IntVar>();
		// Loop through every feature in the CSP
		for (Entry<GenericFeature,IntVar> entry : featuresAndVariablesSet) {
			GenericFeature feature = entry.getKey();
			IntVar featureVariable = entry.getValue();
			// Check if the feature appears in the invalid product
			if (invalidProductFeatures.contains(feature)) {
				// If the feature appears in the invalid product
				// it will be deselected
				IntVar deselectedFeatureVariable = VariableFactory.bool("D-" + featureVariable.getName(), solver);
				Constraint auxConstraint1 = IntConstraintFactory.arithm(deselectedFeatureVariable, "=", 1);
				Constraint auxConstraint2 = IntConstraintFactory.arithm(featureVariable, "=", 0);
				Constraint deselectedFeatureConstraint = LogicalConstraintFactory.and(
						LogicalConstraintFactory.ifThen(auxConstraint1, auxConstraint2), 
						LogicalConstraintFactory.ifThen(auxConstraint2, auxConstraint1));
				solver.post(deselectedFeatureConstraint);
				deselections.put(feature, deselectedFeatureVariable);
			} else {
				// If the feature does not appear in the invalid product
				// it will be selected
				IntVar selectedFeatureVariable = VariableFactory.bool("S-" + featureVariable.getName(), solver);
				Constraint auxConstraint1 = IntConstraintFactory.arithm(selectedFeatureVariable, "=", 1);
				Constraint auxConstraint2 = IntConstraintFactory.arithm(featureVariable, "=", 1);
				Constraint selectedFeatureConstraint = LogicalConstraintFactory.and(
						LogicalConstraintFactory.ifThen(auxConstraint1, auxConstraint2), 
						LogicalConstraintFactory.ifThen(auxConstraint2, auxConstraint1));
				solver.post(selectedFeatureConstraint);
				selections.put(feature, selectedFeatureVariable);
			}
		}
		
		// Minimize Si + Dk
		Collection<IntVar> collectionVariablesToMinimize = new LinkedList<IntVar>();
		collectionVariablesToMinimize.addAll(selections.values());
		collectionVariablesToMinimize.addAll(deselections.values());
		IntVar[] variablesToMinimize = (IntVar[]) collectionVariablesToMinimize.toArray();
		// TODO Check algorithm, and new variables and constraints
		IntVar sumVariable = VariableFactory.enumerated("sum", 0, variablesToMinimize.length, solver);
		Constraint sumConstraint = IntConstraintFactory.sum(variablesToMinimize, sumVariable);
		solver.post(sumConstraint);
		solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, sumVariable);

		// Build the fixed product
		// Get the optimal solution
		Solution solution = solver.getSolutionRecorder().getLastSolution();
		// The fixed product initially contains all the features from the invalid product
		this.fixedProduct.addAllFeatures(this.invalidProduct.getFeatures());
		// Remove the deselected features from the fixed product
		for (Entry<GenericFeature, IntVar> entry : deselections.entrySet()) {
			// Check if the feature was deselected
			if (solution.getIntVal(entry.getValue()) == 1) {
				// Add the feature to the collection of deselected features
				this.featuresToDeselect.add(entry.getKey());
				// Remove the feature from the original configuration
				this.fixedProduct.removeFeature(entry.getKey());
			}
		}
		// Add the selected features to the fixed product
		for (Entry<GenericFeature, IntVar> entry : selections.entrySet()) {
			// Check if the feature was deselected
			if (solution.getIntVal(entry.getValue()) == 1) {
				// Add the feature to the collection of deselected features
				this.featuresToSelect.add(entry.getKey());
				// Remove the feature from the original configuration
				this.fixedProduct.addFeature(entry.getKey());
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
