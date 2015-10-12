package generators;

public interface IFeatureModelGenerator {

	public static final int RELATIONS = 4;
	public static final int MANDATORY = 0;
	public static final int OPTIONAL = 1;
	public static final int OR = 2;
	public static final int ALTERNATIVE = 3;
	
	public void generateFeatureModel(int w, int h, int ch, int d);
	public long getSeed();
	public void setSeed(long seed);

}
