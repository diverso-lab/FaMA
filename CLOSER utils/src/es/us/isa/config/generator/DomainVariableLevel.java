package es.us.isa.config.generator;

import java.util.Random;

public class DomainVariableLevel {

	private int min;
	private int max;
	
	public DomainVariableLevel(int min, int max) {
		super();
		this.min = min;
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
	public int getRandomValue(){
		int range = max - min;
		Random r = new Random();
		int val = r.nextInt(range);
		int result = min + val;
		return result;
	}
	
}
