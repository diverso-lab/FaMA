package es.us.isa.fama.experiments.generators;

import java.util.Collection;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.soup.preferences.Preference;

public abstract class PreferencesGenerator {

	protected FAMAAttributedFeatureModel fm;

	public FAMAAttributedFeatureModel getFm() {
		return fm;
	}

	public void setFm(FAMAAttributedFeatureModel fm) {
		this.fm = fm;
	}
	
	public abstract Collection<Preference> generatePreferences(int n);
	
	public Collection<Preference> generatePreferences(Collection<Preference> prefs, int n){
		//XXX default implementation. subclasses may refine it
		return this.generatePreferences(n);
	}
	
	public abstract int getMaxNumberOfPreferences();
	
}
