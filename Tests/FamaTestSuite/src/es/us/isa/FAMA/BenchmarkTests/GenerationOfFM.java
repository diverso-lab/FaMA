package es.us.isa.FAMA.BenchmarkTests;

import es.us.isa.FAMA.Reasoner.QuestionTrader;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.generators.ExactRandomGenerator;
import es.us.isa.benchmarking.generators.FMCharacteristics;

public class GenerationOfFM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		int noOfFeats[]= { 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 10000 };
		int noOfFeats[]= { 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000};
		int noOfDependenciesAsInt[]={0,10,25};
		float noOfDependencies[]={0.0f,0.10f,0.25f};
		QuestionTrader qt=new QuestionTrader();
		ExactRandomGenerator gen = new ExactRandomGenerator();
		gen.setPercentageOfAlternate(0.25f);
		gen.setPercentageOfMandatories(0.25f);
		gen.setPercentageOfOptionals(0.25f);
		gen.setPercentageOfOrs(0.25f);
		
		for (int i = 0;i<noOfFeats.length;i++){
			for(int j=0;j<noOfDependencies.length;j++){
				for(int v=0;v<10;v++){
					FMCharacteristics caracteristicas = new FMCharacteristics();
					caracteristicas.setChoose(10);
					caracteristicas.setNumberOfFeatures(noOfFeats[i]);
					caracteristicas.setPercentageOfDependencies(noOfDependencies[j]);
					caracteristicas.setSeed(-158546+v);
					VariabilityModel fm=gen.generate(caracteristicas);
					String str="./tests/"+noOfFeats[i]+"-"+noOfDependenciesAsInt[j]+"-"+v+".xml";
					qt.writeFile(str, fm);
				}
			}
		}
		
		
		
		
	}

}
