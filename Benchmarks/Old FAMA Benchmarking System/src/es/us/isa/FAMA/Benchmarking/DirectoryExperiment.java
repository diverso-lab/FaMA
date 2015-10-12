package es.us.isa.FAMA.Benchmarking;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class DirectoryExperiment extends Experiment {
	/**
	 * @uml.property  name="directory"
	 */
	protected String directory;
	
	public DirectoryExperiment(QuestionTrader qt) {
		super(qt);
		directory = "";
	}

	/**
	 * @param directory
	 * @uml.property  name="directory"
	 */
	public void setDirectory (String directory) {
		this.directory = directory;
	}
	
	
	@Override
	public void execute() throws IOException {
		File dir = new File(directory);
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return (!name.startsWith(".") && name.endsWith(fileExtension));
	        }
	    };

		String[] files = dir.list(filter);
		writeHeadings();
		for (int i = 0; i < files.length; i++) {
			try {
				VariabilityModel fm = questionTrader.openFile(directory+"/" +files[i]);
				Iterator<Entry<Reasoner,Question>> itr = reasoners.entrySet().iterator();
				List<Object> result = new ArrayList<Object>();
				csvPrinter.print(files[i]);
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
			} catch (Exception e) {
				System.out.println("File "+files[i]+" is not a valid FM");
			}
		}

		
	}

}
