package es.us.isa.fama.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.JMException;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.domain.Domain;
import es.us.isa.FAMA.models.domain.IntegerDomain;
import es.us.isa.FAMA.models.domain.ObjectDomain;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.fama.solvers.ChocoSolver;
import es.us.isa.fama.solvers.Solver;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;
import es.us.isa.soup.preferences.User;

/**
 * We're using 5 SOUP preferences.
 * @author jesus
 *
 */
public abstract class AAFMProblem extends Problem {

	public final static double PREF_MAX_VALUE = 1;
	public final static double PREF_MIN_VALUE = 0;
	protected User[] userPreferences;
	protected Solver solver;
	
	protected FAMAAttributedFeatureModel fm;
	protected GenericAttributedFeature[] features;
	//attributes arranged by index
	protected GenericAttribute[] attributes;
	protected Map<String,Integer> featureSolutionIndex;
	protected Map<String,Integer> attributeSolutionIndex;
	
	protected Collection<Solution> seeds;
	
	
//	public AAFMProblem(FAMAAttributedFeatureModel fm, Collection<User> preferences, int numberOfSeeds){
//		//XXX is this part complete?
//		this.fm = fm;
//		this.userPreferences = preferences.toArray(new User[1]);
//		fmToJMetalVars();
//		solutionType_ = new ArrayIntSolutionType(this);
//		solver = new ChocoSolver();
//		//first we obtain the seeds
//		solver.translate(fm, false);
//		this.numberOfVariables_ = this.features.length + this.attributes.length;
//		//XXX we include the correctness of the configuration as an additional objective
//		this.numberOfObjectives_ = preferences.size() + 1;
//		this.seeds = solver.computeSeeds(this, numberOfSeeds);
//		this.numberOfConstraints_ = solver.getNumberOfConstraints();
//		//then we ready the solver to check solutions
//		solver.translate(fm,true);
//		
//	}
	
	@Override
	 public void evaluateConstraints(Solution solution) throws JMException {
		solver.checkSolution(solution);
		int violations = solver.getViolations();
		solution.setOverallConstraintViolation(violations);    
	    solution.setNumberOfViolatedConstraint(violations); 		
	 } // evaluateConstraints

	
	protected void fmToJMetalVars() {
		featureSolutionIndex = new HashMap<String, Integer>();
		attributeSolutionIndex = new HashMap<String, Integer>();
		// create the array of features
		
		//XXX now we consider in the same ArrayInt features & attributes
		
		features = fm.getAttributedFeatures().toArray(new GenericAttributedFeature[1]);

		//walk features to obtain attributes
		Collection<GenericAttribute> temp = new LinkedList<GenericAttribute>();
		for (int i = 0; i < features.length; i++){
			featureSolutionIndex.put(features[i].getName(),i);
			Collection<? extends GenericAttribute> atts = features[i].getAttributes();
			temp.addAll(atts);
		}//for
		attributes = temp.toArray(new GenericAttribute[1]);
		
		int arraySize = attributes.length + features.length;
		this.lowerLimit_ = new double[arraySize];
		this.upperLimit_ = new double[arraySize];
		//features domain
		for (int i = 0; i < features.length; i++){
			this.lowerLimit_[i] = 0;
			this.upperLimit_[i] = 1;
		}
		
		//attributes domain
		for (int i = features.length; i < arraySize; i++){
			int attIndex = i - features.length;
			attributeSolutionIndex.put(attributes[attIndex].getFullName(), i);
			Domain dom = attributes[attIndex].getDomain();
			if (dom instanceof IntegerDomain){
				IntegerDomain idom = (IntegerDomain) dom;
				if (idom instanceof RangeIntegerDomain){
					RangeIntegerDomain ridom = (RangeIntegerDomain) idom;
					Set<Range> ranges = ridom.getRanges();
					Iterator<Range> it = ranges.iterator();
					Range first = it.next();
					Range last = first;
					while (it.hasNext()){
						last = it.next();
					}//while
					int min = first.getMin();
					int max = last.getMax();
					this.lowerLimit_[i] = min;
					this.upperLimit_[i] = max;
				}//if
				
			}//if
			else if (dom instanceof ObjectDomain){
				ObjectDomain odom = (ObjectDomain) dom;
				//make sure that the values are consecutive
				Set<Integer> values = odom.getAllIntegerValues();
				List<Integer> auxList = new ArrayList<Integer>(values);
				Collections.sort(auxList);
				this.lowerLimit_[i] = auxList.get(0);
				this.upperLimit_[i] = auxList.get(auxList.size() - 1);
			}//else if
		}//for
		
	}//fmToArray
	
