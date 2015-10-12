/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.FAMA.Reasoner.questions.defaultImpl;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.FilterQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.SetQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public abstract class DefaultCommonalityQuestion implements CommonalityQuestion {

	private Configuration conf;

	private double commonality;
	private double totalNoP;

	public DefaultCommonalityQuestion() {
		conf = null;
	}

	public DefaultCommonalityQuestion(Configuration f) {
		this.conf = f;
	}

	public void setConfiguration(Configuration f) {
		this.conf = f;
	}

	public double getCommonality() {
		if(totalNoP==0){
			return 0d;
		}else{return commonality/totalNoP;}
	}

	public void preAnswer(Reasoner r) {
		commonality = 0;
		totalNoP = 0;
	}

	public PerformanceResult answer(Reasoner r) {
		PerformanceResult res = this.performanceResultFactory();
		if (conf == null) {
			throw new FAMAParameterException(
					"No feature selected on CommonalityQuestion");
		}

		NumberOfProductsQuestion number = this
				.numberOfProductsQuestionFactory();
		r.ask(number);
		this.totalNoP = number.getNumberOfProducts();

		r.applyStagedConfiguration(conf);
		res = r.ask(number);
		r.unapplyStagedConfigurations();
		commonality = (double) number.getNumberOfProducts();

		return res;
	}

	public String toString() {
		String res = "Commonality  is " + commonality;
		return res;
	}

	public abstract NumberOfProductsQuestion numberOfProductsQuestionFactory();

	public abstract PerformanceResult performanceResultFactory();

}
