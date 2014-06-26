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
		Random r = new Random(System.currentTimeMillis());
		int size = values.size();
		int index;
		if (size > 2){
			index = r.nextInt(values.size());
		}
		else if (size == 2){
			// trying to avoid random boolean bug
			int aux = r.nextInt(100);
			if (aux < 50){
				index = 0;
			}
			else{
				index = 1;
			}
		}
		else{
			index = 0;
		}
		
		return values.get(index);
	}
	
}
