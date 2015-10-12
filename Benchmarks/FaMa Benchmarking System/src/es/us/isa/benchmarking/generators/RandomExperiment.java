package es.us.isa.benchmarking.generators;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class RandomExperiment extends Experiment{

	private ICharacteristics ch;
	
	public RandomExperiment(String n, VariabilityModel model, ICharacteristics c) {
		super(n, model);
		ch = c;
	}

	public RandomExperiment(String experimentName,
			ICharacteristics caracteristica) {
		super(experimentName);
		ch = caracteristica;
	}
	
	public RandomExperiment(){
		super();
	}

	public ICharacteristics getCharacteristics() {
		return ch;
	}

	public void setCharacteristics(ICharacteristics ch) {
		this.ch = ch;
	}

	
}

