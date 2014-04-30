package es.us.isa.benchmarking;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.RandomExperiment;
import es.us.isa.benchmarking.readers.FaMaExperimentReader;
import es.us.isa.benchmarking.readers.IFaMaExperimentReader;

public class FAMABenchmark extends Benchmark {
	String charsStr;
	String reasonersStr;
	String famaFileStr;
	String saveStr;
	public FAMABenchmark(boolean args){
	
	
	
	}
	public FAMABenchmark(){
		
	}
	
	public FAMABenchmark(String chars,String reasoners,String famaFile,String save){
		charsStr=chars;
		reasonersStr=reasoners;
		famaFileStr=famaFile;
		saveStr=save;
	}
	
	public void executeWithArgs(Experiment exp, String questionType,String reasoner){
		
	//Make the question an save the results
		
		QuestionTrader qt=new QuestionTrader();
		qt.setCriteriaSelector("selected");
		qt.setVariabilityModel(exp.getVariabilityModel());
		Question q=qt.createQuestion(questionType);
		setReasoner(reasoner);
		exp.addResult(qt.ask(q).getResults());
		
	}
	
	
	
	@Override
	public void execute() throws FAMAException {
		if(charsStr==""&&reasonersStr==""&&famaFileStr==""){
			throw new IllegalArgumentException("Execute the contructor with arguments please");
		}
		Collection<ICharacteristics> chars = super
				.loadCharacteristics(charsStr);
		IFaMaExperimentReader famaReader = new FaMaExperimentReader();

		Collection<String> reasoners = famaReader
				.getReasoners(reasonersStr);
		Collection<String> questions = famaReader
				.getQuestions(famaFileStr);
		Collection<Experiment> exps = new LinkedList<Experiment>();

		Iterator<ICharacteristics> it = chars.iterator();
		int i = 1;

		QuestionTrader qt = new QuestionTrader();
		qt.setCriteriaSelector("selected");

		String[] reasonersArray = reasoners.toArray(new String[1]);
		while (it.hasNext()) {
			ICharacteristics ch = it.next();
			Iterator<String> questionsIt = questions.iterator();
			RandomExperiment exp = new RandomExperiment(questionsIt.next() + i,
					generator.generate(ch), ch);
			qt.setVariabilityModel(exp.getVariabilityModel());

			for (int j = 0; j < reasonersArray.length; j++) {
				setReasoner(reasonersArray[j]);
				Iterator<String> questionIt = questions.iterator();

				while (questionIt.hasNext()) {
					Question q = qt.createQuestion(questionIt.next());
					if (q != null) {
						PerformanceResult pr = qt.ask(q);
						System.out.println(q);
						exp.addResult(pr.getResults());

					}

				}

			}
			exps.add(exp);
			i++;
		}
		try {
			saver.save(exps, saveStr);
		} catch (IOException e) {
			System.err.println("no se pudo escribir en el archivo de salida");
			e.printStackTrace();
		}

	}

	private void setReasoner(String reasoner) {
		Properties props = new Properties();
		props.setProperty("reasoner", reasoner);
		try {
			props.store(new FileOutputStream("reasoner.properties"),
					"#Insert here the selected reasoner");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
