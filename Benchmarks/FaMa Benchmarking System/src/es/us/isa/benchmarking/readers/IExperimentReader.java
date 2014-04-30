package es.us.isa.benchmarking.readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.benchmarking.generators.ICharacteristics;




public interface IExperimentReader {
	public Collection<ICharacteristics> read(String path) throws FileNotFoundException, IOException, FAMAParameterException;

}
