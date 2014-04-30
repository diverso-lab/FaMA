package es.us.isa.FAMA.BenchmarkTests;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;


import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.generators.ComparativeGenerator;
import es.us.isa.benchmarking.generators.ExactRandomGenerator;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.FMCharacteristics;
import es.us.isa.benchmarking.generators.FixedFeaturesRandomGenerator;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.IRandomVMGenerator;
import es.us.isa.benchmarking.generators.RandomExperiment;
import es.us.isa.benchmarking.generators.RandomGenerator2;
import es.us.isa.benchmarking.readers.ExperimentLoader;
import es.us.isa.benchmarking.writers.ExperimentSaver;

public class BenchmarkAndFamaTest {

	IRandomVMGenerator gen1, gen2,gen3,gen4;
	VariabilityModel model1;
	ExperimentLoader loader;
	
	ExperimentSaver saver;
	
	@Before
	public void setUp(){
		gen1 = new RandomGenerator2();
		gen2 = new FixedFeaturesRandomGenerator();
		gen3 = new ExactRandomGenerator();
		gen4 = new ComparativeGenerator();

		loader = new ExperimentLoader();
		saver = new ExperimentSaver();
	}
	
	/*
	 * 1. Crear caracteristicas
	 * 2. Generar modelo con dichas caracteristicas
	 * 3. Salvar modelo y caracteristicas
	 */
	@Test
	public void test1() throws Exception{

		FMCharacteristics chars = new FMCharacteristics();//toman valores aleatorios
		chars.setWidth(6);
		chars.setHeight(4);
		chars.setChoose(2);
		chars.setNumberOfDependencies(57);//lo expresamos como porcentaje
		//chars.setPercentageOfDependencies(0.25f);
		//chars.setNumberOfFeatures(300);
		//chars.setMaxNumberOfFeatures(-1);
		//chars.setMinNumberOfFeatures(-1);
		chars.setSeed(-1814029828);
		
		model1 = gen4.generate(chars);
		//VariabilityModel model2 = gen2.generate(chars);
		
		QuestionTrader qt2=new QuestionTrader();
		//saver.saveCharacteristics(chars, "files/test/fixedchars.csv");
		qt2.writeFile("test.xml", model1);
		qt2.writeFile("test.x3d", model1);

	
		Collection<ICharacteristics> charsCol = 
				loader.loadCharacteristics("files/test/fixedchars.csv");
		Collection<Experiment> exps= new LinkedList<Experiment>();
		Iterator<ICharacteristics> it = charsCol.iterator();
		QuestionTrader qt = new QuestionTrader();
		//con este CriteriaSelector, podemos elegir el razonador
		qt.setCriteriaSelector("selected");
		int i = 1;
		
		//para cada ICharacteristics (solo hay una)
		while (it.hasNext()){
			ICharacteristics c = it.next();
			//VariabilityModel model = gen3.generate(c);
			RandomExperiment exp1 = new RandomExperiment("Experiment Valid "+i,model1,c),
							exp2 = new RandomExperiment("Experiment #Products "+i,model1,c);
			
			qt.setVariabilityModel(model1);
			
			
			String[] reasoners = {"JavaBDD"};
			long start=System.currentTimeMillis();

			for (int j = 0; j < reasoners.length; j++){
				//con este metodo privado, seleccionamos el razonador
				setReasoner(reasoners[j]);
				//Question q1 = qt.createQuestion("Valid");
				Question q2 = qt.createQuestion("#Products");
				//PerformanceResult pr1 = qt.ask(q1);
				//Map<String,String> results1 = pr1.getResults();
			//	exp1.addResult(results1);
				PerformanceResult pr2 = qt.ask(q2);
				//Map<String,String> results2 = pr2.getResults();
			//	exp2.addResult(results2);
				
				//System.out.println(q1);
				System.out.println(q2);
				i++;
				
			}
			long finish=System.currentTimeMillis();

			exps.add(exp1);
			exps.add(exp2);
			System.out.println(exp2.getResults());
			System.out.println(finish-start);
		}
		
		saver.save(exps, "files/test/results.csv");
	}
	
	private void setReasoner(String reasoner){
		Properties props = new Properties();
		props.setProperty("reasoner", reasoner);
		try {
			props.store(new FileOutputStream("reasoner.properties"), "#Insert here the selected reasoner");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
