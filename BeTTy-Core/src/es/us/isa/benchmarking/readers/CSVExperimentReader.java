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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.csvreader.CsvReader;

import es.us.isa.generator.Characteristics;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;
/**
 * This writer will save the experiment in Comma-Separated Values (CSV) format.
 * This format is adequate to process the data in Excel worksheets.
 * */
public class CSVExperimentReader implements IExperimentReader {

	@Override
	public Collection<Characteristics> read(String patch) throws IOException, BettyException {
		File fichero = new File(patch);
		FileReader freader = new FileReader(fichero);
		CsvReader reader = new CsvReader(freader, ';');
		// String experimentName ="experimentName";
		Collection<Characteristics> col = new ArrayList<Characteristics>();
		if (reader.readHeaders()) {
			String[] headers = reader.getHeaders();
			System.out.println("------- CABECERAS DEL FICHERO ------------");
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			// experimentName = headers[0];
			System.out
					.println("-----------------------------------------------------");
		}
		GeneratorCharacteristics caracteristica = null;
		while (reader.readRecord()) {
			String experimentName = reader.get("Experiment name");
			String branchFactor = reader.get("branchFactor");
			String choose = reader.get("choose");
			String numberOfFeatures = reader.get("numberOfFeatures");
			String probabilityMandatory = reader.get("probabilityMandatory");
			String probabilityOptional = reader.get("probabilityOptional");
			String probabilityOr = reader.get("probabilityOr");
			String probabilityAlternative = reader.get("probabilityAlternative");
			String percentageCTC = reader.get("percentageCTC");
			String seed = reader.get("seed");

			// Now convert to long and integers

			int branchFactorValue = Integer.valueOf(branchFactor);
			int chooseValue = Integer.valueOf(choose);
			int numberOfFeaturesValue = Integer.valueOf(numberOfFeatures);
			int probabilityMandatoryValue = Integer.valueOf(probabilityMandatory);
			int probabilityOptionalValue = Integer.valueOf(probabilityOptional);
			int probabilityOrValue = Integer.valueOf(probabilityOr);
			int probabilityAlternativeValue = Integer
					.valueOf(probabilityAlternative);
			int percentageCTCValue = Integer.valueOf(percentageCTC);
				int seedValue = Integer.valueOf(seed);
			String experimentNameValue = experimentName;

			caracteristica = new GeneratorCharacteristics();
			caracteristica.setSeed(seedValue);
			
			caracteristica.setMaxSetChildren(chooseValue);
			caracteristica.setModelName(experimentNameValue);
			caracteristica.setMaxBranchingFactor(branchFactorValue);
			caracteristica.setNumberOfFeatures(numberOfFeaturesValue);
			caracteristica.setProbabilityAlternative(probabilityAlternativeValue);
			caracteristica.setPercentageCTC(percentageCTCValue);
			caracteristica.setProbabilityMandatory(probabilityMandatoryValue);
			caracteristica.setProbabilityOptional(probabilityOptionalValue);
			caracteristica.setProbabilityOr(probabilityOrValue);

			col.add(caracteristica);
		}

		return col;
	}

}
