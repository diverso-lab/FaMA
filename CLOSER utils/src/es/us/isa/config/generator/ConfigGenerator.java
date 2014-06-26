package es.us.isa.config.generator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.parser.FMFParser;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public class ConfigGenerator {

	public Collection<ExtendedConfiguration> generateConfigurations(
			FAMAAttributedFeatureModel fm, int n) {
		Collection<ExtendedConfiguration> result = new LinkedList<ExtendedConfiguration>();

		ConfigSelectionMethod area1 = createArea1(fm);
		ConfigSelectionMethod area2 = createArea2(fm);
		ConfigSelectionMethod area3 = createArea3(fm);
		
		for (int i = 0; i < n; i++) {
			result.add(area1.generateRandomConfig());
			result.add(area2.generateRandomConfig());
			result.add(area3.generateRandomConfig());
		}
		
		return result;
	}

	private ConfigSelectionMethod createArea1(FAMAAttributedFeatureModel ec2fm) {
		Map<VariabilityElement, EnumeratedVariableLevel> featureGroups = this
				.createFeatureGroups(ec2fm);
		Map<GenericAttribute, DomainVariableLevel> attGroups = this
				.createCommonAttGroups(ec2fm);
		Map<GenericAttribute, DomainVariableLevel> auxGroups = this.createVariableAttGroups(ec2fm, 1, 10, 1, 20, 1, 1000);
		attGroups.putAll(auxGroups);
		ConfigSelectionMethod result = new ConfigSelectionMethod(ec2fm,
				featureGroups, attGroups);
		return result;
	}
	
	private ConfigSelectionMethod createArea2(FAMAAttributedFeatureModel ec2fm) {
		Map<VariabilityElement, EnumeratedVariableLevel> featureGroups = this
				.createFeatureGroups(ec2fm);
		Map<GenericAttribute, DomainVariableLevel> attGroups = this
				.createCommonAttGroups(ec2fm);
		Map<GenericAttribute, DomainVariableLevel> auxGroups = this.createVariableAttGroups(ec2fm,
				10, 40, 6, 60, 1, 1000);
		attGroups.putAll(auxGroups);
		ConfigSelectionMethod result = new ConfigSelectionMethod(ec2fm,
				featureGroups, attGroups);
		return result;
	}
	
	private ConfigSelectionMethod createArea3(FAMAAttributedFeatureModel ec2fm) {
		Map<VariabilityElement, EnumeratedVariableLevel> featureGroups = this
				.createFeatureGroups(ec2fm);
		Map<GenericAttribute, DomainVariableLevel> attGroups = this
				.createCommonAttGroups(ec2fm);
		Map<GenericAttribute, DomainVariableLevel> auxGroups = this.createVariableAttGroups(ec2fm,
				40, 244, 20, 108, 1000, 10000);
		attGroups.putAll(auxGroups);
		ConfigSelectionMethod result = new ConfigSelectionMethod(ec2fm,
				featureGroups, attGroups);
		return result;
	}

	private Map<VariabilityElement, EnumeratedVariableLevel> createFeatureGroups(
			FAMAAttributedFeatureModel ec2fm) {
		Map<VariabilityElement, EnumeratedVariableLevel> featureGroups = new HashMap<VariabilityElement, EnumeratedVariableLevel>();

		// OS
		VariabilityElement os = ec2fm.searchFeatureByName("OS");
		EnumeratedVariableLevel osLevel = new EnumeratedVariableLevel();
		List<String> osValues = new ArrayList<String>();
		osValues.add("LinuxBased");
		osValues.add("WindowsBased");
		osValues.add("Linux");
		osValues.add("Suse");
		osValues.add("RedHat");
		osValues.add("Windows");
		osValues.add("WindowsSQLServer");
		osValues.add("WindowsSQLWeb");
		osValues.add("WindowsSQLStd");
		osLevel.setValues(osValues);
		featureGroups.put(os, osLevel);

		// Dedication
		VariabilityElement ded = ec2fm.searchFeatureByName("Dedication");
		EnumeratedVariableLevel dedLevel = new EnumeratedVariableLevel();
		List<String> dedValues = new ArrayList<String>();
		dedValues.add("Public");
		dedValues.add("Dedicated");
		dedLevel.setValues(dedValues);
		featureGroups.put(ded, dedLevel);

		// Location
		VariabilityElement location = ec2fm.searchFeatureByName("Location");
		EnumeratedVariableLevel locLevel = new EnumeratedVariableLevel();
		List<String> locValues = new ArrayList<String>();
		locValues.add("NorthAmerica");
		locValues.add("Europe");
		locValues.add("AsiaOceania");
		locValues.add("SouthAmerica");
		locValues.add("VA");
		locValues.add("CA");
		locValues.add("ORE");
		locValues.add("SIN");
		locValues.add("JP");
		locValues.add("AUS");
		locLevel.setValues(locValues);
		featureGroups.put(location, locLevel);

		return featureGroups;
	}

	private Map<GenericAttribute, DomainVariableLevel> createCommonAttGroups(
			FAMAAttributedFeatureModel ec2fm) {
		Map<GenericAttribute, DomainVariableLevel> attGroups = new HashMap<GenericAttribute, DomainVariableLevel>();

		// Purchasing options
		// usage
		GenericAttribute usage = ec2fm.searchAttributeByName("Use.usage");
		DomainVariableLevel usageLevel = new DomainVariableLevel(0, 730);
		attGroups.put(usage, usageLevel);

		// period
		GenericAttribute period = ec2fm.searchAttributeByName("Use.period");
		DomainVariableLevel periodLevel = new DomainVariableLevel(1, 48);
		attGroups.put(period, periodLevel);

		// ssd backed
		GenericAttribute ssdBacked = ec2fm
				.searchAttributeByName("Instance.ssdBacked");
		DomainVariableLevel ssdLevel = new DomainVariableLevel(0, 1);
		attGroups.put(ssdBacked, ssdLevel);

		return attGroups;
	}

	private Map<GenericAttribute, DomainVariableLevel> createVariableAttGroups(
			FAMAAttributedFeatureModel ec2fm, int ramMin, int ramMax,
			int ecuMin, int ecuMax, int stMin, int stMax) {
		Map<GenericAttribute, DomainVariableLevel> attGroups = new HashMap<GenericAttribute, DomainVariableLevel>();
		
		// ram XXX repeat it for the other two areas
		GenericAttribute ram = ec2fm.searchAttributeByName("Instance.ram");
		DomainVariableLevel ramLevel = new DomainVariableLevel(ramMin, ramMax);
		attGroups.put(ram, ramLevel);

		// ECU XXX repeat it for the other two areas
		GenericAttribute ecu = ec2fm.searchAttributeByName("Instance.ecu");
		DomainVariableLevel ecuLevel = new DomainVariableLevel(ecuMin, ecuMax);
		attGroups.put(ecu, ecuLevel);

		// Storage XXX repeat it for the other two areas
		GenericAttribute storage = ec2fm
				.searchAttributeByName("EC2.storageSize");
		DomainVariableLevel stLevel = new DomainVariableLevel(stMin, stMax);
		attGroups.put(storage, stLevel);
		
		return attGroups;
	}
	
	public static void main(String... args){
		ConfigGenerator gen = new ConfigGenerator();
		FMFParser parser = new FMFParser();
		FAMAAttributedFeatureModel fm = parser.parseModel("ec2-by-date/2014-6-18/AmazonEC2Atts.afm");
		Collection<ExtendedConfiguration> configs = gen.generateConfigurations(fm, 200);
		int i = 0;
		for (ExtendedConfiguration c:configs){
			i++;
			System.out.println("\nConfig "+i);
			try {
				FileOutputStream out = new FileOutputStream("./exp configs/Config "+i+".fmc");
				byte[] bytes = c.toString().getBytes();
				out.write("%Configuration\n".getBytes());
				out.write(bytes);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.out.println(c);
		}
	}

}
