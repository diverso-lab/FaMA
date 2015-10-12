/**
 *  This file is part of FaMaTS.
 *
 *  FaMaTS is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FaMaTS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.icesi.i2t.Choco3Reasoner.simple.questions;

import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCommonalityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

/**
 * Implementation to solve the commonality question using the Choco 3 reasoner.
 * This operation calculates the percentage of products represented by the feature model
 * including the input configuration.
 * 
 * Commonality = number of products after applying a configuration / number of products
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoCommonalityQuestion Choco 2 implementation for the commonality question.
 * @version 1.0, June 2014
 */
public class Choco3CommonalityQuestion extends Choco3Question implements
		CommonalityQuestion {

	/**
	 * Default implementation for the commonality question provided by the FAMA SDK.
	 */
	private DefaultCommonalityQuestionImpl commonalityQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	/**
	 * A configuration to be evaluated.
	 */
	private Configuration configuration;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion#setConfiguration(es.us.isa.FAMA.stagedConfigManager.Configuration)
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion#getCommonality()
	 */
	public double getCommonality() {
		return this.commonalityQuestion.getCommonality();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.commonalityQuestion = new DefaultCommonalityQuestionImpl();
		this.commonalityQuestion.preAnswer(reasoner);
		this.commonalityQuestion.setConfiguration(this.configuration);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		return this.commonalityQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.commonalityQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the commonality question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultCommonalityQuestionImpl extends DefaultCommonalityQuestion {
		
		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCommonalityQuestion#numberOfProductsQuestionFactory()
		 */
		@Override
		public NumberOfProductsQuestion numberOfProductsQuestionFactory() {
			return new Choco3NumberOfProductsQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCommonalityQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}
		
		/**
		 * Returns <code>true</code> if and only if the given feature is a valid feature in the feature model.
		 * 
		 * @param feature Feature that is to be tested
		 * @return <code>true</code> if the given feature is valid
		 */
		public boolean isValid(GenericFeature feature) {
			return choco3Reasoner.getAllFeatures().contains(feature);
		}
	}

}
