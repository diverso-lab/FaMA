package es.us.isa.benchmarking.generators;

import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public interface IRandomVMGenerator {
	
	public VariabilityModel generate(ICharacteristics c);
}
