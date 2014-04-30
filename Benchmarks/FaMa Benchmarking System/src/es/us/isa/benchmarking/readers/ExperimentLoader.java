package es.us.isa.benchmarking.readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.RandomExperiment;


public class ExperimentLoader {

	private IExperimentReader expLoader;
	
	private IReader modelLoader;
	
	public ExperimentLoader(){
		//FIXME en un futuro, los atributos estaran parametrizados en un archivo config
		expLoader = new CSVExperimentReader();
		modelLoader = new XMLReader();
	}
	
	public Collection<ICharacteristics> loadCharacteristics(String path) throws FileNotFoundException, IOException, FAMAParameterException{
		return expLoader.read(path);
	}
	
	public Experiment loadVariabilityModel(String path){
		
		Experiment rm = new Experiment(path);
		
		try {
			//FIXME parametrizar en un futuro el tipo de Experiment por archivo de config
			VariabilityModel m = modelLoader.parseFile(path);
			rm.setVariabilityModel(m);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rm;
	}
	
}
