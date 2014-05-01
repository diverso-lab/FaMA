/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.tests.FMGeneration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.MetamorphicFMGenerator;
import es.us.isa.utils.BettyException;

public class ProductsGenerationTest extends TestCase {

	
	/**
	 * Try to reveal failures when introducing a non-valid maximum number of products
	 */
	@Test
	public void testProductsGenerationWrongNoProducts() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setMaxProducts(-100);     // Maximum number of products of the feature model to be generated
		} catch (BettyException e) {
			assertEquals("Wrong number of products accepted!", "Wrong argument. It must be positive", e.getMessage());
			return;
		}	
		
		fail("Wrong number of products accepted!");
	}
	
	
	
	/**
	 * Reveal inconsistencies between the calculated number of products and the actual number of products generated
	 */
	@Test
	public void testProductsGenerationInconsistenNumberOfProducts() {
		
		int MaxNumberOfFeatures = 30;				// Max number of features
		int MinNumberOfFeatures = 10;				// Minimum number of features
		int MaxPercentageCTC = 60;					// Max percentage of constraints
		int MaxProbabilityMandatory = 100;			// Max probability for mandatory features
		int MaxProbabilityOptional = 100;			// Max probability for optional features
		int MaxProbabilityOr = 100;					// Max probability for or children
		int MaxProbabilityAlternative = 100;		// Max probability for alternative children
		int MaxBranchingFactor = 20;				// Max branching factor
		int MaxSetChildren = 10;					// Max number of children in set relationships
		int MaxNumberOfProducts= 10000;				// Max number of products of the feature model to be generated
		
		
		int numberOfIterations = 50;
		Random random = new Random();
		
		for(int i=0; i<numberOfIterations ;i++) {

			int numberOfFeatures = Math.max(MinNumberOfFeatures,random.nextInt(MaxNumberOfFeatures+1)) ;
			int percentageCTC = random.nextInt(MaxPercentageCTC+1);
			int maxNumberOfProducts = random.nextInt(MaxNumberOfProducts);
			int probMandatory = -1;
			int probOptional = -1;
			int probOrChildren = -1;
			int probAltChildren = -1;
			int maxBranchingFactor = -1;
			int maxSetChildren = -1;
			
			if (random.nextBoolean())
				probMandatory = random.nextInt(MaxProbabilityMandatory);
			
			if (random.nextBoolean())
				probOptional = random.nextInt(MaxProbabilityOptional);
			
			if (random.nextBoolean())
				probOrChildren = random.nextInt(MaxProbabilityOr);
			
			if (random.nextBoolean())
				probAltChildren = random.nextInt(MaxProbabilityAlternative);
			
			if (random.nextBoolean())
				maxBranchingFactor = random.nextInt(MaxBranchingFactor);
			
			if (random.nextBoolean())
				maxSetChildren = random.nextInt(MaxSetChildren);
			
			System.out.println("======== Test " + i + " ==========");
			System.out.println("Number of features: " + numberOfFeatures);
			System.out.println("Percentage of CTC: " + percentageCTC);
			System.out.println("Probability of mandatory: " + probMandatory);
			System.out.println("Probability of optional: " + probOptional);
			System.out.println("Probability of features in or-relations: " + probOrChildren);
			System.out.println("Probability of alternative features: " + probAltChildren);
			System.out.println("Maximum branching factor: " + maxBranchingFactor);
			System.out.println("Maximum number of children in set relationships: " + maxSetChildren);
			

			try {
				// Specify the user's preferences for the generation (so-called characteristics)
				GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
				characteristics.setNumberOfFeatures(numberOfFeatures);					// Number of features.
				characteristics.setPercentageCTC(percentageCTC);						// Probability of constraints.
				characteristics.setMaxProducts(maxNumberOfProducts);					// Max number of products of the feature model to be generated.
				if (probMandatory != -1)
					characteristics.setProbabilityMandatory(probMandatory);				// Probability of a feature being mandatory.
				
				if (probOptional != -1)
					characteristics.setProbabilityOptional(probOptional);				// Probability of a feature being optional.
				
				if (probOrChildren != -1)
					characteristics.setProbabilityOr(probOrChildren);					// Probability of a feature being in an or-relation
				
				if (probAltChildren != -1)
					characteristics.setProbabilityAlternative(probAltChildren);			// Probability of feature being in an alternative relation
				
				if (maxBranchingFactor != -1)
					characteristics.setMaxBranchingFactor(maxBranchingFactor);			// Max branching factor (default value = 10)
				
				if (maxSetChildren != -1)
					characteristics.setMaxSetChildren(maxSetChildren);					// Max number of children in a set relationship	
	
				
				// Generate the model with the specific characteristics (FaMa FM metamodel is used)
				FMGenerator randomGenerator = new FMGenerator();
				MetamorphicFMGenerator generator = new MetamorphicFMGenerator(randomGenerator);		// Decorator
				FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
				
				assertTrue("Wrong number of products!",generator.getNumberOfProducts() == generator.getPoducts().size());
				
				} catch (BettyException e) {
					//System.out.println("Error: " + e.getMessage());
					System.out.println("Non-valid input constraints. Input configuration discarded.");
				}
			}
	}
	

	
	/**
	 * Reveal inconsistencies between the calculated set of products and the actual set products calculated using FaMa
	*/ 
	@Test
	public void testProductsGenerationWrongSetProducts() {
		
		int MaxNumberOfFeatures = 20;				// Max number of features
		int MinNumberOfFeatures = 10;				// Minimum number of features
		int MaxPercentageCTC = 100;					// Max percentage of constraints
		int MaxProbabilityMandatory = 100;			// Max probability for mandatory features
		int MaxProbabilityOptional = 100;			// Max probability for optional features
		int MaxProbabilityOr = 100;					// Max probability for or children
		int MaxProbabilityAlternative = 100;		// Max probability for alternative children
		int MaxBranchingFactor = 20;				// Max branching factor
		int MaxSetChildren = 10;					// Max number of children in set relationships
		int MaxNumberOfProducts= 100000;			// Max number of products of the feature model to be generated
		
		
		int numberOfIterations = 50;
		Random random = new Random();
		
		for(int i=0; i<numberOfIterations ;i++) {

			int numberOfFeatures = Math.max(MinNumberOfFeatures,random.nextInt(MaxNumberOfFeatures+1)) ;
			int percentageCTC = random.nextInt(MaxPercentageCTC+1);
			int maxNumberOfProducts = random.nextInt(MaxNumberOfProducts);
			int probMandatory = -1;
			int probOptional = -1;
			int probOrChildren = -1;
			int probAltChildren = -1;
			int maxBranchingFactor = -1;
			int maxSetChildren = -1;
			
			if (random.nextBoolean())
				probMandatory = random.nextInt(MaxProbabilityMandatory);
			
			if (random.nextBoolean())
				probOptional = random.nextInt(MaxProbabilityOptional);
			
			if (random.nextBoolean())
				probOrChildren = random.nextInt(MaxProbabilityOr);
			
			if (random.nextBoolean())
				probAltChildren = random.nextInt(MaxProbabilityAlternative);
			
			if (random.nextBoolean())
				maxBranchingFactor = random.nextInt(MaxBranchingFactor);
			
			if (random.nextBoolean())
				maxSetChildren = random.nextInt(MaxSetChildren);
			
			System.out.println("======== Test " + i + " ==========");
			System.out.println("Number of features: " + numberOfFeatures);
			System.out.println("Percentage of CTC: " + percentageCTC);
			System.out.println("Probability of mandatory: " + probMandatory);
			System.out.println("Probability of optional: " + probOptional);
			System.out.println("Probability of features in or-relations: " + probOrChildren);
			System.out.println("Probability of alternative features: " + probAltChildren);
			System.out.println("Maximum branching factor: " + maxBranchingFactor);
			System.out.println("Maximum number of children in set relationships: " + maxSetChildren);
			

			try {
				// Specify the user's preferences for the generation (so-called characteristics)
				GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
				characteristics.setNumberOfFeatures(numberOfFeatures);					// Number of features.
				characteristics.setPercentageCTC(percentageCTC);						// Probability of constraints.
				characteristics.setMaxProducts(maxNumberOfProducts);					// Max number of products of the feature model to be generated.
				if (probMandatory != -1)
					characteristics.setProbabilityMandatory(probMandatory);				// Probability of a feature being mandatory.
				
				if (probOptional != -1)
					characteristics.setProbabilityOptional(probOptional);				// Probability of a feature being optional.
				
				if (probOrChildren != -1)
					characteristics.setProbabilityOr(probOrChildren);					// Probability of a feature being in an or-relation
				
				if (probAltChildren != -1)
					characteristics.setProbabilityAlternative(probAltChildren);			// Probability of feature being in an alternative relation
				
				if (maxBranchingFactor != -1)
					characteristics.setMaxBranchingFactor(maxBranchingFactor);			// Max branching factor (default value = 10)
				
				if (maxSetChildren != -1)
					characteristics.setMaxSetChildren(maxSetChildren);					// Max number of children in a set relationship	
	
				
				// Generate the model with the specific characteristics (FaMa FM metamodel is used)
				FMGenerator randomGenerator = new FMGenerator();
				MetamorphicFMGenerator generator = new MetamorphicFMGenerator(randomGenerator);		// Decorator
				FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
				
				QuestionTrader qt = new QuestionTrader();
				qt.setVariabilityModel(fm);
				qt.setCriteriaSelector("selected"); // We select a specific solver
				
				Question q = qt.createQuestion("Products");
				ProductsQuestion pq = (ProductsQuestion) q;
				PerformanceResult pr = qt.ask(pq);

				assertTrue("Wrong set of products!",sameSetOfProducts((Collection<Product>) pq.getAllProducts(), generator.getPoducts()));
				
				} catch (BettyException e) {
					//System.out.println("Error: " + e.getMessage());
					System.out.println("Non-valid input constraints. Input configuration discarded.");
				}
			}
	}
	

	
	// Check if the inputs sets of products are equal
	private boolean sameSetOfProducts(Collection<Product> analysisProducts, List<Product> metamorphicProducts) {
		
		if (analysisProducts.size()!=metamorphicProducts.size()) {
			System.err.println("The operation returns a wrong number of products");
			return false;
		}
		
		Iterator<Product> it = analysisProducts.iterator();
		while (it.hasNext()){
			Product p = it.next();
			int j=0;
			boolean found=false;
			while (j<analysisProducts.size() && !found) {
				Product pe = metamorphicProducts.get(j);
				if (p.equals(pe))
					found=true;
				else
					j++;
			}
			if(!found) {
				System.err.println("The sets of products are not equal");
				return false;
			}
		}
		return true;
	}

}
