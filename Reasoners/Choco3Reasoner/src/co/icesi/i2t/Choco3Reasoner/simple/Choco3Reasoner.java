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
package co.icesi.i2t.Choco3Reasoner.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import solver.Solver;
import solver.search.strategy.strategy.AbstractStrategy;
import solver.variables.IntVar;
import solver.variables.VariableFactory;
import solver.constraints.Constraint;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.KeyWords;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.util.Tree;
import es.us.isa.util.Node;

/**
 * Choco 3 reasoner implementation for the simple feature model.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.ChocoReasoner Choco 2 reasoner implementation.
 * @version 1.0, June 2014
 */
public class Choco3Reasoner extends FeatureModelReasoner {

	/**
	 * Collection of the feature model's features.
	 */
	protected Map<String, GenericFeature> features;
	/**
	 * Collection of the CSP's variables.
	 */
	protected Map<String, IntVar> variables;
	/**
	 * Collection of the CSP's set relation variables.
	 */
	protected Map<String, IntVar> setRelationVariables;
	/**
	 * Collection of the CSP's constraints.
	 */
	protected Map<String, Constraint> dependencies;
	/**
	 * Collection of the CSP's feature model configuration constraints.
	 */
	protected Map<String, Constraint> configurationConstraints;
	/**
	 * The CSP solver provided by the Choco 3 library.
	 */
	protected Solver solver;
	/**
	 * Collection of the heuristics used in the reasoner.
	 * 
	 * TODO Consider removing as it is never used
	 */
	private Map<String, Object> heuristics;
	/**
	 * The heuristic used in the reasoner.
	 */
	private AbstractStrategy<?> heuristic;
	/**
	 * A parser for handling complex cross-tree constraints in a simple feature model.
	 */
	private Choco3Parser choco3Parser;
	
	/**
	 * Builds a new Choco 3 reasoner instance.
	 */
	public Choco3Reasoner() {
		super();
		reset();
	}
	
	/**
	 * Resets the Choco 3 reasoner.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#reset()
	 */
	@Override
	public void reset() {
		// Initialize all fields.
		this.features = new HashMap<String, GenericFeature>();
		this.variables = new HashMap<String, IntVar>();
		this.setRelationVariables = new HashMap<String, IntVar>();
		this.dependencies = new HashMap<String, Constraint>();
		this.configurationConstraints = new HashMap<String, Constraint>();
		// Create a Choco 3 Solver instance.
		// As of version 3 of the Choco library there is no distinction between model and solver objects.
		// Therefore, with Choco 3 all variables and constraint are posted to the solver.
		this.solver = new Solver("Simple Feature Model Reasoner using Choco 3");
		this.heuristics = new HashMap<String, Object>();
		this.choco3Parser = new Choco3Parser();
	}
	
	/**
	 * Returns the collection of features currently in the reasoner.
	 * 
	 * @return The features in the reasoner.
	 */
	public Collection<GenericFeature> getAllFeatures() {
		return this.features.values();
	}
	
	/**
	 * Searches for the feature matching the given feature name.
	 * 
	 * @param featureName The feature's name whose associated feature is to be returned.
	 * @return The feature matching the given name if found, or null if it is not found.
	 */
	public GenericFeature searchFeatureByName(String featureName) {
		return this.features.get(featureName);
	}

	/**
	 * Returns the collection of variables currently in the reasoner.
	 * 
	 * @return The variables in the reasoner.
	 */
	public Map<String, IntVar> getVariables() {
		return this.variables;
	}

	/**
	 * Returns the collection of set relation variables currently in the reasoner.
	 * 
	 * @return The set relation variables in the reasoner.
	 */
	public Map<String, IntVar> getSetRelationVariables() {
		return this.setRelationVariables;
	}

	/**
	 * Returns the collection of dependencies or relations between features currently in the reasoner.
	 * 
	 * @return The dependencies or relations between features in the reasoner.
	 */
	public Map<String, Constraint> getDependencies() {
		return this.dependencies;
	}

	/**
	 * Returns the solver instance in the reasoner.
	 * 
	 * @return The solver instance.
	 */
	public Solver getSolver() {
		return this.solver;
	}

	/**
	 * Sets the solver instance to be used in the reasoner.
	 * 
	 * @param solver The solver instance to be used.
	 */
	public void setSolver(Solver solver) {
		this.solver = solver;
	}

	/**
	 * Returns a collection of the reasoner's heuristics.
	 * 
	 * @return A map with the reasoner's heuristics.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.Reasoner#getHeusistics()
	 */
	@Override
	public Map<String, Object> getHeusistics() {
		// TODO Fix method name in FAMA SDK; it should be getHeuristics.
		return this.heuristics;
	}

	/**
	 * Returns the heuristic being used by the reasoner.
	 * 
	 * @return The heuristic being used by the reasoner.
	 */
	public AbstractStrategy<?> getHeuristic() {
		return this.heuristic;
	}

	/**
	 * Sets a heuristic for the reasoner to use.
	 * 
	 * @param heuristic An heuristic.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.Reasoner#setHeuristic(java.lang.Object)
	 */
	@Override
	public void setHeuristic(Object heuristic) {
		this.heuristic = (AbstractStrategy<?>) heuristic;
	}

