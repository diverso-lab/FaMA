package es.us.isa.benchmarking.generators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class FMCharacteristics implements ICharacteristics{
	
	private int width;
	private int height;
	private int choose;
	private int numberOfDependencies;
	private float percentageOfDependencies;
	private int numberOfFeatures;
	private int minNumberOfFeatures;
	private int maxNumberOfFeatures;
	private int seed;

	/**
	 * El generador del metodo, devuelve una nueva caracteristica contodos sus parametros
	 * @param width
	 * @param height
	 * @param choose
	 * @param numberOfDependencies
	 * @param percentageOfDependencies
	 * @param numberOfFeatures
	 * @param minNumberOfFeatures
	 * @param maxNumberOfFeatures
	 * @param seed
	 * @param experimentColection
	 */
	public FMCharacteristics(int width, int height, int choose,
			int numberOfDependencies, float percentageOfDependencies,
			int numberOfFeatures, int minNumberOfFeatures,
			int maxNumberOfFeatures, int seed) {
		this.width = width;
		this.height = height;
		this.choose = choose;
		this.numberOfDependencies = numberOfDependencies;
		this.percentageOfDependencies = percentageOfDependencies;
		this.numberOfFeatures = numberOfFeatures;
		this.minNumberOfFeatures = minNumberOfFeatures;
		this.maxNumberOfFeatures = maxNumberOfFeatures;
		this.seed = seed;
	}
	
	public FMCharacteristics(){
		Random generator = new Random();
		this.width = generator.nextInt();
		this.height = generator.nextInt();
		this.choose = generator.nextInt();
		this.numberOfDependencies = generator.nextInt();
		this.percentageOfDependencies = generator.nextLong();
		this.numberOfFeatures = generator.nextInt();
		this.minNumberOfFeatures = generator.nextInt();
		this.maxNumberOfFeatures = generator.nextInt();
		this.seed = generator.nextInt();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getChoose() {
		return choose;
	}

	public void setChoose(int choose) {
		this.choose = choose;
	}

	public int getNumberOfDependencies() {
		return numberOfDependencies;
	}

	public void setNumberOfDependencies(int numberOfDependencies) {
		this.numberOfDependencies = numberOfDependencies;
	}

	public float getPercentageOfDependencies() {
		return percentageOfDependencies;
	}

	public void setPercentageOfDependencies(float percentageOfDependencies) {
		this.percentageOfDependencies = percentageOfDependencies;
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public void setNumberOfFeatures(int numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}

	public int getMinNumberOfFeatures() {
		return minNumberOfFeatures;
	}

	public void setMinNumberOfFeatures(int minNumberOfFeatures) {
		this.minNumberOfFeatures = minNumberOfFeatures;
	}

	public int getMaxNumberOfFeatures() {
		return maxNumberOfFeatures;
	}

	public void setMaxNumberOfFeatures(int maxNumberOfFeatures) {
		this.maxNumberOfFeatures = maxNumberOfFeatures;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * Devuelve un map con clave valor String,String, 
	 * con este método se hará mas cómodo la lectura escritura en ficheros
	 */
	@Override
	public Map<String, String> getCharacteristicsAsString() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("width",String.valueOf(this.width));
		map.put("height",String.valueOf(this.height));
		map.put("numberOfFeatures",String.valueOf(this.numberOfFeatures));
		map.put("maxNumberOfFeatures",String.valueOf(this.maxNumberOfFeatures));
		map.put("minNumberOfFeatures",String.valueOf(this.minNumberOfFeatures));
		map.put("seed",String.valueOf(this.seed));
		map.put("choose",String.valueOf(this.choose));
		map.put("numberOfDependencies",String.valueOf(this.numberOfDependencies));
		return map;

	}
	/**
	 * Este metodo devuelve un Map asociando la caracteristica a su valor
	 */
	public Map<String, Object> getCharacteristicsMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("width",this.width);
		map.put("height",this.height);
		map.put("numberOfFeatures",this.numberOfFeatures);
		map.put("maxNumberOfFeatures",this.maxNumberOfFeatures);
		map.put("minNumberOfFeatures",this.minNumberOfFeatures);
		map.put("seed",this.seed);
		map.put("choose",this.choose);
		map.put("numberOfDependencies",this.numberOfDependencies);
		return map;

	}
	public String toString(){
		String str="";
		Iterator<Entry<String,String>> it = getCharacteristicsAsString().entrySet().iterator();
		while (it.hasNext()){
			Entry<String,String> es = it.next();
			str.concat(es.getKey()+es.getValue()+"/n");
		}
		return str;
		
	}

}
