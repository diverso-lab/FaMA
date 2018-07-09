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
package es.us.isa.Sat4jReasoner.questions;

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCoreFeaturesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jCoreFeaturesQuestion extends Sat4jQuestion implements
		CoreFeaturesQuestion {

	private DefCoreQuestion vq;
	
	public Sat4jCoreFeaturesQuestion(){
		vq = new DefCoreQuestion();
	}
	
	

	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		Sat4jReasoner c = (Sat4jReasoner) r;
		vq.setAllFeatures(c.getAllFeatures());
		return vq.answer(r);
	}
	
	class DefCoreQuestion extends DefaultCoreFeaturesQuestion{
		Collection<? extends GenericFeature> features;
			
		@Override
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

		@Override
		public ProductsQuestion productsQuestionFactory() {
			return new Sat4jProductsQuestion();

		}

		public void setAllFeatures(Collection<? extends GenericFeature> feats) {
			this.features=feats;
		}

		
		@Override
		public Collection<? extends GenericFeature> getAllFeatures() {
			return features;
		}

		@Override
		public PerformanceResult performanceResultFactory() {
			return new Sat4jResult();
		}
		
	}

	@Override
	public Collection<GenericFeature> getCoreFeats() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
