package es.us.isa.fama.experiments.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;
import es.us.isa.soup.preferences.User;
import es.us.isa.utils.FMUtils;

public class ScenarioREconfigAssignMethod extends
		AbstractPreferencesREconfigAssignMethod {

	private List<User> defaultUsers;
	
	private final static int MIN_USERS = 10;
	private final static int MAX_USERS = 80;
	
	private final static int MAX_TOTAL_USERS = 200;
//	private final static int MAX_TOTAL_GROUPS = 10;
	private final static int MAX_TOTAL_GROUPS = 5;
	private final static int MAX_GROUP_PREFERENCES = 10;
	
	public ScenarioREconfigAssignMethod(FAMAAttributedFeatureModel afm){
//		RandomPreferencesGenerator aux = new RandomPreferencesGenerator();
//		aux.setFm(afm);
//		aux.populateDefaultPreferences();
		this.generator = new RandomPreferencesGenerator();
		this.generator.setFm(afm);
		this.initialPrefs = 3;
		this.preferencesChangeProbability = 0.5;
		this.weightChange = 0.3;
		this.weightChangeProbability = 0.9;
//		this.generator = aux;
//		defaultUsers = generateDefaultUsers(afm);
	}
	
	/**
	 * This method changes the weights of the users randomly,
	 * but respecting the limit of MAX_TOTAL_USERS
	 */
	@Override
	public Collection<User> changeUsersWeight(Collection<User> users) {
		// XXX TEST this method
		List<User> result = new ArrayList<User>(users);
		int totalWeight = 0;
		for (User u:result){
			totalWeight = totalWeight + u.getWeight();
		}
		
		Random random = new Random(System.nanoTime());
		for (User u:result){
			boolean b1 = (weightChangeProbability >= random.nextDouble());
			if (b1){
				double weightVariation = u.getWeight() * this.weightChange;
				boolean b2 = random.nextBoolean();
				int newWeight;
				if (b2 && (totalWeight + weightVariation < MAX_TOTAL_USERS)){
					newWeight = u.getWeight() + (int)weightVariation;
					if (newWeight > MAX_USERS){
						newWeight = MAX_USERS;
						weightVariation = newWeight - u.getWeight();
					}
					totalWeight = totalWeight + (int)weightVariation;
				}
				else{
					newWeight = u.getWeight() - (int)weightVariation;
					if (newWeight < MIN_USERS){
						newWeight = MIN_USERS;
						weightVariation = u.getWeight() - newWeight;
					}
					totalWeight = totalWeight - (int)weightVariation;
				}
				u.setWeight(newWeight);
			}
			
		}
		
		return result;
	}

	/**
	 * This method changes a preference (add or remove) of each user,
	 * adding new ones or removing existing. the probability
	 * of change of each user is given by preferencesChange field
	 */
	@Override
	public Collection<User> changeUsersPreferences(Collection<User> users) {
		// XXX TEST this method
		List<User> result = new ArrayList<User>(users);
		
		Random r = new Random(System.nanoTime());
		for (User u:result){
			List<Preference> prefs = new ArrayList<Preference>(u.getPreferences());
			boolean b = (this.preferencesChangeProbability >= r.nextDouble());
			if (b){
				//changes for this user
				if (prefs.size() == MAX_GROUP_PREFERENCES){
					int index = r.nextInt(prefs.size());
					prefs.remove(index);
				}
				else if (prefs.size() <= 2){
					Collection<Preference> newPreferences = generator.generatePreferences(u.getPreferences(),1);
					prefs.addAll(newPreferences);
				}
				else{
					b = r.nextBoolean();
					if (b){
						//we add
						Collection<Preference> newPreferences = generator.generatePreferences(u.getPreferences(),1);
						prefs.addAll(newPreferences);
					}
					else{
						//we remove
						int index = r.nextInt(prefs.size());
						prefs.remove(index);
					}
				}
				u.setPreferences(prefs);
			}
		}
		return result;
	}

	/**
	 * This method randomly adds or remove a new user
	 */
	@Override
	public Collection<User> changeUsers(Collection<User> users) {
		// XXX TEST this method
		
		List<User> result = new ArrayList<User>(users);
		int size = result.size();
		Random random = new Random(System.nanoTime());
		if (size == MAX_TOTAL_GROUPS){
			int index = random.nextInt(size);
			result.remove(index);
		}
		else if (size <= 2){
			int currentWeight = 0;
			for (User u:users){
				currentWeight = currentWeight + u.getWeight();
			}
			User newUser = createNewUser(MAX_TOTAL_USERS - currentWeight,this.initialPrefs);
			result.add(newUser);
		}
		else{
			//first decide if we add or remove
			int currentWeight = 0;
			for (User u:users){
				currentWeight = currentWeight + u.getWeight();
			}
			int aux = random.nextInt(2);
			if (aux == 0){
				int index = random.nextInt(size);
				result.remove(index);
			}
			else{
				User newUser = createNewUser(MAX_TOTAL_USERS - currentWeight,this.initialPrefs);
				result.add(newUser);
			}
		}
		
		return result;
	}

	private User createNewUser(int maxWeight, int nprefs) {
		Random r = new Random(System.nanoTime());
//		int index = r.nextInt(defaultUsers.size());
		User u = new User();
		Collection<Preference> prefs = generator.generatePreferences(nprefs);
		u.setPreferences(prefs);
		u.setName("User"+System.nanoTime());
		int weight = maxWeight/2 + r.nextInt(maxWeight/3);
		u.setWeight(weight);
		return u;
	}
	
	
	private List<User> generateDefaultUsers(FAMAAttributedFeatureModel fm){
		List<User> result = new LinkedList<User>();
		
		// XXX add the other 2 tenants
		Collection<Preference> prefs = new LinkedList<Preference>();
		Preference p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		Preference p2 = new LikesPreference(
				fm.searchFeatureByName("OfficeUpdt"));
		Preference p3 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Backup.frequency"), 2.0);
		Preference p4 = new LowestPreference(FMUtils.searchAttribute(fm,
				"Antivirus.checkPeriod"));
		Preference p5 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Firewall.level"), 2.0);
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u1 = new User(prefs, "Admin");

		prefs = new LinkedList<Preference>();
		p1 = new DislikesPreference(fm.searchFeatureByName("Classic"));
		p2 = new LikesPreference(fm.searchFeatureByName("Indexing"));
		p3 = new LikesPreference(fm.searchFeatureByName("Defragmenter"));
		p4 = new AroundPreference(FMUtils.searchAttribute(fm, "Firewall.level"),
				1.0);
		p5 = new DislikesPreference(fm.searchFeatureByName("Classic"));
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u2 = new User(prefs, "Developer");

		prefs = new LinkedList<Preference>();
		p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		p2 = new AroundPreference(FMUtils.searchAttribute(fm,
				"OfficeUpdt.frequency"), 2.0);
		p3 = new AroundPreference(FMUtils.searchAttribute(fm, "Backup.frequency"),
				2.0);
		p4 = new HighestPreference(FMUtils.searchAttribute(fm,
				"Antivirus.checkPeriod"));
		p5 = new HighestPreference(FMUtils.searchAttribute(fm, "Firewall.level"));
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u3 = new User(prefs, "Manager");

		prefs = new LinkedList<Preference>();
		p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		p2 = new AroundPreference(FMUtils.searchAttribute(fm,
				"LatexUpdt.frequency"), 3.0);
		p3 = new AroundPreference(FMUtils.searchAttribute(fm,
				"OfficeUpdt.frequency"), 3.0);
		p4 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Antivirus.checkPeriod"), 3.0);
		p5 = new AroundPreference(FMUtils.searchAttribute(fm, "Firewall.level"),
				2.0);
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u4 = new User(prefs, "Researcher");

		u1.setWeight(11);
		u2.setWeight(43);
		u3.setWeight(5);
		u4.setWeight(30);
		
		result.add(u1);
		result.add(u2);
		result.add(u3);
		result.add(u4);

		return result;
	}

	public Collection<User> getInitialUsers(){
		return defaultUsers;
	}

	@Override
	public Collection<User> getRandomUsers(int n) {
		Collection<User> result = new LinkedList<User>();
		int k = MAX_TOTAL_USERS / n;
		for (int i = 0; i < n; i++){
			User u = createNewUser(k, initialPrefs);
			result.add(u);
		}
		return result;
	}
	
	public static void main(String[] args){
		AttributedReader reader = new AttributedReader();
		FAMAAttributedFeatureModel fm = null;
		try {
			fm = (FAMAAttributedFeatureModel) reader.parseFile("./inputs/LeroDaaSIntegers.afm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ScenarioREconfigAssignMethod am = new ScenarioREconfigAssignMethod(fm);
//		am.setInitialPreferences(MAX_GROUP_PREFERENCES);
		Collection<User> users = am.getRandomUsers(10);
		for (User u:users){
			System.out.println(u);
		}
	}

}
