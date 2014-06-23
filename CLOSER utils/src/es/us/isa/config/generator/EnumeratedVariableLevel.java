package es.us.isa.config.generator;

import java.util.List;
import java.util.Random;

public class EnumeratedVariableLevel {

	List<String> values;

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public String getRandomValue(){
		Random r = new Random();
		int index = r.nextInt(values.size());
		return values.get(index);
	}
	
}
