package es.us.isa.fama.experiments.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.fama.experiments.generators.ScenarioREconfigAssignMethod;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.Preference;
import es.us.isa.soup.preferences.User;

public class AssignMethodTests {

	private ScenarioREconfigAssignMethod assignMethod;
	private FAMAAttributedFeatureModel fm;

	@Before
	public void setUp() {
		loadFM();
		assignMethod = new ScenarioREconfigAssignMethod(fm);
	}

	protected void loadFM() {
		AttributedReader reader = new AttributedReader();
		try {
			fm = (FAMAAttributedFeatureModel) reader
					.parseFile("./inputs/LeroDaaSIntegers.afm");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWeightChanges() {
		Collection<User> users = assignMethod.getInitialUsers();
		Collection<User> postUsers;

		// XXX no change expected
		assignMethod.setWeightChangeProbability(0);
		assignMethod.setWeightChange(1);
		List<Integer> preWeights = getWeights(users);
		postUsers = assignMethod.changeUsersWeight(users);
		List<Integer> postWeights = getWeights(postUsers);
		assertTrue("Unexpected weight changes (Prob = 0%, Change = 100%)",
				preWeights.equals(postWeights));

		// XXX no change expected
		assignMethod.setWeightChangeProbability(1);
		assignMethod.setWeightChange(0);
		preWeights = getWeights(users);
		postUsers = assignMethod.changeUsersWeight(users);
		postWeights = getWeights(postUsers);
		assertTrue("Unexpected weight changes (Prob = 100%, Change = 0%)",
				preWeights.equals(postWeights));

		// XXX changes for all the users
		assignMethod.setWeightChangeProbability(1);
		assignMethod.setWeightChange(0.5);
		preWeights = getWeights(users);
		postUsers = assignMethod.changeUsersWeight(users);
		postWeights = getWeights(postUsers);
		Iterator<Integer> it1 = preWeights.iterator(), it2 = postWeights
				.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			Integer u1 = it1.next();
			Integer u2 = it2.next();
			assertTrue(!u1.equals(u2));
		}
	}

	@Test
	public void testPreferenceChanges() {
		Collection<User> users = assignMethod.getInitialUsers();
		Collection<User> postUsers;

		// XXX no change expected
		assignMethod.setPreferencesChangeProbability(0);
		Collection<Collection<Preference>> prePreferences = copyPreferences(users);
		postUsers = assignMethod.changeUsersPreferences(users);
		Collection<Collection<Preference>> postPreferences = copyPreferences(postUsers);
		Iterator<Collection<Preference>> it1 = prePreferences.iterator(), 
				it2 = postPreferences.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			Collection<Preference> u1 = it1.next();
			Collection<Preference> u2 = it2.next();
			assertTrue(u1.equals(u2));
		}

		// XXX all the users should change
		assignMethod.setPreferencesChangeProbability(1);
		prePreferences = copyPreferences(users);
		postUsers = assignMethod.changeUsersPreferences(users);
		postPreferences = copyPreferences(postUsers);
		it1 = prePreferences.iterator();
		it2 = postPreferences.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			Collection<Preference> u1 = it1.next();
			Collection<Preference> u2 = it2.next();
			assertTrue(!u1.equals(u2));
		}
	}

	@Test
	public void testUserChanges() {
		Collection<User> users = assignMethod.getInitialUsers();
		Collection<User> postUsers;

		// XXX there should be a different number of users
		postUsers = assignMethod.changeUsers(users);
		assertTrue(users.size() != postUsers.size());
	}

	private List<Integer> getWeights(Collection<User> users) {
		List<Integer> result = new ArrayList<Integer>();
		for (User u : users) {
			result.add(u.getWeight());
		}
		return result;
	}

	private Collection<Collection<Preference>> copyPreferences(Collection<User> users) {
		Collection<Collection<Preference>> result = new LinkedList<Collection<Preference>>();
		for (User u : users) {
			Collection<Preference> preferences = u.getPreferences();
			Collection<Preference> subList = new LinkedList<Preference>();
			for (Preference p : preferences) {
				Class clazz = p.getClass();
				try {
					Preference copy = (Preference) clazz.newInstance();
					copy.setItem(p.getItem());
					if (p instanceof AroundPreference){
						((AroundPreference)copy).setValue(((AroundPreference) p).getValue());
					}
					subList.add(copy);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			result.add(subList);
		}
		return result;
	}

}
