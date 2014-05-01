package es.us.isa.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import es.us.isa.soup.preferences.User;

public class PreferenceUtils {

	public static double computeWeightedNashProduct(Collection<User> users, Solution sol){
		//XXX be careful here to compute the Nash product
		//all the users should be considered equally
		double result = 1;
		int i = 0;
		for (User u:users){
			double numberOfPrefs = u.getPreferences().size();
			double satisfaction = sol.getObjective(i)/numberOfPrefs;
			result = result * u.getWeight() * satisfaction;
			i++;
		}
		return result;
	}
	
	public static Map<Solution,Double> processSolutionSet(SolutionSet solSet, Collection<User> users) {
		// we should normalize the domain of all the fitness functions
		// i.e. U_i = U_i*MAX/max_i
		int max = 1;
		for (User u:users){
			int tempSize = u.getPreferences().size(); 
			if (tempSize > max){
				max = tempSize;
			}
		}
		
		
		Map<Solution,Double> result = new HashMap<Solution, Double>();
//		Solution bestSol = null;
//		double nashProduct = 0;
		int size = solSet.size();
//		int numberOfObjectives = p.getNumberOfObjectives();
		for (int i = 0; i < size; i++){
			Solution s = solSet.get(i);
			if (s.getNumberOfViolatedConstraint() == 0){
				//if no constraint has been violated 
				// we consider this solution and compute
				// the weighted nash product
				double currentNashProduct = 1;
				int j = 0;
				for (User u:users){
					int noP = u.getPreferences().size();
					//XXX normalizing all the objective values to the scale of the
					//maximum number of preferences
					double obj = s.getObjective(j);
					if (obj < 0){
						obj = -obj;
					}
					double currentOperand = obj * max/noP;
					currentNashProduct = currentNashProduct * currentOperand * u.getWeight(); 
					j++;
				}
				
				result.put(s, currentNashProduct);
				
//				if (currentNashProduct > nashProduct){
//					nashProduct = currentNashProduct;
//					bestSol = s;
//				}
			}
		}
		
		return result;
	}
	
	public static Solution obtainBestSolution(Map<Solution,Double> pairs, Collection<User> users){
		Set<Entry<Solution,Double>> entries = pairs.entrySet();
		Solution result = null;
		double max = Double.NEGATIVE_INFINITY;
		for (Entry<Solution,Double> e:entries){
			if (e.getValue() > max){
				max = e.getValue();
				result = e.getKey();
			}
		}
		if (max <= 0){
			//no solution satisfying at least a preference per user
			//we use weighted mean as an alternative measure 
			for (Entry<Solution,Double> e:entries){
				double mean = getSatisfactionMean(e.getKey(), users);
				if (mean > max){
					max = e.getValue();
					result = e.getKey();
				}
			}
		}
		return result;
	}
	
	public static Solution cloneSolution(Solution s, Problem p){
		Solution result = null;
		try {
			result = Solution.getNewSolution(p);
			Variable[] decisionVariables1 = s.getDecisionVariables();
			Variable[] decisionVariables11 = new Variable[decisionVariables1.length];
			for (int i = 0; i < decisionVariables1.length; i++){
				decisionVariables11[i] = decisionVariables1[i].deepCopy();
			}
			result.setDecisionVariables(decisionVariables11);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static double getSatisfactionMean(Solution s, Collection<User> users){
		int i = 0;
		double sum = 0;
		double div = 0;
		for (User u:users){
			double satisfied = s.getObjective(i);
			double prefs = u.getPreferences().size();
			sum += (satisfied/prefs)*u.getWeight();
			div += u.getWeight();
			i++;
		}
		double result = sum / div;
		return result;
	}
}
