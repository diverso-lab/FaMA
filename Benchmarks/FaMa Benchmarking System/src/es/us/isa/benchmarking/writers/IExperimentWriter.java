package es.us.isa.benchmarking.writers;

import java.io.IOException;
import java.util.Collection;

import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.ICharacteristics;


public interface IExperimentWriter {

	public void save(Collection<Experiment> col,String path) throws IOException;
	
	public void saveCharacteristics(Collection<ICharacteristics> col,String path) throws IOException;
	
}
