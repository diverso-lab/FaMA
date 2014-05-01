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

import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;

public class FMGeneratorTest extends TestCase {

	
	/**
	 * Try to reveal failures when introducing a non-valid number of features
	 */
	@Test
	public void testGenerateFMWrongNoFeatures() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setNumberOfFeatures(-10);     // Number of features
		} catch (BettyException e) {
			assertEquals("Wrong number of features accepted!", "Wrong argument. It must be positive", e.getMessage());
			return;
		}	
		
		fail("Wrong number of features accepted!");
	}
	
	
	/**
	 * Try to reveal failures when introducing a non-valid percentage of cross-tree constraints
	 */
	@Test
	public void testGenerateFMWrongPercCTC() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setPercentageCTC(-10);     // Percentage of CTC
		} catch (BettyException e) {
			assertEquals("Wrong percentage of constraints accepted!", "Wrong argument. It must be a value between 0 and 100", e.getMessage());
			return;
		}	
		
		fail("Wrong percentage of constraints accepted!");
		
	}
	
	/**
	 * Try to reveal failures when introducing a non-valid probability for mandatory features
	 */
	@Test
	public void testGenerateFMWrongPercMandatory() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setProbabilityMandatory(-10);     // Probability of a feature being mandatory.
		} catch (BettyException e) {
			assertEquals("Wrong probability for mandatory features!", "Wrong argument. It must be a value between 0 and 100", e.getMessage());
			return;
		}	
		
		fail("Wrong probability for mandatory features!");
		
	}
	
	
	/**
	 * Try to reveal failures when introducing a non-valid probability for optional features
	 */
	@Test
	public void testGenerateFMWrongPercOptional() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setProbabilityOptional(-10);     // Percentage of a feature being optional.
		} catch (BettyException e) {
			assertEquals("Wrong probability for optional features!", "Wrong argument. It must be a value between 0 and 100", e.getMessage());
			return;
		}	
		
		fail("Wrong probability for optional features!");
		
	}
	
	/**
	 * Try to reveal failures when introducing a non-valid probability for or children features
	 */
	@Test
	public void testGenerateFMWrongPercOrChildren() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setProbabilityOr(-10);     // Probability of a feature being in an or-relation
		} catch (BettyException e) {
			assertEquals("Wrong probability for or children!", "Wrong argument. It must be a value between 0 and 100", e.getMessage());
			return;
		}	
		
		fail("Wrong probability for or children!");
	}
	
	
	/**
	 * Try to reveal failures when introducing a non-valid probability for alternative features
	 */
	@Test
	public void testGenerateFMWrongPercAltChildren() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setProbabilityAlternative(-10);     // Probability of a feature being in an alternative relation
		} catch (BettyException e) {
			assertEquals("Wrong percentage of alternative features accepted!", "Wrong argument. It must be a value between 0 and 100", e.getMessage());
			return;
		}	
		
		fail("Wrong percentage of alternative features accepted!");
	}
	
	/**
	 * Try to reveal failures when introducing a non-valid maximum branching factor
	 */
	@Test
	public void testGenerateFMWrongBranchingFactor() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setMaxBranchingFactor(-10);     // Max branching factor
		} catch (BettyException e) {
			assertEquals("Wrong branching factor accepted!", "Wrong argument. It must be positive", e.getMessage());
			return;
		}	
		
		fail("Wrong branching factor accepted!");
	}
	
	
	/**
	 * Try to reveal failures when introducing a non-valid maximum number of features in set relations
	 */
	@Test
	public void testGenerateFMWrongMaxSetChildren() {
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setMaxSetChildren(-10);     // Maximum number of children in set relationships
		} catch (BettyException e) {
			assertEquals("Wrong maximum number of features in set relationships accepted!", "Wrong argument. It must be positive", e.getMessage());
			return;
		}	
		
		fail("Wrong maximum number of features in set relationships accepted!");
	}
	
	
	/**
	 * Try to reveal failures when introducing a wrong combination of probabilities (sum > 100)
	 */
	@Test
	public void testGenerateFMWrongProbabilitiesOver100() {
		
		int numberOfFeatures = 100;
		int percentageCTC = 30;
		float probMandatory = 30;
		float probOptional = 30;
		float probOrChildren = 30;
		float probAltChildren = 30;
		
		try {
		// Specify the user's preferences for the generation (so-called characteristics)
		GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(numberOfFeatures);			// Number of features
		characteristics.setPercentageCTC(percentageCTC);				// Percentage of constraints.
		characteristics.setProbabilityMandatory(probMandatory);			// Probability of a feature being mandatory
		characteristics.setProbabilityOptional(probOptional);			// Probability of a feature being optional
		characteristics.setProbabilityOr(probOrChildren);				// Probability of a feature being in an or-relation
		characteristics.setProbabilityAlternative(probAltChildren);		// Probability of a feature being in an alternative relationship
		
		// Generate the model with the specific characteristics (FaMa FM metamodel is used)
		AbstractFMGenerator generator = new FMGenerator();
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		
		} catch (BettyException e) {
			assertEquals("The sum probabilities should not be greater than 100!", "Wrong arguments. The sum of the probabilities can not be greater than 100", e.getMessage());
			return;
		}
		
		fail("The sum probabilities should not be greater than 100!");
	}
	
	/**
	 * Try to reveal failures when introducing a wrong combination of probabilities (sum < 100)
	 */
	@Test
	public void testGenerateFMWrongProbabilitiesUnder100() {
		
		int numberOfFeatures = 100;
		int percentageCTC = 30;
		float probMandatory = 10;
		float probOptional = 10;
		float probOrChildren = 10;
		float probAltChildren = 10;
		
		try {
		// Specify the user's preferences for the generation (so-called characteristics)
		GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(numberOfFeatures);			// Number of features
		characteristics.setPercentageCTC(percentageCTC);				// Percentage of constraints.
		characteristics.setProbabilityMandatory(probMandatory);			// Probability of a feature being mandatory
		characteristics.setProbabilityOptional(probOptional);			// Probability of a feature being optional
		characteristics.setProbabilityOr(probOrChildren);				// Probability of a feature being in an or-relation
		characteristics.setProbabilityAlternative(probAltChildren);		// Probability of a feature being in an alternative relationship
		
		// Generate the model with the specific characteristics (FaMa FM metamodel is used)
		AbstractFMGenerator generator = new FMGenerator();
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		
		} catch (BettyException e) {
			assertEquals("The sum probabilities should not be lower than 100!", "Wrong arguments. The sum of the probabilities can not be lower than 100", e.getMessage());
			return;
		}
		
		fail("The sum probabilities should not be lower than 100!");
	}
	
	
	/**
	 * Reveal failures when trying to make a generation reproducible
	 */
	@Test
	public void testGenerateFMNotReproducible() {
		
		int numberOfFeatures = 100;
		int percentageCTC = 30;
		float probMandatory = 25;
		float probOptional = 25;
		float probOrChildren = 25;
		float probAltChildren = 25;
		
		try {
		// Specify the user's preferences for the generation (so-called characteristics)
		GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(numberOfFeatures);			// Number of features
		characteristics.setPercentageCTC(percentageCTC);				// Percentage of constraints.
		characteristics.setProbabilityMandatory(probMandatory);			// Probability of a feature being mandatory
		characteristics.setProbabilityOptional(probOptional);			// Probability of a feature being optional
		characteristics.setProbabilityOr(probOrChildren);				// Probability of a feature being in an or-relation
		characteristics.setProbabilityAlternative(probAltChildren);		// Probability of a feature being in an alternative relationship
		
		// Generate the model with the specific characteristics (FaMa FM metamodel is used)
		AbstractFMGenerator generator = new FMGenerator();
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		FMStatistics statistics = new FMStatistics(fm);
		
		long seed = characteristics.getSeed();
		
		characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(numberOfFeatures);			// Number of features
		characteristics.setPercentageCTC(percentageCTC);				// Percentage of constraints.
		characteristics.setProbabilityMandatory(probMandatory);			// Probability of a feature being mandatory
		characteristics.setProbabilityOptional(probOptional);			// Probability of a feature being optional
		characteristics.setProbabilityOr(probOrChildren);				// Probability of a feature being in an or-relation
		characteristics.setProbabilityAlternative(probAltChildren);		// Probability of a feature being in an alternative relationship
		characteristics.setSeed(seed);									// Set the seed
		
		// Generate the model with the specific characteristics (FaMa FM metamodel is used)
		generator = new FMGenerator();
		fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		FMStatistics statistics2 = new FMStatistics(fm);
		
		assertEquals("Wrong CTCR", statistics.getCTCR(), statistics2.getCTCR());
		assertEquals("Wrong maximum branching factor", statistics.getMaxBranchingFactor(), statistics2.getMaxBranchingFactor());
		assertEquals("Wrong maximum set of children", statistics.getMaxSetChildren(), statistics2.getMaxSetChildren());
		assertEquals("Wrong number of altnernatives", statistics.getNoAlternative(), statistics2.getNoAlternative());
		assertEquals("Wrong number of altnernative children", statistics.getNoAlternativeChildren(), statistics2.getNoAlternativeChildren());
		assertEquals("Wrong number of CTC", statistics.getNoCrossTree(), statistics2.getNoCrossTree());
		assertEquals("Wrong number of excludes", statistics.getNoExcludes(), statistics2.getNoExcludes());
		assertEquals("Wrong number of features", statistics.getNoFeatures(), statistics2.getNoFeatures());
		assertEquals("Wrong number of mandatory features", statistics.getNoMandatory(), statistics2.getNoMandatory());
		assertEquals("Wrong number of optional features", statistics.getNoOptional(), statistics2.getNoOptional());
		assertEquals("Wrong number of or relations", statistics.getNoOr(), statistics2.getNoOr());
		assertEquals("Wrong number of or children", statistics.getNoOrChildren(), statistics2.getNoOrChildren());
		assertEquals("Wrong number of requires", statistics.getNoRequires(), statistics2.getNoRequires());
		
		} catch (BettyException e) {
			fail("wrong generation!");
		}
	}
	
	/**
	 * Try to reveal failures when generating a feature model specifying the basic input constraints (numer of features and percentage of constraints)
	 */
	@Test
	public void testGenerateFMMinimumConstraints() {
		
		int numberOfFeatures = 100;
		int percentageCTC = 30;
		
		try {
		// Specify the user's preferences for the generation (so-called characteristics)
		GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(numberOfFeatures);			// Number of features
		characteristics.setPercentageCTC(percentageCTC);				// Percentage of constraints.		
		
		// Generate the model with the specific characteristics (FaMa FM metamodel is used)
		AbstractFMGenerator generator = new FMGenerator();
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		
		// Get detailed statistics of the feature model generated
		FMStatistics statistics = new FMStatistics(fm);
		
		assertEquals("Wrong number of features generated",numberOfFeatures, statistics.getNoFeatures());
		assertEquals("Wrong percentage of CTC generated",(float)percentageCTC,statistics.getPercentageCTC());
		
		
		} catch (BettyException e) {
			fail("Wrong generation!");
		}
	}
	

	
	/**
	 * Try to reveal failures when generating a feature model specifying all possible input constraints. Low input values are used
	 */
	@Test
	public void testGenerateFMMaximumConstraints() {
		
		int numberOfFeatures = 100;
		int percentageCTC = 30;
		int maxBranchingFactor = 12;
		int maxSetChildren = 6;
		float probMandatory = 30;
		float probOptional = 30;
		float probOrChildren = 20;
		float probAltChildren = 20;
		
		float variation = 20;						// Variations allowed in the results (because of the random component)
		
		try {
			// Specify the user's preferences for the generation (so-called characteristics)
			GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
			characteristics.setNumberOfFeatures(numberOfFeatures);			// Number of features
			characteristics.setPercentageCTC(percentageCTC);				// Percentage of constraints.
			characteristics.setProbabilityMandatory(probMandatory);			// Probability of a feature being mandatory
			characteristics.setProbabilityOptional(probOptional);			// Probability of a feature being optional
			characteristics.setProbabilityOr(probOrChildren);				// Probability of a feature being in an or-relation
			characteristics.setProbabilityAlternative(probAltChildren);		// Probability of a feature being in an alternative relationship
			characteristics.setMaxBranchingFactor(maxBranchingFactor);		// Maximum branching factor
			characteristics.setMaxSetChildren(maxSetChildren);				// Maximum number of features in a set relationship
			
			// Generate the model with the specific characteristics (FaMa FM metamodel is used)
			AbstractFMGenerator generator = new FMGenerator();
			FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
			
			// Get detailed statistics of the feature model generated
			FMStatistics statistics = new FMStatistics(fm);
			System.out.println(statistics);
			
			assertEquals("Wrong number of features generated",numberOfFeatures, statistics.getNoFeatures());
			assertTrue("Wrong branching factor",maxBranchingFactor >= statistics.getMaxBranchingFactor());
			assertTrue("Wrong number of children in set relationships",maxSetChildren >= statistics.getMaxSetChildren());
			
			// Variations of +-10 are allowed because of the random nature of the experiment.
			if (!((statistics.getPercentageCTC() >= (percentageCTC-5)) && (statistics.getPercentageCTC() <= (percentageCTC+5))))
				fail("Wrong percentage of CTC generated");
			
			if (!((statistics.getPercentageMandatory() >= (probMandatory-variation)) && (statistics.getPercentageMandatory() <= (probMandatory+variation))))
				fail("Wrong percentage of mandatory features generated. Expected: " + probMandatory + ". Output: " + statistics.getPercentageMandatory());
			
			if (!((statistics.getPercentageOptional() >= (probOptional-variation)) && (statistics.getPercentageOptional() <= (probOptional+variation))))
				fail("Wrong percentage of optional features generated. Expected: " + probOptional + ". Output: " + statistics.getPercentageOptional());
			
			if (!((statistics.getPercentageOrChildren() >= (probOrChildren-variation)) && (statistics.getPercentageOrChildren() <= (probOrChildren+variation))))
				fail("Wrong percentage of features in or-relations. Expected: " + probOrChildren + ". Output: " + statistics.getPercentageOrChildren());
			
			if (!((statistics.getPercentageAltChildren() >= (probAltChildren-variation)) && (statistics.getPercentageAltChildren() <= (probAltChildren+variation))))
				fail("Wrong percentage of alternative children. Expected: " + probAltChildren + ". Output: " + statistics.getPercentageAltChildren());
	
		} catch (BettyException e) {
			fail("Wrong generation!");
		}
	}
	
	/**
	 * Try to reveal failures when generating a feature models automatically with random input constraints. Large input values are used
	 */
	@Test
	public void testGenerateFMRandomConstraints() {
		
		int MaxNumberOfFeatures = 500;				// Max number of features
		int MinNumberOfFeatures = 200;				// Minimum number of features
		int MaxPercentageCTC = 100;					// Max percentage of constraints
		int MaxProbabilityMandatory = 100;			// Max probability for mandatory features
		int MaxProbabilityOptional = 100;			// Max probability for optional features
		int MaxProbabilityOr = 100;					// Max probability for or children
		int MaxProbabilityAlternative = 100;		// Max probability for alternative children
		int MaxBranchingFactor = 20;				// Max branching factor
		int MaxSetChildren = 10;					// Max number of children in set relationships
		
		float variation = 10;						// Max variations allowed in the results (because of the random component). Depend on the size and number of the model generated
		int numberOfIterations = 20;
		int numberOfModels = 30;

		Random random = new Random();
		
		float avgPercentageMandatory = 0;
		float avgPercentageOptional = 0;
		float avgPercentageOr = 0;
		float avgPercentageAlt = 0;

		boolean modelGenerated = true;
		
		for(int i=0; i<numberOfIterations ;i++) {

			int numberOfFeatures = Math.max(MinNumberOfFeatures,random.nextInt(MaxNumberOfFeatures+1)) ;
			int percentageCTC = random.nextInt(MaxPercentageCTC+1);
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
			
			modelGenerated = true;
			for (int j=0;j<numberOfModels;j++) {
		
				try {
					// Specify the user's preferences for the generation (so-called characteristics)
					GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
					characteristics.setNumberOfFeatures(numberOfFeatures);					// Number of features.
					characteristics.setPercentageCTC(percentageCTC);						// Probability of constraints.
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
					AbstractFMGenerator generator = new FMGenerator();
					FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
					
					//System.out.println("Seed: " + characteristics.getSeed());
					
					// Get detailed statistics of the feature model generated
					FMStatistics statistics = new FMStatistics(fm);
					
					assertEquals("Wrong number of features generated",numberOfFeatures, statistics.getNoFeatures());
					
					if (!((statistics.getPercentageCTC() >= (percentageCTC-5)) && (statistics.getPercentageCTC() <= (percentageCTC+5))))
						fail("Wrong percentage of CTC generated");
					
					if (maxBranchingFactor != -1)
						assertTrue("Wrong branching factor",maxBranchingFactor >= statistics.getMaxBranchingFactor());
					
					if (maxSetChildren != -1)
						assertTrue("Wrong number of children in set relationships",maxSetChildren >= statistics.getMaxSetChildren());
					
					
					avgPercentageMandatory += statistics.getNoMandatory();
					avgPercentageOptional += statistics.getNoOptional();
					avgPercentageOr += statistics.getNoOrChildren();
					avgPercentageAlt += statistics.getNoAlternativeChildren();
					
					
					} catch (BettyException e) {
						System.out.println("Non-valid input constraints. Input configuration discarded.");
						modelGenerated = false;
						break;
					}
				}
			
				if (modelGenerated) {
					avgPercentageMandatory = ((avgPercentageMandatory / numberOfModels) * 100)/ numberOfFeatures;
					avgPercentageOptional = ((avgPercentageOptional / numberOfModels) * 100)/ numberOfFeatures;
					avgPercentageOr = ((avgPercentageOr / numberOfModels) * 100)/ numberOfFeatures;
					avgPercentageAlt = ((avgPercentageAlt / numberOfModels) * 100)/ numberOfFeatures;
					
					if (probMandatory != -1)
						if (!((avgPercentageMandatory >= (probMandatory-variation)) && (avgPercentageMandatory <= (probMandatory+variation))))
							fail("Wrong percentage of mandatory features generated. Expected: " + probMandatory + ". Output: " + avgPercentageMandatory);
					
					if (probOptional != -1)
						if (!((avgPercentageOptional >= (probOptional-variation)) && (avgPercentageOptional <= (probOptional+variation))))
							fail("Wrong percentage of optional features generated. Expected: " + probOptional + ". Output: " + avgPercentageOptional);
					
					if (probOrChildren != -1)
						if (!((avgPercentageOr >= (probOrChildren-variation)) && (avgPercentageOr <= (probOrChildren+variation))))
							fail("Wrong percentage of features in or-relations. Expected: " + probOrChildren + ". Output: " + avgPercentageOr);
					
					if (probAltChildren != -1)
						if (!((avgPercentageAlt >= (probAltChildren-variation)) && (avgPercentageAlt <= (probAltChildren+variation))))
							fail("Wrong percentage of alternative children. Expected: " + probAltChildren + ". Output: " + avgPercentageAlt);
				}
				
			
			}
			
		}
}
