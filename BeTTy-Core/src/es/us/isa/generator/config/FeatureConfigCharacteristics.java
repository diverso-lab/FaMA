package es.us.isa.generator.config;

import java.util.List;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.generator.config.enums.FeatureState;
import es.us.isa.generator.config.enums.RelType;

public class FeatureConfigCharacteristics {

	/**
	 * List of features which can be configured. An empty list means the whole model
	 */
	private List<GenericFeature> area;
	
	/**
	 * Minimal number of features to be configured
	 */
	private int min;
	
	/**
	 * Maximal number of features to be configured
	 */
	private int max;
	
	/**
	 * States we are going to use to configure the features
	 */
	private List<FeatureState> feasibleStates;
	
	/**
	 * Type of features we can configure
	 */
	private List<RelType> relationshipTypes;
	
	public List<GenericFeature> getArea() {
		return area;
	}
	public void setArea(List<GenericFeature> area) {
		this.area = area;
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
	public List<FeatureState> getFeasibleStates() {
		return feasibleStates;
	}
	public void setFeasibleStates(List<FeatureState> feasibleStates) {
		this.feasibleStates = feasibleStates;
	}
	public List<RelType> getRelationshipTypes() {
		return relationshipTypes;
	}
	public void setRelationshipTypes(List<RelType> relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
	}
	
	
	
}
