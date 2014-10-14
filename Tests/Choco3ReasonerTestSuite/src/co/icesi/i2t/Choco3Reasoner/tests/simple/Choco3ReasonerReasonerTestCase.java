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
package co.icesi.i2t.Choco3Reasoner.tests.simple;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import solver.constraints.Constraint;
import solver.variables.IntVar;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import co.icesi.i2t.FAMA.TestSuite2.TestLoader;
import co.icesi.i2t.FAMA.TestSuite2.reasoners.AbstractReasonerTestCase;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

/**
 * Test case for the Choco 3 Reasoner.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @version 0.1, October 2014
 */
public class Choco3ReasonerReasonerTestCase extends AbstractReasonerTestCase {

	/**
	 * Test configuration file path
	 */
	private static final String TEST_CONFIG_FILE = "test-resources/Choco3TestConfig.xml";

	private Choco3Reasoner choco3Reasoner;

	/*
	 * (non-Javadoc)
	 * 
	 * @see co.icesi.i2t.FAMA.TestSuite2.reasoners#AbstractReasonerTestCase(java
	 * .lang.String)
	 */
	public Choco3ReasonerReasonerTestCase(String variabilityModelPath) {
		super(variabilityModelPath);
	}

	/**
	 * Loads the tests for the Choco 3 reasoner. Tests are specified in an XML
	 * file with the information of the variability models to test.
	 * 
	 * @return A collection of feature models.
	 * @throws FileNotFoundException
	 *             If the test configuration file is not found.
	 * @throws Exception
	 *             If any other errors occur.
	 */
	@Parameters
	public static Collection<?> loadTests() throws FileNotFoundException,
			Exception {
		return Arrays.asList(TestLoader.loadReasonerTests(TEST_CONFIG_FILE));
	}

	/**
	 * Sets up the Choco 3 reasoner instance needed to perform the tests.
	 * 
	 * @throws Exception
	 *             If any errors occur.
	 */
	@Before
	public void setUp() throws Exception {
		questionTrader = new QuestionTrader();
		// Initialize the question trader.
		choco3Reasoner = new Choco3Reasoner();
	}

	/**
	 * Releases all resources used during the test.
	 * 
	 * @throws Exception
	 *             If any error occurs.
	 */
	@After
	public void tearDown() throws Exception {
		choco3Reasoner = null;
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#unapplyStagedConfigurations()}
	 * .
	 */
	@Test
	public void testUnapplyStagedConfigurations() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#applyStagedConfiguration(es.us.isa.FAMA.stagedConfigManager.Configuration)}
	 * .
	 */
	@Test
	public void testApplyStagedConfiguration() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addFeature(es.us.isa.FAMA.models.featureModel.GenericFeature, java.util.Collection)}
	 * .
	 */
	@Test
	public void testAddFeature() {
		System.out.println("\n[TEST] Add feature");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;
		
		if (featureModel.getFeaturesNumber() > 0) {
			Iterator<Feature> featuresIterator = featureModel.getFeatures()
					.iterator();
			
			Random random = new Random();
			int randomFeatureIndex = random.nextInt(featureModel
					.getFeaturesNumber());
			
			Feature feature = null;
			
			for (int i = 0; featuresIterator.hasNext() && i <= randomFeatureIndex; i++) {
				feature = (Feature) featuresIterator.next();
			}
			choco3Reasoner.addFeature(feature, getFeatureCardinalities(feature));
			Map<String, IntVar> variables = choco3Reasoner.getVariables();

			System.out.println("For feature: " + feature.getName());
			System.out.println("Obtained variable: " + variables.get(feature.getName()));
		} else {
			System.out.println("The model has no features.");
		}
	}
	
