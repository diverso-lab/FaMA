package es.us.isa.fama.experiments.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;
import es.us.isa.soup.preferences.User;
import es.us.isa.utils.FMUtils;

public class DefaultPreferencesSelector extends PreferencesGenerator {

	private Map<String,List<Preference>> defaultPreferences;
	
	public DefaultPreferencesSelector(){
		super();
	}
	
	public DefaultPreferencesSelector(
			Map<String, List<Preference>> defaultPreferences) {
		super();
		this.defaultPreferences = defaultPreferences;
	}

	/**
	 * This method returns a random collection of 'n' preferences,
	 * based on the default preferences stored in the instance.
	 */
	@Override
	public Collection<Preference> generatePreferences(int n) {
		Map<String,List<Preference>> copy = 
				new HashMap<String, List<Preference>>(defaultPreferences);
		List<List<Preference>> auxList = new ArrayList<List<Preference>>(copy.values());
		Collection<Preference> result = new LinkedList<Preference>();
		Random randomGenerator = new Random(System.nanoTime());
		for (int i = 0; i < n; i++){
			int index1 = randomGenerator.nextInt(auxList.size());
			List<Preference> preferencesList =  auxList.get(index1);
			int index2 = randomGenerator.nextInt(preferencesList.size());
			Preference p = preferencesList.get(index2);
			result.add(p);
			//we ensure that we dont include conflicting preferences for the same user
			auxList.remove(index1);
		}
		return result;
	}

	public Map<String, List<Preference>> getDefaultPreferences() {
		return defaultPreferences;
	}

	public void setDefaultPreferences(
			Map<String, List<Preference>> defaultPreferences) {
		this.defaultPreferences = defaultPreferences;
	}
	
	/**
	 * Method to populate default preferences for
	 * the DaaS scenario.
	 */
	public void populateDefaultPreferences(){
		defaultPreferences = new HashMap<String, List<Preference>>();
		List<Preference> auxList;
		GenericFeature f;
		GenericAttribute att;
		
		//Look & Feel
		auxList = new LinkedList<Preference>();
		Preference p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		Preference p2 = new LikesPreference(fm.searchFeatureByName("Classic"));
		auxList.add(p1);
		auxList.add(p2);
		defaultPreferences.put("LookAndFeel", auxList);
		
		//OfficeUpdt
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("OfficeUpdt");
		att = FMUtils.searchAttribute(fm, "OfficeUpdt.period");
		Preference p3 = new LikesPreference(f);
		Preference p4 = new DislikesPreference(f);
		Preference p5 = new HighestPreference(att);
		Preference p6 = new LowestPreference(att);
		Preference p7 = new AroundPreference(att, 2.0);
		auxList.add(p3);
		auxList.add(p4);
		auxList.add(p5);
		auxList.add(p6);
		auxList.add(p7);
		defaultPreferences.put("OfficeUpdt", auxList);
		
		//EclipseUpdt
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("EclipseUpdt");
		att = FMUtils.searchAttribute(fm, "EclipseUpdt.period");
		Preference p8 = new LikesPreference(f);
		Preference p9 = new DislikesPreference(f);
		Preference p10 = new HighestPreference(att);
		Preference p11 = new LowestPreference(att);
		Preference p12 = new AroundPreference(att, 2.0);
		auxList.add(p8);
		auxList.add(p9);
		auxList.add(p10);
		auxList.add(p11);
		auxList.add(p12);
		defaultPreferences.put("EclipseUpdt", auxList);
		
		//LatextUpdt
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("LatexUpdt");
		att = FMUtils.searchAttribute(fm, "LatexUpdt.period");
		Preference p13 = new LikesPreference(f);
		Preference p14 = new DislikesPreference(f);
		Preference p15 = new HighestPreference(att);
		Preference p16 = new LowestPreference(att);
		Preference p17 = new AroundPreference(att, 2.0);
		auxList.add(p13);
		auxList.add(p14);
		auxList.add(p15);
		auxList.add(p16);
		auxList.add(p17);
		defaultPreferences.put("LatextUpdt", auxList);
		
		//OSUpdates
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("OSUpdates");
		att = FMUtils.searchAttribute(fm, "OSUpdates.period");
		Preference p18 = new LikesPreference(f);
		Preference p19 = new DislikesPreference(f);
		Preference p20 = new HighestPreference(att);
		Preference p21 = new LowestPreference(att);
		Preference p22 = new AroundPreference(att, 2.0);
		auxList.add(p18);
		auxList.add(p19);
		auxList.add(p20);
		auxList.add(p21);
		auxList.add(p22);
		defaultPreferences.put("OSUpdates", auxList);
		
		//Antivirus
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("Antivirus");
		att = FMUtils.searchAttribute(fm, "Antivirus.frequency");
		Preference p23 = new LikesPreference(f);
		Preference p24 = new DislikesPreference(f);
		Preference p25 = new HighestPreference(att);
		Preference p26 = new LowestPreference(att);
		Preference p27 = new AroundPreference(att, 4.0);
		Preference p28 = new AroundPreference(att, 2.0);
		Preference p29 = new AroundPreference(att, 6.0);
		auxList.add(p23);
		auxList.add(p24);
		auxList.add(p25);
		auxList.add(p26);
		auxList.add(p27);
		auxList.add(p28);
		auxList.add(p29);
		defaultPreferences.put("Antivirus", auxList);
		
		//Firewall
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("Firewall");
		att = FMUtils.searchAttribute(fm, "Firewall.level");
		Preference p30 = new LikesPreference(f);
		Preference p31 = new DislikesPreference(f);
		Preference p32 = new HighestPreference(att);
		Preference p33 = new LowestPreference(att);
		Preference p34 = new AroundPreference(att, 2.0);
		Preference p35 = new AroundPreference(att, 3.0);
		auxList.add(p30);
		auxList.add(p31);
		auxList.add(p32);
		auxList.add(p33);
		auxList.add(p34);
		auxList.add(p35);
		defaultPreferences.put("Firewall", auxList);
		
		//Backup
		auxList = new LinkedList<Preference>();
		att = FMUtils.searchAttribute(fm, "Backup.period");
		Preference p38 = new HighestPreference(att);
		Preference p39 = new LowestPreference(att);
		Preference p40 = new AroundPreference(att, 2.0);
		auxList.add(p38);
		auxList.add(p39);
		auxList.add(p40);
		defaultPreferences.put("Backup", auxList);
		
		//Indexing
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("Indexing");
		Preference p41 = new LikesPreference();
		Preference p42 = new DislikesPreference(f);
		auxList.add(p41);
		auxList.add(p42);
		defaultPreferences.put("Indexing", auxList);
		
		//Defragmenter
		auxList = new LinkedList<Preference>();
		f = fm.searchFeatureByName("Defragmenter");
		Preference p43 = new LikesPreference(f);
		Preference p44 = new DislikesPreference(f);
		auxList.add(p43);
		auxList.add(p44);
		defaultPreferences.put("Defragmenter", auxList);
		
	}
	
	public int getMaxNumberOfPreferences(){
		return defaultPreferences.size();
	}

}
