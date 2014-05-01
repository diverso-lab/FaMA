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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import main.samples.guidedFMGeneration.fitness.NoProductsFitness;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.Evolutionay.EvolutionaryFMGenerator;
import es.us.isa.utils.BettyException;

/* This example compares the effectiveness evolutionary and random search in searching for 
 * a feature model maximizing its number of products. (average of 10 executions is shown).
 * 
 * Note 1: Including the FaMa Framework in the classpath is required to run this experiment.
 * Note 2: The execution may take several minutes. 
 * Note 3: The default values for the evolutionary algorithm are:
 * 	- Population size: 200
 * 	- Crossover probability: 0.7
 * 	- Mutation probability: 0.01
 * 	- Maximum number of generation: 25
 * 	- Selection mechanism: Roulette Wheel
 */

public class SampleFMGeneration4 {

	/**
	 * @param args
	 * @throws BettyException
	 * @throws Exception
	 */

	public static void main(String[] args) throws BettyException, Exception {
		int hits = 0;
		List<Double> randomFitnesses = new ArrayList<Double>();
		List<Double> EAFitnesses = new ArrayList<Double>();
		int iterations = 10;
		for (int u = 0; u < iterations; u++) {
			// STEP 1: Specify the user's preferences for the generation
			// (characteristics)

			GeneratorCharacteristics ch = new GeneratorCharacteristics();
			ch.setMaxBranchingFactor(10);
			ch.setNumberOfFeatures(20);
			ch.setProbabilityAlternative(20);
			ch.setPercentageCTC(20);
			ch.setProbabilityMandatory(40);	
			ch.setProbabilityOptional(20);
			ch.setProbabilityOr(20);
			// STEP 2.1: Generate the model with the specific characteristics
			// (FaMa FM metamodel is used) using a random generator. We do
			// this 5000 times to give the same opportunities for the two differents
			// generators

			FMGenerator randomGen = new FMGenerator();
			double bestRandomFitness = 0;
			for (int i = 0; i < 5000; i++) {
				randomGen.generateFM(ch);
				NoProductsFitness fitnessClass = new NoProductsFitness();
				double randomFitness = fitnessClass.fitness((FAMAFeatureModel) randomGen.generateFM(ch));
				if (randomFitness > bestRandomFitness) {
					bestRandomFitness = randomFitness;
				}
			}
			randomFitnesses.add(bestRandomFitness);

			// STEP 2.2: Generate the model with the specific characteristics
			// (FaMa FM metamodel is used) using the evoluionary generator.
			EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
			generator.setFitnessFunction(new NoProductsFitness());

			// STEP 2.3: Set the fitness function for the evolutionary algorithm.
			generator.generateFM(ch);
			double bestEAFitness = generator.getBestFitness();
			EAFitnesses.add(bestEAFitness);

			// STEP 3: Compare results

			if (bestEAFitness > bestRandomFitness) {
				hits++;
			}
		}

		System.out.println("=============  RESULTS (" + iterations + " ITERATIONS) ====================");
		System.out.println("ITERATION            RANDOM FITNESS            EVOLUTIONARY ALGORITHM FITNESS");
		
		DecimalFormat df = new DecimalFormat("#.##");
		int i =0;
		while (i<iterations) {
			System.out.println("    "  +i + "                    " + df.format(randomFitnesses.get(i))  + "                             " + df.format(EAFitnesses.get(i)));
			i++;
		}
		
		System.out.println("\n The evolutive generator obtained better results in "+ hits + " out of 10 iterations (Effectiveness = " + ((hits*100)/iterations) + "%)");

	}

}
