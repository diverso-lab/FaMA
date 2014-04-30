/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.Sat4jReasoner.reified;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;

public class Sat4jReifiedReasoner extends FeatureModelReasoner{

	/**
	 * @uml.property name="variables"
	 * @uml.associationEnd qualifier="key:java.lang.Object java.lang.String"
	 */
	private Map<String, Integer> variables; // Variables<FeatureName,SATVarNumber>

	private Map<String, GenericFeature> featuresMap;

	private String pathFile;
//	private ArrayList<String> clauses; // Clauses

	private int numvar; // Number of variables

//	private ArrayList<String> addedClauses;
	
	private List<IVecInt> clauses;
	
	private List<IVecInt> addedClauses;
	
	private Map<Integer,GenericRelation> reifiedRelations;

	private IVecInt reifiedVars;
	

	public Sat4jReifiedReasoner() {
		reset();
	}

	protected void finalize() {
	}

	public Map<String, Integer> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Integer> variables) {
		this.variables = variables;
	}

	
	@Override
	public void reset() {
		variables = new HashMap<String, Integer>();
		featuresMap = new HashMap<String, GenericFeature>();
//		clauses = new ArrayList<String>();
//		addedClauses = new ArrayList<String>();
		clauses = new LinkedList<IVecInt>();
		addedClauses = new LinkedList<IVecInt>();
		reifiedRelations = new HashMap<Integer,GenericRelation>();
		reifiedVars = new VecInt();
		numvar = 1;
	}


	public List<IVecInt> getClauses() {
		return clauses;
	}

	public Integer getCNFVar(String featurename) {
		return variables.get(featurename);
	}

	public String getPathFile() {
		return this.pathFile;
	}

	// Return the name of the feature associate to cnf_var
	public GenericFeature getFeature(Integer cnf_var) {
		String featureName = "";
		Iterator<Entry<String, Integer>> it = variables.entrySet().iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			Entry<String, Integer> e = it.next();
			if (e.getValue().equals(cnf_var)) {
				found = true;
				featureName = e.getKey();
			}
		}
		return featuresMap.get(featureName);
	}

	// Generate the CNF File (Input of Sat4j)
	public void createSAT() {

		//FIXME modificar este metodo
		//en teoria, no debe ni hacer falta
//		String cnf_content = "c CNF file\n";
//
//		// We show as comments the variables's number
//		Iterator<String> it = variables.keySet().iterator();
//		while (it.hasNext()) {
//			String varName = it.next();
//			cnf_content += "c var " + variables.get(varName) + " = " + varName
//					+ "\n";
//		}
//
//		// Start the problem
//		cnf_content += "p cnf " + variables.size() + " " + clauses.size()
//				+ "\n";
//
//		// Clauses
//		Iterator<IVecInt> it2 = clauses.iterator();
//		while (it2.hasNext()) {
//			cnf_content += (String) it.next() + "\n";
//		}
//
//		// End file
//		cnf_content += "0";
//
//		// Create the .cnf file
//		File outputFile = null;
//		try {
//
//			outputFile = File.createTempFile("cnf", "txt");// new
//			// File(filepath);
//			pathFile = outputFile.getAbsolutePath();
//			FileWriter out;
//			out = new FileWriter(outputFile);
//			out.write(cnf_content);
//			out.close();
//
//		} catch (IOException e) {
//			System.out.println("SAT: Error creating temporary cnf file");
//		}
	}

	@Override
	public void addCardinality(GenericRelation rel, GenericFeature child,
			GenericFeature parent, Iterator<Cardinality> cardinalities) {
		// TODO Cardinalities are not supported by SAT4j solver

	}

	@Override
	public void addExcludes(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		// Get features
//		String cnf_origin = variables.get(origin.getName());
//		String cnf_destination = variables.get(destination.getName());
//		String cnf_rel = createReifiedVar(rel);
		int cnf_origin = variables.get(origin.getName());
		int cnf_destination = variables.get(destination.getName());
		int cnf_rel = createReifiedVar(rel);

		// Clauses
		IVecInt cnf1 = new VecInt();
		cnf1.push(-cnf_rel).push(cnf_origin);
		IVecInt cnf2 = new VecInt();
		cnf2.push(-cnf_rel).push(cnf_destination);
		IVecInt cnf3 = new VecInt();
		cnf3.push(cnf_rel).push(-cnf_origin).push(cnf_destination);
//		String cnf1 = "-" + cnf_rel + " " + cnf_origin + " 0";
//		String cnf2 = "-" + cnf_rel + " " + cnf_destination + " 0";
//		String cnf3 = cnf_rel + " -" + cnf_origin + " -" + cnf_destination + " 0";
		clauses.add(cnf1);
		clauses.add(cnf2);
		clauses.add(cnf3);
//		String cnf_excludes = "-" + cnf_origin + " -" + cnf_destination + " 0";
//		clauses.add(cnf_excludes);
	}

	@Override
	public void addFeature(GenericFeature feature,
			Collection<Cardinality> cardIt) {
		variables.put(feature.getName(), numvar);
		numvar++;
		featuresMap.put(feature.getName(), feature);
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		// Get features
		int cnf_parent = variables.get(parent.getName());
		int cnf_child = variables.get(child.getName());
		int cnf_rel = createReifiedVar(rel);
		
		// Clauses
//		String cnf1 = "-" + cnf_rel + " -" + cnf_parent + " -" + cnf_child + " 0";
//		String cnf2 = "-" + cnf_rel + " " + cnf_parent + " " + cnf_child + " 0";
//		String cnf3 = cnf_rel + " " + cnf_parent + " -" + cnf_child + " 0";
//		String cnf4 = cnf_rel + " -" + cnf_parent + " " + cnf_child + " 0";
		IVecInt cnf1 = new VecInt();
		IVecInt cnf2 = new VecInt();
		IVecInt cnf3 = new VecInt();
		IVecInt cnf4 = new VecInt();
		cnf1.push(-cnf_rel).push(-cnf_parent).push(-cnf_child);
		cnf2.push(-cnf_rel).push(cnf_parent).push(cnf_child);
		cnf3.push(cnf_rel).push(cnf_parent).push(-cnf_child);
		cnf4.push(cnf_rel).push(-cnf_parent).push(cnf_child);
		clauses.add(cnf1);
		clauses.add(cnf2);
		clauses.add(cnf3);
		clauses.add(cnf4);

	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		// Get features
		int cnf_parent = variables.get(parent.getName());
		int cnf_child = variables.get(child.getName());
		int cnf_rel = createReifiedVar(rel);

		// Clauses
//		String cnf1 = "-" + cnf_rel + " " + cnf_child + " 0";
//		String cnf2 = "-" + cnf_rel + " -" + cnf_parent + " 0";
//		String cnf3 = cnf_rel + " " + cnf_parent + " -" + cnf_child + " 0";
		IVecInt cnf1 = new VecInt();
		IVecInt cnf2 = new VecInt();
		IVecInt cnf3 = new VecInt();
		cnf1.push(-cnf_rel).push(cnf_child);
		cnf2.push(-cnf_rel).push(-cnf_parent);
		cnf3.push(cnf_rel).push(cnf_parent).push(-cnf_child);
		clauses.add(cnf1);
		clauses.add(cnf2);
		clauses.add(cnf3);
//		String cnf_optional = "-" + cnf_child + " " + cnf_parent + " 0";
//		clauses.add(cnf_optional);

	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		// Get features
		int cnf_origin = variables.get(origin.getName());
		int cnf_destination = variables.get(destination.getName());
		int cnf_rel = createReifiedVar(rel);

		// Clauses
//		String cnf1 = "-" + cnf_rel + " " + cnf_origin + " 0";
//		String cnf2 = "-" + cnf_rel + " -" + cnf_destination + " 0";
//		String cnf3 = cnf_rel + " -" + cnf_origin + " " + cnf_destination + " 0";
		IVecInt cnf1 = new VecInt();
		IVecInt cnf2 = new VecInt();
		IVecInt cnf3 = new VecInt();
		cnf1.push(-cnf_rel).push(cnf_origin);
		cnf2.push(-cnf_rel).push(-cnf_destination);
		cnf3.push(cnf_rel).push(-cnf_origin).push(cnf_destination);
		clauses.add(cnf1);
		clauses.add(cnf2);
		clauses.add(cnf3);
//		String cnf_requires = "-" + cnf_origin + " " + cnf_destination + " 0";
//		clauses.add(cnf_requires);

	}

	@Override
	public void addRoot(GenericFeature feature) {
		int root = variables.get(feature.getName());

		// Clause
//		String cnf_root = root + " 0";
		IVecInt cnf_root = new VecInt();
		cnf_root.push(root);
		clauses.add(cnf_root);

	}

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		// TODO: Lanzar un error si no hay una y s�lo una cardinalidad en la
		// lista

		Iterator<Cardinality> iter = cardinalities.iterator();
		Cardinality card = iter.next();

		if (card.getMax() != 1) {

			// =============
			// OR Relation
			// =============

			int cnf_parent = variables.get(parent.getName());
			int cnf_rel = createReifiedVar(rel);
			
//			String r1 = " -" + cnf_rel + " " + cnf_parent;
//			String r2 = cnf_rel + " -"+ cnf_parent;
			IVecInt r1 = new VecInt();
			IVecInt r2 = new VecInt();
			r1.push(-cnf_rel).push(cnf_parent);
			r2.push(cnf_rel).push(-cnf_parent);
			Iterator<GenericFeature> it = children.iterator();
			while (it.hasNext()){
				GenericFeature f = it.next();
				int cnf_child = variables.get(f.getName());
//				r1 = r1 + " " + cnf_child;
//				r2 = r2 + " " + cnf_child;
				r1.push(cnf_child);
				r2.push(cnf_child);
//				String aux1 = "-" + cnf_rel + " -" + cnf_parent + " -" + cnf_child + " 0";
//				String aux2 = cnf_rel + " " + cnf_parent + " -" + cnf_child + " 0";
				IVecInt aux1 = new VecInt();
				IVecInt aux2 = new VecInt();
				aux1.push(-cnf_rel).push(-cnf_parent).push(-cnf_child);
				aux2.push(cnf_rel).push(cnf_parent).push(-cnf_child);
				clauses.add(aux1);
				clauses.add(aux2);
			}
//			r1 = r1 + " 0";
//			r2 = r2 + " 0";
			clauses.add(r1);
			clauses.add(r2);
			
		} else {

			// ======================
			// ALTERNATIVE Relation
			// ======================

			int cnf_parent = variables.get(parent.getName());
			int cnf_rel = createReifiedVar(rel);
			Iterator<GenericFeature> it1 = children.iterator();
			Iterator<GenericFeature> it2 = children.iterator();
			
//			String r1 = " -" + cnf_rel + " " + cnf_parent;
//			String r2 = cnf_rel + " -" + cnf_parent;
			IVecInt r1 = new VecInt();
			IVecInt r2 = new VecInt();
			r1.push(-cnf_rel).push(cnf_parent);
			r2.push(cnf_rel).push(-cnf_parent);
			
			while (it1.hasNext()){
				GenericFeature f1 = it1.next();
				int var1 = variables.get(f1.getName());
//				r1 = r1 + " " + var1;
//				r2 = r2 + " " + var1;
				r1.push(var1);
				r2.push(var1);
//				String aux1 = "-" + cnf_rel + " -" + cnf_parent + " -" + var1;
//				String aux2 = cnf_rel + " " + cnf_parent + " -" + var1;
				IVecInt aux1 = new VecInt();
				IVecInt aux2 = new VecInt();
				aux1.push(-cnf_rel).push(-cnf_parent).push(-var1);
				aux2.push(cnf_rel).push(cnf_parent).push(-var1);
				while (it2.hasNext()){
					GenericFeature f2 = it2.next();
					int var2 = variables.get(f2.getName());
					if (var1 != var2){
//						aux1 = aux1 + " " + var2;
//						aux2 = aux2 + " " + var2;
						aux1.push(var2);
						aux2.push(var2);
//						String aux3 = "-" + cnf_rel + " " + cnf_parent + " -" + var1 + " -" + var2 + " 0";
//						String aux4 = cnf_rel + " -" + cnf_parent + " -" + var1 + " -" + var2 + " 0";
						IVecInt aux3 = new VecInt();
						IVecInt aux4 = new VecInt();
						aux3.push(-cnf_rel).push(cnf_parent).push(-var1).push(var2);
						aux4.push(cnf_rel).push(-cnf_parent).push(-var1).push(-var2);
						clauses.add(aux3);
						clauses.add(aux4);
					}
				}
//				aux1 = aux1 + " 0";
//				aux2 = aux2 + " 0";
				clauses.add(aux1);
				clauses.add(aux2);
			}
//			r1 = r1 + " 0";
//			r2 = r2 + " 0";
			clauses.add(r1);
			clauses.add(r2);

		}

	}

	public PerformanceResult ask(Question q) {
		if (q == null) {
			throw new FAMAParameterException("questions :Not specified");
		}
		
		PerformanceResult res;
		Sat4jReifiedQuestion sq = (Sat4jReifiedQuestion) q;
		sq.preAnswer(this);
		res = sq.answer(this);
		
		sq.postAnswer(this);

		return res;
	}

	public Collection<GenericFeature> getAllFeatures() {
		return this.featuresMap.values();
	}

	public int getnumVar() {
		return numvar;
	}

	@Override
	public void applyStagedConfiguration(Configuration conf) {
		// Added Features
		Iterator<Entry<VariabilityElement, Integer>> it = conf.getElements()
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<VariabilityElement, Integer> entry = it.next();
			if (entry.getKey() instanceof GenericFeature) {
				int cnf_var = getCNFVar(entry.getKey().getName());
//				String clause = "";
				IVecInt clause = new VecInt();
				if (entry.getValue() == 0) {
//					clause = "-"+cnf_var + " 0";
					clause.push(-cnf_var);
				}
				if (entry.getValue() > 0) {
//					clause = cnf_var + " 0";
					clause.push(cnf_var);
					clauses.add(clause);
					addedClauses.add(clause);
				}
				clauses.add(clause);
				addedClauses.add(clause);
			}
		}

	}

	@Override
	public void unapplyStagedConfigurations() {
		Iterator<IVecInt> it = addedClauses.iterator();
		while (it.hasNext())
			this.getClauses().remove(it.next());
	}

	@Override
	public Map<String, Object> getHeusistics() {
		return new HashMap<String, Object>();
	}

	@Override
	public void setHeuristic(Object obj) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<Integer, GenericRelation> getReifiedRelations() {
		return reifiedRelations;
	}
	
	private int createReifiedVar(GenericRelation r){
//		String aux = Integer.toString(numvar);
		int aux = numvar;
		variables.put(r.getName(), aux);
		numvar++;
		reifiedRelations.put(numvar, r);
		reifiedVars.push(aux);
		return aux;
	}
	
	public IVecInt getReifiedVars(){
		return reifiedVars;
	}

}
