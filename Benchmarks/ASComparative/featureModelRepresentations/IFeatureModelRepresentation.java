package featureModelRepresentations;

import featureModel.FeatureModel;

public interface IFeatureModelRepresentation {
	
	public abstract String getName();
	public abstract void setName(String name);
	public void setSeed(long seed);
	public long getSeed();
}
