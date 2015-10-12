package experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.Ostermiller.util.CSVParser;
import com.Ostermiller.util.ExcelCSVParser;

public class ExperimentReader {

	public ArrayList<Experiment> readExperiments(String path) {
		ArrayList<Experiment> expList = new ArrayList<Experiment>();

		FileInputStream inputFile;
		try {
			inputFile = new FileInputStream(path);

			ExcelCSVParser parser = new ExcelCSVParser(inputFile);
			String[][] values = parser.getAllValues();

			// Save the data as an ArrayList of Experiments
			for (int i = 0; i < values.length; i++) {
				Experiment exp = new Experiment();
				for (int j = 0; j < values[i].length; j++) {

					switch (j) {
					case 0:
						exp.setGeneratorSeed(Integer.parseInt(values[i][j]));
						break;
					case 1:
						exp.setW(Integer.parseInt(values[i][j]));
						break;
					case 2:
						exp.setH(Integer.parseInt(values[i][j]));
						break;
					case 3:
						exp.setE(Integer.parseInt(values[i][j]));
						break;
					case 4:
						exp.setD(Integer.parseInt(values[i][j]));
						break;
					case 5:
						exp.setFeatureNumber(Integer.parseInt(values[i][j]));
						break;
					default:
						break;
					}
				}
				expList.add(exp);
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: No se encontró el fichero CSV en "
					+ path);
		} catch (IOException e) {
			System.out
					.println("ERROR: Ocurrió un error mientras se leía el fichero "
							+ path + ". Message: " + e.getMessage());
		}

		return expList;

	}

}
