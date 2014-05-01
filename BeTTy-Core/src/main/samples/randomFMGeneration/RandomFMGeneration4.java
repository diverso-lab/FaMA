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
package main.samples.randomFMGeneration;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.MetamorphicFMGenerator;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;
import es.us.isa.utils.FMWriter;

/* This example shows how to generate a feature model and its corresponding set of products. The set of products is generated at the same
 * time that the model and does not use any analysis tool. Both, the model and its set of products can be used for functional testing.
 * For more details, please go to the BeTTy Website.
 */

public class RandomFMGeneration4 {


	public static void main(String[] args) throws Exception, BettyException {
		
		// STEP 1: Specify the user's preferences for the generation (characteristics)
		GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(30);			// Number of features.
		characteristics.setPercentageCTC(10);				// Percentage of constraints.
		characteristics.setProbabilityMandatory(25);		// Probability of a feature being mandatory
		characteristics.setProbabilityOptional(25);			// Probability of a feature being optional.
		characteristics.setProbabilityOr(25);				// Probability of a feature being in an or-relation.
		characteristics.setProbabilityAlternative(25);		// Probability of a feature being in an alternative relation.
		characteristics.setMaxBranchingFactor(12);			// Max branching factor (default value = 10)
		characteristics.setMaxSetChildren(6);				// Max number of children in a set relationship (default value = 5)
		
		// Max number of products of the feature model to be generated. Too large values could cause memory overflows or the program getting stuck.
		characteristics.setMaxProducts(10000);			
		
		// STEP 2: Generate the model with the specific characteristics (FaMa FM metamodel is used)
		FMGenerator fmGen = new FMGenerator();
		MetamorphicFMGenerator generator = new MetamorphicFMGenerator(fmGen);
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		
		// OPTIONAL: Show detailed statistics of the feature model generated
		FMStatistics statistics = new FMStatistics(fm);
		System.out.println(statistics);
		System.out.println("Number of products of the feature model generated: " + generator.getNumberOfProducts());
		
		// STEP 3: Save the model and the products
		FMWriter writer = new FMWriter();
		writer.saveFM(fm, "./model.xml"); 					// Other valid formats: .splx, .fm, .dot
		writer.saveProducts(generator.getPoducts(), "./products.csv");
	}

}