	/**
	 * Adds a feature from a feature model as a variable in the CSP.
	 * 
	 * @param feature The feature model's feature to add as variable in the CSP.
	 * @param cardinalities The feature's cardinalities.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addFeature(es.us.isa.FAMA.models.featureModel.GenericFeature, java.util.Collection)
	 */
	@Override
	public void addFeature(GenericFeature feature, 
			Collection<Cardinality> cardinalities) {

		// Save the feature for future reference.
		features.put(feature.getName(), feature);
		
		// Before creating the variable its domain must be known.
		// We determine the feature variable's domain from the feature's cardinalities.
		// We look for all the cardinalities in the cardinalities collection and save them to a sorted set of integer values.
		Iterator<Cardinality> cardinalityIterator = cardinalities.iterator();
		int[] domainValues = getCardinalityDomainValues(cardinalityIterator);
		// Create a variable for the feature using the variable factory.
		// The variable factory includes five types of variables: IntVar, BoolVar, SetVar, GraphVar and RealVar.
		// A variable requires at least a name and a solver to be declared in.
		// A variable is posted to the solver automatically at creation.
		// The name is only helpful for the user, to read the computed results.
		// We create an integer variable with an enumerated domain (or enumerated variables) representing the feature in the CSP and add it to the solver
		// Enumerated variables may take their value in the range [a, b] where a < b or in an array of ordered values a, b, c, ..., z where a < ... < z
		// In this case the feature variable will take its value from an array of values given by its cardinality, therefore we use an enumerated variable
		IntVar featureVariable = VariableFactory.enumerated(feature.getName(), domainValues, this.solver);
		// Save the feature variable for future reference (it will be used when creating the constraints)
		this.variables.put(feature.getName(), featureVariable);
	}

	/**
	 * Returns the domain values for a given collection of cardinalities.
	 * 
	 * @param cardinalitiesIterator An iterator to a collection of cardinalities.
	 * @return An array of integer values holding the domain of values for the collection of cardinalities.
	 */
	private int[] getCardinalityDomainValues(
			Iterator<Cardinality> cardinalitiesIterator) {
		// We look for all the cardinalities in the cardinalities collection and save them to a sorted set of integer values.
		SortedSet<Integer> cardinalityValues = new TreeSet<Integer>();
		// We add the value 0.
		cardinalityValues.add(0);
		while (cardinalitiesIterator.hasNext()) {
			Cardinality cardinality = cardinalitiesIterator.next();
			// A cardinality has a minimum value and a maximum value.
			int min = cardinality.getMin();
			int max = cardinality.getMax();
			for (int i = min; i < max; i++) {
				// We don't have to check if it is already inserted into the set,
				// because no repeated elements are allowed in the set.
				cardinalityValues.add(i);
			}
		}
		// We convert the sorted set to an array of integer values.
		Iterator<Integer> cardinalityValuesIterator = cardinalityValues.iterator();
		int[] domainValues = new int[cardinalityValues.size()];
		int index = 0;
		while (cardinalityValuesIterator.hasNext()) {
			domainValues[index++] = cardinalityValuesIterator.next();			
		}
		return domainValues;
	}

	/**
	 * Adds the feature model's root feature as an equality constraint in the CSP ensuring the root feature is selected.
	 * The root feature is used to identify the SPL.
	 * 
	 * @param feature The feature model's root feature.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addRoot(es.us.isa.FAMA.models.featureModel.GenericFeature)
	 */
	@Override
	public void addRoot(GenericFeature feature) {
		// Retrieves the variable representing the feature model's root feature in the CSP.
		IntVar root = variables.get(feature.getName());
		
		// A constraint can be created by using one of the available constraint factories.
		// The most used constraint factories in the Choco 3 reasoner are the IntConstraintFactory 
		// and LogicalConstraintFactory.
		// The IntConstraintFactory allows us to create constraints for integer variables,
		// for example a constraint of the type: feature variable OPERATOR constant,
		// where OPERATOR can be one of { = , != , < , <= , > , => }.
		
		// Create a constraint that ensures the feature model's root feature is selected.
		// A root feature can be represented with an equality constraint to the constant value 1.
		// This means the root feature has to be present in a product, hence the constraint: root = 1.
		Constraint rootFeatureConstraint = IntConstraintFactory.arithm(root, "=", 1);
		// Save the root constraint for future reference.
		this.dependencies.put("Root", rootFeatureConstraint);
		// Add the root constraint to the solver.
		// Constraints need to be posted manually to the solver.
		this.solver.post(rootFeatureConstraint);
	}

