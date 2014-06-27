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

import java.util.Collection;

import co.icesi.i2t.Choco3Reasoner.Choco3PerformanceResult;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Question;
import co.icesi.i2t.Choco3Reasoner.simple.Choco3Reasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

/**
 * Implementation to solve the valid product question using the Choco 3 reasoner.
 * This operation determines if a product is valid or not for a given feature model represented by a CSP.
 * A product is a valid product if it is a solution of the CSP.
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoValidProductQuestion Choco 2 implementation for the valid product question
 * @version 0.1, June 2014
 */
public class Choco3ValidProductQuestion extends Choco3Question implements
		ValidProductQuestion {

	/**
	 * Default implementation for the valid product question provided by the FAMA SDK.
	 */
	private DefaultValidProductQuestionImpl validProductQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	
	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion#setProduct(es.us.isa.FAMA.models.featureModel.Product)
	 */
	public void setProduct(Product product) {
		this.validProductQuestion.setProduct(product);
	}

	/* (non-Javadoc)
	 * @see es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion#isValid()
	 */
	public boolean isValid() {
		return this.validProductQuestion.isValid();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.validProductQuestion = new DefaultValidProductQuestionImpl();
//		this.validProductQuestion.preAnswer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		return this.validProductQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.validProductQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the valid product question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultValidProductQuestionImpl extends DefaultValidProductQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion#validQuestionFactory()
		 */
		@Override
		public ValidQuestion validQuestionFactory() {
			return new Choco3ValidQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion#getAllFeatures()
		 */
		@Override
		public Collection<? extends GenericFeature> getAllFeatures() {
			return choco3Reasoner.getAllFeatures();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultValidProductQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}
		
	}

}
