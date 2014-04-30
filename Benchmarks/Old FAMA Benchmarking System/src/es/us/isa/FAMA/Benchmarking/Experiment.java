package es.us.isa.FAMA.Benchmarking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.us.isa.FAMA.Benchmarking.CSV.ExcelCSVPrinter;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

/**
 * @author   Dani
 */
public abstract class Experiment {
	/**
	 * @uml.property  name="reasoners"
	 * @uml.associationEnd  qualifier="key:java.lang.Object tdg.SPL.Reasoner.Question"
	 */
	protected Map<Reasoner,Question> reasoners;
	/**
	 * @uml.property  name="captions"
	 * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.String"
	 */
	protected Map<Reasoner,String> captions;
	protected long time;
	/**
	 * @uml.property  name="results"
	 * @uml.associationEnd  qualifier="key:java.lang.Object java.util.List<Object>"
	 */
	protected Map<VariabilityModel,List<Object>> results;
	protected String resultsFile;
	protected ExcelCSVPrinter csvPrinter;
	protected String fileExtension;
	
	protected PerformanceResultWriter resultWriter;
	protected QuestionTrader questionTrader;
	
	public Experiment(QuestionTrader qt) {
		reasoners = new HashMap<Reasoner,Question>();
		captions = new HashMap<Reasoner,String>();
		// linked hash map has a predictable iteration. If we are going to search
		// for particular results later, choose IdentityHashMap for example
		results = new LinkedHashMap<VariabilityModel,List<Object>>();
		resultsFile = "results.csv";
		questionTrader = qt;
		fileExtension = "xml";
	}
	
/*	public Experiment(PerformanceResultWriter resultWriter) {
		this();
		this.resultWriter = resultWriter;
	}
	*/
	public void addReasoner(String caption, Reasoner r,Question q) {
		captions.put(r,caption);	
		reasoners.put(r,q);
	}
	
	public void removeReasoner(Reasoner r) {
		if (reasoners.containsKey(r)) {
			captions.remove(r);
			reasoners.remove(r);
		}
	}
	
	public void setResultFile(String resultsFile) {
		this.resultsFile = resultsFile;
	}
	
	public abstract void execute() throws IOException;
	
	protected void startTiming() {
		time = System.nanoTime();
	}
	
	protected void endTiming() {
		time = (System.nanoTime()-time);///1000000;
	}
	
	protected void writeHeadings() throws IOException {
		OutputStream out;

		File f = new File(resultsFile);
		if (!f.exists()){
			f.createNewFile();
		}
		if (f.canWrite()){
			out = new FileOutputStream(f);
		} else {
			throw new IOException("Could not open " + resultsFile);
		}
	
		// creates the Excel CSV File and place the headers
		csvPrinter  = new ExcelCSVPrinter(out);
		csvPrinter.print("Instances");
		Iterator<String> itr = captions.values().iterator();
		while (itr.hasNext()) {
			csvPrinter.print(itr.next());
		}
		csvPrinter.println();	
		csvPrinter.flush();
	}
	
	protected void writeResults() throws IOException {	
		// writes the results
		int instance = 1;
		Iterator<List<Object>> resultsIt = results.values().iterator();
		while (resultsIt.hasNext()) {
			Iterator<Object> valuesIt = resultsIt.next().iterator();
			csvPrinter.print(String.valueOf(instance));
			while (valuesIt.hasNext()) {
				csvPrinter.print(valuesIt.next().toString());
			}
			csvPrinter.println();
			instance++;
		}
		csvPrinter.close();
	}
	
	public String toString() {
		String res = "Experiment\r\n";
		res += "Reasoners:\r\n\r\n";	
		Iterator<Reasoner> itr = reasoners.keySet().iterator();
		while (itr.hasNext()) {
			Reasoner r = itr.next();
			res += r.getClass().getName() + "\r\n";
		}
		
		return res;
	}
	
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}