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


import main.samples.guidedFMGeneration.fitness.BranchFitness;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLWriter;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.Evolutionay.EvolutionaryFMGenerator;



public class SampleFMGeneration8 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int Nof[] = { 10, 100, 200, 300, 400, 500 };
		int ctc[] = { 10, 15, 50 };
		XMLWriter writer = new XMLWriter();

		for (int i = 0; i < Nof.length; i++) {
			for (int j = 0; j < ctc.length; j++) {
				for (int u = 0; u < 10; u++) {
					// STEP 1: Specify the user's preferences for the generation (characteristics)

					GeneratorCharacteristics ch = new GeneratorCharacteristics();
					ch.setMaxBranchingFactor(200);
					ch.setNumberOfFeatures(Nof[i]);
					ch.setProbabilityAlternative(20);
					ch.setPercentageCTC(ctc[j]);
					ch.setProbabilityMandatory(40);
					ch.setProbabilityOptional(20);
					ch.setProbabilityOr(20);
					// STEP 2.1: Generate the model with the specific characteristics (FaMa FM metamodel is used) using a stantard generator

					FMGenerator standarGen = new FMGenerator();
					VariabilityModel generateFM = standarGen.generateFM(ch);
					BranchFitness bf = new BranchFitness();
					double res1 = bf.fitness((FAMAFeatureModel) generateFM);
					// STEP 2.2: Generate the model with the specific characteristics (FaMa FM metamodel is used) using a genetic generator

					EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
					
					//STEP 2.3: Set the fitness function for the genetic algorithm and the max number of generations allowed
					generator.setFitnessFunction(new BranchFitness());
					generator.setMaximize(false);
					generator.setMaxGenerations(20);

					VariabilityModel geneticg=generator.generateFM(ch);
					double res2 = generator.getBestFitness();
					// STEP 3: Compare results
					if(res1>res2){
						System.out.println("HIT");
					}else{
						System.out.println("MISS");
					}
					// STEP 4: Save the model

					writer.writeFile("evolutiveTests/brachingFactor/Evolutive-"+Nof[i]+"-"+ctc[j]+"-"+u+".xml",generateFM );
					writer.writeFile("evolutiveTests/brachingFactor/Standard-"+Nof[i]+"-"+ctc[j]+"-"+u+".xml",geneticg );
				}
			}
		}

	}

}
