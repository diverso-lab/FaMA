package es.us.isa.aws.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public class EC2InputsTest {

	protected GenericAttributedFeatureModel vm;
	protected QuestionTrader qt;
	protected OptimisingConfigurationQuestion question;
	protected List<GenericAttribute> attList;

	public void executeTest(String ec2fmPath, String inputDir, String outputCSV)
			throws IOException {
		setUp(ec2fmPath);
		PrintWriter writer = null;

		writer = new PrintWriter(outputCSV);
		writer.write("Input;Result;Time (ms);Total Cost;Cost Month;Cost Hour;ECU;Cores;RAM;SSD;"
				+ "Storage;EBS Storage;Usage;Period;\n");

		File f = new File(inputDir);
		if (f.isDirectory()) {
			File[] children = f.listFiles();
			for (int i = 0; i < 5; i++) {
//			for (int i = 0; i < children.length; i++) {
				if (children[i].getName().endsWith(".fmc")) {
					analyse(children[i], writer);
				}
			}
		}
		writer.flush();
		writer.close();
	}

	private void analyse(File path, Writer w) throws IOException {
		ExtendedConfiguration config = (ExtendedConfiguration) qt
				.loadConfigurationFile(vm, path.getAbsolutePath());
		ValidConfigurationQuestion vcq = (ValidConfigurationQuestion) 
				qt.createQuestion("ValidConfiguration");
		vcq.setConfiguration(config);
		qt.ask(vcq);
		String line = path.getName() + "; ";
		if (vcq.isValid()){
			question.setConfiguration(config);
			question.minimise("EC2.totalCost");
			PerformanceResult pf = qt.ask(question);
			ExtendedConfiguration result = question.getOptimalConfiguration();
			
//			line = path.getName() + "; ";
			Collection<String> leaves = getConfigurationResult(result);
			for (String s : leaves) {
				line += s + " ";
			}
			line += ";";
			line += pf.getTime() + ";";
			line += getAttValsAsString(result);
		}
		else{
			line += "Invalid;";
		}
		
		
		w.write(line + "\n");
	}

	private void setUp(String ec2fmPath) {
		qt = new QuestionTrader("single-file");
		vm = (GenericAttributedFeatureModel) qt.openFile(ec2fmPath);
		qt.setVariabilityModel(vm);
		question = (OptimisingConfigurationQuestion) qt
				.createQuestion("Optimising");
		attList = new ArrayList<GenericAttribute>();
		attList.add(this.vm.searchAttributeByName("EC2.totalCost"));
		attList.add(this.vm.searchAttributeByName("EC2.costMonth"));
		attList.add(this.vm.searchAttributeByName("Instance.costHour"));
		attList.add(this.vm.searchAttributeByName("Instance.ecu"));
		attList.add(this.vm.searchAttributeByName("Instance.cores"));
		attList.add(this.vm.searchAttributeByName("Instance.ram"));
		attList.add(this.vm.searchAttributeByName("Instance.ssdBacked"));
		attList.add(this.vm.searchAttributeByName("EC2.storageSize"));
		attList.add(this.vm.searchAttributeByName("EBS.extraSpace"));
		attList.add(this.vm.searchAttributeByName("Use.usage"));
		attList.add(this.vm.searchAttributeByName("Use.period"));
	}

	private Collection<String> getConfigurationResult(ExtendedConfiguration conf) {
		Collection<String> result = new LinkedList<String>();
		Map<VariabilityElement, Integer> features = conf.getElements();
		for (Entry<VariabilityElement, Integer> e : features.entrySet()) {
			if (e.getValue() == 1) {
				// if selected
				AttributedFeature f = (AttributedFeature) e.getKey();
				if (f.isLeaf()) {
					result.add(f.getName());
				}
			}
		}
		return result;
	}

	private String getAttValsAsString(ExtendedConfiguration conf) {
		String result = "";
		Map<GenericAttribute, Double> atts = conf.getAttValues();

		for (GenericAttribute att : attList) {
			Double value = atts.get(att);
			result += value + ";";
		}

		return result;
	}

	public static void main(String... args){
		EC2InputsTest eval = new EC2InputsTest();
		try {
			eval.executeTest("./ec2-by-date/2014-6-18/AmazonEC2Atts.afm", "exp configs", "results.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
