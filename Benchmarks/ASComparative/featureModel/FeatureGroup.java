package featureModel;

import java.util.ArrayList;
import java.util.Collection;

public class FeatureGroup {

	private int cardMin;

	private int cardMax;

	private Collection features;
	
	public FeatureGroup()
	{
		features = new ArrayList();
		
	}

	public int getCardMax() {
		return cardMax;
	}

	public void setCardMax(int cardMax) throws IllegalArgumentException{
		 if(cardMax > features.size()){
			 throw new IllegalArgumentException("Setting an unknown feature relation type");
		 }else{
			 this.cardMax = cardMax;
		 }

	}

	public int getCardMin() {
		return cardMin;
	}

	public void setCardMin(int cardMin) throws IllegalArgumentException{
		 if(cardMax > features.size()){
			 throw new IllegalArgumentException("Setting an unknown feature relation type");
		 }else{
			 this.cardMax = cardMin;
		 }

	}

	public Collection getFeatures() {
		return features;
	}

	public void setFeatures(Collection features) {
		this.features = features;
	}
	
	public void addFeature(FeatureNode feature) {
		features.add(feature);
	}


}
