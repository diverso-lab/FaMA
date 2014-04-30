package es.us.isa.FAMA.attributed.tests;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;

public class AttributedReasonerTest {

	private QuestionTrader qt;

	@Before
	public void setUp(){
		qt = new QuestionTrader();
	}
	
	@Test
	public void testValid(){
		System.out.println();
		System.out.println("Valid Question");
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/attributed/James.fm");
		qt.setVariabilityModel(afm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(vq);
		System.out.println(vq.isValid());
	}
	
	@Test
	public void testBasicDetectAndExplainErrors1(){
		System.out.println();
		System.out.println("Detect Errors Question");
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/attributed/dead-feature.fm");
		doDetectAndExplain(afm);
	}
	
	//@Test
	public void testNoAttDetectAndExplainErrors1(){
		System.out.println();
		System.out.println("Detect Errors Question");
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile("src/test/resources/no-att/df-case1.fama");
		doDetectAndExplain(fm);
	}
	
	@Test
	public void testBasicDetectAndExplainErrors2(){
		System.out.println();
		System.out.println("Detect Errors Question");
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/attributed/dead-feature2.fm");
		doDetectAndExplain(afm);
	}
	
	//@Test
	public void testNoAttDetectAndExplainErrors2(){
		System.out.println();
		System.out.println("Detect Errors Question");
		GenericFeatureModel fm = (GenericFeatureModel) qt.openFile("src/test/resources/no-att/df-case7.fama");
		doDetectAndExplain(fm);
	}
	
	private void doDetectAndExplain(GenericFeatureModel fm){
		qt.setVariabilityModel(fm);
		DetectErrorsQuestion vq = (DetectErrorsQuestion ) qt.createQuestion("DetectErrors");
		vq.setObservations(fm.getObservations());
		qt.ask(vq);
		System.out.println("Number of errors:"+vq.getErrors().size());
//		Iterator<es.us.isa.FAMA.errors.Error> it = vq.getErrors().iterator();
//		while (it.hasNext()){
//			es.us.isa.FAMA.errors.Error e = it.next();
//			System.out.println(e);
//		}
		ExplainErrorsQuestion eeq = (ExplainErrorsQuestion) qt.createQuestion("Explanations");
		Collection<es.us.isa.FAMA.errors.Error> errors = vq.getErrors();
		eeq.setErrors(errors);
		qt.ask(eeq);
		Collection<es.us.isa.FAMA.errors.Error> newErrors = eeq.getErrors();
		Iterator<es.us.isa.FAMA.errors.Error> itEx2 = newErrors.iterator();
		while (itEx2.hasNext()){
			es.us.isa.FAMA.errors.Error e = itEx2.next();
			//System.out.println();
			System.out.println("Error "+" "+e+". Explanations: ");
			Iterator<Explanation> itExps = e.getExplanations().iterator();
			while (itExps.hasNext()){
				Explanation exp = itExps.next();
				Iterator<GenericRelation> itRel = exp.getRelations().iterator();
				while (itRel.hasNext()){
					GenericRelation rel = itRel.next();
					System.out.println(rel);
				}
			}
		}
	}
	
	@Test
	public void testComplexDetectAndExplainErrors(){
		System.out.println();
		System.out.println("Detect Errors Question");
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/attributed/James.fm");
		doDetectAndExplain(afm);
	}
	
	//@Test
	public void testValidProduct(){
		System.out.println();
		System.out.println("Valid Product Question");
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/attributed/James.fm");
		qt.setVariabilityModel(afm);
		Product p = new Product();
		GenericAttributedFeature f = afm.searchFeatureByName("James");
		p.addFeature(f);
		ValidProductQuestion vcq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
		vcq.setProduct(p);
		qt.ask(vcq);
		System.out.println(vcq.isValid());
	}
	
	//@Test
	public void testValidConfig(){
		System.out.println();
		System.out.println("Valid Configuration Question");
		GenericAttributedFeatureModel afm = (GenericAttributedFeatureModel) qt.openFile("src/test/resources/attributed/James.fm");
		qt.setVariabilityModel(afm);
		Product p = new Product();
		GenericAttributedFeature f = afm.searchFeatureByName("James");
		p.addFeature(f);
		ValidConfigurationQuestion vcq = (ValidConfigurationQuestion) qt.createQuestion("ValidConfiguration");
		vcq.setProduct(p);
		qt.ask(vcq);
		System.out.println(vcq.isValid());
	}

}