	/**
	 * Adds a constraint to the CSP representing a mandatory relation between parent and child features.
	 * A mandatory relationship states that if a parent feature is present in a product its 
	 * child feature must be present too.
	 * 
	 * A mandatory relation between a parent feature and its child can be represented with the 
	 * logical expression 'parent is present if and only if child is present' (parent present <=> child present)
	 * This expression is equivalent to '(if parent is present then child is present) and (if child is present then parent is present)'
	 * (parent present => child present) and (child present => parent present).
	 * This last expression is the one used in this implementation since the Choco 3 library
	 * no longer provides native support for biconditional expressions.
	 * 
	 * @param relation The mandatory relation between the parent and child features.
	 * @param child The child feature.
	 * @param parent The parent feature.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addMandatory(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)
	 */
	@Override
	public void addMandatory(GenericRelation relation, GenericFeature child,
			GenericFeature parent) {
		// Create the mandatory constraint.
		Constraint mandatoryConstraint = createMandatoryConstraint(relation, child, parent);
		// Add the mandatory constraint to the solver.
		this.solver.post(mandatoryConstraint);
	}

	/**
	 * Creates a constraint representing a mandatory relation between parent and child features.
	 * 
	 * @param relation The mandatory relation between the parent and child features.
	 * @param child The child feature.
	 * @param parent The parent feature.
	 * @return A constraint representing a mandatory relation between a parent feature and its child.
	 */
	protected Constraint createMandatoryConstraint(GenericRelation relation,
			GenericFeature child, GenericFeature parent) {
		// Retrieve the corresponding variables for the parent and child features involved in the relation.
		IntVar childVariable = this.variables.get(child.getName());
		IntVar parentVariable = this.variables.get(parent.getName());
		
		// We create a constraint that ensures the parent is present in a product: parent = 1.
//		Constraint parentConstraint = IntConstraintFactory.arithm(parentVariable, "=", 1);
		// We create a constraint that ensures the child is present in a product: child = 1.
//		Constraint childConstraint = IntConstraintFactory.arithm(childVariable, "=", 1);
		
		// The LogicalConstraintFactory allows us to create constraints involving propositional logic,
		// for example a constraint of the type: feature 1 variable LOGIC CONNECTOR feature 2 variable,
		// where LOGIC CONNECTOR can be one of { ∧ , ∨ , => , <=> }.
		
		// We create the mandatory constraint using the previous two constraints to build the
		// expression (parent present => child present) and (child present => parent present).
//		Constraint mandatoryConstraint = LogicalConstraintFactory.and(
//				LogicalConstraintFactory.ifThen(parentConstraint, childConstraint),
//		   		LogicalConstraintFactory.ifThen(childConstraint, parentConstraint));
		
		// We could create the previous constraints, but in CSP representation the mandatory 
		// constraint can be reduced to the expression: (parent = child)
		Constraint mandatoryConstraint = IntConstraintFactory.arithm(parentVariable, "=", childVariable);
		
		// Save the mandatory constraint for future reference.
		this.dependencies.put(relation.getName(), mandatoryConstraint);
		// Return the mandatory constraint.
		return mandatoryConstraint;
	}

	/**
	 * Adds a constraint to the CSP representing an optional relation between parent and child features.
	 * An optional relationship states that if a parent feature is present in a product its child 
	 * feature may or may not be present.
	 * 
	 * An optional relation between a parent feature and its child can be represented with the 
	 * logical expression 'if parent is not present then child is not present'
	 * (parent not selected => child not selected).
	 * 
	 * @param relation The optional relation between parent and child features.
	 * @param child The child feature.
	 * @param parent The parent feature.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addOptional(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)
	 */
	@Override
	public void addOptional(GenericRelation relation, GenericFeature child,
			GenericFeature parent) {
		// Create the optional constraint.
		Constraint optionalConstraint = createOptionalConstraint(relation, child, parent);
		// Add the optional constraint to the solver.
		this.solver.post(optionalConstraint);
	}
	
	/**
	 * Creates a constraint representing an optional relation between parent and child features.
	 * 
	 * @param relation The optional relation between the parent and child features.
	 * @param child The child feature.
	 * @param parent The parent feature.
	 * @return A constraint representing an optional relation between a parent feature and its child.
	 */
	protected Constraint createOptionalConstraint(GenericRelation relation,
			GenericFeature child, GenericFeature parent) {
		// Retrieve the corresponding variables for the parent and child features involved in the relation.
		IntVar childVariable = this.variables.get(child.getName());
		IntVar parentVariable = this.variables.get(parent.getName());
		
		// We create a constraint that ensures the parent is not present in a product: parent = 0.
//		Constraint parentConstraint = IntConstraintFactory.arithm(parentVariable, "=", 0);
		// We create a constraint that ensures the child is not present in a product: child = 0.
//		Constraint childConstraint = IntConstraintFactory.arithm(childVariable, "=", 0);
		// We create the optional constraint using the previous two constraints to build the
		// expression (parent not selected => child not selected).
//		Constraint optionalConstraint = LogicalConstraintFactory.ifThen(parentConstraint, childConstraint);
		
		// We could create the previous constraints, but in CSP representation the optional 
		// constraint can be reduced to the expression: (parent >= child)
		Constraint optionalConstraint = IntConstraintFactory.arithm(parentVariable, ">=", childVariable);
		
		// Save the optional constraint for future reference.
		this.dependencies.put(relation.getName(), optionalConstraint);
		// Return the optional constraint.
		return optionalConstraint;
	}

