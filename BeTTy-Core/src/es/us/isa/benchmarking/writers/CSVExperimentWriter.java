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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.csvreader.CsvWriter;

import es.us.isa.benchmarking.RandomExperiment;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.FM.GeneratorCharacteristics;

public class CSVExperimentWriter implements IExperimentWriter {
	ArrayList<String> indice = new ArrayList<String>();
	Map<String, String> allExps = new HashMap<String, String>();
	int counter = 1;
	File fichero;
	FileWriter fwriter;
	CsvWriter writercsv;

	public CSVExperimentWriter() {
	}

	public void saveResults(Collection<RandomExperiment> col, String path)
			throws IOException {

		fichero = new File(path);
		FileWriter fwriter = new FileWriter(fichero, true);
		writercsv = new CsvWriter(fwriter, ';');

		if (!(fichero.length() > 0)) {// IF file is not empty
			writeHeader(writercsv, col);
		} else {
			getindice(col, false);
		}
		// At first we need to read all experiments to know where to write
		// the results

		Iterator<RandomExperiment> it = col.iterator();
		while (it.hasNext()) {
			RandomExperiment exp = (RandomExperiment) it.next();
			Collection<Map<String, String>> results = exp.getResults();
			GeneratorCharacteristics caracteristica = (GeneratorCharacteristics) exp.getCharacteristics();
			// write the characteristics
			if (caracteristica != null) {
				writercsv.write(exp.getName());
				writercsv.write(String.valueOf(caracteristica.getMaxBranchingFactor()));
				writercsv.write(String.valueOf(caracteristica.getNumberOfFeatures()));
				writercsv.write(String.valueOf(caracteristica.getProbabilityMandatory()));
				writercsv.write(String.valueOf(caracteristica.getProbabilityOptional()));
				writercsv.write(String.valueOf(caracteristica.getProbabilityOr()));
				writercsv.write(String.valueOf(caracteristica.getProbabilityAlternative()));
				writercsv.write(String.valueOf(caracteristica.getPercentageCTC()));
				writercsv.write(String.valueOf(caracteristica.getSeed()));
				writercsv.write(String.valueOf(""));
			} else {
				//INSERT ONE BLANCK LINE
				for (int i = 0; i < 15; i++) {
					writercsv.write("");
				}
			}
			
			
			Iterator<String> itInd = indice.iterator();
			while (itInd.hasNext()) {
				String aux = itInd.next();
				Iterator<Map<String, String>> resultsIterator = results.iterator();
				String param = null;
				while (resultsIterator.hasNext() && param == null) {
					Map<String, String> mapAux = resultsIterator.next();
					param = mapAux.get(aux);
					if (param != null) {
						writercsv.write(param);
					}
				}
				if (param == null) {
					writercsv.write("");
				}
			}

			writercsv.endRecord();

		}
		writercsv.close();
	}

	private void getindice(Collection<RandomExperiment> col, boolean first)
			throws IOException {

		Iterator<RandomExperiment> it = col.iterator();
		while (it.hasNext()) {
			RandomExperiment exp = (RandomExperiment) it.next();
			Collection<Map<String, String>> results = exp.getResults();
			Iterator<Map<String, String>> it2 = results.iterator();
			while (it2.hasNext()) {
				Map<String, String> mapa = it2.next();
				Iterator<Entry<String, String>> it3 = mapa.entrySet()
						.iterator();
				while (it3.hasNext()) {
					Entry<String, String> entry = it3.next();
					if (!indice.contains(entry.getKey())) {
						indice.add(entry.getKey());
						allExps.put(entry.getKey(), entry.getValue());
						if (first) {
							writercsv.write(entry.getKey());
						}
						counter++;

					}
				}
			}
		}
	}

	@Override
	public void saveCharacteristics(Collection<Characteristics> col, String path)
			throws IOException {
		File fichero = new File(path);
		FileWriter fwriter = new FileWriter(fichero);
		CsvWriter writercsv = new CsvWriter(fwriter, ';');

		writercsv.write("Experiment name");
		writercsv.write("BanchingFactor");
		writercsv.write("numberOfFeatures");
		writercsv.write("probabilityMandatory");
		writercsv.write("probabilityOptional");
		writercsv.write("probabilityOr");
		writercsv.write("probabilityAlternative");
		writercsv.write("probabilityCTC");
		writercsv.write("seed");
		writercsv.endRecord();

		Iterator<Characteristics> it = col.iterator();
		while (it.hasNext()) {

			GeneratorCharacteristics caracteristica = (GeneratorCharacteristics) it.next();
			
			writercsv.write(caracteristica.getModelName());
			writercsv.write(String.valueOf(caracteristica.getMaxBranchingFactor()));
			writercsv.write(String.valueOf(caracteristica.getNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica.getProbabilityMandatory()));
			writercsv.write(String.valueOf(caracteristica.getProbabilityOptional()));
			writercsv.write(String.valueOf(caracteristica.getProbabilityOr()));
			writercsv.write(String.valueOf(caracteristica.getProbabilityAlternative()));
			writercsv.write(String.valueOf(caracteristica.getPercentageCTC()));
			writercsv.write(String.valueOf(caracteristica.getSeed()));
			writercsv.endRecord();

		}
		writercsv.close();
	}

	private void writeHeader(CsvWriter writercsv,
			Collection<RandomExperiment> col) throws IOException {
			writercsv.write("Experiment name");
			writercsv.write("BanchingFactor");
			writercsv.write("numberOfFeatures");
			writercsv.write("probabilityMandatory");
			writercsv.write("probabilityOptional");
			writercsv.write("probabilityOr");
			writercsv.write("probabilityAlternative");
			writercsv.write("percentageCTC");
			writercsv.write("seed");
			writercsv.write("Results");

		getindice(col, true);

		writercsv.endRecord();
	}

}
