package es.us.isa.FAMA.BenchmarkTests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.FMCharacteristics;
import es.us.isa.benchmarking.generators.FixedFeaturesRandomGenerator;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.IRandomVMGenerator;
import es.us.isa.benchmarking.generators.RandomExperiment;
import es.us.isa.benchmarking.generators.RandomGenerator2;
import es.us.isa.benchmarking.readers.ExperimentLoader;
import es.us.isa.benchmarking.writers.ExperimentSaver;


public class BenchmarkTest {

	IRandomVMGenerator gen1, gen2;
	
	ExperimentLoader loader;
	
	ExperimentSaver saver;
	
	@Before
	public void setUp(){
		gen1 = new RandomGenerator2();
		gen2 = new FixedFeaturesRandomGenerator();
		loader = new ExperimentLoader();
		saver = new ExperimentSaver();
	}
	
	@Test
	public void testLoad1(){
		//para cargar un VM y caracteristicas de un csv
		Collection<? extends Experiment> exps = loader.loadVariabilityModel("files/HIS.xml");
		RandomExperiment e =  (RandomExperiment)exps.iterator().next();
		Collection<ICharacteristics> characs;
		try {
			characs = loader.loadCharacteristics("files/sample1.csv");
			Iterator<ICharacteristics> it = characs.iterator();
			while (it.hasNext()){
				ICharacteristics o = it.next();
				e.setCharacteristics(o);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (FAMAParameterException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@Test
	public void testWriteCharacteristics1(){
		Collection<ICharacteristics> col = new LinkedList<ICharacteristics>();
		for (int i = 0; i < 3; i++){
			ICharacteristics chars = new FMCharacteristics(i,i,i,i,i,i,i,i,i);
			col.add(chars);
		}
		try {
			saver.saveCharacteristics(col, "files/testChars.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testWriteExperiments1(){
		
		try {
			Collection<ICharacteristics> characteristics = loader.loadCharacteristics("files/sample1.csv");
			Collection<Experiment> exps = loader.loadVariabilityModel("files/HIS.xml");
			//RandomExperiment exp1, exp2, exp3;
			Iterator<ICharacteristics> itChars = characteristics.iterator();
			Iterator<? extends Experiment> itExps = exps.iterator();
			int i = 1;
			while (itChars.hasNext() && itExps.hasNext()){
				ICharacteristics c = itChars.next();
				RandomExperiment exp = (RandomExperiment)itExps.next();
				exp.setCharacteristics(c);
				exp.setName("Experiment "+i);
				
				Map<String,String> results = new HashMap<String,String>();
				results.put("ChocoTime", String.valueOf(0.29));
				results.put("ChocoBacktracks", String.valueOf(29));
				results.put("ChocoSpace", "29 M");
				exp.addResult(results);
			}
			
			saver.save(exps, "files/testWriteExperiments1.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FAMAParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	public void testRandom1(){
		//para cargar caracteristicas de un csv y generar un VM
		//RandomExperiment e =  (RandomExperiment)exps.iterator().next();
		Collection<ICharacteristics> characs;
		Collection<VariabilityModel> models = new LinkedList<VariabilityModel>();
		try {
			characs = loader.loadCharacteristics("files/sample1.csv");
			Iterator<ICharacteristics> it = characs.iterator();
			int i = 1;
			while (it.hasNext()){
				ICharacteristics o = it.next();
				VariabilityModel aux1, aux2; 
				aux1 = gen1.generate(o);
				aux2 = gen2.generate(o);
				this.saver.saveVM(aux1, "files/test/r"+i+".xml");
				this.saver.saveVM(aux2, "files/test/f"+i+".xml");
				models.add(aux1);
				models.add(aux2);
				i++;
			}
			
			//System.out.println("Number of models generated: "+models.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
