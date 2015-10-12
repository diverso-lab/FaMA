package experiments;

public class Experiment {
	
	private String name; // Experiment name
	private int w; // maximun number of children relations for a node
	private int h; // maximun heighth of the tree
	private int ch; //maximun number for the cardinality
	private int d; // number of dependencies
	private int e; // maximun number of elements of a set relation
	private int featureNumber; // Number of features
	private long generatorSeed; // seed for the generation
	private long flushSeed;//seed for the flush of the variable ordering
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}
	public int getCh() {
		return ch;
	}
	public void setCh(int ch) {
		this.ch = ch;
	}
	public int getD() {
		return d;
	}
	public void setD(int d) {
		this.d = d;
	}
	public int getE() {
		return e;
	}
	public int getFeatureNumber() {
		return this.featureNumber;
	}
	public void setFeatureNumber(int fn) {
		this.featureNumber=fn;
	}
	public void setE(int e) {
		this.e = e;
	}
	public long getFlushSeed() {
		return flushSeed;
	}
	public void setFlushSeed(long flushSeed) {
		this.flushSeed = flushSeed;
	}
	public long getGeneratorSeed() {
		return generatorSeed;
	}
	public void setGeneratorSeed(long seed) {
		this.generatorSeed = seed;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}

}
