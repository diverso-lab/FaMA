package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.Ostermiller.util.ExcelCSVPrinter;

import featureModel.FeatureModel;

public class ExperimentSaver {

	private Experiment exp;			// Experiment
	private FeatureModel fm;		// Feature model generated
	private String results;			// Results to print
	private ArrayList<String> csvResults=new ArrayList<String>();	  // CSV results
	
	public ExperimentSaver() {
	}
	
	public ExperimentSaver(Experiment exp, FeatureModel fm) {
		this.exp=exp;
		this.results="";
		this.fm=fm;
	}
	
	public void setExperiment(Experiment exp) {
		this.exp=exp;
	}
	
	// Add a string with results to show
	public void addResult(String result) {
		this.results+=result;
	}
	
	// Add results to print in CSV format
	public void addCSVResult(ArrayList<String> results) {
		
		Iterator it=results.iterator();
		while (it.hasNext()) {
			this.csvResults.add((String)it.next());
		}
	}
	
	// Add data to print in CSV format
	public void addCSVData(String data) {
		this.csvResults.add(data);
	}
	
	// Save the experiment and the results in path
	public void save(String path, boolean append) {	
		
		String filePath=path + "\\" + exp.getName() + ".sol";
		File outputFile = null;
		outputFile = new File(filePath);
		FileWriter out;
		try {
			out = new FileWriter(outputFile, append);
			out.write(printExperiment());	// Save experiment data
			out.write(results);				// Save results
			out.close();
		}
		catch (IOException oops) {
			System.out.println("Unable to save experiment to the file " + filePath + " .Reason: " + oops.getMessage());
		}
	}
	
	// Return a string with all experiment data
	private String printExperiment() {
		
		String res=""; 
		
		res+= "************** EXPERIMENT " + exp.getName() + "*************\n";
		res+="NUMBER OF FEATURES: "+ fm.getFeaturesNumber() + "\n";
		res+="NUMBER OF DEPENDENCIES: " + fm.getNumberOfDependencies() + "\n";
		res+="NUMBER OF LEVELS: " + fm.getNumberOfLevels() + "\n";
		res+="AVAILABLE MEMORY: " + (Runtime.getRuntime().maxMemory()/1024)/1024 + "Mb\n";
		res+="PARAMETERS: ";
		res+="W:" + exp.getW() + " H:" + exp.getH() + " E:" + exp.getE() + " D:" + exp.getD() + " GENERATOR_SEED:"
				+ exp.getGeneratorSeed() + "\n\n";
		
		return res;
	}
	
	// Save experiment data in cvs format
	public void saveCSV(String path) {
		
		File outputFile = new File(path);
		try {
			FileWriter out=new FileWriter(outputFile,true);
			ExcelCSVPrinter ecsvp = new ExcelCSVPrinter(out);
			
			ecsvp.writeln(
				    new String[]{
				        Long.toString(this.exp.getGeneratorSeed()),
				        Integer.toString(this.exp.getW()),
				        Integer.toString(this.exp.getH()),
				        Integer.toString(this.exp.getE()),
				        Integer.toString(this.exp.getD()),
				        Integer.toString(this.exp.getFeatureNumber())
				    }
				);
			
		} catch (IOException e) {
			
			System.out.println("ERROR: Ocurrió un error al almacenar los parámetros del experimento en formato CVS: " + e.getMessage());
		}
	}
	
	// Save experiment data and results in CSV format
	public void saveCSVResults(String path) {
		
		try
		{		
			File outputFile = new File(path);
			FileWriter out=new FileWriter(outputFile,true);
			ExcelCSVPrinter ecsvp = new ExcelCSVPrinter(out);
			
			// Print experiment data
			ecsvp.write(
				    new String[]{
				    	this.exp.getName(),
				        Long.toString(this.exp.getGeneratorSeed()),
				        Integer.toString(this.exp.getW()),
				        Integer.toString(this.exp.getH()),
				        Integer.toString(this.exp.getE()),
				        Integer.toString(this.exp.getD())
				    }
				);
			
			// Print Results
			ecsvp.writeln((String[])this.csvResults.toArray(new String[this.csvResults.size()]));
			
		} catch (IOException e) {
			
			System.out.println("ERROR: Ocurrió un error al almacenar los parámetros del experimento en formato CVS: " + e.getMessage());
		}
	}
}
