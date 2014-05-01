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
package es.us.isa.benchmarking.writers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLWriter;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import es.us.isa.benchmarking.RandomExperiment;
import es.us.isa.generator.Characteristics;

public class ExperimentSaver {

	private IExperimentWriter expWriter;	
	private IWriter modelWriter;
	
	public ExperimentSaver(){
		//determinar las clases que implementan las interfaces mediante reflexion
		expWriter = new CSVExperimentWriter();
		modelWriter = new XMLWriter();
	}
	
	public void saveCharacteristics(Characteristics chars, String path) throws IOException{
		Collection<Characteristics> col = new LinkedList<Characteristics>();
		col.add(chars);
		saveCharacteristics(col, path);
	}
	public void saveAllChars(Collection<RandomExperiment> col,
			String path) throws IOException{
		Collection<Characteristics> cars=new ArrayList<Characteristics>();
		Iterator<RandomExperiment> it= col.iterator();
		while(it.hasNext()){
			cars.add(it.next().getCharacteristics());
		}
		saveCharacteristics(cars, path);
	}
	public void saveCharacteristics(Collection<Characteristics> col, String path) throws IOException{
		expWriter.saveCharacteristics(col, path);
	}
	
	public void saveVM(VariabilityModel vm, String path) throws Exception{
			modelWriter.writeFile(path, vm);
	}
	

	
	public void save(RandomExperiment exp, String path) throws IOException{
		Collection<RandomExperiment> col = new ArrayList<RandomExperiment>();
		col.add(exp);
		save(col,path);
	} 
	
	public void save(Collection<RandomExperiment> col, String path) throws IOException{
		expWriter.saveResults(col, path);
	}
	
}