	public Solver getSolver(){
		return solver;
	}
	
	public Collection<Solution> getSeeds(){
		return seeds;
	}

	/**
	 * TODO how do we deal with the different weights of the users?
	 */
	@Override
	public void evaluate(Solution solution) throws JMException {
		Variable[] values = solution.getDecisionVariables();
		ArrayInt elementValues = (ArrayInt) values[0];

		for (int i = 0; i < userPreferences.length; i++) {
			double fitness = 0;
			Collection<Preference> prefs = userPreferences[i].getPreferences();
			for (Preference p : prefs) {
				if (p instanceof LikesPreference) {
					fitness += evaluateLikes((LikesPreference) p, elementValues);
				} else if (p instanceof DislikesPreference) {
					fitness += evaluateDislikes((DislikesPreference) p,
							elementValues);
				} else if (p instanceof HighestPreference) {
					fitness += evaluateHighest((HighestPreference) p,
							elementValues);
				} else if (p instanceof LowestPreference) {
					fitness += evaluateLowest((LowestPreference) p,
							elementValues);
				} else if (p instanceof AroundPreference) {
					fitness += evaluateAround((AroundPreference) p,
							elementValues);
				}
			}
			solution.setObjective(i, 0 - fitness);
		}

		// XXX finally, we evaluate how many constraints have been violated
		this.evaluateConstraints(solution);
		solution.setObjective(this.numberOfObjectives_ - 1,
				solution.getOverallConstraintViolation());

	}

	/**
	 * Computes the value for an around preference. if value = aroundValue =>
	 * return Max else result(aroundValue - K) = result (aroundValue + K)
	 * 
	 * @param p
	 * @param attValues
	 * @return
	 */
	private double evaluateAround(AroundPreference p, ArrayInt elementValues) {
		Integer index = attributeSolutionIndex.get(this.getName(p.getItem()));
		int nullValue = 0;
		if (p.getItem() instanceof GenericAttribute){
			GenericAttribute att = ((GenericAttribute)p.getItem());
			nullValue = att.getIntegerValue(att.getNullValue());
		}

		double upperBound, lowerBound, value, around, maxRange, result;
		try {
			upperBound = elementValues.getUpperBound(index);
			lowerBound = elementValues.getLowerBound(index);
			value = elementValues.getValue(index);
			around = p.getValue();
			
			if (value == nullValue){
				result = AAFMProblem.PREF_MIN_VALUE;
			}
			else{
				if ((around - lowerBound) >= (upperBound - around)) {
					maxRange = around - lowerBound;
				} else {
					maxRange = upperBound - around;
				}
				
				result = (maxRange - Math.abs(around-value))/maxRange;
				
			}
//			else if (value == around) {
//				result = AAFMProblem.PREF_MAX_VALUE;
//			} else {
//				if ((around - lowerBound) >= (upperBound - around)) {
//					maxRange = around - lowerBound;
//				} else {
//					maxRange = upperBound - around;
//				}
//
//				if (value < around) {
//					result = AAFMProblem.PREF_MAX_VALUE * (value - lowerBound)
//							/ maxRange;
//				} else {
//					// value > around
//					result = AAFMProblem.PREF_MAX_VALUE * (upperBound - value)
//							/ maxRange;
//				}
//			}
//			this.doNothing();
		} catch (JMException e) {
			result = AAFMProblem.PREF_MIN_VALUE;
			e.printStackTrace();
		}
		return result;
	}

