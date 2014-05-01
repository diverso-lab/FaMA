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
package es.us.isa.benchmarking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.IGenerator;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;
/**
 * this facade will allow to have an out off-shelve woriking benchm system using FaMa TS
 * @author malawito
 *
 */
public class FAMABenchmark extends Benchmark {

	public FAMABenchmark(IGenerator generator) {
		super(generator);
	}

	public FAMABenchmark() {
		super();
	}
	
	Timer thread = null;
	/**
	 * Willl execute a experiment with some questions and reasoners. 
	 * @param exp the experiment
	 * @param questionType The FaMa questionto be performed
	 * @param reasoner The reasoner to be used
	 * @param p the product(needed in some faMa questions)
	 * @return a set of results 
	 */
	public Map<String, String> executeWithArgs(Experiment exp,String questionType, String reasoner, Product p) {

		Map<String, String> results = null;
		QuestionTrader qt = new QuestionTrader();
		qt.setVariabilityModel(exp.getVariabilityModel());
		
		qt.setSelectedReasoner(reasoner);
		if (questionType.equals("Valid")) {
			
			ValidQuestion q = (ValidQuestion) qt.createQuestion(questionType);
			results = qt.ask(q).getResults();
			results.put(reasoner + ":" + "isValid", String.valueOf(q.isValid()));
			
		} else if (questionType.equals("#Products")) {
		
			NumberOfProductsQuestion q = (NumberOfProductsQuestion) qt
					.createQuestion(questionType);
			results = qt.ask(q).getResults();
			results.put(reasoner + ":" + "#P", String.valueOf(q.getNumberOfProducts()));
		
		} else if (questionType.equals("ValidProduct")) {
			
			ValidProductQuestion q = (ValidProductQuestion) qt
					.createQuestion(questionType);
			q.setProduct(p);
			results = qt.ask(q).getResults();
			results.put(reasoner + ":" + "isProductValid", String.valueOf(q.isValid()));
		}

		return results;
	}
	
	/**
	 * Execute a set of experiments, with a set of reasoners and a set of questions 
	 * @param reasoners The reasoners to be used
	 * @param questions The questions to be performed
	 * @param exps The experiments to be processed
	 * @throws Exception A exception that means that a new thread per questions and reasoners can not be propertly used
	 */
	public void execute(Collection<String> reasoners,Collection<String> questions, Collection<RandomExperiment> exps) throws Exception {

		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		QuestionTrader qt= new QuestionTrader();
		Iterator<RandomExperiment> expIt= exps.iterator();
		while(expIt.hasNext()){
			RandomExperiment exp=expIt.next();
			qt.setVariabilityModel(exp.getVariabilityModel());
			Iterator<String> reasonersIt= reasoners.iterator();
			while(reasonersIt.hasNext()){
				String reasoner=reasonersIt.next();
				Iterator<String> questionIt = questions.iterator();
				while (questionIt.hasNext()) {
					thread = new Timer(exp,questionIt.next(), reasoner, this);
					thread.execute();
				}

			}
			
			
		}
		

	}

	/**
	 * Generates a set of randomFm, using the desired generator and using a set of characteristics
	 * @param Chars The characteristics
	 * @return a set of random experiments
	 * @throws IOException
	 * @throws BettyException 
	 */
	public Collection<RandomExperiment> generateRandomFM(
			Collection<Characteristics> Chars) throws IOException, BettyException {

		Collection<RandomExperiment> col = new ArrayList<RandomExperiment>();
		Iterator<Characteristics> it = Chars.iterator();

		while (it.hasNext()) {

			GeneratorCharacteristics character = (GeneratorCharacteristics) it.next();
			RandomExperiment exp = super.createRandomExperiment(character);

			exp.setName(character.getNumberOfFeatures() +"-"+ character.getPercentageCTC());
			col.add(exp);

		}

		return col;

	}



}
