/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
package es.us.isa.ChocoReasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.search.integer.varselector.DomOverDeg;
import choco.cp.solver.search.integer.varselector.DomOverDynDeg;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.MostConstrained;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.search.integer.HeuristicIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoReasoner extends FeatureModelReasoner {

	protected Map<String, GenericFeature> features;
	protected Map<String, IntegerVariable> variables;
	protected Map<String, Constraint> dependencies;
	protected Map<String, IntegerExpressionVariable> setRelations;
	protected Model problem;
	private List<Constraint> configConstraints;
	private AbstractIntVarSelector heuristic;
	private Map<String, Object> heuristicsMap;

	public ChocoReasoner() {
		super();
		reset();
	}

	@Override
	public void reset() {

		this.features = new HashMap<String, GenericFeature>();
		this.variables = new HashMap<String, IntegerVariable>();
		this.problem = new CPModel();
		this.dependencies = new HashMap<String, Constraint>();
		this.setRelations = new HashMap<String, IntegerExpressionVariable>();
		this.configConstraints = new ArrayList<Constraint>();
		heuristicsMap = new HashMap<String, Object>();

//		heuristicsMap.put("StaticVarOrder", new StaticVarOrder(null));
//		heuristicsMap.put("MinDomain", new MinDomain(null));
//		heuristicsMap.put("DomOverDeg", new DomOverDeg(null));
//		heuristicsMap.put("DomOverDynDeg", new DomOverDynDeg(null));
//		heuristicsMap.put("MostConstrained", new MostConstrained(null));
//		heuristicsMap.put("RandomIntVarSelector",
//				new RandomIntVarSelector(null));

	}

	public Model getProblem() {
		return problem;
	}

	public void setProblem(Model problem) {
		this.problem = problem;
	}

	@Override
	public void addRoot(GenericFeature feature) {
		IntegerVariable root = variables.get(feature.getName());
		problem.addConstraint(eq(root, 1));
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {

		Constraint mandatoryConstraint = createMandatory(rel, child, parent);
		problem.addConstraint(mandatoryConstraint);

	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {

		Constraint optionalConstraint = createOptional(rel, child, parent);
		problem.addConstraint(optionalConstraint);

	}

	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {

		Constraint cardConstraint = createCardinality(rel, child, parent,
				cardinalities);
		problem.addConstraint(cardConstraint);

	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		Constraint requiresConstraint = createRequires(rel, origin, destination);
		problem.addConstraint(requiresConstraint);

	}

	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature dest) {

		Constraint excludesConstraint = createExcludes(rel, origin, dest);
		problem.addConstraint(excludesConstraint);

	}

	@Override
	public void addFeature(GenericFeature f, Collection<Cardinality> cards) {

		features.put(f.getName(), f); // Save the feature
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

		// we don't have to check if it is already inserted into the set,
		// because
		// no repeated elements are allowed.
		vals.add(0);
		// we convert the ordered set to an array of ints
		int[] domain = new int[vals.size()];
		Iterator<Integer> itv = vals.iterator();
		int pos = 0;
		while (itv.hasNext()) {
			domain[pos] = itv.next();
			pos++;
		}
		var = makeIntVar(f.getName(), domain);
		problem.addVariable(var);
		this.variables.put(f.getName(), var);

	}

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {

		Constraint setConstraint = createSet(rel, parent, children,
				cardinalities);
		problem.addConstraint(setConstraint);// add only this constraint

	}

	@Override
	public PerformanceResult ask(Question q) {
		if (q == null) {
			throw new FAMAException("Question: Not specified");
		}
		PerformanceResult res;
		ChocoQuestion chq = (ChocoQuestion) q;
		if(heuristic!=null){
			chq.setHeuristic(heuristic);
		}
		chq.preAnswer(this);
		res = chq.answer(this);
		chq.postAnswer(this);
		return res;

	}

	public Map<String, IntegerVariable> getVariables() {
		return variables;
	}

	public Map<String, IntegerExpressionVariable> getSetRelations() {
		return setRelations;
	}

	public Map<String, Constraint> getRelations() {
		return dependencies;
	}

	public GenericFeature searchFeatureByName(String id) {
		return features.get(id);
	}

	public Collection<GenericFeature> getAllFeatures() {
		return this.features.values();
	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {

		Iterator<Entry<VariabilityElement, Integer>> it = conf.getElements()
				.entrySet().iterator();
		Map<String, IntegerVariable> vars = this.getVariables();
		Map<String, IntegerExpressionVariable> rels = this.getSetRelations();
		while (it.hasNext()) {
			Entry<VariabilityElement, Integer> e = it.next();
			VariabilityElement v = e.getKey();
			Model p = this.getProblem();
			int arg1 = e.getValue().intValue();
			Constraint aux;
			
			// IntegerVariable arg0 = vars.get(v.getName());
			// aux = eq(arg0, arg1);
			if (v instanceof GenericFeature) {
				IntegerVariable arg0 = vars.get(v.getName());
				aux = eq(arg0, arg1);
				if (!features.containsKey(((GenericFeature) v).getName())) {
					//si no existe la feature, nos encargamos de que pete la configuracion
					// the constraint is created to not have a solution for the problem
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
					p.addConstraint(aux);
					this.configConstraints.add(aux);
				}

			} else {
				IntegerExpressionVariable arg0 = rels.get(v.getName());
				aux = eq(arg0, arg1);
				if (!this.getSetRelations().keySet().contains(v.getName())) {
					//si no existe la relacion, nos encargamos de que pete la configuracion
					// the constraint is created to not have a solution for the problem
					IntegerVariable errorVar = makeIntVar("error", 0, 0,
							"cp:no_decision");
					Constraint error = eq(1, errorVar);
					if (e.getValue() == 0) {
						System.err.println("The relation " + v.getName()
								+ "do not exist already in to the model");

					} else {
						p.addConstraint(error);
						this.configConstraints.add(error);
						System.err.println("The relation " + v.getName()
								+ "do not exist, and can not be added");
					}
				} else {
					p.addConstraint(aux);
					this.configConstraints.add(aux);
				}
			}

		}

	}

	@Override
	public void unapplyStagedConfigurations() {
		Iterator<Constraint> it = this.configConstraints.iterator();
		Model p = this.getProblem();
		while (it.hasNext()) {
			Constraint cons = it.next();
			p.removeConstraint(cons);
			it.remove();
		}

	}

	protected Constraint createMandatory(GenericRelation rel,
			GenericFeature child, GenericFeature parent) {

		IntegerVariable childVar = variables.get(child.getName());
		IntegerVariable parentVar = variables.get(parent.getName());
		Constraint mandatoryConstraint = ifOnlyIf(eq(parentVar, 1), eq(
				childVar, 1));
		dependencies.put(rel.getName(), mandatoryConstraint);
		return mandatoryConstraint;

	}

	protected Constraint createOptional(GenericRelation rel,
			GenericFeature child, GenericFeature parent) {

		IntegerVariable childVar = variables.get(child.getName());
		IntegerVariable parentVar = variables.get(parent.getName());
		Constraint optionalConstraint = implies(eq(parentVar, 0), eq(childVar,
				0));
		dependencies.put(rel.getName(), optionalConstraint);
		return optionalConstraint;

	}

	protected Constraint createCardinality(GenericRelation rel,
			GenericFeature child, GenericFeature parent,
			Iterator<Cardinality> cardinalities) {

		IntegerVariable childVar = variables.get(child.getName());
		IntegerVariable parentVar = variables.get(parent.getName());

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
		IntegerVariable cardinalityVar = makeIntVar(rel.getName() + "_card",
				cardValuesArray, "cp:no_decision");
		Constraint cardConstraint = ifThenElse(gt(parentVar, 0), eq(childVar,
				cardinalityVar), eq(childVar, 0));
		dependencies.put(rel.getName(), cardConstraint);
		return cardConstraint;

	}

	protected Constraint createRequires(GenericRelation rel,
			GenericFeature origin, GenericFeature destination) {

		IntegerVariable originVar = variables.get(origin.getName());
		IntegerVariable destinationVar = variables.get(destination.getName());
		Constraint requiresConstraint = implies(gt(originVar, 0), gt(
				destinationVar, 0));
		dependencies.put(rel.getName(), requiresConstraint);
		return requiresConstraint;

	}

	protected Constraint createExcludes(GenericRelation rel,
			GenericFeature origin, GenericFeature dest) {

		IntegerVariable originVar = variables.get(origin.getName());
		IntegerVariable destVar = variables.get(dest.getName());
		Constraint excludesConstraint = implies(gt(originVar, 0),
				eq(destVar, 0));
		dependencies.put(rel.getName(), excludesConstraint);
		return excludesConstraint;

	}

	protected Constraint createSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {

		Cardinality card = null;
		// This constraint should be as ifThenElse(A>0;sum(B,C) in
		// {n,m};B=0,C=0)
		// Save the parent to check the value
		IntegerVariable parentVar = variables.get(parent.getName());

		// Save the cardninality if exist from the parameter cardinalities
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

		IntegerVariable cardinalityVar = makeIntVar(rel.getName() + "_card",
				cardValuesArray, "cp:no_decision");// cp:no_decision
		problem.addVariable(cardinalityVar);
		// Save all children to have the posiblitily of sum them
		ArrayList<IntegerVariable> varsList = new ArrayList<IntegerVariable>();
		Iterator<GenericFeature> it = children.iterator();

		while (it.hasNext()) {
			varsList.add(variables.get(it.next().getName()));
		}

		// creates the sum constraint with the cardinality variable
		// If parent var is equal to 0 then he sum of children has to be 0
		IntegerVariable[] aux = {};
		aux = varsList.toArray(aux);

		// If parent is greater than 0, then apply the restriction
		// ifThenElse(A>0;sum(B,C) in {n,m};B=0,C=0)
		Constraint setConstraint = ifThenElse(gt(parentVar, 0), eq(sum(aux),
				cardinalityVar), eq(sum(aux), 0));
		dependencies.put(rel.getName(), setConstraint);
		setRelations.put(rel.getName(), sum(aux));
		// setRelations.put(rel.getName(), cardinalityVar);
		return setConstraint;
	}

	@Override
	public Map<String, Object> getHeusistics() {
		return heuristicsMap;
	}

	@Override
	public void setHeuristic(Object obj) {
		this.heuristic = (AbstractIntVarSelector) obj;

	}

	public AbstractIntVarSelector getHeuristic() {
		return this.heuristic;
	}

	public IntegerVariable[] getVars() {
		
		IntegerVariable[] res=new IntegerVariable[variables.values().size()];
		Iterator<IntegerVariable> it= variables.values().iterator();
		int i =0;
		while(it.hasNext()){
			res[i]=it.next();
			i++;
		}
		return res;
	}
}
