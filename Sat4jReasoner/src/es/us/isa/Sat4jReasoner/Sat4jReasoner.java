/*
o, 	This file is part of FaMaTS.

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
package es.us.isa.Sat4jReasoner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

//import net.sf.javabdd.BDD;
//import net.sf.javabdd.JFactory;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAParameterException;
import es.us.isa.FAMA.Reasoner.FeatureModelReasoner;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class Sat4jReasoner extends FeatureModelReasoner {

	/**
	 * @uml.property name="variables"
	 * @uml.associationEnd qualifier="key:java.lang.Object java.lang.String"
	 */
	public Map<String, String> variables; // Variables<FeatureName,SATVarNumber>
	
	public boolean easeGeneration=false;
	public String easeGenString;
	public int easeIncrement=0;
	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	private Map<String, GenericFeature> featuresMap;

	//private String pathFile;
	public ArrayList<String> clauses; // Clauses

	private int numvar; // Number of variables

	private ArrayList<String> addedClauses;

	private InputStream stream;
	
	public Sat4jReasoner() {
		reset();
	}

	protected void finalize() {
	}

	@Override
	public void reset() {
		variables = new HashMap<String, String>();
		featuresMap = new HashMap<String, GenericFeature>();
		clauses = new ArrayList<String>();
		addedClauses = new ArrayList<String>();
		numvar = 1;
	}


	public ArrayList<String> getClauses() {
		return clauses;
	}

	public String getCNFVar(String featurename) {
		return variables.get(featurename);
	}

