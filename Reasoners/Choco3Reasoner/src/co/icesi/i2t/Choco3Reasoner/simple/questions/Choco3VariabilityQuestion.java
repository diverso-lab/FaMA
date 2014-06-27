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
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariabilityQuestion;

/**
 * Implementation to solve the variability question using the Choco 3 reasoner.
 * This operation calculates the variability degree of a feature model.
 * The variability degree is the ratio between the number of products and 2^n where n is 
 * the number of features considered. In particular, 2^n is the potential number of products 
 * represented by a feature model assuming that any combination of features is allowed. 
 * The root and non-leaf features are often not considered.
 * 
 * Variability = number of products / 2^n , where n is the number of features considered
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoVariabilityQuestion Choco 2 implementation for the variability question
 * @version 0.1, June 2014
 */
public class Choco3VariabilityQuestion extends Choco3Question implements
		VariabilityQuestion {

	/**
	 * Default implementation for the variability question provided by the FAMA SDK.
	 */
	private DefaultVariabilityQuestionImpl variabilityQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion#getVariability()
	 */
	public double getVariability() {
		return this.variabilityQuestion.getVariability();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.variabilityQuestion = new DefaultVariabilityQuestionImpl();
//		this.variabilityQuestion.preAnswer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		return this.variabilityQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.variabilityQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the variability question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultVariabilityQuestionImpl extends DefaultVariabilityQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariabilityQuestion#numberOfProductsQuestionFactory()
		 */
		@Override
		public NumberOfProductsQuestion numberOfProductsQuestionFactory() {
			return new Choco3NumberOfProductsQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariabilityQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariabilityQuestion#getNumberOfFeatures()
		 */
		@Override
		public double getNumberOfFeatures() {
			return choco3Reasoner.getAllFeatures().size();
		}
		
	}

}
