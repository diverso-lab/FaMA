package es.us.isa.benchmarking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.FMCharacteristics;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.IRandomVMGenerator;
import es.us.isa.benchmarking.generators.RandomExperiment;
import es.us.isa.benchmarking.generators.RandomGenerator2;
import es.us.isa.benchmarking.readers.ExperimentLoader;
import es.us.isa.benchmarking.writers.ExperimentSaver;


/**
 * Clase fachada con la que se relacionar� el usuario del benchmark.
 * Debemos ofrecerle aqu� todos los m�todos necesarios para poder 
 * inteactuar con el sistema de Benchmark.
 */
public abstract class Benchmark {

	protected IRandomVMGenerator generator;
	
	protected ExperimentSaver saver;
	
	protected ExperimentLoader loader;
	
	protected Collection<Experiment> exps;
	
	public Benchmark(IRandomVMGenerator generator){
		this.generator=generator;
		loader = new ExperimentLoader();
		saver=new ExperimentSaver();

	}
	
	public Benchmark(){
		generator=new RandomGenerator2();
		loader = new ExperimentLoader();
		saver=new ExperimentSaver();

	}
	public Experiment createRandomExperiment(ICharacteristics ch){
		Experiment exp=new Experiment("Experiment",generator.generate(ch));
		return exp;
	}
	public Experiment createRandomExperiment(String name,ICharacteristics ch){
		Experiment exp=new Experiment(name,generator.generate(ch));
		return exp;
	}
	public ArrayList<Experiment> createSetRandomExperiment(int n, FMCharacteristics ch){
		ArrayList<Experiment> exps=new ArrayList<Experiment>(n);
		for(int i=0;i<n;i++){
			ch.setSeed(ch.getSeed()+i);
			Experiment exp=new Experiment("ExperimentNo"+i,generator.generate(ch));
			exps.add(exp);
		}
		return exps;
	}
	
	public Collection<ICharacteristics> loadCharacteristics(String path){
		Collection<ICharacteristics> col=null;
		try {
			col= loader.loadCharacteristics(path);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (FAMAParameterException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return col;
	}
	
	public void saveCharacteristics(ICharacteristics ch, String path){
		Collection<ICharacteristics> col=new ArrayList<ICharacteristics>();
		col.add(ch);
		try {
			saver.saveCharacteristics(ch, path);
		} catch (IOException e) {
			System.out.println("Archivo no encsntrado");
			e.printStackTrace();
		}
	}
	
	public void saveCharacteristics(Collection<? extends Experiment> exps, String path){
		Iterator<? extends Experiment> it=exps.iterator();
		Collection<ICharacteristics> chars=new ArrayList<ICharacteristics>();
		while(it.hasNext()){
			RandomExperiment exp=(RandomExperiment) it.next();
			chars.add(exp.getCharacteristics());
		}
		try {
			saver.saveCharacteristics(chars, path);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}	
	
	public Experiment loadVariabilityModel(String path){
		return loader.loadVariabilityModel(path);
	}
	
	public void saveVariabilityModel(VariabilityModel vm,String path){
		try {
			saver.saveVM(vm, path);
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}
	
	public void saveVariabilityModel(Collection<Experiment> exps, String path){
		Iterator<Experiment> it = exps.iterator();
		while(it.hasNext()){
			Experiment exp=it.next();
			try {
				saver.saveVM(exp.getVariabilityModel(), path+exp.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public void saveExperiment(Experiment exp,String path){
		try {
			saver.save(exp, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveExperiments(Collection<Experiment> exps,String path) throws IOException{
		saver.save(exps, path);
	}
	
	public abstract void execute() throws FAMAException;
	
	public void setExps(Collection<Experiment> exps) {
		this.exps = exps;
	}
	public Collection<Experiment> getExps() {
		return exps;
	}
	public IRandomVMGenerator getGenerator() {
		return generator;
	}
	public void setGenerator(IRandomVMGenerator generator) {
		this.generator = generator;
	}
	public ExperimentSaver getSaver() {
		return saver;
	}
	public void setSaver(ExperimentSaver saver) {
		this.saver = saver;
	}
	public ExperimentLoader getLoader() {
		return loader;
	}
	public void setLoader(ExperimentLoader loader) {
		this.loader = loader;
	}
	
	
}
