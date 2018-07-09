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
package es.us.isa.JaCoPReasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import JaCoP.constraints.And;
import JaCoP.constraints.Constraint;
import JaCoP.constraints.Eq;
import JaCoP.constraints.IfThen;
import JaCoP.constraints.IfThenElse;
import JaCoP.constraints.In;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XgtC;
import JaCoP.core.Domain;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Variable;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.LargestDomain;
import JaCoP.search.LargestMin;
import JaCoP.search.MaxRegret;
import JaCoP.search.MinDomainOverDegree;
import JaCoP.search.MostConstrainedDynamic;
import JaCoP.search.SmallestDomain;
import JaCoP.search.SmallestMax;
import JaCoP.search.SmallestMin;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class JaCoPReasoner extends FeatureModelReasoner {

	protected Map<String, GenericFeature> features;
	protected GenericFeature root;
	protected FDstore store;
	protected Map<String, FDV> variables;
	protected ArrayList<Constraint> constraints;
	protected Map<String, Constraint> relationConstraintMap;
	protected boolean consistent;
	protected ComparatorVariable heuristics;
	protected static Map<String, Object> heuristicsMap;
	protected Stack<Configuration> configStack;

	public JaCoPReasoner() {
		reset();
//		store = new FDstore();
//		variables = new HashMap<String, FDV>();
//		constraints = new ArrayList<Constraint>();
//		relationConstraintMap = new HashMap<String, Constraint>();
//		consistent = true;
		heuristics = new MostConstrainedDynamic();
		heuristicsMap = new HashMap<String, Object>();
		heuristicsMap.put("MostConstrainedDynamic", new MostConstrainedDynamic());
		heuristicsMap.put("MostConstrainedDynamic", new MostConstrainedDynamic());
		heuristicsMap.put("LargestDomain", new LargestDomain());
		heuristicsMap.put("LargestMin", new LargestMin());
		heuristicsMap.put("MaxRegret", new MaxRegret());
		heuristicsMap.put("MinDomainOverDegree", new MinDomainOverDegree());
		heuristicsMap.put("SmallestDomain", new SmallestDomain());
		heuristicsMap.put("SmallestMax", new SmallestMax());
		heuristicsMap.put("SmallestMin", new SmallestMin());
//		features = new HashMap<String, GenericFeature>();
//		configStack = new Stack<Configuration>();
	}

	@Override
	public void reset() {
		store = new FDstore();
		variables = new TreeMap<String, FDV>();
		constraints = new ArrayList<Constraint>();
		relationConstraintMap = new TreeMap<String, Constraint>();
		consistent = true;
		root = null;
		features = new TreeMap<String, GenericFeature>();
		configStack = new Stack<Configuration>();
	}

	@Override
	public void addFeature(GenericFeature f, Collection<Cardinality> cards) {
		FDV var = createFeature(f, cards);
		features.put(f.getName(), f);
		variables.put(f.getName(), var);
	}

	@Override
	public void addRoot(GenericFeature feature) {
		Variable root = variables.get(feature.getName());
		PrimitiveConstraint constraint = createRoot(root);
		constraints.add(constraint);
		store.impose(constraint);
		// relationConstraintMap.put(null,constraint);
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		PrimitiveConstraint constraint = createMandatory(rel, child, parent);
		store.impose(constraint);
	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		
		PrimitiveConstraint constraint = createOptional(rel, child, parent);
		store.impose(constraint);
		
	}

	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {

		PrimitiveConstraint constraint = createCardinality(rel, child, parent,
				cardinalities);
		store.impose(constraint);

	}

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {

		PrimitiveConstraint constraint = createSet(rel, parent, children,
				cardinalities);
		store.impose(constraint);

	}

	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		PrimitiveConstraint constraint = createExcludes(rel, origin,
				destination);
		store.impose(constraint);

	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		PrimitiveConstraint constraint = createRequires(rel, origin,
				destination);
		store.impose(constraint);

	}

	protected PrimitiveConstraint createRoot(Variable root) {
		PrimitiveConstraint constraint = new XeqC(root, 1);
		return constraint;
	}

	protected FDV createFeature(GenericFeature f,
			Collection<Cardinality> cards) {
		Iterator<Cardinality> cardIt = cards.iterator();
		FDV var = new FDV(store, f.getName(), 0, 1);
		while (cardIt.hasNext()) {
			Cardinality card = cardIt.next();
			var.addDom(card.getMin(), card.getMax());
		}
		return var;
	}

	protected PrimitiveConstraint createMandatory(GenericRelation rel,
			GenericFeature child, GenericFeature parent) {
		Variable childVar = variables.get(child.getName());
		Variable parentVar = variables.get(parent.getName());
		// esta forma de representar el mandatory, �es correcta?
		// PrimitiveConstraint constraint = new XeqY(childVar,parentVar);
		// esta forma es mas correcta
		PrimitiveConstraint constraint = new Eq(new XeqC(parentVar, 1),
				new XeqC(childVar, 1));
		saveConstraint(rel, constraint);
		return constraint;
	}

	protected PrimitiveConstraint createOptional(GenericRelation rel,
			GenericFeature child, GenericFeature parent) {
		
		Variable childVar = variables.get(child.getName());
		Variable parentVar = variables.get(parent.getName());
//		PrimitiveConstraint constraint = new IfThen(new XeqC(parentVar, 0),
//				new XeqC(childVar, 0));
		PrimitiveConstraint constraint = new IfThen(new XeqC(childVar, 1),
				new XeqC(parentVar, 1));
		saveConstraint(rel, constraint);
		return constraint;
		
	}

	protected PrimitiveConstraint createSet(GenericRelation rel,
				GenericFeature parent, Collection<GenericFeature> children,
				Collection<Cardinality> cardinalities) {
			
			String setVarName = rel.getName();
			FDV parentVar = variables.get(parent.getName());
			// create the sum variable
			FDV setVar = new FDV(store, setVarName);
			setVar.addDom(0, children.size());
			Domain domain = new IntervalDomain();
			Iterator<Cardinality> itc = cardinalities.iterator();
			while (itc.hasNext()) {
				Cardinality card = itc.next();
				domain.addDom(card.getMin(), card.getMax());
			}
			// guardamos como una variable mas la suma de los hijos
			variables.put(rel.getName(), setVar);
	
			// create a set of variables to sum them
			ArrayList<Variable> varsList = new ArrayList<Variable>();
			Iterator<GenericFeature> it = children.iterator();
			while (it.hasNext()) {
				varsList.add(variables.get(it.next().getName()));
			}
	
			// creates the sum constraint with the cardinality variable
			Constraint suma = new Sum(varsList, setVar);
			// it is not a primitive constraint and cannot be reified
			constraints.add(suma);
			store.impose(suma);
	
			// creates the arraylist of constraints for the And constraint
			ArrayList<PrimitiveConstraint> localConstraints = new ArrayList<PrimitiveConstraint>();
			it = children.iterator();
			while (it.hasNext()) {
				Variable childVar = variables.get(it.next().getName());
				localConstraints.add(new XeqC(childVar, 0));
			}
	
			// TODO done 4 David Benavides
	//		PrimitiveConstraint constraintG = new IfThen(new XeqC(setVar, 0),
	//				new XeqC(parentVar, 0));
	//		constraints.add(constraintG);
	//		store.impose(constraintG);
			// relationConstraintMap.put(constraintG,rel);
	
			// adding the second constraint to the store
			// TODO cambiar In por XeqC(setVar,0)
			PrimitiveConstraint constraint = new IfThenElse(new XgtC(parentVar, 0),
					new In(setVar, domain), new And(localConstraints));
			saveConstraint(rel, constraint);
			return constraint;
			
		}

	protected PrimitiveConstraint createCardinality(GenericRelation rel,
				GenericFeature child, GenericFeature parent,
				Iterator<Cardinality> cardinalities) {
	
			Variable childVar = variables.get(child.getName());
			Variable parentVar = variables.get(parent.getName());
	
			Domain domain = new IntervalDomain();
			if (!cardinalities.hasNext())
				domain.addDom(0, 0);
			while (cardinalities.hasNext()) {
				Cardinality card = cardinalities.next();
				domain.addDom(card.getMin(), card.getMax());
			}
	
			// TODO constraint done 4 David Benavides
	//		PrimitiveConstraint constraintG = new IfThen(new XeqC(childVar, 0),
	//				new XeqC(parentVar, 0));
	//		constraints.add(constraintG);
	//		// relationConstraintMap.put(constraintG,rel);
	//		store.impose(constraintG);
	
			// FIXME no s� si funcionar� correctamente esta nueva expresi�n,
			// creo que s�
			PrimitiveConstraint constraint = new IfThenElse(new XeqC(parentVar, 0),
					new XeqC(childVar, 0), new In(childVar, domain));
			saveConstraint(rel, constraint);
			return constraint;
		}

	protected PrimitiveConstraint createExcludes(GenericRelation rel,
			GenericFeature origin, GenericFeature destination) {
	
		Variable originVar = variables.get(origin.getName());
		Variable destVar = variables.get(destination.getName());
		// FIXME it should work in this way, but not sure
		PrimitiveConstraint constraint = new IfThen(new XgtC(originVar, 0),
				new XeqC(destVar, 0));
		saveConstraint(rel, constraint);
		return constraint;
	
	}

	protected PrimitiveConstraint createRequires(GenericRelation rel,
			GenericFeature origin, GenericFeature destination) {

		Variable originVar = variables.get(origin.getName());
		Variable destVar = variables.get(destination.getName());
		// FIXME it should work in this way, but not sure
		PrimitiveConstraint constraint = new IfThen(new XgtC(originVar, 0),
				new XgtC(destVar, 0));
		saveConstraint(rel, constraint);
		return constraint;

	}

	protected void saveConstraint(GenericRelation rel,
			PrimitiveConstraint constraint) {
		constraints.add(constraint);
		relationConstraintMap.put(rel.getName(), constraint);
	}

	public PerformanceResult ask(Question q) {
		if (q == null) {
			throw new FAMAParameterException("Question: Not specified");
		}
		PerformanceResult res;
		JaCoPQuestion jq = (JaCoPQuestion) q;
		if (heuristics != null)
			jq.setHeuristics(heuristics);
		jq.preAnswer(this);
		res = jq.answer(this);
		jq.postAnswer(this);
		consistent = true;
		return res;
	}

	public Constraint getRelationConstraint(GenericRelation r) {
		String s = r.getName();
		return relationConstraintMap.get(s);
	}

	public FDV getVariable(VariabilityElement v) {
		String s = v.getName();
		return variables.get(s);
	}

	public boolean consistency() {
		if (consistent)
			consistent = store.consistency();
		return consistent;
	}

	public ArrayList<FDV> getVariables() {
		ArrayList<FDV> res = new ArrayList<FDV>();
		Iterator<Entry<String, FDV>> it = variables.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, FDV> elem = it.next();
			res.add(elem.getValue());
		}
		return res;
	}

	public FDstore getStore() {
		return store;
	}

	public GenericFeature searchFeatureByName(String id) {
		return features.get(id);
	}

	public GenericFeature getRoot() {
		return root;
	}

	public Collection<GenericFeature> getAllFeatures() {
		return this.features.values();
	}

	public int getStoreLevel() {
		return store.level;
	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {
		
		if (!conf.getElements().isEmpty()) {
			configStack.push(conf);
			FDstore store = this.getStore();
			int storeLevel = store.level;
			// and apply consistency to make the changes available to the next
			// level
			// TODO es necesario aplicar consistencia? funcionara con las
			// explanations?
			
//			if (consistent) {
//				consistent = this.consistency();
				store.setLevel(storeLevel + 1);

				// now we impose the new constraints
				Iterator<Entry<VariabilityElement, Integer>> it = conf
						.getElements().entrySet().iterator();
				ArrayList<FDV> vars = this.getVariables();
				while (it.hasNext()) {
					Entry<VariabilityElement, Integer> e = it.next();
					VariabilityElement f = e.getKey();
					Iterator<FDV> it2 = vars.iterator();
					// Variable v = variables.get(f.getName());
					// boolean varFound = (v != null);
					boolean varFound = false;
					Variable v;
					while (it2.hasNext() && !varFound) {
						v = it2.next();
						if (v.id().equalsIgnoreCase(f.getName())) {
							store.impose(new XeqC(v, e.getValue().intValue()));
							varFound = true;
						}

					}

					if (!varFound && e.getValue().intValue() == 0) {
						// la variable no esta en el modelo y no se quiere
						// a�adir
						System.err.println("The feature " + f.getName()
								+ " do not exist on the model");
					}
					if (!varFound && e.getValue().intValue() == 1) {
						// la variable no esta en el modelo y se quiere a�adir
						System.err
								.println("The feature "
										+ f.getName()
										+ " do not exist on the model, and can not be added");
						Variable a = new Variable(store, f.getName(), 0, 0);
						store.impose(new XeqC(a, 1));

					}
				}
			}
//		}

	}

	@Override
	public void unapplyStagedConfigurations() {
		
		if (consistent && !configStack.empty()) {
//		if (!configStack.empty()){
			//int size = configStack.size();
			int level = store.level;
			store.removeLevel(level);
//			store.setLevel(level - 1);
			
			
			configStack.pop();
		}
		
	}

	@Override
	public Map<String, Object> getHeusistics() {
		return heuristicsMap;
	}

	@Override
	public void setHeuristic(Object obj) {
		this.heuristics=(ComparatorVariable)obj;
	}

}