	/**
	 * Adds a constraint to the CSP representing an excludes relation between origin and destination features.
	 * An excludes relationship is a cross tree constraint that states that if feature A excludes feature B
	 * then features A and B can not be present at the same time in a product.
	 * 
	 * An excludes relation between an origin feature and its destination can be represented with the 
	 * logical expression '¬ ( origin present ∧ destination present )' that can also be expressed as 
	 * 'if origin is present then destination not present'
	 * (origin present => destination not present).
	 * 
	 * @param relation The excludes relation between origin and destination features
	 * @param origin The origin feature
	 * @param destination The excluded feature
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addExcludes(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)
	 */
	@Override
	public void addExcludes(GenericRelation relation, GenericFeature origin,
			GenericFeature destination) {
		// Create the excludes constraint
		Constraint excludesConstraint = createExcludesConstraint(relation, origin, destination);
		// Add the excludes constraint to the solver
		this.solver.post(excludesConstraint);
	}

	/**
	 * Creates a constraint representing an excludes relation between origin and destination features.
	 * 
	 * @param relation The excludes relation between origin and destination features
	 * @param origin The origin feature
	 * @param destination The excluded feature
	 * @return A constraint representing an excludes relation between an origin feature and its destination
	 */
	protected Constraint createExcludesConstraint(GenericRelation relation,
			GenericFeature origin, GenericFeature destination) {
		// Retrieve the corresponding variables for the origin and destination features 
		// involved in the relation.
		IntVar originVariable = this.variables.get(origin.getName());
		IntVar destinationVariable = this.variables.get(destination.getName());
		
		// We create a constraint that states the origin feature must be present at least one time
		// (this depends on its cardinality) in a product: origin > 0.
		Constraint originConstraint = IntConstraintFactory.arithm(originVariable, ">", 0);
		// We create a constraint that states the destination feature can not not be present in the product: destination = 0.
		Constraint destinationConstraint = IntConstraintFactory.arithm(destinationVariable, "=", 0);
		// We create the excludes constraint using the previous two constraints to build the
		// expression (origin present => destination not present).
		Constraint excludesConstraint = LogicalConstraintFactory.ifThen(originConstraint, destinationConstraint);
		
		// Save the excludes constraint for future reference.
		this.dependencies.put(relation.getName(), excludesConstraint);
		// Return the excludes constraint.
		return excludesConstraint;
	}

	/**
	 * Adds a constraint to the CSP representing an optional relation between parent and child features.
	 * A requires relationship is a cross tree constraint that states that
	 * if feature A requires feature B then if feature A is present in a product, feature B must be present too.
	 * 
	 * A requires relation between an origin feature and its destination can be represented with the 
	 * logical expression 'if origin present then destination present'
	 * (origin present => destination present).
	 * 
	 * @param relation The requires relation between origin and destination features.
	 * @param origin The origin feature.
	 * @param destination The required feature.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addRequires(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)
	 */
	@Override
	public void addRequires(GenericRelation relation, GenericFeature origin,
			GenericFeature destination) {
		// Create the requires constraint.
		Constraint requiresConstraint = createRequiresConstraint(relation, origin, destination);
		// Add the requires constraint to the solver.
		this.solver.post(requiresConstraint);
	}

	/**
	 * Creates a constraint representing a requires relation between origin and destination features.
	 * 
	 * @param relation The requires relation between origin and destination features.
	 * @param origin The origin feature.
	 * @param destination The required feature.
	 * @return A constraint representing a requires relation between an origin feature and its destination.
	 */
	protected Constraint createRequiresConstraint(GenericRelation relation,
			GenericFeature origin, GenericFeature destination) {
		// Retrieve the corresponding variables for the origin and destination features involved in the relation.
		IntVar originVariable = this.variables.get(origin.getName());
		IntVar destinationVariable = this.variables.get(destination.getName());
		
		// We create a constraint that states the origin feature must be present at least one time
		// (this depends on its cardinality) in a product: origin > 0.
		Constraint originConstraint = IntConstraintFactory.arithm(originVariable, ">", 0);
		// We create a constraint that states the destination feature must be present at least one time
		// (this depends on its cardinality) in a product: destination > 0.
		Constraint destinationConstraint = IntConstraintFactory.arithm(destinationVariable, ">", 0);
		// We create the requires constraint using the previous two constraints to build the
		// expression (origin present => destination present).
		Constraint requiresConstraint = LogicalConstraintFactory.ifThen(originConstraint, destinationConstraint);
		
		// Save the excludes constraint for future reference.
		this.dependencies.put(relation.getName(), requiresConstraint);
		// Return the requires constraint.
		return requiresConstraint;
	}

	/**
	 * Adds a constraint to the CSP representing the cardinality of a relation between parent and child features.
	 * Cardinality-based relationships state that if a parent is present in a product, one can define how many
	 * children must be present.
	 * 
	 * The cardinality of a relation between a parent feature and its child can be represented with the 
	 * logical expression 'if parent present then child present in the range of the cardinality, else child not present'.
	 * 
	 * @param relation The relation between origin and destination features that has the cardinality.
	 * @param child The child feature.
	 * @param parent The parent feature.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addCardinality(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature, java.util.Iterator)
	 */
	@Override
	public void addCardinality(GenericRelation relation, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalitiesIterator) {
		// Create the cardinality constraint.
		Constraint cardinalityConstraint = createCardinalityConstraint(relation, child, parent, cardinalitiesIterator);
		// Add the cardinality constraint to the solver.
		this.solver.post(cardinalityConstraint);
	}

