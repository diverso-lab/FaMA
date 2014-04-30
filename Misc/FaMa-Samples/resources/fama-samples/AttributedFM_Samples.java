package main;

import java.util.Iterator;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class AttributedFM_Samples {

	private QuestionTrader qt;
	
	private GenericAttributedFeatureModel afm;
	
	public AttributedFM_Samples(){
		qt = new QuestionTrader();
		afm = (GenericAttributedFeatureModel) qt.openFile("fm-samples/atts/James/James.afm");
		qt.setVariabilityModel(afm);
	}
	
	public void testValid(){
		System.out.println("Valid Question");
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(vq);
		System.out.println(vq.isValid());
	}
	
	public void testDetectAndExplainErrors(){
		System.out.println("Detect Errors Question");
		DetectErrorsQuestion vq = (DetectErrorsQuestion ) qt.createQuestion("DetectErrors");
		vq.setObservations(afm.getObservations());
		qt.ask(vq);
		System.out.println(vq.getErrors().size());
		Iterator<es.us.isa.FAMA.errors.Error> it = vq.getErrors().iterator();
		while (it.hasNext()){
			es.us.isa.FAMA.errors.Error e = it.next();
			System.out.println(e);
		}
	}
	
	public void testValidProduct(){
		System.out.println("Valid Product Question");
		Product p = new Product();
		GenericAttributedFeature f = afm.searchFeatureByName("James");
		p.addFeature(f);
		ValidProductQuestion vcq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
		vcq.setProduct(p);
		qt.ask(vcq);
		System.out.println(vcq.isValid());
	}
	
	public void testValidConfig(){
		System.out.println("Valid Configuration Question");
		Configuration p = new Configuration();
		GenericAttributedFeature f = afm.searchFeatureByName("James");
		p.addElement(f,1);
		ValidConfigurationQuestion vcq = (ValidConfigurationQuestion) qt.createQuestion("ValidConfiguration");
		vcq.setConfiguration(p);
		qt.ask(vcq);
		System.out.println(vcq.isValid());
	}
	public static void main(String[] args){
		AttributedFM_Samples t = new AttributedFM_Samples();
		t.testValid();
		t.testDetectAndExplainErrors();
		t.testValidConfig();
		t.testValidProduct();
	}

}
