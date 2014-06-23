package es.us.isa.aws.scraper.ec2;

import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.parser.FMFParser;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public class EC2ModelValidator extends AmazonEC2Scraper {

	// XXX We take AmazonEC2Scraper superclass as a template, and just override
	// processConfiguration(String s) method
	
//	private FAMAAttributedFeatureModel ec2fm;
//	private QuestionTrader qt;
	private int configCounter;
	private Collection<Configuration> configs;
	FMFParser parser;
	
	public EC2ModelValidator(String currentGenPage, String prevGenPage,
			String dedicatedPage, String propertiesDir) {
		super(currentGenPage, prevGenPage, dedicatedPage, propertiesDir);
		parser = new FMFParser();
	}
	
	public int countEC2Configs(){
		configCounter = 0;
		configs = new LinkedList<Configuration>();
//		qt = new QuestionTrader();
//		ec2fm = (FAMAAttributedFeatureModel) qt.openFile(fmPath);
		parseEC2();
		return configCounter;
	}
	
	@Override
	protected void processConfiguration(String config){
		// here we parse the constraint and check
		
		if (!config.contains("IMPLIES NOT")){
			configCounter++;
//			System.out.println(configCounter+": "+config);
			ExtendedConfiguration c = new ExtendedConfiguration();
			int index = config.indexOf("IMPLIES");
			String auxConfig = config.substring(0, index)+";";
			c.addAttConfig(parser.parseConstraint(auxConfig));
			configs.add(c);
		}
		
	}

	public Collection<Configuration> getConfigurations(){
		return configs;
	}
	
	public static void main(String... args){
		EC2ModelValidator validator = new EC2ModelValidator(
				"./ec2-by-date/2014-6-12/current-pricing.html",
				"./ec2-by-date/2014-6-12/prev-gen-pricing.html",
				"./ec2-by-date/2014-6-12/dedicated-pricing.html",
				"./properties");
		int configs = validator.countEC2Configs();
		System.out.println("Configs: "+configs);
	}
	
	
}
