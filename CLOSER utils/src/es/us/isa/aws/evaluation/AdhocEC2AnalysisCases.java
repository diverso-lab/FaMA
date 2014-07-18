package es.us.isa.aws.evaluation;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.adhoc.ec2.cpopt.CPOptEC2Reasoner;
import es.us.isa.aws.scraper.ec2.AmazonEC2Scraper;

public class AdhocEC2AnalysisCases extends FaMaEC2AnalysisCases{

	private CPOptEC2Reasoner r;
	
	@Override
	protected void analyse(File path, Writer w) throws IOException {
		ExtendedConfiguration config = (ExtendedConfiguration) qt
				.loadConfigurationFile(vm, path.getAbsolutePath());
		ExtendedConfiguration result = r.optimise(config, "EC2.totalCost");
//		ExtendedConfiguration result = r.optimise(config, "Instance.costHour");
		String line = path.getName() + "; ";
		line += r.getAnalysisTime() + ";";
		String auxLine = extractFromConfig(config);
		line += auxLine;
		if (result != null){			
//			line = path.getName() + "; ";
			
			String[] leaves = getConfigurationResult(result);
			for (String s : leaves) {
				line += s + ";";
			}
//			line += ";";
			
			line += getAttValsAsString(result);
//			
//			
//			
//			Collection<String> leaves = getConfigurationResult(result);
//			for (String s : leaves) {
//				line += s + " ";
//			}
//			line += ";";
//			line += r.getAnalysisTime() + ";";
//			line += getAttValsAsString(result);
		}
		else{
			line += "Invalid;";
		}
		w.write(line + "\n");
	}
	
	@Override
	protected void analysisSetup(String ec2fmPath) {
		AmazonEC2Scraper scraper = new AmazonEC2Scraper(
				"./ec2-by-date/2014-6-24/current-pricing.html",
				"./ec2-by-date/2014-6-24/prev-gen-pricing.html",
				"./ec2-by-date/2014-6-24/dedicated-pricing.html",
				"./properties");
		r = new CPOptEC2Reasoner(scraper);
		r.mapEC2Model(vm);
	}
	
	public static void main(String... args){
		AdhocEC2AnalysisCases tests = new AdhocEC2AnalysisCases();
		try {
//			tests.executeTest("./ec2-by-date/2014-6-24/AmazonEC2Atts.afm", "old exp configs", "adhocResults.csv");
//			tests.executeTest("./ec2-by-date/2014-6-24/AmazonEC2Atts.afm", "exp configs", "adhocResults.csv");
			tests.executeTest("./ec2-by-date/2014-6-24/AmazonEC2Atts.afm", "CloudScreener configs", "CSResults.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
