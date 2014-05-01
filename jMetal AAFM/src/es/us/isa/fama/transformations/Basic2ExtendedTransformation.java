package es.us.isa.fama.transformations;

import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.ITransform;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;

public class Basic2ExtendedTransformation implements IVariabilityModelTransform{

	private VariabilityModel originalModel;
	
	public VariabilityModel doTransform(VariabilityModel efm){
//		originalModel = efm;
//		
//		if (efm instanceof FAMAAttributedFeatureModel){
//			FAMAAttributedFeatureModel model = (FAMAAttributedFeatureModel) efm;
////			Collection<GenericAttribute> atts = this.getAttributes()
//		}
//		else{
//			throw new IllegalArgumentException("An attributed FM is required");
//		}
//		
//		return efm;
		return null;
	}
	
//	private Feature transformAttributedFeature(GenericAttributedFeature f){
//		
//	}

	@Override
	public VariabilityModel undoTransform() {
		return originalModel;
	}

	
}
