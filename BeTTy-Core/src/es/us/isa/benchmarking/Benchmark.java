/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.benchmarking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.readers.ExperimentLoader;
import es.us.isa.benchmarking.writers.ExperimentSaver;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.IGenerator;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.generator.FM.attributed.AttributedCharacteristic;
import es.us.isa.utils.BettyException;


/**
 * This class represents a new benchmark
 */
public abstract class Benchmark {

	protected IGenerator generator;
	
	protected ExperimentSaver saver;
	
	protected ExperimentLoader loader;
	
	protected Collection<Experiment> exps;
	/**
	 * A new Benchmark using an specified generator 
	 * @param generator the generator to be used
	 */
	public Benchmark(IGenerator generator){
		this.generator=generator;
		loader = new ExperimentLoader();
		saver=new ExperimentSaver();

	}
	
	public Benchmark() {
		loader = new ExperimentLoader();
		saver=new ExperimentSaver();	
	}
	/**
	 * Creates a new RandomExperiments, using giving characteristics.
	 * @param ch The desired characteristics of the generated models
	 * @return A random experiment
	 * @throws BettyException 
	 */
	public RandomExperiment createRandomExperiment(Characteristics ch) throws BettyException{
		RandomExperiment exp=new RandomExperiment("Experiment",generator.generateFM(ch),ch);
		return exp;
	}
	
	/**
	 * Creates a random experiments with defined name
	 * @param name the name of the experiment
	 * @param ch The characteristics of the generated model
	 * @return The experiment with desired characteristics
	 * @throws BettyException 
	 */
	public Experiment createRandomExperiment(String name,Characteristics ch) throws BettyException{
		Experiment exp=new Experiment(name,generator.generateFM(ch));
		return exp;
	}
	
	/**
	 * Creates a set of experiments to be executed
	 * @param n the number of desired experiments
	 * @param chl The Characteristics to be used
	 * @return the set of experiment
	 * @throws BettyException 
	 */
	
	public ArrayList<RandomExperiment> createSetRandomExperiment(int n, Characteristics chl) throws BettyException{
		Random random = new Random();
		//((FMGenerator)generator).setOnlyValidModels(true);
		
		ArrayList<RandomExperiment> exps=new ArrayList<RandomExperiment>(n);
		for(int i=0;i<n;i++){
			Characteristics chc = null;
			
			if(chl instanceof AttributedCharacteristic){
				AttributedCharacteristic gc=((AttributedCharacteristic)chl).clone();
				gc.setSeed(gc.getSeed()+random.nextInt());
				gc.setModelName(gc.getNumberOfFeatures()+ "-" + gc.getPercentageCTC() + "-" + i);
				chc=gc;
			}else if(chl instanceof GeneratorCharacteristics){
				GeneratorCharacteristics gc=((GeneratorCharacteristics)chl).clone();
				gc.setSeed(gc.getSeed()+random.nextInt());
				gc.setModelName(gc.getNumberOfFeatures()+ "-" + gc.getPercentageCTC() + "-" + i);
				chc=gc;
			}
			
			
			
			RandomExperiment exp=new RandomExperiment("ExperimentNo"+i,generator.generateFM(chc),chc);
			exps.add(exp);
		}
		return exps;
	}
	
	/**
	 * Creates a set of experiments to be executed
	 * @param n the number of desired experiments
	 * @param chl The Characteristics to be used
	 * @return the set of experiment
	 * @throws BettyException 
	 */
	
	public ArrayList<RandomExperiment> createSetRandomExperiment(String name,int n, Characteristics chl) throws BettyException{
		Random random = new Random();
		//((FMGenerator)generator).setOnlyValidModels(true);
		
		ArrayList<RandomExperiment> exps=new ArrayList<RandomExperiment>(n);
		for(int i=0;i<n;i++){
			Characteristics chc = null;
			
			if(chl instanceof AttributedCharacteristic){
				AttributedCharacteristic gc=((AttributedCharacteristic)chl).clone();
				gc.setSeed(gc.getSeed()+random.nextInt());
				gc.setModelName(gc.getNumberOfFeatures()+ "-" + gc.getPercentageCTC() + "-" + i);
				chc=gc;
			}else if(chl instanceof GeneratorCharacteristics){
				GeneratorCharacteristics gc=((GeneratorCharacteristics)chl).clone();
				gc.setSeed(gc.getSeed()+random.nextInt());
				gc.setModelName(gc.getNumberOfFeatures()+ "-" + gc.getPercentageCTC() + "-" + i);
				chc=gc;
			}
			
			
			
			RandomExperiment exp=new RandomExperiment(name+i,generator.generateFM(chc),chc);
			exps.add(exp);
		}
		return exps;
	}
	
	/**
	 * This method will load a set of charateristics from a file 
	 * @param path where is the file
	 * @return A collection of {@link Characteristics}
	 * @throws BettyException 
	 */
	public ArrayList<Characteristics> loadCharacteristics(String path) throws BettyException{
		ArrayList<Characteristics> col=new ArrayList<Characteristics>();;
		try {
			col.addAll(loader.loadCharacteristics(path));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (FAMAParameterException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return col;
	}
	
	/**
	 * This method will save a characteristic into a file
	 * @param ch the characteristic to be saved
	 * @param path Where we want to save it.
	 */
	public void saveCharacteristics(Characteristics ch, String path){
		Collection<Characteristics> col=new ArrayList<Characteristics>();
		col.add(ch);
		try {
			saver.saveCharacteristics(ch, path);
		} catch (IOException e) {
			System.out.println("Archivo no encontrado");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will save a set of characteristics from a set of experiments
	 * @param exps The set of experiments
	 * @param path Where we want to save the characteristics
	 */
	public void saveCharacteristics(Collection<? extends Experiment> exps, String path){
		Iterator<? extends Experiment> it=exps.iterator();
		Collection<Characteristics> chars=new ArrayList<Characteristics>();
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
	
	/**
	 * This method will genreate a experiment with given variability model
	 * @param path Were is located the file that describes the variability model
	 * @return The random experiment
	 */
	public RandomExperiment loadVariabilityModel(String path){
		return loader.loadVariabilityModel(path);
	}
	
	/**
	 * This method will save the variability model into the file path specified
	 * @param vm The variability Model to be saved 
	 * @param path Where we want to save the file.
	 */
	public void saveVariabilityModel(VariabilityModel vm,String path){
		try {
			saver.saveVM(vm, path);
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will save a set of variability models coming from a set of experiments into a specified path
	 * @param exps the collection of experiments
	 * @param path where we want to save the models
	 */
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
	
	/**
	 * This method will save a experiment into a path
	 * @param exp The experiment to save
	 * @param path The path where save the experiment
	 */
	public void saveExperiment(RandomExperiment exp,String path){
		try {
			saver.save(exp, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Save a set of experiments
	 * @param exps the experiments to be save
	 * @param path The path where the experiments are going to be saved
	 * @throws IOException When the path does'nt exist
	 */
	public void saveExperiments(Collection<RandomExperiment> exps,String path) throws IOException{
		saver.save(exps, path);
	}
	
	public void setExps(Collection<Experiment> exps) {
		this.exps = exps;
	}
	public Collection<Experiment> getExps() {
		return exps;
	}
	public IGenerator getGenerator() {
		return generator;
	}
	public void setGenerator(IGenerator generator) {
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
