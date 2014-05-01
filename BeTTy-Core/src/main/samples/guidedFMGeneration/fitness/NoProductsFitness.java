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
package main.samples.guidedFMGeneration.fitness;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.generator.FM.Evolutionay.FitnessFunction;


public class NoProductsFitness implements FitnessFunction {


	@Override
	public double fitness(FAMAFeatureModel fm) {
		QuestionTrader qt = new QuestionTrader();
		qt.setSelectedReasoner("JaCoP");
		NumberOfProductsQuestion nopq=(NumberOfProductsQuestion) qt.createQuestion("#Products");
		qt.setVariabilityModel(fm);
		qt.ask(nopq);
		if(nopq==null){
			throw new NullPointerException("FaMa must be installed and configured to run this experiment");
		}
	    return nopq.getNumberOfProducts();		
	}

}
