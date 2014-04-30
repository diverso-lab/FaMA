package es.us.isa.FAMA.Reasoner.questions;

import java.util.Collection;
import java.util.List;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.soup.preferences.Preference;

public interface OptimisingPreferencesQuestion extends Question {

	public List<ExtendedConfiguration> getRankedConfigurations();
	
	public void setConfiguration(ExtendedConfiguration config);
	
	public void setPreferences(Collection<Preference> prefs);
	
}