	/**
	 * Creates a constraint representing the cardinality of a relation between parent and child features.
	 * 
	 * @param relation The relation between origin and destination features that has the cardinality.
	 * @param child The child feature.
	 * @param parent The parent feature.
	 * @param cardinalitiesIterator The relation's cardinalities.
	 * @return A constraint representing the cardinality of a relation between a parent feature and its child.
	 */
	protected Constraint createCardinalityConstraint(GenericRelation relation,
			GenericFeature child, GenericFeature parent,
			Iterator<Cardinality> cardinalitiesIterator) {
		// Retrieve the corresponding variables for the parent and child features involved in the relation.
		IntVar childVariable = this.variables.get(child.getName());
		IntVar parentVariable = this.variables.get(parent.getName());
		
		// Before creating the cardinality constraint a variable must be created with the domain of the cardinality
		// as it will be used to constraint the values of the child feature's variable.
		
		int[] cardinalityDomainValues = getCardinalityDomainValues(cardinalitiesIterator);
		// We create an enumerated variable representing the cardinality of the relation in the CSP and add it to the solver.
		// Enumerated variables may take their value in the range [a, b] where a < b or in an array of ordered values a, b, c, ..., z where a < ... < z.
		// In this case the cardinality variable will take its value from an array of values given by the cardinality of the relation, therefore we use an enumerated variable.
		// However, for this variable the solver should not calculate a value since it will be given when applying a configuration.
		// Up to Choco 2 this was supported through the options parameter when creating a new variable. Using the option 'cp:no_decision' would tell the solver not to assign 
		// a value. Since Choco 3 this is no longer supported, and during the creation of this Choco 3 reasoner implementation no alternative way of telling the solver not to 
		// assign a value to a variable was found.
		// TODO Tell the Choco 3 solver not to assign a value to the cardinality variable
		IntVar cardinalityVariable = VariableFactory.enumerated(relation.getName() + "_cardinality", cardinalityDomainValues, this.solver);
		
		// We create a constraint that states the parent feature must be present at least one time in a product: parent > 0.
		Constraint parentConstraint = IntConstraintFactory.arithm(parentVariable, ">", 0);
		// We create a constraint that ensures the child is present in a product the times stated by the cardinality: child = cardinality.
		Constraint childConstraint = IntConstraintFactory.arithm(childVariable, "=", cardinalityVariable);
		// We create a constraint that ensures the child is not present if the parent is not present.
		Constraint childElseConstraint = IntConstraintFactory.arithm(childVariable, "=", 0);
		// We create the cardinality constraint using the previous two constraints to build the
		// expression 'if parent present then child present in the range of the cardinality, else child not present'.
		Constraint cardinalityConstraint = LogicalConstraintFactory.ifThenElse(parentConstraint, childConstraint, childElseConstraint);
		// Save the cardinality constraint for future reference.
		this.dependencies.put(relation.getName(), cardinalityConstraint);
		// Return the cardinality constraint.
		return cardinalityConstraint;
	}

	/**
	 * Adds a constraint to the CSP representing a set relation between a parent feature and its children features.
	 * A set of child features has a set relation with their parent when a number of them can be 
	 * included in the products in which its parent feature appears. The amount of child features that may
	 * be included is given by the relation cardinality. The relation cardinality is expressed as a range
	 * of values [x,y], where x ≤ y and y ≤ number of child features.
	 * 
	 * Two special cases of set relations can be identified: Alternative and Or relations.
	 * A set of child features have an Alternative relation with their parent when only one child can be
	 * present when its parent feature is present in the product. In this case the relation cardinality
	 * can be expressed as [0,1].
	 * A set of child features have an Or relation with their parent when one or more of them can be 
	 * present when its parent feature is present in the product. In this case the relation cardinality
	 * can be expressed as [0,n], where n is the number of child features.
	 * 
	 * @param relation The relation between parent and children features
	 * @param parent The parent feature
	 * @param children A collection of children features
	 * @param cardinalities A collection of cardinalities for the respective cardinalities between the parent and each of its children 
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addSet(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, java.util.Collection, java.util.Collection)
	 */
	@Override
	public void addSet(GenericRelation relation, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		// Create the set constraint
		Constraint setConstraint = createSetConstraint(relation, parent, children, cardinalities);
		// Add the set constraint to the solver
		this.solver.post(setConstraint);
	}

