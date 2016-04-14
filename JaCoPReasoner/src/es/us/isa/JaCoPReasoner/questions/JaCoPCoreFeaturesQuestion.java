/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.JaCoPReasoner.questions;

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCoreFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.JaCoPReasoner.JaCoPQuestion;
import es.us.isa.JaCoPReasoner.JaCoPReasoner;
import es.us.isa.JaCoPReasoner.JaCoPResult;

public class JaCoPCoreFeaturesQuestion extends JaCoPQuestion implements
		CoreFeaturesQuestion {

	JaCoPReasoner reasoner;
	DefaultCoreFeaturesQuestion defq;
	Collection<GenericFeature> coreF;

	public JaCoPCoreFeaturesQuestion() {
		super();
		defq = new DefQuestion();
	}

	
	
	@Override
	public PerformanceResult answer(JaCoPReasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner:Not specified");
		}
		reasoner=r;
		this.coreF=defq.getCoreFeats();
		return this.defq.answer(r);
	}
	
	
	
		@Override
	public Collection<GenericFeature> getCoreFeats() {
		return this.coreF;
	}

	class DefQuestion extends DefaultCoreFeaturesQuestion {

		@Override
		public Collection<? extends GenericFeature> getAllFeatures() {
			return reasoner.getAllFeatures();
		}

		@Override
		public PerformanceResult performanceResultFactory() {
			return new JaCoPResult();
		}

		@Override
		public ProductsQuestion productsQuestionFactory() {
			return new JaCoPProductsQuestion();
		}

		@Override
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

	}

}
