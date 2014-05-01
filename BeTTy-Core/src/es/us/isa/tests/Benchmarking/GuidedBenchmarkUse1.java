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
package es.us.isa.tests.Benchmarking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.benchmarking.FAMABenchmark;
import es.us.isa.benchmarking.RandomExperiment;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.FMWriter;

public class GuidedBenchmarkUse1 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//STEP 1. We defines the characteristics of the desired models
		GeneratorCharacteristics ch = new GeneratorCharacteristics();
		ch.setMaxBranchingFactor(20);
		ch.setNumberOfFeatures(100);
		ch.setProbabilityAlternative(20);
		ch.setPercentageCTC(20);
		ch.setProbabilityMandatory(40);
		ch.setProbabilityOptional(20);
		ch.setProbabilityOr(20);
		ch.setSeed(12045451);
		
		//STEP 2. Wich reasoners we will use
		Collection<String> reasoners= new ArrayList<String>();
		reasoners.add("Sat4j");
		//STEP 3. Wich questions we will perform
		Collection<String> questions= new ArrayList<String>();
		questions.add("Valid");
		//Create the desired generador for the experiments
		FMGenerator generator= new FMGenerator();
		//In this example we will use the integration with FaMaFW,
		//so we use the FaMAFacade for benchmarking
		FAMABenchmark bech = new FAMABenchmark(generator);
		
		//We creates a set of 20 different experiments (diferent models)
		ArrayList<RandomExperiment> setOfExperiments = bech.createSetRandomExperiment(20, ch);
		
		//Launch the experiments
		bech.execute(reasoners, questions, setOfExperiments);

		//STEP 4 SAVE the results
		bech.saveExperiments(setOfExperiments,"results.csv");

//		If we want to save also the models
		Iterator<RandomExperiment> it = setOfExperiments.iterator();
		FMWriter VMwriter = new FMWriter();
		
		int u=0;
		while(it.hasNext()){
			VMwriter.saveFM((FAMAFeatureModel) it.next().getVariabilityModel(), "./exps/"+u+".xml");
			u++;
			
		}
	}

}