	/**
	 * Creates a constraint representing a set relation between a parent feature and its children features.
	 * 
	 * @param relation The relation between parent and children features.
	 * @param parent The parent feature.
	 * @param children A collection of children features.
	 * @param cardinalities A collection of cardinalities for the respective 
	 * 			cardinalities between the parent and each of its children.
	 * @return A constraint representing a relation between a parent feature and 
	 * 			its children features along with their respective cardinalities
	 */
	protected Constraint createSetConstraint(GenericRelation relation,
			GenericFeature parent, Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		// Retrieve the corresponding variable for the parent feature.
		IntVar parentVariable = this.variables.get(parent.getName());
		// Retrieve the corresponding variable for each child feature.
		ArrayList<IntVar> childrenVariables = new ArrayList<IntVar>();
		Iterator<GenericFeature> childrenIterator = children.iterator();
		while (childrenIterator.hasNext()) {
			GenericFeature child = childrenIterator.next();
			childrenVariables.add(this.variables.get(child.getName()));
		}
		// Save the children variables into an array to create a set variable from them.
		IntVar[] auxChildrenVariables = new IntVar[childrenVariables.size()];
		int i = 0;
		for (IntVar childVariable : childrenVariables) {
			auxChildrenVariables[i++] = childVariable;
		}
		
		// Save the cardinality in a variable.
		// Before creating the cardinality constraint a variable must be created with the domain of the cardinality
		// as it will be used to constraint the values of the child feature's variable.
		int[] cardinalityDomainValues = getCardinalityDomainValues(cardinalities.iterator());
		// We create the variable representing the cardinality of the relation and add it to the solver.
		// However, for this variable the solver should not calculate a value since it will be given when applying a configuration.
		// TODO Tell the Choco 3 solver not to assign a value to the cardinality variable
		IntVar cardinalityVariable = VariableFactory.enumerated(relation.getName() + "_cardinality", cardinalityDomainValues, this.solver);
		
		// We create a constraint that states the parent feature must be present in a product: parent > 0.
		Constraint parentConstraint = IntConstraintFactory.arithm(parentVariable, ">", 0);
		// We create a constraint that states the sum of the children features that are present in a product
		// needs to be contained within the cardinality.
		Constraint sumConstraint = IntConstraintFactory.sum(auxChildrenVariables, cardinalityVariable);
		// If the previous constraint is not satisfied then all child features cannot be present in the product.
		Constraint sumElseConstraint = IntConstraintFactory.sum(auxChildrenVariables, VariableFactory.fixed(0, this.solver));
		// We create the set constraint using the previous constraints to build the expression
		// 'if parent present then children present in the range of the cardinality, else children not present'.
		Constraint setConstraint = LogicalConstraintFactory.ifThenElse(parentConstraint, sumConstraint, sumElseConstraint);
		// Save the set constraint for future reference
		this.dependencies.put(relation.getName(), setConstraint);
		
		// Return the set constraint.
		return setConstraint;
	}

	/**
	 * Applies a staged configuration of a feature model as constraints in the CSP.
	 * 
	 * @param configuration The staged configuration to apply as constraints in the CSP.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.Reasoner#applyStagedConfiguration(es.us.isa.FAMA.stagedConfigManager.Configuration)
	 */
	@Override
	public void applyStagedConfiguration(Configuration configuration) {
		Constraint configurationConstraint;
		// We need to add each configuration of the feature model as a constraint.
		// We iterate over the collection of configuration elements from the given configuration.
		Iterator<Entry<VariabilityElement, Integer>> configurationIterator = configuration.getElements().entrySet().iterator();
		while (configurationIterator.hasNext()) {
			Map.Entry<VariabilityElement, Integer> entry = configurationIterator.next();
			VariabilityElement variabilityElement = entry.getKey();
			int variabilityElementValue = entry.getValue().intValue();
			
			// Check if the variability element is either a feature or a relation between features.
			if (variabilityElement instanceof GenericFeature) {
				// If the variability element is a feature,
				// check if the features collection contains the feature.
				if (this.features.containsKey(variabilityElement.getName())) {
					// If the features collection contains the feature,
					// we retrieve the feature variable from the variables collection.
					IntVar featureVariable = this.variables.get(variabilityElement.getName());
					// Create the configuration constraint.
					configurationConstraint = IntConstraintFactory.arithm(featureVariable, "=", variabilityElementValue);
					// Save the configuration constraint for future reference.
					this.configurationConstraints.put(variabilityElement.getName() + "_eq_" + variabilityElementValue, configurationConstraint);
					// Add the configuration constraint to the solver.
					this.solver.post(configurationConstraint);
				} else {
					if (variabilityElementValue > 0) {
						// If the features collection does not contain the feature,
						// but the feature is selected, then we need to create a contradiction
						// as the CSP should not have a solution.
						// For this, we create an error variable with a fixed value of 0.
						IntVar errorVariable = VariableFactory.fixed("error", 0, this.solver);
						// And create a constraint that will be used to generate a contradiction in the solver during the 
						// constraint propagation phase.
						// The constraint will ensure the error variable needs to have a value of 1
						// for a solution to exist, but the error variable has a reduced domain of 0.
						Constraint errorConstraint = IntConstraintFactory.arithm(errorVariable, "=", 1);
						// Add the error constraint to the solver. 
						this.solver.post(errorConstraint);
						// ,
						// no constraint will be created as it will not be a decisional variable and constraint.
						System.out.println("The feature " + variabilityElement.getName() + " does not exist in the CSP model and cannot be added");
					} else {
						System.out.println("The feature " + variabilityElement.getName() + " does not exist in the CSP model");
					}
				}
			} else {
				// If the variability element is a relation between features,
				// check if the relations collection contains the relation.
				if (this.dependencies.containsKey(variabilityElement.getName())) {
					// If the relations collection contains the relation,
					// we retrieve the relation variable from the set variables collection.
					IntVar relationVariable = this.setRelationVariables.get(variabilityElement.getName());
					// Create the configuration constraint
					configurationConstraint = IntConstraintFactory.arithm(relationVariable, "=", variabilityElementValue);
					// Save the configuration constraint for future reference
					this.configurationConstraints.put(variabilityElement.getName() + "_eq_" + variabilityElementValue, configurationConstraint);
					// Add the configuration constraint to the solver
					this.solver.post(configurationConstraint);
				} else {
					if (variabilityElementValue > 0) {
						// If the features collection does not contain the feature,
						// but the feature is selected, then we need to create a contradiction
						// as the CSP should not have a solution.
						// For this, we create an error variable with a fixed value of 0.
						IntVar errorVariable = VariableFactory.fixed("error", 0, this.solver);
						// And create a constraint that will be used to generate a contradiction in the solver during the 
						// constraint propagation phase.
						// The constraint will ensure the error variable needs to have a value of 1
						// for a solution to exist, but the error variable has a reduced domain of 0.
						Constraint errorConstraint = IntConstraintFactory.arithm(errorVariable, "=", 1);
						// Add the error constraint to the solver. 
						this.solver.post(errorConstraint);
						// ,
						// no constraint will be created as it will not be a decisional variable and constraint.
						System.out.println("The feature " + variabilityElement.getName() + " does not exist in the CSP model and cannot be added");
					} else {
						System.out.println("The feature " + variabilityElement.getName() + " does not exist in the CSP model");
					}
				}
			}
		}
	}

