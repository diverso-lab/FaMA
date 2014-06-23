package es.us.isa.closer.optimising;

import java.util.Map;

import org.junit.Assert;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public abstract class AbstractModelsTest {

	protected GenericAttributedFeatureModel vm;
	protected QuestionTrader qt;
	protected OptimisingConfigurationQuestion question;
	
	protected void setUp(String path){
		qt = new QuestionTrader("single-file");
		vm = (GenericAttributedFeatureModel) qt.openFile(path);
		qt.setVariabilityModel(vm);
		question = (OptimisingConfigurationQuestion) qt.createQuestion("Optimising");
	}
	
	protected void analyse(ExtendedConfiguration config){
//		ExtendedConfiguration config = new ExtendedConfiguration();
//		config.addElement(vm.searchFeatureByName("FirstGen256MB"), 1);
		question.setConfiguration(config);
		
//		question.minimise("Computing.costMonth");
		question.minimise("Computing.totalCost");
		qt.ask(question);
	}
	
	protected void checkCost(double val){
		ExtendedConfiguration result = question.getOptimalConfiguration();
		Map<GenericAttribute, Double> values = result.getAttValues();
		double v = values.get(vm.searchAttributeByName("Instance.costHour"));
		System.out.println("Instance.costHour = "+v);
		System.out.println("Computing.costMonth = "+values.get(vm.searchAttributeByName("Computing.costMonth")));
		System.out.println(result);
		
		Assert.assertTrue(v == val);
	}

	
}
