package es.us.isa.FAMA.Benchmarking;

/**
 * @author  Dani
 */
public class Characteristics {
	/**
	 * @uml.property  name="width"
	 */
	private int width;
	/**
	 * @uml.property  name="height"
	 */
	private int height;
	/**
	 * @uml.property  name="choose"
	 */
	private int choose;
	/**
	 * @uml.property  name="dependencies"
	 */
	private int dependencies;
	/**
	 * @uml.property  name="numberOfInstances"
	 */
	private int numberOfInstances;
	/**
	 * @uml.property  name="numberOfFeatures"
	 */
	private int numberOfFeatures;
	private int seed;

	public Characteristics(int width, int height, int choose, int dependencies, int numberOfInstances,int numberOfFeatures) {
		this(width, height,choose,dependencies,numberOfInstances,numberOfFeatures,-1);
	}
	
	public Characteristics(int width, int height, int choose, int dependencies, int numberOfInstances,int numberOfFeatures, int seed) {
		this.width = width;
		this.height = height;
		this.choose = choose;
		this.dependencies = dependencies;
		this.numberOfInstances = numberOfInstances;
		this.numberOfFeatures = numberOfFeatures;
		this.seed = seed;
	}
	/**
	 * @return  Returns the choose.
	 * @uml.property  name="choose"
	 */
	public int getChoose() {
		return choose;
	}
	/**
	 * @param choose  The choose to set.
	 * @uml.property  name="choose"
	 */
	public void setChoose(int choose) {
		this.choose = choose;
	}
	/**
	 * @return  Returns the dependencies.
	 * @uml.property  name="dependencies"
	 */
	public int getDependencies() {
		return dependencies;
	}
	/**
	 * @param dependencies  The dependencies to set.
	 * @uml.property  name="dependencies"
	 */
	public void setDependencies(int dependencies) {
		this.dependencies = dependencies;
	}
	/**
	 * @return  Returns the height.
	 * @uml.property  name="height"
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height  The height to set.
	 * @uml.property  name="height"
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return  Returns the numberOfInstances.
	 * @uml.property  name="numberOfInstances"
	 */
	public int getNumberOfInstances() {
		return numberOfInstances;
	}
	/**
	 * @param numberOfInstances  The numberOfInstances to set.
	 * @uml.property  name="numberOfInstances"
	 */
	public void setNumberOfInstances(int numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}
	/**
	 * @return  Returns the width.
	 * @uml.property  name="width"
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width  The width to set.
	 * @uml.property  name="width"
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * @return  Returns the numberOfFeatures.
	 * @uml.property  name="numberOfFeatures"
	 */
	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}
	/**
	 * @param numberOfFeatures  The numberOfFeatures to set.
	 * @uml.property  name="numberOfFeatures"
	 */
	public void setNumberOfFeatures(int numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}
	
	public String toString () {
		String res = "Width: " + width + " ;\t";
		res += "Height: " + height + " ;\t";
		res += "#Choose: " + choose + " ;\t";
		res += "#Dependencies: " + dependencies + " ;\t";
		res += "#Instances: " + numberOfInstances + " ;\t";
		res += "#Features: " + numberOfFeatures;
		return res;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}
}
