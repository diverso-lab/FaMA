package es.us.isa.fama.experiments.generators;

import java.util.Collection;

import es.us.isa.soup.preferences.User;

public abstract class AbstractPreferencesREconfigAssignMethod {

	protected PreferencesGenerator generator;
	//probability of change [0,1]
	protected double weightChangeProbability;
	//percentage of change [0,1]
	protected double weightChange;
	protected double preferencesChangeProbability;
	//number of initial preferences
	protected int initialPrefs;
	
	public abstract Collection<User> changeUsersWeight(Collection<User> users);
	
	/**
	 * Adds or removes randomly preferences for each user based on
	 * a probability . the probability of change of every user is
	 * given by preferencesChangeProbability field
	 * @param users current users of the system
	 * @return Collection<User> current users with changed preferences
	 */
	public abstract Collection<User> changeUsersPreferences(Collection<User> users);
	
	/**
	 * Adds or removes randomly a user
	 * @param users previous users
	 * @return Collection<User> new set of users
	 */
	public abstract Collection<User> changeUsers(Collection<User> users);

	public int getInitialPreferences(){
		return initialPrefs;
	}
	
	public void setInitialPreferences(int n){
		initialPrefs = n;
	}
	
	public double getWeightChange() {
		return weightChange;
	}

	public void setWeightChange(double weightChange) {
		this.weightChange = weightChange;
	}

	public double getPreferencesChangeProbability() {
		return preferencesChangeProbability;
	}

	public void setPreferencesChangeProbability(double preferencesChange) {
		this.preferencesChangeProbability = preferencesChange;
	}

	public double getWeightChangeProbability() {
		return weightChangeProbability;
	}

	public void setWeightChangeProbability(double weightChangeProbability) {
		this.weightChangeProbability = weightChangeProbability;
	}
	
	public abstract Collection<User> getInitialUsers();
	
	public abstract Collection<User> getRandomUsers(int n);

	
}
