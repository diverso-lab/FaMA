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
package es.us.isa.tests.Evolutionary;

import static org.junit.Assert.assertEquals;
import main.samples.guidedFMGeneration.fitness.BranchFitness;
import main.samples.guidedFMGeneration.fitness.CTCRFitness;
import main.samples.guidedFMGeneration.fitness.NoProductsFitness;
import main.samples.guidedFMGeneration.fitness.TimeFitness;

import org.junit.Test;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.Evolutionay.EvolutionaryFMGenerator;

public class GeneticTest {
	int Nof[] = { 10, 100, 200, 300, 400, 500 };
	int ctc[] = { 10, 15, 50 };
	GeneratorCharacteristics ch = new GeneratorCharacteristics();

	
	public void resetCharacteristics(int nof, int ctc){
	
		ch.setMaxBranchingFactor(50);
		ch.setNumberOfFeatures(nof);
		ch.setProbabilityAlternative(20);
		ch.setPercentageCTC(ctc);
		ch.setProbabilityMandatory(40);
		ch.setProbabilityOptional(20);
		ch.setProbabilityOr(20);
		
	}
	
	@Test
	public void MinimizingBranchingFactor() throws Exception {
		int hitRate=0;
		for (int i = 0; i < Nof.length; i++) {
			for (int j = 0; j < ctc.length; j++) {
				for (int u = 0; u < 10; u++) {
					resetCharacteristics(Nof[i],ctc[j]);
					
					BranchFitness bf = new BranchFitness();
					FMGenerator standarGen = new FMGenerator();
					double bestFitnessStandardGenerator = 0;
						for (int v = 0; v < 5000; v++) {
							VariabilityModel generateFM = standarGen.generateFM(ch);
							double res1 = bf.fitness((FAMAFeatureModel) generateFM);
							if (res1 > bestFitnessStandardGenerator) {
								bestFitnessStandardGenerator = res1;
							}
						}
		
					EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
					generator.setFitnessFunction(new BranchFitness());
					generator.setMaximize(false);
					generator.generateFM(ch);
					double bestFitnessEvolutiveGenerator = generator.getBestFitness();
		
					if(bestFitnessStandardGenerator>bestFitnessEvolutiveGenerator){
						hitRate++;
					}
		
					boolean improvementReached=false;
					if(hitRate>8){
						improvementReached=true;
						assertEquals("Genetic Generator has not improved the standard generation",true, improvementReached);
					}
				}
			}
		}
	}
	@Test
	public void MaximizeCTCRatio() throws Exception {
		int hitRate=0;

		for (int i = 0; i < Nof.length; i++) {
			for (int j = 0; j < ctc.length; j++) {
				for (int u = 0; u < 10; u++) {
					resetCharacteristics(Nof[i],ctc[j]);
					
					CTCRFitness bf = new CTCRFitness();
					FMGenerator standarGen = new FMGenerator();
					double bestFitnessStandardGenerator = 0;
						for (int v = 0; v < 5000; v++) {
							VariabilityModel generateFM = standarGen.generateFM(ch);
							double res1 = bf.fitness((FAMAFeatureModel) generateFM);
							
							if (res1 > bestFitnessStandardGenerator) {
								bestFitnessStandardGenerator = res1;
							}
						}
		
					EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
					generator.setFitnessFunction(new CTCRFitness());
					generator.generateFM(ch);
					double bestFitnessEvolutiveGenerator = generator.getBestFitness();
		
					if(bestFitnessStandardGenerator>bestFitnessEvolutiveGenerator){
						hitRate++;
					}
		
					boolean improvementReached=false;
					if(hitRate>8){
						improvementReached=true;
						assertEquals("Genetic Generator has not improved the standard generation",true, improvementReached);
					}
				
				}
			}
		}

	}
	@Test
	public void MaximizeNumberOfProducts() throws Exception {
		int hitRate=0;

		for (int i = 0; i < Nof.length-2; i++) {
			for (int j = 0; j < ctc.length-2; j++) {
				for (int u = 0; u < 10; u++) {
					
					

					resetCharacteristics(Nof[i],ctc[j]);
					
					NoProductsFitness bf = new NoProductsFitness();
					FMGenerator standarGen = new FMGenerator();
					double bestFitnessStandardGenerator = 0;
						for (int v = 0; v < 5000; v++) {
							VariabilityModel generateFM = standarGen.generateFM(ch);
							double res1 = bf.fitness((FAMAFeatureModel) generateFM);
							if (res1 > bestFitnessStandardGenerator) {
								bestFitnessStandardGenerator = res1;
							}
						}
		
					EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
					generator.setFitnessFunction(new NoProductsFitness());
					generator.generateFM(ch);
					double bestFitnessEvolutiveGenerator = generator.getBestFitness();
		
					if(bestFitnessStandardGenerator>bestFitnessEvolutiveGenerator){
						hitRate++;
					}
		
					boolean improvementReached=false;
					if(hitRate>8){
						improvementReached=true;
						assertEquals("Genetic Generator has not improved the standard generation",true, improvementReached);
					}
				}
			}
		}
	
	}
	
	@Test
	public void MaximizeTime() throws Exception {
	
		int hitRate=0;
		System.out.println("This test will take lots of time, be sure if you launch it");
		
		resetCharacteristics(100, 10);

		TimeFitness bf = new TimeFitness();
		FMGenerator standarGen = new FMGenerator();
		double bestFitnessStandardGenerator = 0;
		for (int v = 0; v < 5000; v++) {
			VariabilityModel generateFM = standarGen.generateFM(ch);
			double res1 = bf.fitness((FAMAFeatureModel) generateFM);
			if (res1 > bestFitnessStandardGenerator) {
				bestFitnessStandardGenerator = res1;
			}
		}
		
		EvolutionaryFMGenerator generator = new EvolutionaryFMGenerator();
		generator.setFitnessFunction(new TimeFitness());
		generator.generateFM(ch);
		double bestFitnessEvolutiveGenerator = generator.getBestFitness();
		if(bestFitnessStandardGenerator>bestFitnessEvolutiveGenerator){
			hitRate++;
		}
		boolean improvementReached=false;
		if(hitRate>8){
			improvementReached=true;
			assertEquals("Genetic Generator has not improved the standard generation",true, improvementReached);
		}
	}
}
