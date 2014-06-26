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
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Node;
import es.us.isa.util.Tree;

public class FaMaEC2AnalysisCases {

	protected FAMAAttributedFeatureModel vm;
	protected QuestionTrader qt;
	private OptimisingConfigurationQuestion question;
	protected List<GenericAttribute> attList;
	protected AttributedFeature[] configPoints;
	

	public void executeTest(String ec2fmPath, String inputDir, String outputCSV)
			throws IOException {
		setUp(ec2fmPath);
		PrintWriter writer = null;

		writer = new PrintWriter(outputCSV);
		writer.write("sep=;\n");
		writer.write("Input;Time (ms);Desired Location;Desired OS;"
				+ "Desired dedication;Desired ECU;Desired period;Desired RAM;Desired storage;"
				+ "Desired usage;Desired ssd;"
				+ "Result Location;Result OS;Result Dedication;Result Purchase;"
				+ "Result Instance;Result EBS;"
				+ "Total Cost;Cost Month;Cost Hour;ECU;Cores;RAM;SSD;"
				+ "Storage;EBS Storage;Signed Usage;Period;\n");

		File f = new File(inputDir);
		if (f.isDirectory()) {
			File[] children = f.listFiles();
//			for (int i = 0; i < children.length; i++) {
			for (int i = 0; i < 10; i++) {
				if (children[i].getName().endsWith(".fmc")) {
					analyse(children[i], writer);
				}
			}
		}
		writer.flush();
		writer.close();
	}

	protected void analyse(File path, Writer w) throws IOException {
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
			
			line += pf.getTime() + ";";
			String auxLine = extractFromConfig(config);
			line += auxLine;
			String[] leaves = getConfigurationResult(result);
			for (String s : leaves) {
				line += s + ";";
			}
//			line += ";";
			
			line += getAttValsAsString(result);
		}
		else{
			line += "Invalid;";
		}
		
		
		w.write(line + "\n");
	}

	protected String extractFromConfig(ExtendedConfiguration config){
		String result = "";
		Collection<Tree<String>> constraints = config.getAttConfigs();
		for (Tree<String> t:constraints){
			Node<String> node = t.getRootElement();
			if (node.getData().equals(">=")){
				result += node.getChildren().get(1).getData()+";";
			}
			else{
				result += node.getData()+";";
			}
		}
		return result;
	}
	
	private void setUp(String ec2fmPath) {
		qt = new QuestionTrader("single-file");
		vm = (FAMAAttributedFeatureModel) qt.openFile(ec2fmPath);
		qt.setVariabilityModel(vm);
		analysisSetup(ec2fmPath);
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
		attList.add(this.vm.searchAttributeByName("Use.signedUsage"));
		attList.add(this.vm.searchAttributeByName("Use.period"));
		configPoints = new AttributedFeature[6];
		configPoints[0] = vm.searchFeatureByName("Location");
		configPoints[1] = vm.searchFeatureByName("OS");
		configPoints[2] = vm.searchFeatureByName("Dedication");
		configPoints[3] = vm.searchFeatureByName("Use");
		configPoints[4] = vm.searchFeatureByName("Instance");
		configPoints[5] = vm.searchFeatureByName("EBS");
	}

	protected void analysisSetup(String ec2fmPath) {
		question = (OptimisingConfigurationQuestion) qt
				.createQuestion("Optimising");
	}

	protected String[] getConfigurationResult(ExtendedConfiguration conf) {
		String[] res = new String[6];
		
//		Collection<String> result = new LinkedList<String>();
		Map<VariabilityElement, Integer> features = conf.getElements();
		for (Entry<VariabilityElement, Integer> e : features.entrySet()) {
			if (e.getValue() == 1) {
				// if selected
				AttributedFeature f = (AttributedFeature) e.getKey();
				if (f.isLeaf()) {
					int aux = 0;
					boolean found = false;
					while (aux < 6 && !found){
						if (f.isDescendantOf(configPoints[aux])){
							res[aux] = f.getName();
							found = true;
						}
						aux++;
					}
//					result.add(f.getName());
				}
			}
		}
		return res;
//		return result;
	}

	protected String getAttValsAsString(ExtendedConfiguration conf) {
		String result = "";
		Map<GenericAttribute, Double> atts = conf.getAttValues();

		for (GenericAttribute att : attList) {
			Double value = atts.get(att);
			result += value + ";";
		}

		return result;
	}

	public static void main(String... args){
		FaMaEC2AnalysisCases eval = new FaMaEC2AnalysisCases();
		try {
			eval.executeTest("./ec2-by-date/2014-6-18/AmazonEC2Atts.afm", "exp configs", "results.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
