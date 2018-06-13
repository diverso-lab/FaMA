package es.us.isa.ChocoReasoner.multistep;

import static choco.Choco.eq;
import static choco.Choco.gt;
import static choco.Choco.ifOnlyIf;
import static choco.Choco.ifThenElse;
import static choco.Choco.implies;
import static choco.Choco.leq;
import static choco.Choco.makeIntVar;
import static choco.Choco.neg;
import static choco.Choco.sum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoReasoner extends FeatureModelReasoner {

	private Collection<Constraint> configConstraints = null;
	public Map<GenericFeature, Map<Integer, Variable>> feature_allVariables;
	public Map<Integer, Variable> costs;// By default the cost will be the sum
										// of active variables
	public Map<String, Map<Integer, Collection<
	Constraint>>> relation_constraints;
	public Collection<String[]> model_drift;
	int steps;
	public Model problem;
	public Map<String,Object[]> setdetails;
	
	public ChocoReasoner(int k_steps) {
		this.steps = k_steps;
		reset();

	}

	public void applyChanges() {
		for (String[] change : model_drift) {
			
			int step = Integer.parseInt(change[1]);
			GenericFeature feat_1 = searchFeatureByName(change[2]);
			if (change[0].equals("ADD")) {
				chocoGenericFeature child = new chocoGenericFeature(change[3]);	
				Collection<Cardinality> cards=new ArrayList<Cardinality>();
				cards.add(new Cardinality(0, 1));
				this.addFeature(child, cards,step);
				if(change[4].equals("M")){
					this.addMandatory(null, child, feat_1,step);
				}else if(change[4].equals("O")){
					this.addOptional(null, child, feat_1, step);
				}
				
			} else if (change[0].equals("REMOVE")) {
				System.out.println("IMPORTANT: We not grant results when removing not a leaft feature. The changes of commonalities are not allowed");
				for (String realtionStr : relation_constraints.keySet()) {
					// just looking for any constraints where the feature is
					// involved
					// in m or o
					for (int i = step; i < steps; i++) {
						if (realtionStr.contains(feat_1.getName())&& !realtionStr.contains("-")) {

							Collection<Constraint> collection = relation_constraints.get(realtionStr).get(i);
							if(collection!=null){//null means that its a configuration for a staged config and doent esxit in that step
							for (Constraint c : collection) {
								problem.removeConstraint(c);
							}}
							relation_constraints.get(realtionStr).put(i,	new ArrayList<Constraint>());
							Variable variable = feature_allVariables.get(feat_1).get(i);
							problem.removeVariable(variable);
							
							// inset
						} else if (realtionStr.contains(feat_1.getName())&& realtionStr.contains("-")) {
							System.out.println("IMPORTANT: At this momment we dont accept set relationships");
							//se puede hacer guardando el tema de los sets
						}
					}
				}
			} 
		}

	}

	private void costIncrementFunction() {
		for (int i = 0; i < steps - 1; i++) {
			Constraint c = leq(
					sum((IntegerVariable) costs.get(i + 1),
							neg((IntegerVariable) costs.get(i))), 50);
			problem.addConstraint(c);
		}

	}

	private void generateCostVariablesAndAddConstraint() {
		for (int i = 0; i < steps; i++) {

			IntegerVariable var = makeIntVar("cost_" + i, 0,
					feature_allVariables.size());
			Collection<Variable> tmpCol = new ArrayList<Variable>();
			for (GenericFeature f : feature_allVariables.keySet()) {
				tmpCol.add(feature_allVariables.get(f).get(i));
			}
			IntegerVariable[] array = tmpCol.toArray(new IntegerVariable[tmpCol
					.size()]);
			Constraint cons = eq(var, sum(array));
			problem.addVariable(var);
			problem.addConstraint(cons);
			costs.put(i, var);

		}

	}

	public void reset() {
		this.problem = new CPModel();
		this.feature_allVariables = new HashMap<GenericFeature, Map<Integer, Variable>>();
		costs = new HashMap<Integer, Variable>();
		configConstraints = new ArrayList<Constraint>();
		relation_constraints = new HashMap<String, Map<Integer,Collection<Constraint>>>();
		
	}

	public void addFeature(GenericFeature f, Collection<Cardinality> cards) {
		this.addFeature(f, cards, 0);
	}
	public void addFeature(GenericFeature f, Collection<Cardinality> cards,int j) {
		Iterator<Cardinality> cardIt = cards.iterator();// Looks for all the
		// cardinality and save
		// it
		IntegerVariable var;
		SortedSet<Integer> vals = new TreeSet<Integer>();

		while (cardIt.hasNext()) {
			Cardinality card = cardIt.next();
			int min = card.getMin();
			int max = card.getMax();
			for (int i = min; i <= max; i++) {
				vals.add(i);
			}
		}

		vals.add(0);
		// we convert the ordered set to an array of ints
		int[] domain = new int[vals.size()];
		Iterator<Integer> itv = vals.iterator();
		int pos = 0;
		while (itv.hasNext()) {
			domain[pos] = itv.next();
			pos++;
		}
		Map<Integer, Variable> allVars = new HashMap<Integer, Variable>();
		for (int s = j; s < steps; s++) {
			var = makeIntVar(f.getName() + "~" + s, domain);
			allVars.put(s, var);
			problem.addVariable(var);
		}

		this.feature_allVariables.put(f, allVars);

	}

	public void addRoot(GenericFeature feature) {
		Collection<Variable> roots = feature_allVariables.get(feature).values();
		for (Variable v : roots) {
			problem.addConstraint(eq((IntegerVariable) v, 1));
		}

	}
	
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		this.addMandatory(rel, child, parent, 0);
	}
	
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent,int i) {

		Map<Integer, Variable> childVars = this.feature_allVariables.get(child);
		Map<Integer, Variable> parentVars = this.feature_allVariables
				.get(parent);
		//
		String relation = parent.getName() + "~" + child.getName();
		Map<Integer, Collection<Constraint>> step_cons = new HashMap<Integer, Collection<Constraint>>();
		if (relation_constraints.containsKey(relation)) {
			step_cons = relation_constraints.get(relation);
		}
		//
		for (int s = i; s < steps; s++) {
			IntegerVariable childVar = (IntegerVariable) childVars.get(s);
			IntegerVariable parentVar = (IntegerVariable) parentVars.get(s);
			Constraint mandatoryConstraint = ifOnlyIf(eq(parentVar, 1),
					eq(childVar, 1));
			problem.addConstraint(mandatoryConstraint);

			//
			Collection<Constraint> cs = new ArrayList<Constraint>();
			if (step_cons.containsKey(s)) {
				cs = step_cons.get(s);
			}
			cs.add(mandatoryConstraint);
			step_cons.put(s, cs);
			//
		}
		relation_constraints.put(relation, step_cons);

	}
	
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		this.addOptional(rel, child, parent,0);
	}
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent,int i) {

		Map<Integer, Variable> childVars = this.feature_allVariables.get(child);
		Map<Integer, Variable> parentVars = this.feature_allVariables
				.get(parent);
		//
		String relation = parent.getName() + "~" + child.getName();
		Map<Integer, Collection<Constraint>> step_cons = new HashMap<Integer, Collection<Constraint>>();
		if (relation_constraints.containsKey(relation)) {
			step_cons = relation_constraints.get(relation);
		}
		//
		for (int s = i; s < steps; s++) {
			IntegerVariable childVar = (IntegerVariable) childVars.get(s);
			IntegerVariable parentVar = (IntegerVariable) parentVars.get(s);
			Constraint optionalConstraint = implies(eq(parentVar, 0),
					eq(childVar, 0));
			problem.addConstraint(optionalConstraint);
			//
			Collection<Constraint> cs = new ArrayList<Constraint>();
			if (step_cons.containsKey(s)) {
				cs = step_cons.get(s);
			}
			cs.add(optionalConstraint);
			step_cons.put(s, cs);
			//
		}
		relation_constraints.put(relation, step_cons);


	}
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {
		this.addCardinality(rel, child, parent, cardinalities, 0);
	}
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities,int j) {

		Map<Integer, Variable> childVars = this.feature_allVariables.get(child);
		Map<Integer, Variable> parentVars = this.feature_allVariables
				.get(parent);
		//
		String relation = parent.getName() + "~" + child.getName();
		Map<Integer, Collection<Constraint>> step_cons = new HashMap<Integer, Collection<Constraint>>();
		if (relation_constraints.containsKey(relation)) {
			step_cons = relation_constraints.get(relation);
		}
		//
		for (int s = j; s < steps; s++) {
			IntegerVariable childVar = (IntegerVariable) childVars.get(s);
			IntegerVariable parentVar = (IntegerVariable) parentVars.get(s);

			SortedSet<Integer> cardValues = new TreeSet<Integer>();
			Iterator<Cardinality> itc = cardinalities;
			while (itc.hasNext()) {
				Cardinality card = itc.next();
				for (int i = card.getMin(); i <= card.getMax(); i++)
					cardValues.add(i);
			}
			int[] cardValuesArray = new int[cardValues.size()];
			Iterator<Integer> itcv = cardValues.iterator();
			int pos = 0;
			while (itcv.hasNext()) {
				cardValuesArray[pos] = itcv.next();
				pos++;
			}
			IntegerVariable cardinalityVar = makeIntVar(
					rel.getName() + "_card", cardValuesArray, "cp:no_decision");
			Constraint cardConstraint = ifThenElse(gt(parentVar, 0),
					eq(childVar, cardinalityVar), eq(childVar, 0));
			problem.addConstraint(cardConstraint);
			//
			Collection<Constraint> cs = new ArrayList<Constraint>();
			if (step_cons.containsKey(s)) {
				cs = step_cons.get(s);
			}
			cs.add(cardConstraint);
			step_cons.put(s, cs);
			//
		}
		relation_constraints.put(relation, step_cons);


	}
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		this.addSet(rel, parent, children, cardinalities, 0);
	}

	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities,int j) {
		
		Map<Integer, Variable> parentVars = this.feature_allVariables
				.get(parent);
		//
		String relation = parent.getName() + "~";
		//
		for (int s = j; s < steps; s++) {
			IntegerVariable parentVar = (IntegerVariable) parentVars.get(s);

			Cardinality card = null;
			SortedSet<Integer> cardValues = new TreeSet<Integer>();
			Iterator<Cardinality> itc = cardinalities.iterator();
			while (itc.hasNext()) {
				card = itc.next();
				for (int i = card.getMin(); i <= card.getMax(); i++)
					cardValues.add(i);
			}
			int[] cardValuesArray = new int[cardValues.size()];
			Iterator<Integer> itcv = cardValues.iterator();
			int pos = 0;
			while (itcv.hasNext()) {
				cardValuesArray[pos] = itcv.next();
				pos++;
			}

			IntegerVariable cardinalityVar = makeIntVar(
					rel.getName() + "_card", cardValuesArray, "cp:no_decision");// cp:no_decision
			problem.addVariable(cardinalityVar);
			// Save all children to have the posiblitily of sum them
			ArrayList<IntegerVariable> varsList = new ArrayList<IntegerVariable>();
			Iterator<GenericFeature> it = children.iterator();

			while (it.hasNext()) {
				GenericFeature next = it.next();
				varsList.add((IntegerVariable) feature_allVariables.get(next)
						.get(s));
				relation += next.getName() + "-";
			}
			Map<Integer, Collection<Constraint>> step_cons = new HashMap<Integer, Collection<Constraint>>();
			if (relation_constraints.containsKey(relation)) {
				step_cons = relation_constraints.get(relation);
			}
			// creates the sum constraint with the cardinality variable
			// If parent var is equal to 0 then he sum of children has to be 0
			IntegerVariable[] aux = {};
			aux = varsList.toArray(aux);

			// If parent is greater than 0, then apply the restriction
			// ifThenElse(A>0;sum(B,C) in {n,m};B=0,C=0)
			Constraint setConstraint = ifThenElse(gt(parentVar, 0),
					eq(sum(aux), cardinalityVar), eq(sum(aux), 0));
			problem.addConstraint(setConstraint);
			//
			Collection<Constraint> cs = new ArrayList<Constraint>();
			if (step_cons.containsKey(s)) {
				cs = step_cons.get(s);
			}
			cs.add(setConstraint);
			step_cons.put(s, cs);
			//
			relation_constraints.put(relation, step_cons);

		}
		//this.setdetails.put(key, value)

	}


	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {
		this.addExcludes(rel, origin, destination, 0);
	}

	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature destination,int j) {

		Map<Integer, Variable> childVars = this.feature_allVariables
				.get(destination);
		Map<Integer, Variable> parentVars = this.feature_allVariables
				.get(origin);
		//
		String relation = origin.getName() + "~" + destination.getName();
		Map<Integer, Collection<Constraint>> step_cons = new HashMap<Integer, Collection<Constraint>>();
		if (relation_constraints.containsKey(relation)) {
			step_cons = relation_constraints.get(relation);
		}
		//
		for (int s = j; s < steps; s++) {
			IntegerVariable destVar = (IntegerVariable) childVars.get(s);
			IntegerVariable originVar = (IntegerVariable) parentVars.get(s);
			Constraint excludesConstraint = implies(gt(originVar, 0),
					eq(destVar, 0));
			problem.addConstraint(excludesConstraint);
			//
			Collection<Constraint> cs = new ArrayList<Constraint>();
			if (step_cons.containsKey(s)) {
				cs = step_cons.get(s);
			}
			cs.add(excludesConstraint);
			step_cons.put(s, cs);
			//
		}
		relation_constraints.put(relation, step_cons);

	}

	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {
		this.addRequires(rel, origin, destination, 0);
	}
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination, int j) {

		Map<Integer, Variable> childVars = this.feature_allVariables
				.get(destination);
		Map<Integer, Variable> parentVars = this.feature_allVariables
				.get(origin);
		//
		String relation = origin.getName() + "~" + destination.getName();
		Map<Integer, Collection<Constraint>> step_cons = new HashMap<Integer, Collection<Constraint>>();
		if (relation_constraints.containsKey(relation)) {
			step_cons = relation_constraints.get(relation);
		}
		//
		for (int s = j; s < steps; s++) {
			IntegerVariable destVar = (IntegerVariable) childVars.get(s);
			IntegerVariable originVar = (IntegerVariable) parentVars.get(s);
			Constraint requiresConstraint = implies(gt(originVar, 0),
					gt(destVar, 0));
			problem.addConstraint(requiresConstraint);
			//
			Collection<Constraint> cs = new ArrayList<Constraint>();
			if (step_cons.containsKey(s)) {
				cs = step_cons.get(s);
			}
			cs.add(requiresConstraint);
			step_cons.put(s, cs);
			//
		}
		relation_constraints.put(relation, step_cons);

	}

	@Override
	public PerformanceResult ask(Question q) {

		generateCostVariablesAndAddConstraint();
		this.costIncrementFunction();
		
		return ((ChocoQuestion) q).answer(this);
	}

	@Override
	public void unapplyStagedConfigurations() {
		for (Constraint c : configConstraints) {
			problem.removeConstraint(c);
		}

	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {
		this.applyStagedConfiguration(conf, 0);
	}

	public void applyStagedConfiguration(Configuration conf, int k) {

		Iterator<Entry<VariabilityElement, Integer>> it = conf.getElements()
				.entrySet().iterator();

		while (it.hasNext()) {
			Entry<VariabilityElement, Integer> e = it.next();
			VariabilityElement v = e.getKey();
			Model p = this.problem;
			int arg1 = e.getValue().intValue();
			Constraint aux;

			if (v instanceof GenericFeature) {
				GenericFeature searchFeatureByName = searchFeatureByName(v
						.getName());
				if (searchFeatureByName == null) {
					IntegerVariable errorVar = makeIntVar("error", 0, 0,
							"cp:no_decision");
					Constraint error = eq(1, errorVar);
					if (e.getValue() == 0) {
						System.err.println("The feature " + v.getName()
								+ " do not exist on the model");
					} else {
						p.addConstraint(error);
						this.configConstraints.add(error);
						System.err.println("The feature " + v.getName()
								+ " do not exist, and can not be added");
					}
				} else {
					Map<Integer, Variable> arg0 = feature_allVariables
							.get(searchFeatureByName);
					aux = eq((IntegerVariable) arg0.get(k), arg1);
					p.addConstraint(aux);
					this.configConstraints.add(aux);
					Collection<Constraint> col= new ArrayList<Constraint>();
					Map<Integer, Collection<Constraint>> map = relation_constraints.get(searchFeatureByName.getName());
					if(map==null){map= new HashMap<Integer, Collection<Constraint>>();}
					if(relation_constraints.containsKey(searchFeatureByName)){
						col=map.get(k);
					}
					col.add(aux);
					map.put(k, col);
					this.relation_constraints.put(searchFeatureByName.getName(), map);
				}
			}
		}

	}

	@Override
	public Map<String, Object> getHeusistics() {
		return null;
	}

	@Override
	public void setHeuristic(Object obj) {

	}

	public GenericFeature searchFeatureByName(String temp) {
		for (GenericFeature f : this.feature_allVariables.keySet()) {
			if (f.getName().equals(temp)) {
				return f;
			}
		}
		return null;
	}
	
	public class chocoGenericFeature extends GenericFeature{
		public chocoGenericFeature(String name){
			this.name=name;
		}
	}

}