	/**
	 * Removes the constraints from the CSP corresponding to the applied staged configuration. 
	 * 
	 * @see es.us.isa.FAMA.Reasoner.Reasoner#unapplyStagedConfigurations()
	 */
	@Override
	public void unapplyStagedConfigurations() {
		// Loop through the constraints created during the application of the staged configuration
		// and unpost them from the solver.
		Iterator<Constraint> configurationConstraintsIterator = this.configurationConstraints.values().iterator();
		while (configurationConstraintsIterator.hasNext()) {
			Constraint constraint = configurationConstraintsIterator.next();
			this.solver.unpost(constraint);
			configurationConstraintsIterator.remove();
		}
	}

	/**
	 * Ask a question and attempt to answer it using the Choco 3 reasoner.
	 * 
	 * @param question The question that wants to be answered.
	 * @return A performance result about the reasoner while attempting to answer the question.
	 * @throws FAMAException If question is <code>null</code>.
	 * @throws ClassCastException If question is not a Choco 3 question instance.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.Reasoner#ask(es.us.isa.FAMA.Reasoner.Question)
	 */
	@Override
	public PerformanceResult ask(Question question) throws FAMAException, ClassCastException {
		// If the question is null.
		if (question == null) {
			// Throw a FAMA exception.
			throw new FAMAException("Question not specified");
		}
		// Cast the generic question into a Choco 3 question.
		Choco3Question choco3Question = (Choco3Question) question;
		
		// Set the heuristic to be used when answering the question.
		if (this.heuristic != null) {
			choco3Question.setHeuristic(this.heuristic);
		}
		
		// Prepare the reasoner and additional resources prior to answering the question.
		// This method should always be called before answering the question.
		choco3Question.preAnswer(this);
		// Attempt to answer the question and capture its performance result.
		PerformanceResult performanceResult = choco3Question.answer(this);
		// Release any resources associated with answering the question.
		// This method should always be called after answering the question.
		choco3Question.postAnswer(this);
		
		// Return the performance result for the reasoner.
		return performanceResult;
	}

	/**
	 * Translates a FAMA constraint to a Choco 3 constraint and adds it to the CSP.
	 * This is used when dealing with complex cross-tree constraints.
	 * 
	 * @param famaConstraint The FAMA constraint to add to the CSP.
	 * 
	 * @see es.us.isa.FAMA.Reasoner.FeatureModelReasoner#addConstraint(es.us.isa.FAMA.models.featureModel.Constraint)
	 */
	@Override
	public void addConstraint(es.us.isa.FAMA.models.featureModel.Constraint famaConstraint) {
		Constraint choco3Constraint = this.choco3Parser.translateToConstraint(famaConstraint.getAST());
		this.dependencies.put(famaConstraint.getName(), choco3Constraint);
		this.solver.post(choco3Constraint);
	}
	
	/**
	 * Choco 3 parser implementation for the simple feature model.
	 * This parser transforms complex cross-tree constraints in the feature model
	 * into constraints understandable by the Choco 3 library.
	 * 
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @see es.us.isa.ChocoReasoner.ChocoReasoner.ChocoParser Choco 2 parser implementation.
	 * @version 0.1, june 2014
	 */
	protected class Choco3Parser {
		
