package es.us.isa.FAMA.Benchmarking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class RandomExperiment extends Experiment {
	private String outputDirectory;
	private boolean toFile;
	protected Collection<Characteristics> chars;
	
	public RandomExperiment(QuestionTrader qt) {
		super(qt);
		outputDirectory = "";
		toFile = true;
		chars = new ArrayList<Characteristics>();
	}

	/**
	 * @param toFile
	 * @uml.property  name="toFile"
	 */
	public void setToFile(boolean toFile) {
		this.toFile = toFile;
	}
	
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	public void addCharacteristics (Characteristics c) {
		chars.add(c);
	}
	
	@Override
	public void execute() throws IOException {
		Iterator<Characteristics> itc = chars.iterator();
		RandomGenerator fixedrgen = new FixedFeaturesRandomGenerator();
		RandomGenerator nonfixedrgen = new RandomGenerator();
		RandomGenerator rgen=nonfixedrgen;
		writeHeadings();
		int instance = 1;
		while (itc.hasNext()) {
			Characteristics c = itc.next();
			if (c.getNumberOfFeatures() == 0)
				rgen = nonfixedrgen;
			else
				rgen = fixedrgen;
			Iterator<VariabilityModel> fms = rgen.generate(c).iterator();
			while (fms.hasNext()) {
				VariabilityModel fm = fms.next();
				if (toFile)
					try{
						questionTrader.writeFile(outputDirectory + "Instance" + instance + "." + fileExtension,fm);
					} catch (Exception e){
						e.printStackTrace();
					}
				Iterator<Entry<Reasoner,Question>> itr = reasoners.entrySet().iterator();
				List<Object> result = new ArrayList<Object>();
				csvPrinter.print(String.valueOf(instance++));
				while (itr.hasNext()) {
					Entry<Reasoner,Question> entry = itr.next();
					Reasoner r = entry.getKey();
					Question q = entry.getValue();
					fm.transformTo(r);
					startTiming();
					PerformanceResult pr = r.ask(q);
					endTiming();
					if (pr == null)
						csvPrinter.print("time: "+String.valueOf(time));
					else {
						if (resultWriter == null) {
							csvPrinter.print(pr.toString());
						} else {
							pr.setTime(time);
							csvPrinter.print(resultWriter.toString(pr));
						}
					}
					result.add(new Long(time));
					if (!itr.hasNext())
						csvPrinter.print(q.toString());
				}
				csvPrinter.println();
				csvPrinter.flush();
				results.put(fm,result);
			}
		}
		// writeResults();
	}

	@Override
	public String toString() {
		String res = super.toString();
		res += "\r\nCharacteristics of the instances:\r\n\r\n";
		
		Iterator<Characteristics> itc = chars.iterator();
		while (itc.hasNext()) {
			Characteristics c = itc.next();
			res += c + "\r\n";
		}
		return res;		
	}

	
}
