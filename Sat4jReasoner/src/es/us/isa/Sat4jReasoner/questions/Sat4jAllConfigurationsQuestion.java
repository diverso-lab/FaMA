/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.Sat4jReasoner.questions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;

public class Sat4jAllConfigurationsQuestion extends Sat4jQuestion {

	private String outputDirectory;

	String cnfmodel;
	String[] features, cnfvalues, configurationKeys;
	ISolver solver;
	Reader reader;
	int[] array;
	IVecInt assump;

	public Sat4jAllConfigurationsQuestion(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void preAnswer(Reasoner r) {
		// Create CNF file
		super.preAnswer(r);
	}

	public PerformanceResult answer(Reasoner r) {
		int iteration = 0;
		if (r == null) {
			throw new FAMAParameterException("Reasoner :Not specified");
		}

		features = new String[((Sat4jReasoner) r).getVariables().size()];
		cnfvalues = new String[((Sat4jReasoner) r).getVariables().size()];

		int i = 0;
		for (Entry<String, String> entry : ((Sat4jReasoner) r).getVariables().entrySet()) {
			features[i] = entry.getKey();
			cnfvalues[i] = entry.getValue();
			i++;
		}

		// prepare the reasoning mechanism
		String cnfmodel = ((Sat4jReasoner) r).getPartialCNF(1);
		solver = SolverFactory.newDefault();
		ModelIterator mi = new ModelIterator(solver);
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(mi);

		try {
			reader.parseInstance(new ByteArrayInputStream((cnfmodel).getBytes(StandardCharsets.UTF_8)));

			// First iteration
			PrintWriter out = new PrintWriter(outputDirectory + "/" + iteration + ".out");
			for (i = 0; i < features.length; i++) {
				if (isValidConf(i + 1)) {
					out.println((i + 1) + "\t" + features[i]);
				}
			}
			out.flush();
			out.close();

			// whatever it remains
			File file = new File(outputDirectory + "/" + iteration + ".out");
			while (file.length() > 0) {
				iteration++;

				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = br.readLine()) != null) {
						// process the line.

						String key = line.substring(0, line.indexOf('\t'));
						String value = line.substring(line.indexOf('\t') + 1, line.length());
						map(key, value, iteration);

					}
				}

				file = new File(outputDirectory + "/" + iteration + ".out");
			}

		} catch (ParseFormatException | ContradictionException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isValidConf(int i) {
		boolean res = false;
		int[] conf = new int[1];
		conf[0] = i;
		try {
			res = solver.isSatisfiable(new VecInt(conf));
			return res;
		} catch (IllegalArgumentException | TimeoutException e) {
			throw new IllegalStateException(i + "");
		}

	}

	private boolean isValidConfiguration(int configuration) {

		boolean res = false;

		// check the validity
		try {
			assump.push(configuration);
			res = solver.isSatisfiable(assump);
			assump.remove(configuration);

			return res;
		} catch (IllegalArgumentException | TimeoutException e) {
			assump.remove(configuration);
			throw new IllegalStateException(cnfmodel + "\n" + assump.toString() + " size:" + assump.size());
		}

	}

	private boolean contains(int[] array2, int i) {
		for (int a : array) {
			if (a == i) {
				return true;
			}
		}
		return false;
	}

	public void map(String key, String value, Integer iteration) {
		PrintWriter out;
		try {

			out = new PrintWriter(new FileOutputStream(
				    new File(outputDirectory + "/" + iteration + ".out"), 
				    true)); 
			
			// Split hold an array with the keys of each configuration
			if (key.trim().contains("-")) {
				configurationKeys = key.toString().trim().split("-");
			} else {
				configurationKeys = new String[1];
				configurationKeys[0] = key.toString().trim();
			}

			array = new int[configurationKeys.length];
			for (int i = 0; i < configurationKeys.length; i++) {
				array[i] = Integer.parseInt(configurationKeys[i]);
			}

			assump = new VecInt(array);

			// get the last int in keys to start over
			for (int i = array[configurationKeys.length - 1] + 1; i < features.length; i++) {
				if (!contains(array, i)) {
					if (isValidConfiguration(i)) {
						out.println(key + "-" + (i) + "\t" + value + " " + features[i]);

					}
				}
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
