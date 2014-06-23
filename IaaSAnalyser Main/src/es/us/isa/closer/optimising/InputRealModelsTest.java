package es.us.isa.closer.optimising;

import static org.junit.Assert.*;

import org.junit.Test;

import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public class InputRealModelsTest extends AbstractModelsTest {

	@Test
	public void testAWSInput() {
		setUp("./iaas models/real models/AWS/AmazonEC2Atts.afm");
		
		ExtendedConfiguration config = new ExtendedConfiguration();
//		config.addElement(vm.searchFeatureByName("VA"), 1);
//		config.addElement(vm.searchFeatureByName("PayInAdvance"), 0);
//		config.addElement(vm.searchFeatureByName("Linux"), 1);
//		config.addAttValue(vm.searchAttributeByName("Storage.size"),3000);
		config.addAttValue(vm.searchAttributeByName("Use.usage"),700);
		config.addAttValue(vm.searchAttributeByName("Use.period"),12);
		
		config.addAttConfig(qt.parseConstraint("EC2.storageSize >= 1000;"));
		config.addAttConfig(qt.parseConstraint("Instance.ecu >= 8;"));
		config.addAttConfig(qt.parseConstraint("Instance.ram >= 20;"));
		
		config.addElement(this.vm.searchFeatureByName("VA"), 1);
		config.addElement(this.vm.searchFeatureByName("Windows"), 1);
		config.addElement(this.vm.searchFeatureByName("OnDemand"), 1);
		config.addElement(this.vm.searchFeatureByName("Public"), 1);
		
//		analyse(config);
		
		question.setConfiguration(config);
		
//		question.minimise("Computing.costMonth");
		question.minimise("EC2.totalCost");
		qt.ask(question);
		System.out.println(question.getOptimalConfiguration());
//		checkCost(0.069);
	}
	
	@Test
	public void testRackspaceInput() {
		setUp("./iaas models/real models/Rackspace/RackspaceComputing.afm");
		
		ExtendedConfiguration config = new ExtendedConfiguration();
		config.addElement(vm.searchFeatureByName("PayInAdvance"), 0);
		config.addAttValue(vm.searchAttributeByName("Use.usage"),700);
		config.addAttValue(vm.searchAttributeByName("Use.period"),12);
//		config.addElement(vm.searchFeatureByName("FirstGen"),1);
		
		config.addAttConfig(qt.parseConstraint("Storage.size >= 1000;"));
		config.addAttConfig(qt.parseConstraint("Instance.cores >= 4;"));
		config.addAttConfig(qt.parseConstraint("Instance.ram >= 20;"));
		
		analyse(config);
		
		checkCost(1.2);
	}

	@Test
	public void testAzureInput() {
		setUp("./iaas models/real models/Azure/AzureComputing.afm");
//		setUp("./models/AzureComputing.afm");
		ExtendedConfiguration config = new ExtendedConfiguration();
		config.addElement(vm.searchFeatureByName("PayInAdvance"), 0);
//		config.addElement(vm.searchFeatureByName("Commitment"), 1);
//		config.addAttValue(vm.searchAttributeByName("Storage.size"),3000);
		config.addAttValue(vm.searchAttributeByName("Use.usage"),700);
		config.addAttValue(vm.searchAttributeByName("Use.period"),12);
		
		config.addAttConfig(qt.parseConstraint("Storage.size >= 1000;"));
		config.addAttConfig(qt.parseConstraint("Instance.cores >= 4;"));
		config.addAttConfig(qt.parseConstraint("Instance.ram >= 20;"));
		
		analyse(config);
		checkCost(0.64);
	}


}
