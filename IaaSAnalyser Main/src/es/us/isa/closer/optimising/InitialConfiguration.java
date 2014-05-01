package es.us.isa.closer.optimising;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.JUnit4;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Tree;

public class InitialConfiguration{
	
	//XXX considering AWS as the IaaS model
	
	protected GenericAttributedFeatureModel vm;
	protected QuestionTrader qt;
	protected OptimisingConfigurationQuestion question;
	
	@Before
	public void setUp(){
		qt = new QuestionTrader();
		vm = (GenericAttributedFeatureModel) qt.openFile("./iaas models/integer models/AWS/AmazonEC2.afm");
		qt.setVariabilityModel(vm);
		question = (OptimisingConfigurationQuestion) qt.createQuestion("Optimising");
	}
	
	@Test
	public void testEmptyConfiguration(){
		ExtendedConfiguration config = new ExtendedConfiguration();
		question.setConfiguration(config);
		question.minimise("Instance.costHour");
		qt.ask(question);
		
		ExtendedConfiguration result = question.getOptimalConfiguration();
		Map<VariabilityElement,Integer> values = result.getElements();
		Assert.assertTrue(values.get(vm.searchAttributeByName("Instance.costHour")) == 60);
		
	}
	
	@Test
	public void testCompleteConfiguration(){
		ExtendedConfiguration config = new ExtendedConfiguration();
		config.addElement(vm.searchFeatureByName("M2_xlarge"), 1);
		config.addElement(vm.searchFeatureByName("VA"), 1);
		config.addElement(vm.searchFeatureByName("Windows"), 1);
		question.setConfiguration(config);
		question.minimise("Instance.costHour");
		qt.ask(question);
		ExtendedConfiguration result = question.getOptimalConfiguration();
		Map<VariabilityElement,Integer> values = result.getElements();
		Assert.assertTrue(values.get(vm.searchAttributeByName("Instance.costHour")) == 510);
	}
	
	@Test
	public void testPartialConfiguration(){
		ExtendedConfiguration config = new ExtendedConfiguration();
		Tree<String> c1 = qt.parseConstraint("Instance.cores >= 4;");
		Tree<String> c2 = qt.parseConstraint("Instance.ram >= 6000;");
		Collection<Tree<String>> constraints = new LinkedList<>();
		constraints.add(c1);
		constraints.add(c2);
		config.setAttConfigs(constraints);
		config.addElement(this.vm.searchFeatureByName("Europe"), 1);
		
		question.setConfiguration(config);
		question.minimise("Instance.costHour");
		qt.ask(question);
		ExtendedConfiguration result = question.getOptimalConfiguration();
		Map<VariabilityElement,Integer> values = result.getElements();
		Assert.assertTrue(values.get(vm.searchAttributeByName("Instance.costHour")) == 342);
	}

}
