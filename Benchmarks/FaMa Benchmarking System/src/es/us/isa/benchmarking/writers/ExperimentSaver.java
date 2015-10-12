package es.us.isa.benchmarking.writers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.ICharacteristics;

public class ExperimentSaver {

	private IExperimentWriter expWriter;	
	private IWriter modelWriter;
	
	public ExperimentSaver(){
		//determinar las clases que implementan las interfaces mediante reflexion
		expWriter = new CSVExperimentWriter();
		modelWriter = new XMLWriter();
	}
	
	public void saveCharacteristics(ICharacteristics chars, String path) throws IOException{
		Collection<ICharacteristics> col = new LinkedList<ICharacteristics>();
		col.add(chars);
		saveCharacteristics(col, path);
	}
	
	public void saveCharacteristics(Collection<ICharacteristics> col, String path) throws IOException{
		expWriter.saveCharacteristics(col, path);
	}
	
	public void saveVM(VariabilityModel vm, String path) throws Exception{
			modelWriter.writeFile(path, vm);
	}
	

	
	public void save(Experiment exp, String path) throws IOException{
		Collection<Experiment> col = new ArrayList<Experiment>();
		col.add(exp);
		save(col,path);
	} 
	
	public void save(Collection<Experiment> col, String path) throws IOException{
		expWriter.save(col, path);
	}
	
}
