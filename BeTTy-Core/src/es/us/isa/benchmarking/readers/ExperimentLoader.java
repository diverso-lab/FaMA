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
package es.us.isa.benchmarking.readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.benchmarking.RandomExperiment;
import es.us.isa.generator.Characteristics;
import es.us.isa.utils.BettyException;

/**
 * This class loads an experiments in CSV format and a variabilitymodel from xml-FaMa format
 */
public class ExperimentLoader {

	private IExperimentReader expLoader;
	
	@SuppressWarnings("unused")
	private IReader modelLoader;
	
	public ExperimentLoader(){
		expLoader = new CSVExperimentReader();
		modelLoader = new XMLReader();
	}
	
	public Collection<Characteristics> loadCharacteristics(String path) throws FileNotFoundException, IOException, FAMAParameterException, BettyException{
		return expLoader.read(path);
	}
	
	public RandomExperiment loadVariabilityModel(String path){
		
		RandomExperiment rm = new RandomExperiment(path);
		
		try {

			QuestionTrader qt = new QuestionTrader();
			
			
			VariabilityModel m=qt.openFile(path);
			rm.setVariabilityModel(m);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rm;
	}
	
}
