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
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;
import es.us.isa.utils.FMWriter;


/* This example shows how to generate a basic feature model and save it in xml format. The number of features and percentage of cross-tree constraints
 * are given as input parameters.
 */

public class RandomFMGeneration1 {

	public static void main(String[] args) throws Exception, BettyException {
		
		// STEP 1: Specify the user's preferences for the generation (so-called characteristics)
		GeneratorCharacteristics characteristics = new GeneratorCharacteristics();
		characteristics.setNumberOfFeatures(5);		// Number of features
		characteristics.setPercentageCTC(100);			// Percentage of cross-tree constraints.
		
		// STEP 2: Generate the model with the specific characteristics (FaMa FM metamodel is used)
		AbstractFMGenerator generator = new FMGenerator();
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(characteristics);
		
		// OPTIONAL: Show detailed statistics of the feature model generated
		FMStatistics statistics = new FMStatistics(fm);
		System.out.println(statistics);
		
		// STEP 3: Save the model
		FMWriter writer = new FMWriter();
		writer.saveFM(fm, "./model.afm"); // Other valid formats: .splx, .fm, .dot

	}

}