//	public String getPathFile() {
//		return this.pathFile;
//	}

	// Return the name of the feature associate to cnf_var
	public GenericFeature getFeature(String cnf_var) {
		String featureName = "";
		Iterator<Entry<String, String>> it = variables.entrySet().iterator();
		boolean found = false;
		while (it.hasNext() && !found) {
			Entry<String, String> e = it.next();
			if (e.getValue().equals(cnf_var)) {
				found = true;
				featureName = e.getKey();
			}
		}
		return featuresMap.get(featureName);
	}

	// Generate the CNF File (Input of Sat4j)
	public void createSAT() {

		String cnf_content = "c CNF file\n";

		// We show as comments the variables's number
		Iterator<String> it = variables.keySet().iterator();
		while (it.hasNext()) {
			String varName = it.next();
			cnf_content += "c var " + variables.get(varName) + " = " + varName
					+ "\n";
		}

		// Start the problem
		if(!easeGeneration){
		cnf_content += "p cnf " + variables.size() + " " + clauses.size()
				+ "\n";
		}else{
			//cnf_content += "p cnf " + variables.size() + " " + (clauses.size() + easeIncrement)	+ "\n";
			cnf_content += "p cnf " + variables.size() + " " + (clauses.size() )	+ "\n";

		}
		// Clauses
		it = clauses.iterator();
		while (it.hasNext()) {
			cnf_content += (String) it.next() + "\n";
		}

		if(!easeGeneration){
		// End file
		cnf_content += "0";
		}else{
			this.easeGenString=cnf_content += "0" ;
		}
		stream= new ByteArrayInputStream(cnf_content.getBytes(StandardCharsets.UTF_8));
		// Create the .cnf file
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
		String cnf_origin = variables.get(origin.getName());
		String cnf_destination = variables.get(destination.getName());

		// Clause
		String cnf_excludes = "-" + cnf_origin + " -" + cnf_destination + " 0";
		clauses.add(cnf_excludes);
	}

	@Override
	public void addFeature(GenericFeature feature,
			Collection<Cardinality> cardIt) {
		variables.put(feature.getName(), Integer.toString(numvar));
		numvar++;
		featuresMap.put(feature.getName(), feature);
	}

	@Override
	public void addMandatory(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		// Get features
		String cnf_parent = variables.get(parent.getName());
		String cnf_child = variables.get(child.getName());

		// Clauses
		String cnf_mandatory1 = "-" + cnf_parent + " " + cnf_child + " 0";
		String cnf_mandatory2 = "-" + cnf_child + " " + cnf_parent + " 0";
		clauses.add(cnf_mandatory1);
		clauses.add(cnf_mandatory2);

	}

	@Override
	public void addOptional(GenericRelation rel, GenericFeature child,
			GenericFeature parent) {
		// Get features
		String cnf_parent = variables.get(parent.getName());
		String cnf_child = variables.get(child.getName());

		// Clause
		String cnf_optional = "-" + cnf_child + " " + cnf_parent + " 0";
		clauses.add(cnf_optional);

	}

	@Override
	public void addRequires(GenericRelation rel, GenericFeature origin,
			GenericFeature destination) {

		// Get features
		String cnf_origin = variables.get(origin.getName());
		String cnf_destination = variables.get(destination.getName());

		// Clause
		String cnf_requires = "-" + cnf_origin + " " + cnf_destination + " 0";
		clauses.add(cnf_requires);

	}

	@Override
	public void addRoot(GenericFeature feature) {
		String root = variables.get(feature.getName());

		// Clause
		String cnf_root = root + " 0";
		clauses.add(cnf_root);

	}

	@Override
	public void addSet(GenericRelation rel, GenericFeature parent,
			Collection<GenericFeature> children,
			Collection<Cardinality> cardinalities) {
		// TODO: Lanzar un error si no hay una y s�lo una cardinalidad en la
		// lista
		// TODO: throw an exception if cardinalities are not correct
		GenericFeature feature;
		Iterator<Cardinality> iter = cardinalities.iterator();
		Cardinality card = iter.next();

		if (card.getMax() != 1) {

			// =============
			// OR Relation
			// =============

			String cnf_parent = variables.get(parent.getName());

			// (no parent or child1 or child2 ...or childn)

			String cnf_or = "-" + cnf_parent + " ";
			Iterator<GenericFeature> it = children.iterator();
			while (it.hasNext()) {
				feature = it.next();
				String cnf_child = variables.get(feature.getName());
				cnf_or += cnf_child + " ";
			}

			cnf_or += "0";
			clauses.add(cnf_or);

			// (no child1 or parent) and (no child2 or parent) and ... (no
			// childn or parent)

			it = children.iterator();
			while (it.hasNext()) {
				feature = (GenericFeature) it.next();
				String cnf_child = variables.get(feature.getName());
				cnf_or = "-" + cnf_child + " " + cnf_parent + " 0";
				clauses.add(cnf_or);
			}
		} else {

			// ======================
			// ALTERNATIVE Relation
			// ======================

			String cnf_parent = variables.get(parent.getName());

			// (children1 or children2 or ... or childrenN or no parent)

			String cnf_alternative = "-" + cnf_parent + " ";

			Iterator<GenericFeature> it = children.iterator();
			while (it.hasNext()) {
				feature = it.next();
				String cnf_child = variables.get(feature.getName());
				cnf_alternative += cnf_child + " ";
			}

			cnf_alternative += "0";
			clauses.add(cnf_alternative);

			// (no child1 or no child2) and ... (no child1 or no childN) and (no
			// child1 or parent) and
			// ...and (no childN or no childN-1) and (no childN or parent)

			// Insert the children collection in an ArrayList (We need direct
			// access with indexes)
			ArrayList<String> childrenList = new ArrayList<String>();
			it = children.iterator();
			while (it.hasNext()) {
				feature = (GenericFeature) it.next();
				String cnf_child = variables.get(feature.getName());
				childrenList.add(cnf_child);
			}

			for (int i = 0; i < childrenList.size(); i++) // for (int i = 0; i <
			// childrenList.size();
			// i++)
			{
				for (int k = i + 1; k < children.size(); k++) // for (int k = 0;
				// k <
				// childrenList.size();
				// k++)
				{
					if (i != k) {
						cnf_alternative = "-" + childrenList.get(i) + " -"
								+ childrenList.get(k) + " 0";
						clauses.add(cnf_alternative);
					}
				}

				cnf_alternative = "-" + childrenList.get(i) + " " + cnf_parent
						+ " 0";
				clauses.add(cnf_alternative);
			}
		}

	}

	public PerformanceResult ask(Question q) {
		if (q == null) {
			throw new FAMAParameterException("questions :Not specified");
		}
		
		PerformanceResult res=new Sat4jResult();
		
		Sat4jQuestion sq = (Sat4jQuestion) q;
		
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
				String cnf_var = getCNFVar(entry.getKey().getName());
				String clause = "";
				if (entry.getValue() == 0) {
					clause = "-"+cnf_var + " 0";
				}
				if (entry.getValue() > 0) {
					clause = cnf_var + " 0";
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
		Iterator<String> it = addedClauses.iterator();
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

	public InputStream getStream() {
		return this.stream;
	}

	public String getPartialCNF(int increment) {
		this.easeGeneration=true;
		this.easeIncrement=increment;
		createSAT();
		this.easeGeneration=false;
		return easeGenString;
	}


}
