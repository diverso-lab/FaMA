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

package es.us.isa.ChocoReasoner.questions;

import java.util.Collection;

import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariantFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

public class ChocoVariantFeaturesQuestion extends ChocoQuestion implements
		VariantFeaturesQuestion {

	private DefVarFeatures defq;
	private Collection<GenericFeature> feats;
	private ChocoReasoner choco;

	public ChocoVariantFeaturesQuestion() {
		super();
		defq = new DefVarFeatures();
	}



	public PerformanceResult answer(Reasoner r) throws FAMAException {
		if (r == null) {
			throw new FAMAException("Reasoner: Reasoner not specified");
		}
		choco = (ChocoReasoner) r;
		feats = this.defq.getVariantFeats();
		return this.defq.answer(r);
	}

	class DefVarFeatures extends DefaultVariantFeaturesQuestion {

		@Override
		public Collection<? extends GenericFeature> getAllFeatures() {
			return choco.getAllFeatures();
		}

		@Override
		public PerformanceResult performanceResultFactory() {
			return new ChocoResult();
		}

		@Override
		public ProductsQuestion productsQuestionFactory() {
			return new ChocoProductsQuestion();
		}

		@Override
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

	}

	@Override
	public Collection<GenericFeature> getVariantFeats() {
		return this.feats;
	}

}
