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
import es.us.isa.FAMA.Reasoner.questions.HomogeneityQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.UniqueFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultHomogeneityQuestion;

/**
 * Implementation to solve the homogeneity question using the Choco 3 reasoner.
 * This operation calculates the degree of homogeneity for the products that can be 
 * derived from the feature model with the specified constraints.
 * Homogeneity is related with the number of unique features among products. The more
 * unique features, the less homogeneous the feature model is.
 * 
 * Homogeneity = 1 - ( number of unique features / number of products)
 * 
 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
 * @see es.us.isa.ChocoReasoner.questions.ChocoHomogeneityQuestion Choco 2 implementation for the homogeneity question.
 * @version 1.0, June 2014
 */
public class Choco3HomogeneityQuestion extends Choco3Question implements
		HomogeneityQuestion {

	/**
	 * Default implementation for the homogeneity question provided by the FAMA SDK.
	 */
	private DefaultHomogeneityQuestionImpl homogeneityQuestion;
	/**
	 * An instance to the Choco 3 reasoner used.
	 */
	private Choco3Reasoner choco3Reasoner;
	
	/**
	 * Returns the degree of homogeneity found.
	 * 
	 * @return A number representing the degree of homogeneity 
	 */
	public double getHomogeneity() {
		return this.homogeneityQuestion.getHomogeneity();
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#preAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void preAnswer(Reasoner reasoner) {
		this.choco3Reasoner = (Choco3Reasoner) reasoner;
		this.homogeneityQuestion = new DefaultHomogeneityQuestionImpl();
//		this.homogeneityQuestion.preAnswer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#answer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public PerformanceResult answer(Reasoner reasoner) {
		return this.homogeneityQuestion.answer(reasoner);
	}

	/* (non-Javadoc)
	 * @see co.icesi.i2t.Choco3Reasoner.simple.Choco3Question#postAnswer(es.us.isa.FAMA.Reasoner.Reasoner)
	 */
	@Override
	public void postAnswer(Reasoner reasoner) {
//		this.homogeneityQuestion.postAnswer(reasoner);
	}
	
	/**
	 * Default implementation for the homogeneity question provided by the FAMA SDK.
	 * Since the default implementation is not complete, some methods have to be
	 * written.
	 *  
	 * @author Andrés Paz, I2T Research Group, Icesi University, Cali - Colombia
	 * @version 0.1, June 2014
	 */
	class DefaultHomogeneityQuestionImpl extends DefaultHomogeneityQuestion {

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultHomogeneityQuestion#setNumberOfFeatures()
		 */
		@Override
		public long getNumberOfFeatures() {
			return choco3Reasoner.getAllFeatures().size();
		}
		
		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.Question#getReasonerClass()
		 */
		public Class<? extends Reasoner> getReasonerClass() {
			return choco3Reasoner.getClass();
		}
		
		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultHomogeneityQuestion#numberOfProductsQuestionFactory()
		 */
		public NumberOfProductsQuestion numberOfProductsQuestionFactory() {
			return new Choco3NumberOfProductsQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultHomogeneityQuestion#uniqueFeaturesQuestionFactory()
		 */
		@Override
		public UniqueFeaturesQuestion uniqueFeaturesQuestionFactory() {
			return new Choco3UniqueFeaturesQuestion();
		}

		/* (non-Javadoc)
		 * @see es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultHomogeneityQuestion#performanceResultFactory()
		 */
		@Override
		public PerformanceResult performanceResultFactory() {
			return new Choco3PerformanceResult();
		}

	}

}
