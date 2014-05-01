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
package main.samples.guidedFMGeneration;

import main.samples.guidedFMGeneration.fitness.CTCRFitness;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.Evolutionay.EvolutionaryFMGenerator;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;
import es.us.isa.utils.FMWriter;

public class GuidedFMGeneration2 {

/* This example shows how to generate a FM with several size constraints and a maximum
 * cross-tree constraint ratio (the optimum value is 0.6).
 * 
 *  Note: The default values for the evolutionary algorithm are:
 * 	- Population size: 200
 * 	- Crossover probability: 0.7
 * 	- Mutation probability: 0.01
 * 	- Maximum number of generation: 25
 * 	- Selection mechanism: Roulette Wheel
 */
 
	
	public static void main(String[] args) throws BettyException, Exception {
	
		// STEP 1: Specify the user's preferences for the generation (characteristics)
		GeneratorCharacteristics ch = new GeneratorCharacteristics();
		ch.setNumberOfFeatures(100);
		ch.setPercentageCTC(30);
					
		// STEP 2: Create a fitness function determining the optimization criteria (e.g. maximize CTCR)
		CTCRFitness fitnessFunction = new CTCRFitness();

		// STEP 3: Search for a feature model with the given properties (100 features, 30% CTC) minimizing the CTC ratio.
		EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
		generator.setFitnessFunction(fitnessFunction);					// Set fitness function
		FAMAFeatureModel fm = (FAMAFeatureModel) generator.generateFM(ch);
		
		// OPTIONAL: Show detailed statistics of the feature model generated
		FMStatistics statistics = new FMStatistics(fm);
		System.out.println(statistics);
		
		// STEP 4: Save the model
		FMWriter writer = new FMWriter();
		writer.saveFM(fm, "./model.xml"); 					// Other valid formats: .sxml, .fm, .dot
	}
}