		/**
		 * Translates a FAMA constraint into a Choco 3 constraint from a given tree representing the FAMA constraint.
		 * 
		 * @param ast The FAMA constraint to translate into Choco 3 constraints represented by a tree.
		 * @return A Choco 3 constraint.
		 */
		public Constraint translateToConstraint(Tree<String> ast) {
			Node<String> node = ast.getRootElement();
			Constraint constraint = translateLogicalFAMAConstraint(node);
			return constraint;
		}
		
		/**
		 * Translates a FAMA constraint into a Choco 3 logical constraint from a given tree representing the FAMA constraint.
		 * A logical constraint can be one of the following: AND, OR, NOT, IMPLIES, IF AND ONLY IF, REQUIRES, EXCLUDES.
		 * 
		 * @param tree The FAMA constraint to translate into Choco 3 constraints represented by a tree.
		 * @return A Choco 3 constraint.
		 * @throws IllegalArgumentException if the data in the node is not recognized.
		 */
		private Constraint translateLogicalFAMAConstraint(Node<String> tree) throws IllegalArgumentException {
			Constraint resultConstraint = null;
			String treeData = tree.getData();
			List<Node<String>> children = tree.getChildren();
			int numberOfChildren = children.size();
			if (numberOfChildren == 2) {
				// Translate the children into Choco 3 constraints.
				Constraint auxConstraint1 = translateLogicalFAMAConstraint(children.get(0));
				Constraint auxConstraint2 = translateLogicalFAMAConstraint(children.get(1));
				
				if (treeData.equals(KeyWords.AND)) {
					// If the FAMA constraint has an AND keyword create a Choco 3 AND constraint.
					resultConstraint = LogicalConstraintFactory.and(auxConstraint1, auxConstraint2);
				} else if (treeData.equals(KeyWords.OR)) {
					// If the FAMA constraint has an OR keyword create a Choco 3 OR constraint.
					resultConstraint = LogicalConstraintFactory.or(auxConstraint1, auxConstraint2);
				} else if (treeData.equals(KeyWords.IMPLIES) || treeData.equals(KeyWords.REQUIRES)) {
					// If the FAMA constraint has an IMPLIES keyword create a Choco 3 IF-THEN constraint.
					resultConstraint = LogicalConstraintFactory.ifThen(auxConstraint1, auxConstraint2);
				} else if (treeData.equals(KeyWords.IFF)) {
					// If the FAMA constraint has an IFF keyword create a Choco 3 constraint with a representation of a biconditional expression
					// since the Choco 3 library no longer provides native support for biconditional expressions.
					// This expression is equivalent to '(if X then Y) and (if Y then X)'
					// (X => Y) and (Y => X).
					resultConstraint = LogicalConstraintFactory.and(
							LogicalConstraintFactory.ifThen(auxConstraint1, auxConstraint2),
							LogicalConstraintFactory.ifThen(auxConstraint2, auxConstraint1));
				} else if (treeData.equals(KeyWords.EXCLUDES)) {
					// If the FAMA constraint has an EXCLUDES keyword create a Choco 3 IF-THEN constraint.
					// Since this method creates constraints for features of the type 'feature is present' (feature > 0)
					// we need to negate the feature constraint that goes on the THEN clause
					// (featureX > 0) => ¬(featureY > 0).
					resultConstraint = LogicalConstraintFactory.ifThen(
							auxConstraint1, 
							LogicalConstraintFactory.not(auxConstraint2));
				} else {
					// The data in the node is not recognized.
					throw new IllegalArgumentException("Non-recognized token: " + treeData);
				}
			} else {
				if (numberOfChildren == 1) {
					if (treeData.equals(KeyWords.NOT)) {
						// If the FAMA constraint has a NOT keyword.
						// Translate the child into a Choco 3 constraint.
						Constraint auxConstraint1 = translateLogicalFAMAConstraint(children.get(0));
						// Create a Choco 3 NOT constraint
						resultConstraint = LogicalConstraintFactory.not(auxConstraint1);
					} else {
						// The data in the node is not recognized.
						throw new IllegalArgumentException("Non-recognized token: " + treeData);
					}
				} else {
					if (isFeature(tree)) {
						// If the node is a feature retrieve its variable
						IntVar feature = variables.get(treeData);
						// and create a feature constraint representing the feature is present (feature > 0).
						resultConstraint = IntConstraintFactory.arithm(feature, ">", 0);
					} else {
						// The data in the node is not recognized.
						throw new IllegalArgumentException("Non-recognized token: " + treeData);
					}
				}
			}
			return resultConstraint;
		}

		/**
		 * Checks if the given node is a feature. It returns <code>true</code> if and only if the Choco 3 reasoner contains
		 * the node as a feature.
		 * 
		 * @param node Node whose presence as a feature is to be tested.
		 * @return <code>true</code> if the Choco 3 reasoner contains the node as a feature.
		 */
		private boolean isFeature(Node<String> node) {
			String nodeData = node.getData();
			return features.containsKey(nodeData);
		}
	}

}