	/**
	 * Returns the cardinalities for the given feature.
	 * 
	 * @param feature The feature from which the cardinalities are needed.
	 * @return A collection of the feature's cardinalities.
	 */
	private Collection<Cardinality> getFeatureCardinalities(Feature feature) {
		Collection<Cardinality> cardinalities = null;
		Relation parentRelation = feature.getParent();
		if (parentRelation != null
				&& parentRelation.getNumberOfDestination() == 1
				&& parentRelation.getCardinalities().hasNext()) {
			Iterator<Cardinality> cardinalitiesIterator = parentRelation
					.getCardinalities();
			cardinalities = new ArrayList<Cardinality>();
			while (cardinalitiesIterator.hasNext()) {
				cardinalities.add(cardinalitiesIterator.next());
			}
		} else {
			cardinalities = new ArrayList<Cardinality>();
			cardinalities.add(new Cardinality(0, 1));
		}
		return cardinalities;
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addRoot(es.us.isa.FAMA.models.featureModel.GenericFeature)}
	 * .
	 */
	@Test
	public void testAddRoot() {
		System.out.println("\n[TEST] Add root feature");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Feature root = featureModel.getRoot();
		ArrayList<Cardinality> cardinalities = null;
		if (root.getParent() == null) {
			cardinalities = new ArrayList<Cardinality>();
			cardinalities.add(new Cardinality(0, 1));
		}

		choco3Reasoner.addFeature(root, cardinalities);
		choco3Reasoner.addRoot(root);
		Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

		System.out.println("For root feature: " + root.getName());
		System.out.println("Obtained constraint: " + dependencies.get("Root"));
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addMandatory(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)}
	 * .
	 */
	@Test
	public void testAddMandatory() {
		System.out.println("\n[TEST] Add mandatory relationship");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Collection<Feature> features = featureModel.getFeatures();
		HashMap<Relation, Feature> mandatoryRelations = new HashMap<Relation, Feature>();
		for (Feature feature : features) {
			Iterator<Relation> relations = feature.getRelations();
			while (relations.hasNext()) {
				Relation relation = (Relation) relations.next();
				if (relation.isMandatory()) {
					mandatoryRelations.put(relation, feature);
				}
			}
		}
		if (mandatoryRelations.size() > 0) {
			Random random = new Random();
			int randomRelationIndex = random.nextInt(mandatoryRelations.size());
			int i = 0;
			Relation relation = null;
			Feature parentFeature = null;
			for (Iterator<Entry<Relation, Feature>> iterator = mandatoryRelations
					.entrySet().iterator(); iterator.hasNext() && i <= randomRelationIndex; i++) {
				Entry<Relation, Feature> entry = (Entry<Relation, Feature>) iterator.next();
				if (i == randomRelationIndex) {
					relation = entry.getKey();
					parentFeature = entry.getValue();
				}
			}
		
			Feature childFeature = relation.getDestinationAt(0);
			
			choco3Reasoner.addFeature(parentFeature, getFeatureCardinalities(parentFeature));
			choco3Reasoner.addFeature(childFeature, getFeatureCardinalities(childFeature));
			choco3Reasoner.addMandatory(relation, childFeature,	parentFeature);

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			System.out.println("For parent feature: " + parentFeature.getName());
			System.out.println("For child feature: " + childFeature.getName());
			System.out.println("Relationship name: " + relation.getName());
			System.out.println("Obtained constraint: " + dependencies.get(relation.getName()));
		} else {
			System.out.println("The model has no mandatory relationships.");
		}
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addOptional(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)}
	 * .
	 */
	@Test
	public void testAddOptional() {
		System.out.println("\n[TEST] Add optional relationship");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Collection<Feature> features = featureModel.getFeatures();
		HashMap<Relation, Feature> optionalRelations = new HashMap<Relation, Feature>();
		for (Feature feature : features) {
			Iterator<Relation> relations = feature.getRelations();
			while (relations.hasNext()) {
				Relation relation = (Relation) relations.next();
				if (relation.isOptional()) {
					optionalRelations.put(relation, feature);
				}
			}
		}
		if (optionalRelations.size() > 0) {
			Random random = new Random();
			int randomRelationIndex = random.nextInt(optionalRelations.size());
			int i = 0;
			Relation relation = null;
			Feature parentFeature = null;
			for (Iterator<Entry<Relation, Feature>> iterator = optionalRelations
					.entrySet().iterator(); iterator.hasNext() && i <= randomRelationIndex; i++) {
				Entry<Relation, Feature> entry = (Entry<Relation, Feature>) iterator.next();
				if (i == randomRelationIndex) {
					relation = entry.getKey();
					parentFeature = entry.getValue();
				}
			}
		
			Feature childFeature = relation.getDestinationAt(0);
			
			choco3Reasoner.addFeature(parentFeature, getFeatureCardinalities(parentFeature));
			choco3Reasoner.addFeature(childFeature, getFeatureCardinalities(childFeature));
			choco3Reasoner.addOptional(relation, childFeature, parentFeature);

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			System.out.println("For parent feature: " + parentFeature.getName());
			System.out.println("For child feature: " + childFeature.getName());
			System.out.println("Relationship name: " + relation.getName());
			System.out.println("Obtained constraint: " + dependencies.get(relation.getName()));
		} else {
			System.out.println("The model has no optional relationships.");
		}
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addCardinality(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature, java.util.Iterator)}
	 * .
	 */
	@Test
	public void testAddCardinality() {
		System.out.println("\n[TEST] Add cardinality relationship");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Collection<Feature> features = featureModel.getFeatures();
		HashMap<Relation, Feature> cardinalityRelations = new HashMap<Relation, Feature>();
		for (Feature feature : features) {
			Iterator<Relation> relations = feature.getRelations();
			while (relations.hasNext()) {
				Relation relation = (Relation) relations.next();
				if (!relation.isMandatory() && !relation.isOptional() && relation.getNumberOfDestination() == 1) {
					cardinalityRelations.put(relation, feature);
				}
			}
		}
		if (cardinalityRelations.size() > 0) {
			Random random = new Random();
			int randomRelationIndex = random.nextInt(cardinalityRelations.size());
			int i = 0;
			Relation relation = null;
			Feature parentFeature = null;
			for (Iterator<Entry<Relation, Feature>> iterator = cardinalityRelations
					.entrySet().iterator(); iterator.hasNext() && i <= randomRelationIndex; i++) {
				Entry<Relation, Feature> entry = (Entry<Relation, Feature>) iterator.next();
				if (i == randomRelationIndex) {
					relation = entry.getKey();
					parentFeature = entry.getValue();
				}
			}
		
			Feature childFeature = relation.getDestinationAt(0);
			
			choco3Reasoner.addFeature(parentFeature, getFeatureCardinalities(parentFeature));
			choco3Reasoner.addFeature(childFeature, getFeatureCardinalities(childFeature));
			choco3Reasoner.addCardinality(relation, childFeature, parentFeature, relation.getCardinalities());

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			System.out.println("For parent feature: " + parentFeature.getName());
			System.out.println("For child feature: " + childFeature.getName());
			System.out.println("Relationship name: " + relation.getName());
			System.out.println("Obtained constraint: " + dependencies.get(relation.getName()));
		} else {
			System.out.println("The model has no cardinality relationships.");
		}
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addSet(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, java.util.Collection, java.util.Collection)}
	 * .
	 */
	@Test
	public void testAddSet() {
		System.out.println("\n[TEST] Add set relationship");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Collection<Feature> features = featureModel.getFeatures();
		HashMap<Relation, Feature> setRelations = new HashMap<Relation, Feature>();
		for (Feature feature : features) {
			Iterator<Relation> relations = feature.getRelations();
			while (relations.hasNext()) {
				Relation relation = (Relation) relations.next();
				if (relation.getNumberOfDestination() > 1) {
					setRelations.put(relation, feature);
				}
			}
		}
		if (setRelations.size() > 0) {
			Random random = new Random();
			int randomRelationIndex = random.nextInt(setRelations.size());
			int i = 0;
			Relation relation = null;
			Feature parentFeature = null;
			for (Iterator<Entry<Relation, Feature>> iterator = setRelations
					.entrySet().iterator(); iterator.hasNext() && i <= randomRelationIndex; i++) {
				Entry<Relation, Feature> entry = (Entry<Relation, Feature>) iterator.next();
				if (i == randomRelationIndex) {
					relation = entry.getKey();
					parentFeature = entry.getValue();
				}
			}
			
			choco3Reasoner.addFeature(parentFeature, getFeatureCardinalities(parentFeature));
			
			Collection<GenericFeature> children = new ArrayList<GenericFeature>();
			Iterator<Feature> destinationIterator = relation.getDestination();
			while (destinationIterator.hasNext()) {
				Feature childFeature = destinationIterator.next();
				children.add(childFeature);
				choco3Reasoner.addFeature(childFeature, getFeatureCardinalities(childFeature));
			}
			Collection<Cardinality> cardinalities = new ArrayList<Cardinality>();
			Iterator<Cardinality> cardinalitiesIterator = relation.getCardinalities();
			while (cardinalitiesIterator.hasNext()) {
				cardinalities.add(cardinalitiesIterator.next());
			}
		
			choco3Reasoner.addSet(relation, parentFeature, children, cardinalities);

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			System.out.println("For parent feature: " + parentFeature.getName());
			System.out.println("For children features: " + children);
			System.out.println("Relationship name: " + relation.getName());
			System.out.println("Obtained constraint: " + dependencies.get(relation.getName()));
		} else {
			System.out.println("The model has no set (alternative, or) relationships.");
		}
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addExcludes(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)}
	 * .
	 */
	@Test
	public void testAddExcludes() {
		System.out.println("\n[TEST] Add excludes constraint");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Iterator<es.us.isa.FAMA.models.featureModel.Constraint> constraintsIterator = featureModel.getDependencies();
		Collection<ExcludesDependency> excludesDependencies = new ArrayList<ExcludesDependency>();
		while (constraintsIterator.hasNext()) {
			es.us.isa.FAMA.models.featureModel.Constraint constraint = constraintsIterator.next();
			if (constraint instanceof ExcludesDependency) {
				ExcludesDependency excludesDependency = (ExcludesDependency) constraint;
				excludesDependencies.add(excludesDependency);
			}
		}
		if (excludesDependencies.size() > 0) {
			Random random = new Random();
			int randomDependencyIndex = random.nextInt(excludesDependencies.size());
			int i = 0;
			ExcludesDependency excludesDependency = null;
			for (Iterator<ExcludesDependency> iterator = excludesDependencies
					.iterator(); iterator.hasNext() && i <= randomDependencyIndex; i++) {
				ExcludesDependency dependency = iterator.next();
				if (i == randomDependencyIndex) {
					excludesDependency = dependency;
				}
			}
			
			Feature originFeature = excludesDependency.getOrigin();
			Feature destinationFeature = excludesDependency.getDestination();
		
			choco3Reasoner.addFeature(originFeature, getFeatureCardinalities(originFeature));
			choco3Reasoner.addFeature(destinationFeature, getFeatureCardinalities(destinationFeature));
			choco3Reasoner.addExcludes(excludesDependency, originFeature, destinationFeature);

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			System.out.println("For origin feature: " + originFeature.getName());
			System.out.println("For destination feature: " + destinationFeature.getName());
			System.out.println("Relationship name: " + excludesDependency.getName());
			System.out.println("Obtained constraint: " + dependencies.get(excludesDependency.getName()));
		} else {
			System.out.println("The model has no excludes constraints.");
		}
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addRequires(es.us.isa.FAMA.models.featureModel.GenericRelation, es.us.isa.FAMA.models.featureModel.GenericFeature, es.us.isa.FAMA.models.featureModel.GenericFeature)}
	 * .
	 */
	@Test
	public void testAddRequires() {
		System.out.println("\n[TEST] Add requires constraint");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Iterator<es.us.isa.FAMA.models.featureModel.Constraint> constraintsIterator = featureModel.getDependencies();
		Collection<RequiresDependency> requiresDependencies = new ArrayList<RequiresDependency>();
		while (constraintsIterator.hasNext()) {
			es.us.isa.FAMA.models.featureModel.Constraint constraint = constraintsIterator.next();
			if (constraint instanceof RequiresDependency) {
				RequiresDependency requiresDependency = (RequiresDependency) constraint;
				requiresDependencies.add(requiresDependency);
			}
		}
		if (requiresDependencies.size() > 0) {
			Random random = new Random();
			int randomDependencyIndex = random.nextInt(requiresDependencies.size());
			int i = 0;
			RequiresDependency requiresDependency = null;
			for (Iterator<RequiresDependency> iterator = requiresDependencies
					.iterator(); iterator.hasNext() && i <= randomDependencyIndex; i++) {
				RequiresDependency dependency = iterator.next();
				if (i == randomDependencyIndex) {
					requiresDependency = dependency;
				}
			}
			
			Feature originFeature = requiresDependency.getOrigin();
			Feature destinationFeature = requiresDependency.getDestination();
		
			choco3Reasoner.addFeature(originFeature, getFeatureCardinalities(originFeature));
			choco3Reasoner.addFeature(destinationFeature, getFeatureCardinalities(destinationFeature));
			choco3Reasoner.addRequires(requiresDependency, originFeature, destinationFeature);

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			System.out.println("For origin feature: " + originFeature.getName());
			System.out.println("For destination feature: " + destinationFeature.getName());
			System.out.println("Relationship name: " + requiresDependency.getName());
			System.out.println("Obtained constraint: " + dependencies.get(requiresDependency.getName()));
		} else {
			System.out.println("The model has no requires constraints.");
		}
	}

