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
package es.us.isa.JavaBDDReasoner;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Reasoner.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BuDDyFactory;
import net.sf.javabdd.JFactory;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;

import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class JavaBDDReasoner extends FeatureModelReasoner {

	/**
	 * @uml.property name="factory"
	 */
	private BDDFactory factory; // JFactory
	/**
	 * @uml.property name="variables"
	 * @uml.associationEnd qualifier="key:java.lang.Object net.sf.javabdd.BDD"
	 */
	private Map<String, BDD> variables; // Variables<featurename,BDD>
	/**
	 * @uml.property name="vars"
	 * @uml.associationEnd qualifier="key:java.lang.Object java.lang.String"
	 */
	private Map<Integer, String> vars; // Variables<indexvar,featurename>
	private ArrayList<BDD> subtrees; // SubTrees
	private int numvar; // Number of variables
	private BDD bdd; // BDD
	private Map<String, GenericFeature> featuresMap; // Features Map that links
														// vars and features

	public JavaBDDReasoner() {
		reset();
	}

	@Override
	public void reset() {
		
		
		variables = new HashMap<String, BDD>();
		featuresMap = new HashMap<String, GenericFeature>();
		vars = new HashMap<Integer, String>();
		subtrees = new ArrayList<BDD>();
		factory = JFactory.init(1000000, 10000); // This can be optimized taking
		numvar = 1;								 // into account the size of
	}

	public BDD getBDD() {
		return bdd;
	}

	public void setBDD(BDD bdd) {
		this.bdd = bdd;
	}

	public BDD getBDDVar(String namefeature) {
		return variables.get(namefeature);
	}

	public String getBDDVar(int index) {
		return vars.get(index);

	}

	public BDDFactory getBDDFactory() {
		return factory;
	}

	// Create the BDD
	void createBDD() {

		Iterator<BDD> it = subtrees.iterator();
		while (it.hasNext()) {
			BDD subtree = (BDD) it.next();
			bdd = bdd.apply(subtree, BDDFactory.and);
		}
	}

	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		// Get features
		BDD bdd_origin = variables.get(origin.getName());
		BDD bdd_destination = variables.get(destination.getName());

		// Relation
		BDD bdd_and = bdd_origin.apply(bdd_destination, BDDFactory.and);
		BDD bdd_excludes = bdd_and.not();
		subtrees.add(bdd_excludes);
	}

	@Override
	public void addFeature(GenericFeature feature,
			Collection<Cardinality> cardIt) {
		String varName = feature.getName();
		factory.setVarNum(numvar);
		BDD bdd_var = factory.ithVar(numvar - 1);
		
		variables.put(varName, bdd_var); // Save (name,bdd)
		vars.put(numvar - 1, varName); // Save (index,name)
		numvar++;
		featuresMap.put(varName, feature);
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		// Get parent feature
		BDD bdd_parent = variables.get(parent.getName());

		// Get child feature
		BDD bdd_child = variables.get(child.getName());

		// Relation
		BDD bdd_mandatory = bdd_parent.apply(bdd_child, BDDFactory.biimp);
		subtrees.add(bdd_mandatory);
	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		// Get parent feature
		BDD bdd_parent = variables.get(parent.getName());

		// Get child feature
		BDD bdd_child = variables.get(child.getName());

		BDD bdd_optional = bdd_child.apply(bdd_parent, BDDFactory.imp);
		subtrees.add(bdd_optional);
	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {
		// Get features
		BDD bdd_origin = variables.get(origin.getName());
		BDD bdd_destination = variables.get(destination.getName());

		// Relation
		BDD bdd_requires = bdd_origin.apply(bdd_destination, BDDFactory.imp);
		subtrees.add(bdd_requires);
	}

	@Override
	public void addRoot(GenericFeature feature) {
		// Get bdd var
		BDD bdd_root = variables.get(feature.getName());
		bdd = bdd_root; // BDD = root
	}

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {

		GenericFeature feature;
		Iterator<Cardinality> iter = cardinalities.iterator();
		Cardinality card = (Cardinality) iter.next();

		if (card.getMax() != 1) {

			// =============
			// OR Relation
			// =============

			// (children1 or children2 or ...)

			BDD or_childrens = factory.zero();

			Iterator<GenericFeature> it = children.iterator();
			while (it.hasNext()) {
				feature = (GenericFeature) it.next();
				BDD bdd_var = variables.get(feature.getName());
				or_childrens = or_childrens.apply(bdd_var, BDDFactory.or);
			}

			// parent <-> (children1 or children2 or ...)

			// Get parent feature
			BDD bdd_parent = variables.get(parent.getName());

			// Relation
			BDD bdd_or_relation = bdd_parent.apply(or_childrens,
					BDDFactory.biimp);
			subtrees.add(bdd_or_relation);

		} else {

			// ======================
			// ALTERNATIVE Relation
			// ======================

			BDD bdd_alternative = null;

			// Get parent feature
			BDD bdd_parent = variables.get(parent.getName());

			// Insert the children collection in an ArrayList (We need direct
			// access with indexes)
			ArrayList<BDD> childrenList = new ArrayList<BDD>();
			Iterator<GenericFeature> it = children.iterator();
			while (it.hasNext()) {
				feature = (GenericFeature) it.next();
				BDD bdd_child = variables.get(feature.getName());
				childrenList.add(bdd_child);
			}

			for (int i = 0; i < childrenList.size(); i++) {
				BDD tmp_and = null;
				boolean first = true;
				for (int k = 0; k < childrenList.size(); k++) {
					if (i != k) {
						BDD bdd_children = childrenList.get(k);
						BDD notChildren = bdd_children.not();
						if (first) {
							tmp_and = notChildren;
							first = false;
						} else
							tmp_and.andWith(notChildren);
					}
				}

				// Not children1 and not children2 and not childrenN and parent
				BDD tmp_alternative = tmp_and.apply(bdd_parent, BDDFactory.and);

				if (bdd_alternative == null)
					bdd_alternative = childrenList.get(i).apply(
							tmp_alternative, BDDFactory.biimp);
				else
					bdd_alternative.andWith(childrenList.get(i).apply(
							tmp_alternative, BDDFactory.biimp));
			}

			// Relation
			subtrees.add(bdd_alternative);
		}
	}

	public PerformanceResult ask(Question q) {
		if (q == null) {
			throw new FAMAParameterException("Question :Not specified");
		}
		PerformanceResult res;
		JavaBDDQuestion sq = (JavaBDDQuestion) q;
		sq.preAnswer(this);
		res = sq.answer(this);
		sq.postAnswer(this);
		return res;
	}

	public GenericFeature getFeatureByVarName(String varName) {
		GenericFeature res = null;
		res = featuresMap.get(varName);
		return res;
	}

	public Collection<GenericFeature> getAllFeatures() {
		return this.featuresMap.values();
	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {

		bdd = this.getBDD();
		Iterator<Entry<VariabilityElement, Integer>> valuesIt = conf
				.getElements().entrySet().iterator();
		while (valuesIt.hasNext()) {
			Entry<VariabilityElement, Integer> value = valuesIt.next();
			if (value.getValue() > 0) {
				BDD one = ((BDDFactory) this.getBDDFactory()).one();
				BDD var = this.getBDDVar(value.getKey().getName());
				BDD filter = one.apply(var,
						BDDFactory.biimp);
				BDD bdd_aux = this.getBDD();
				bdd_aux = bdd_aux.apply(filter, BDDFactory.and);
				this.setBDD(bdd_aux);

			}
			if (value.getValue() == 0) {
				BDD one = ((BDDFactory) this.getBDDFactory()).zero();
				BDD var = this.getBDDVar(value.getKey().getName());
				BDD filter = one.apply(var,
						BDDFactory.biimp);
				BDD bdd_aux = this.getBDD();
				bdd_aux = bdd_aux.apply(filter, BDDFactory.and);
				this.setBDD(bdd_aux);

			}
		}

	}

	@Override
	public void unapplyStagedConfigurations() {
		this.setBDD(bdd);
	}

	@Override
	public Map<String, Object> getHeusistics() {
		
		return new HashMap<String, Object>();
	}

	@Override
	public void setHeuristic(Object obj) {
		// TODO Auto-generated method stub
		
	}
}
