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

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.FMStatistics;

public class SampleFMGeneration7 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	//This example tries to explain how to obtain only feature models with a exact perfectages of requieres
	public static void main(String[] args) throws Exception {
		
//		STEP 1 : Set the Characteristics of the desired experimetn
		GeneratorCharacteristics ch = new GeneratorCharacteristics();
		ch.setMaxBranchingFactor(100);
		ch.setNumberOfFeatures(200);
		ch.setProbabilityAlternative(20);
		ch.setPercentageCTC(10);
		ch.setProbabilityMandatory(40);
		ch.setProbabilityOptional(20);
		ch.setProbabilityOr(20);
		int NOTries=0;
		FMGenerator standarGen = new FMGenerator();

		//In the example we want 10 models with 10% of requires
		for (int u = 0; u < 10; u++) {
			boolean found=false;
			while(!found ){
				ch.setSeed(ch.getSeed()+100);
				VariabilityModel generateFM = standarGen.generateFM(ch);
//				STEP 2 : Creates de Stadistics about the FM

				FMStatistics stadistics= new FMStatistics((FAMAFeatureModel) generateFM);
//				STEP 3 : Check if we have what we want ie. 45% os requires
				if(((100*stadistics.getNoRequires()/stadistics.getNoCrossTree()))==45){
					found=true;
					NOTries=0;
					System.out.println("HIT "+(u+1));
					

				}else{
					NOTries++;
					System.out.println("MISS");
				}
			}
			
			
		}

}

}
