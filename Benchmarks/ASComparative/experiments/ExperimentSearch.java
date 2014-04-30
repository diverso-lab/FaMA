package experiments;

import featureModel.FeatureModel;
import generators.CNFFeatureModelGenerator;

import java.util.Random;

public class ExperimentSearch {

	private int nexp; // Number of experiment to find
	private int minfeatures; // Minimum number of features in each experiment
	private int maxfeatures; // Maximum number of features in each experiment

	public ExperimentSearch(int nexp, int minfeatures, int maxfeatures) {
		this.nexp = nexp;
		this.minfeatures = minfeatures;
		this.maxfeatures= maxfeatures;
	}

	// Search nexp with nfeatures and save it in CSV format in path
	public void search(String path) {

		Experiment exp = null;
		ExperimentSaver exps = new ExperimentSaver();
		CNFFeatureModelGenerator fmg = null;
		FeatureModel fm = null;
		int numexp = 0;
		int w, h, e, seed;

		while (numexp < nexp) {
			seed = new Random().nextInt();
			for (w = 3; w <= 5; w++)
				for (h = 3; h <= 5; h++)
					for (e = 2; e <= 5; e++) {
						fmg = new CNFFeatureModelGenerator();
						fmg.setSeed(seed);
						fmg.generateFeatureModel(w, h, e, 0);
						fm = fmg.getFm().getFeatureModel();
						if (fm.getFeaturesNumber() >= this.minfeatures && fm.getFeaturesNumber()<=this.maxfeatures) {
							System.out.println(numexp + ": Experiment found!");
							exp = new Experiment();
							exp.setW(w);
							exp.setH(h);
							exp.setE(e);
							exp.setFeatureNumber(fm.getFeaturesNumber());
							exp.setGeneratorSeed(seed);
							exps.setExperiment(exp);
							exps.saveCSV(path);
							numexp++;
						}
					}
		}

	}

}
