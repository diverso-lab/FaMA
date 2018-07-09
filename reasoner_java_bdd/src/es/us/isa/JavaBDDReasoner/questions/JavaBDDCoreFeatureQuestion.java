package es.us.isa.JavaBDDReasoner.questions;

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultCoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.defaultImpl.DefaultVariabilityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;
import es.us.isa.JavaBDDReasoner.JavaBDDReasoner;
import es.us.isa.JavaBDDReasoner.JavaBDDResult;

public class JavaBDDCoreFeatureQuestion extends JavaBDDQuestion implements CoreFeaturesQuestion {

	private DefCoreQuestion defQuestion;
	
	public JavaBDDCoreFeatureQuestion() {
		defQuestion= new DefCoreQuestion();
	}
	@Override
	public Class<? extends Reasoner> getReasonerClass() {
		return null;
	}

	@Override
	public Collection<GenericFeature> getCoreFeats() {
		return null;
	}
	
	public PerformanceResult answer(Reasoner r) {
		if(r==null){
			throw new FAMAParameterException("Reasoner :Not specified");
		}
		JavaBDDReasoner c = (JavaBDDReasoner) r;
		defQuestion.setAllFeatures(c.getAllFeatures());
		return defQuestion.answer(r);
	}
	
	class DefCoreQuestion extends DefaultCoreFeaturesQuestion{
		Collection<? extends GenericFeature> features;
			
		@Override
		public Class<? extends Reasoner> getReasonerClass() {
			return null;
		}

		@Override
		public ProductsQuestion productsQuestionFactory() {
			return new JavaBDDProductsQuestion();

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
			return new JavaBDDResult();
		}
		
	}

}
