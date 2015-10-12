package es.us.isa.benchmarking.generators;

import java.util.Map;

public interface ICharacteristics {

	public Map<String,Object> getCharacteristicsMap();
	
	public String toString();

	public Map<String, String> getCharacteristicsAsString();
	
}
