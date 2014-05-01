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
package main.samples.randomAFMGeneration;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.attributed.AttributedCharacteristic;
import es.us.isa.generator.FM.attributed.AttributedFMGenerator;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMWriter;

/* This example shows how to generate an attributed feature model and save it in textual format. The number of features and percentage of cross-tree constraints
 * are given as input parameters.
 */
public class RandomAttributedFMGenerationTest {

	public static void main(String[] args) throws Exception, BettyException {

		// STEP 1: Specify the user's preferences for the generation (so-called
		// characteristics)
		// Our characteristics are AttributedCharacteristics
		AttributedCharacteristic characteristics = new AttributedCharacteristic();
		characteristics.setNumberOfFeatures(20); // Number of features
		characteristics.setPercentageCTC(30); // Percentage of cross-tree
												// constraints.
		characteristics.setNumberOfExtendedCTC(5);
		characteristics.setAttributeType(AttributedCharacteristic.INTEGER_TYPE);
		characteristics
				.setDefaultValueDistributionFunction((AttributedCharacteristic.UNIFORM_DISTRIBUTION));
		characteristics.addRange(new Range(3, 100));
		characteristics.setNumberOfAttibutesPerFeature(5);
		String argumentsDistributionFunction[] = { "3", "100" };
		characteristics
				.setDistributionFunctionArguments(argumentsDistributionFunction);
		characteristics.setHeadAttributeName("Atribute");

		// STEP 2: Generate the model with the specific characteristics (FaMa
		// Attributed FM metamodel is used)
		for (int i = 0; i < 1000; i++) {
		characteristics.setSeed(characteristics.getSeed()+i);
		AbstractFMGenerator gen = new FMGenerator();
		AttributedFMGenerator generator = new AttributedFMGenerator(gen);
		FAMAAttributedFeatureModel afm = (FAMAAttributedFeatureModel) generator
				.generateFM(characteristics);

		// STEP 3: Save the model
		FMWriter writer = new FMWriter();
		writer.saveFM(afm, "./attributedModel.afm");
		AttributedReader reader = new AttributedReader();
		VariabilityModel fm = reader.parseFile("./attributedModel.afm");
		
		
		 }
	}
}
