package es.us.isa.benchmarking.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.csvreader.CsvWriter;

import es.us.isa.benchmarking.generators.Experiment;
import es.us.isa.benchmarking.generators.FMCharacteristics;
import es.us.isa.benchmarking.generators.ICharacteristics;
import es.us.isa.benchmarking.generators.RandomExperiment;

public class CSVExperimentWriter implements IExperimentWriter {
	
	public void save(Collection<Experiment> col, String path)
			throws IOException {

		ArrayList<String> indice = new ArrayList<String>();
		Map<String, String> allExps = new HashMap<String, String>();

		int counter = 1;
		File fichero = new File(path);
		FileWriter fwriter = new FileWriter(fichero);
		CsvWriter writercsv = new CsvWriter(fwriter, ';');

		writercsv.write("Experiment name");
		writercsv.write("width");
		writercsv.write("height");
		writercsv.write("choose");
		writercsv.write("numberOfDependencies");
		writercsv.write("percentageOfDependencies");
		writercsv.write("numberOfFeatures");
		writercsv.write("minNumberOfFeatures");
		writercsv.write("maxNumberOfFeatures");
		writercsv.write("seed");
		writercsv.write("Results");

		// At first we need to read all experiments to know where to write
		// the results
		
		
		Iterator<Experiment> it = col.iterator();
		while (it.hasNext()) {
			RandomExperiment exp = (RandomExperiment) it.next();
			Collection<Map<String, String>> results = exp.getResults();
			Iterator<Map<String, String>> it2 = results.iterator();
			while (it2.hasNext()) {
				Map<String, String> mapa = it2.next();
				Iterator<Entry<String, String>> it3 = mapa.entrySet().iterator();
				while (it3.hasNext()) {
					Entry<String, String> entry = it3.next();
					if (!indice.contains(entry.getKey())) {
						indice.add(entry.getKey());
						allExps.put(entry.getKey(), entry.getValue());
						writercsv.write(entry.getKey());
						counter++;

					}
				}
			}
		}
		
		writercsv.endRecord();
		it=col.iterator();
		while(it.hasNext()){
			RandomExperiment exp = (RandomExperiment) it.next();
			Collection<Map<String, String>> results = exp.getResults();
			FMCharacteristics caracteristica = (FMCharacteristics) exp.getCharacteristics();
			//write the characteristics
			writercsv.write(exp.getName());
			writercsv.write(String.valueOf(caracteristica.getWidth()));
			writercsv.write(String.valueOf(caracteristica.getHeight()));
			writercsv.write(String.valueOf(caracteristica.getChoose()));
			writercsv.write(String.valueOf(caracteristica.getNumberOfDependencies()));
			writercsv.write(String.valueOf(caracteristica.getPercentageOfDependencies()));
			writercsv.write(String.valueOf(caracteristica.getNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica.getMinNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica.getMaxNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica.getSeed()));
			writercsv.write(String.valueOf(""));
			
			/*boolean[] beOrNotBe = new boolean[indice.size()];//por defecto se inicializa a false;
			Iterator<Map<String,String>> resultsIterator = results.iterator();		
			while(resultsIterator.hasNext()){
				Map mapa2=(Map) resultsIterator.next();
				Iterator mapIterator=mapa2.keySet().iterator();
				while(mapIterator.hasNext()){
					 String ent=(String) mapIterator.next();
					 beOrNotBe[indice.indexOf(ent)]=true;
				}
				
			}
			for(int i=0;i<indice.size();i++){
				if(beOrNotBe[i]){
					writercsv.write(allExps.get(indice.get(i)));
				}
			}*/
			
			Iterator<String> itInd = indice.iterator();
			while (itInd.hasNext()){
				String aux = itInd.next();
				Iterator<Map<String,String>> resultsIterator = results.iterator();
				String param = null;
				while (resultsIterator.hasNext() && param == null){
					Map<String,String> mapAux = resultsIterator.next();
					param = mapAux.get(aux);
					if (param != null){
						writercsv.write(param);
					}
				}
				if (param == null){
					writercsv.write("");
				}
			}
			
			
			writercsv.endRecord();


		}
		writercsv.close();
	}
	
		
			
			

	@Override
	public void saveCharacteristics(Collection<ICharacteristics> col,
			String path) throws IOException {
		File fichero = new File(path);
		FileWriter fwriter = new FileWriter(fichero);
		CsvWriter writercsv = new CsvWriter(fwriter, ';');

		writercsv.write("width");
		writercsv.write("height");
		writercsv.write("choose");
		writercsv.write("numberOfDependencies");
		writercsv.write("percentageOfDependencies");
		writercsv.write("numberOfFeatures");
		writercsv.write("minNumberOfFeatures");
		writercsv.write("maxNumberOfFeatures");
		writercsv.write("seed");
		writercsv.endRecord();

		Iterator<ICharacteristics> it = col.iterator();
		while (it.hasNext()) {

			FMCharacteristics caracteristica = (FMCharacteristics) it.next();

			writercsv.write(String.valueOf(caracteristica.getWidth()));
			writercsv.write(String.valueOf(caracteristica.getHeight()));
			writercsv.write(String.valueOf(caracteristica.getChoose()));
			writercsv.write(String.valueOf(caracteristica
					.getNumberOfDependencies()));
			writercsv.write(String.valueOf(caracteristica
					.getPercentageOfDependencies()));
			writercsv.write(String
					.valueOf(caracteristica.getNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica
					.getMinNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica
					.getMaxNumberOfFeatures()));
			writercsv.write(String.valueOf(caracteristica.getSeed()));

			writercsv.endRecord();

		}
		writercsv.close();
	}
	

}