	private double evaluateLowest(LowestPreference p, ArrayInt elementValues) {
		Integer index = attributeSolutionIndex.get(this.getName(p.getItem()));
		int nullValue = 0;
		if (p.getItem() instanceof GenericAttribute){
			GenericAttribute att = ((GenericAttribute)p.getItem());
			nullValue = att.getIntegerValue(att.getNullValue());
		}
		double upperBound, lowerBound, value, result;
		try {
			upperBound = elementValues.getUpperBound(index);
			lowerBound = elementValues.getLowerBound(index);
			value = elementValues.getValue(index);
			if (value == nullValue){
				result = AAFMProblem.PREF_MIN_VALUE;
			}
			else{
				result = AAFMProblem.PREF_MAX_VALUE * (upperBound - value)
						/ (upperBound - lowerBound);
			}
			
//			this.doNothing();
		} catch (JMException e) {
			result = AAFMProblem.PREF_MIN_VALUE;
			e.printStackTrace();
		}
		return result;
	}

	private double evaluateHighest(HighestPreference p, ArrayInt elementValues) {
		Integer index = attributeSolutionIndex.get(this.getName(p.getItem()));
		int nullValue = 0;
		if (p.getItem() instanceof GenericAttribute){
			GenericAttribute att = ((GenericAttribute)p.getItem());
			nullValue = att.getIntegerValue(att.getNullValue());
		}
		double upperBound, lowerBound, value, result;
		try {
			upperBound = elementValues.getUpperBound(index);
			lowerBound = elementValues.getLowerBound(index);
			value = elementValues.getValue(index);
			if (value == nullValue){
				result = AAFMProblem.PREF_MIN_VALUE;
			}
			else{
				result = AAFMProblem.PREF_MAX_VALUE * (value - lowerBound)
						/ (upperBound - lowerBound);
			}
			
//			this.doNothing();
		} catch (JMException e) {
			result = AAFMProblem.PREF_MIN_VALUE;
			e.printStackTrace();
		}
		return result;
	}

	private double evaluateDislikes(DislikesPreference p, ArrayInt elementValues) {
		boolean state = obtainFeatureValue(this.getName(p.getItem()),
				elementValues);
		if (!state) {
			return PREF_MAX_VALUE;
		} else {
			return PREF_MIN_VALUE;
		}
	}

	private double evaluateLikes(LikesPreference p, ArrayInt elementValues) {
		boolean state = obtainFeatureValue(this.getName(p.getItem()),
				elementValues);
		if (state) {
			return PREF_MAX_VALUE;
		} else {
			return PREF_MIN_VALUE;
		}
	}

	private String getName(VariabilityElement elem) {
		if (elem instanceof GenericAttribute) {
			return ((GenericAttribute) elem).getFullName();
		} else {
			return elem.getName();
		}
	}

	private boolean obtainFeatureValue(String name, ArrayInt elementValues) {
		Integer index = featureSolutionIndex.get(name);
		int result = 0;
		try {
			result = elementValues.getValue(index);
		} catch (JMException e) {
			e.printStackTrace();
		}
		return (result == 1);
	}

	public Solution computeRandomValidSolution(){
		ChocoSolver auxSolver = new ChocoSolver();
		auxSolver.translate(fm, false);
		//collection of just 1 element
		Collection<Solution> auxCol = auxSolver.computeSeeds(this, 1);
		Solution aux = auxCol.iterator().next();
		Solution result = null;
		try {
			result = new Solution(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		result.setDecisionVariables(aux.getDecisionVariables());
		return result;
	}
	
//
//	public void doNothing() {
//
//	}
}
