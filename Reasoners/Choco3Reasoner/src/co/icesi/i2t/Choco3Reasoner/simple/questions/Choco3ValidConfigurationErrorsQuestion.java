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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

/**
 * Implementation to solve the valid configuration errors question using the Choco 3 reasoner.
 * This operation looks for explanations for errors in configurations of a given feature model. 
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoValidConfigurationErrorsQuestion Choco 2 implementation for the valid configuration errors question
 * @version 0.1, June 2014
 */
public class Choco3ValidConfigurationErrorsQuestion extends Choco3Question
		implements ValidConfigurationErrorsQuestion {

	// TODO What is the difference with ExplainInvalidProductQuestion?
	
	/**
	 * Configuration to be checked for errors.
	 */
	private Product product;
	/**
	 * Indicates if the configuration is valid.
	 */
	private boolean isValid;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion#setProduct(es.us.isa.FAMA.models.featureModel.Product)
	 */
	public void setProduct(Product product) {
		this.product = product;
		// TODO Why not setConfiguration?
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidConfigurationErrorsQuestion#isValid()
	 */
	public boolean isValid() {
		return this.isValid;
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
	public PerformanceResult answer(Reasoner reasoner) throws IllegalArgumentException {
		// Cast the reasoner into a Choco 3 Reasoner instance
		Choco3Reasoner choco3Reasoner = (Choco3Reasoner) reasoner;
		Solver solver = choco3Reasoner.getSolver();
		
		// A configuration has to be specified
		if (this.product == null) {
			throw new IllegalArgumentException("Product not specified");
		}
		
		// Check if it is necessary to validate the configuration
		// TODO Why not use ValidConfigurationQuestion?
		ValidProductQuestion validProductQuestion = new Choco3ValidProductQuestion();
		validProductQuestion.setProduct(this.product);
		reasoner.ask(validProductQuestion);
		this.isValid = validProductQuestion.isValid();
		
		if (this.isValid) {
			// If the configuration is valid, the configuration has no errors
			System.out.println("The configuration has no errors");
		} else {
			// The product is not valid, then the configuration has errors
			
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
			
			// Get the features from the configuration
			Collection<GenericFeature> invalidConfigurationFeatures = this.product.getFeatures();
			
			// Create a set of potential features to be selected
			// A potential feature to be selected is one that satisfies mandatory or required constraints
			// or desired in the configuration (and, thus, part of the invalid configuration)
			// For every feature Fi in the feature model, create a variable Si representing the potential selection
			// with a constraint expressing that Si = 1 <=> Fi = 1
			Map<GenericFeature, IntVar> selections = new HashMap<GenericFeature, IntVar>();
			// Create a set of potential features to be deselected
			// A potential feature is to be deselected if it is not mandatory, or it is excluded by a selected feature,
			// or it is not desired in the configuration
			// For every feature Fk in the feature model, create a variable Dk representing the potential deselection
			// with a constraint expressing that Dk = 1 <=> Fk = 0
			Map<GenericFeature, IntVar> deselections = new HashMap<GenericFeature, IntVar>();
			
			// Loop through every feature in the feature model as new variables and constraints
			// need to be added to the CSP representing the selected and deselected features,
			// and their relationship with the original variables
			for (Entry<GenericFeature,IntVar> entry : featuresAndVariablesSet) {
				GenericFeature feature = entry.getKey();
				IntVar featureVariable = entry.getValue();
				// Check if the feature appears in the invalid product
				if (invalidConfigurationFeatures.contains(feature)) {
					// If the feature is present in the configuration
					// Create a new variable Si and a constraint Si=1 <=> Fi=1
					IntVar selectedFeatureVariable = VariableFactory.bool("S-" + feature.getName(), solver);
					Constraint auxConstraint1 = IntConstraintFactory.arithm(selectedFeatureVariable, "=", 1);
					Constraint auxConstraint2 = IntConstraintFactory.arithm(featureVariable, "=", 1);
					Constraint selectedFeatureConstraint = LogicalConstraintFactory.and(
							LogicalConstraintFactory.ifThen(auxConstraint1, auxConstraint2), 
							LogicalConstraintFactory.ifThen(auxConstraint2, auxConstraint1));
					solver.post(selectedFeatureConstraint);
					selections.put(feature, selectedFeatureVariable);
				} else {
					// If the feature is not present in the configuration
					// Create a new variable Dk and a constraint Dk=1 <=> Fk=0
					IntVar deselectedFeatureVariable = VariableFactory.bool("D-" + feature.getName(), solver);
					Constraint auxConstraint1 = IntConstraintFactory.arithm(deselectedFeatureVariable, "=", 1);
					Constraint auxConstraint2 = IntConstraintFactory.arithm(featureVariable, "=", 0);
					Constraint deselectedFeatureConstraint = LogicalConstraintFactory.and(
							LogicalConstraintFactory.ifThen(auxConstraint1, auxConstraint2), 
							LogicalConstraintFactory.ifThen(auxConstraint2, auxConstraint1));
					solver.post(deselectedFeatureConstraint);
					deselections.put(feature, deselectedFeatureVariable);
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
			
			// Set the heuristic or strategy to be used by the reasoner
			// TODO Set heuristic MinDomain
			
			// Find all optimal solutions
			solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, sumVariable, true);	
			
			// Get the possible solutions
			List<Solution> solutions = solver.getSolutionRecorder().getSolutions();
			int index = 0;
			for (Solution solution : solutions) {
				System.out.println("Solution " + ++index);
				for (Entry<GenericFeature, IntVar> selectedFeature : selections.entrySet()) {
					if (solution.getIntVal(selectedFeature.getValue()) == 1) {
						System.out.println("- The feature " + selectedFeature.getKey().getName() + "has to be selected");
					}
				}
				for (Entry<GenericFeature, IntVar> deselectedFeature : deselections.entrySet()) {
					if (solution.getIntVal(deselectedFeature.getValue()) == 1) {
						System.out.println("- The feature " + deselectedFeature.getKey().getName() + "has to be deselected");
					}
				}
				System.out.println();
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