	/**
	 * Test method for
	 * {@link co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner#addConstraint(es.us.isa.FAMA.models.featureModel.Constraint)}
	 * .
	 */
	@Test
	public void testAddConstraintConstraint() {
		System.out.println("\n[TEST] Add custom constraint");
		System.out.println("For model: \"" + variabilityModelPath + "\"");

		// Load the variability model that will be evaluated during the test.
		variabilityModel = questionTrader.openFile(variabilityModelPath);

		FAMAFeatureModel featureModel = (FAMAFeatureModel) variabilityModel;

		Iterator<es.us.isa.FAMA.models.featureModel.Constraint> constraintsIterator = featureModel.getDependencies();
		Collection<es.us.isa.FAMA.models.featureModel.Constraint> constraintDependencies = new ArrayList<es.us.isa.FAMA.models.featureModel.Constraint>();
		while (constraintsIterator.hasNext()) {
			es.us.isa.FAMA.models.featureModel.Constraint constraint = constraintsIterator.next();
			if (!(constraint instanceof RequiresDependency) && !(constraint instanceof ExcludesDependency)) {
				constraintDependencies.add(constraint);
			}
		}
		if (constraintDependencies.size() > 0) {
			Random random = new Random();
			int randomDependencyIndex = random.nextInt(constraintDependencies.size());
			int i = 0;
			es.us.isa.FAMA.models.featureModel.Constraint constraintDependency = null;
			for (Iterator<es.us.isa.FAMA.models.featureModel.Constraint> iterator = constraintDependencies
					.iterator(); iterator.hasNext() && i <= randomDependencyIndex; i++) {
				es.us.isa.FAMA.models.featureModel.Constraint dependency = iterator.next();
				if (i == randomDependencyIndex) {
					constraintDependency = dependency;
				}
			}
			
			// TODO Add the features involved in the constraint.
//			choco3Reasoner.addFeature(feature, getFeatureCardinalities(feature));
			choco3Reasoner.addConstraint(constraintDependency);

			Map<String, Constraint> dependencies = choco3Reasoner.getDependencies();

			// TODO Show involved features.
			System.out.println("For features: ?");
			System.out.println("Relationship name: " + constraintDependency.getName());
			System.out.println("Obtained constraint: " + dependencies.get(constraintDependency.getName()));
			fail("Not yet finished implementation"); // TODO
		} else {
			System.out.println("The model has no custom constraints.");
		}
	}

}
