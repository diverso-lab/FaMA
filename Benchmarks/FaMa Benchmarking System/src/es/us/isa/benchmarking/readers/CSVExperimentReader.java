package es.us.isa.benchmarking.readers;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.FMCharacteristics;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.RandomExperiment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.csvreader.CsvReader;

public class CSVExperimentReader implements IExperimentReader {

	@Override
	public Collection<ICharacteristics> read(String patch) throws IOException, FAMAParameterException {
		File fichero = new File(patch);
		FileReader freader = new FileReader(fichero);
		CsvReader reader = new CsvReader(freader, ';');
	//	String experimentName ="experimentName";
		Collection<ICharacteristics> col = new ArrayList<ICharacteristics>();
		if (reader.readHeaders()) {
			String[] headers = reader.getHeaders();
			System.out.println("------- CABECERAS DEL FICHERO ------------");
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
		//	experimentName = headers[0];
			System.out.println("-----------------------------------------------------");
		}
		ICharacteristics caracteristica = null;
		while(reader.readRecord()) {
			
			String width = reader.get("width");
			String height = reader.get("height");
			String choose = reader.get("choose");
			String numberOfDependencies = reader.get("numberOfDependencies");
			String percentageOfDependencies = reader.get("percentageOfDependencies");
			String numberOfFeatures = reader.get("numberOfFeatures");
			String minNumberOfFeatures = reader.get("minNumberOfFeatures");
			String maxNumberOfFeatures = reader.get("maxNumberOfFeatures");
			String seed = reader.get("seed");
			
			//realizar un preprocesamiento de los parametros
			//1) percentageOfDependencies solo se usara si no tenemos numberOfDependencies
			//2) minNumberOfFeatures y maxNumberOfFeatures solo se usaran si
			// no tenemos numberOfFeatures
						
			int widthValue=Integer.valueOf(width);
			int heightValue=Integer.valueOf(height);
			int chooseValue=Integer.valueOf(choose);
			int numberOfDependenciesValue=Integer.valueOf(numberOfDependencies);
			float percentageOfDependenciesValue=Float.valueOf(percentageOfDependencies);
			int numberOfFeaturesValue=Integer.valueOf(numberOfFeatures);
			int minNumberOfFeaturesValue=Integer.valueOf(minNumberOfFeatures);
			int maxNumberOfFeaturesValue=Integer.valueOf(maxNumberOfFeatures);
			int seedValue=Integer.valueOf(seed);
			
			//features
			if ((maxNumberOfFeaturesValue == -1 || minNumberOfFeaturesValue == -1) && numberOfFeaturesValue == -1){
				System.err.println("Warning. You have not written a number of features " +
						"(or an interval) on "+patch+". You cannot use FixedFeaturesRandomGenerator");
			}

			
			//dependencies
			//en que momento conozco cuantas features voy a tener en el modelo?
			if(numberOfDependenciesValue == -1 && percentageOfDependenciesValue == -1){
				throw new FAMAParameterException("You must specify how many dependencies you want " +
						"at "+patch);
			}

			
			caracteristica=new FMCharacteristics(widthValue,heightValue,chooseValue,numberOfDependenciesValue,
					percentageOfDependenciesValue,numberOfFeaturesValue,minNumberOfFeaturesValue,
					maxNumberOfFeaturesValue, seedValue);
			//Experiment exp = new RandomExperiment(experimentName, caracteristica);
			col.add(caracteristica);
		}
		
		return col;
	}

	

}
