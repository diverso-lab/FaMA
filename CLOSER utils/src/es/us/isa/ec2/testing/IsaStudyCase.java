package es.us.isa.ec2.testing;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Tree;


/**
 * What if we mark attributes as decision or non decision variables depending if we have configured them????
 * then, we won't obtain values of variables marked as non-decision
 * @author jesus
 *
 */
public class IsaStudyCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuestionTrader qt;
		GenericAttributedFeatureModel afm;
		
		qt = new QuestionTrader();
		afm = (GenericAttributedFeatureModel) qt.openFile("AWS FMs/ec2/AmazonEC2AttsConstraints.afm");
		qt.setVariabilityModel(afm);
		//You still need a great knowledge about your system
		
		//We need more attributes and elements to model fine the problem
		//RAM, CPU, reserved instances, and data in/out
		//and also the amazon free layer
		ExtendedConfiguration labsConf = new ExtendedConfiguration();
		labsConf.addElement(afm.searchFeatureByName("REDHAT"), 1);
		labsConf.addElement(afm.searchFeatureByName("DEDICATED"), 0);
		//XXX problem with cloud watch detected
//		labsConf.addElement(afm.searchFeatureByName("CLOUD_WATCH"), 0);
		
		Tree<String> attConstraint = qt.parseConstraint("EC2.usage == 400;");
		labsConf.getAttConfigs().add(attConstraint);
		attConstraint = qt.parseConstraint("EC2.cu >= 70;");
		labsConf.getAttConfigs().add(attConstraint);
		attConstraint = qt.parseConstraint("EC2.ram >= 40;");
		labsConf.getAttConfigs().add(attConstraint);
//		attConstraint = qt.parseConstraint("AMAZON_SERVICES.useLenght == 1;");
//		labsConf.getAttConfigs().add(attConstraint);
		System.out.println("Labs conf: "+labsConf.toString());
		
//		ExtendedConfiguration clinkerConf = new ExtendedConfiguration();
//		clinkerConf.addElement(afm.searchFeatureByName("REDHAT"), 1);
//		attConstraint = qt.parseConstraint("EC2.usage == 30;");
//		clinkerConf.getAttConfigs().add(attConstraint);
//		attConstraint = qt.parseConstraint("EC2.cu >= 3;");
//		clinkerConf.getAttConfigs().add(attConstraint);
//		attConstraint = qt.parseConstraint("EC2.ram >= 2;");
//		clinkerConf.getAttConfigs().add(attConstraint);
//		
//		ExtendedConfiguration clusterConf = new ExtendedConfiguration();
//		clusterConf.addElement(afm.searchFeatureByName("LINUX"), 1);
//		clusterConf.addElement(afm.searchFeatureByName("CLUSTER"), 1);
//		clusterConf.addElement(afm.searchFeatureByName("SO_64BIT"), 1);
//		attConstraint = qt.parseConstraint("EC2.usage == 10;");
//		clusterConf.getAttConfigs().add(attConstraint);
//		attConstraint = qt.parseConstraint("EC2.cu >= 16;");
//		clusterConf.getAttConfigs().add(attConstraint);
//		attConstraint = qt.parseConstraint("EC2.ram >= 10;");
//		clusterConf.getAttConfigs().add(attConstraint);
		
		OptimisingConfigurationQuestion oc = (OptimisingConfigurationQuestion) qt.createQuestion("OptimalConfig");
//		oc.minimise("AMAZON_SERVICES.cost");
		oc.minimise("EC2.instanceCost");
		
		oc.setTimeLimit(600000); //10 minutos
		oc.setConfiguration(labsConf);
		qt.ask(oc);
		ExtendedConfiguration res = oc.getOptimalConfiguration();
		
		System.out.println(res);
	}

}
