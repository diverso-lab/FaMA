/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.generator.FM.Evolutionay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.FMStatistics;

/**
 * This class contains the based methods to encode and decode feature models as chromosomes.
 */
public class Chromosome implements IChromosome {

	/**
	 * Feature tree encoding
	 */
	protected List<String[]> tree;
	/**
	 * CTC encoding
	 */
	protected List<String[]> ctc;

	// Encoding variables
	/**
	 * Save the position in the array of each feature
	 */
	protected Map<String, Integer> positions;
	/**
	 * Current position
	 */
	protected int position; 

	
	protected int featureId;
	
	protected int relationId;
	
	/**
	 * Features
	 */
	protected Map<String, Feature> features; 
	/**
	 * Intermediate structure for decoding or relationships.
	 */
	protected Map<Feature, List<Feature>> orRelationships;
	/**
	 * Intermediate structure for decoding alternative relationships
	 */
	protected Map<Feature, List<Feature>> altRelationships; 
	/**
	 * Decoding statistics
	 */
	protected FMStatistics stats;
	/**
	 * Seed for the genrtation of the first chromosomes
	 */
	protected long seed;
	
	/**
	 * Random class to be used along the generation
	 */
	protected Random rand; 

	/**
	 * Input constraints for the generation
	 */
	protected GeneratorCharacteristics characteristics;

	/**
	 * Fitness (we save it to facilitate crossover)
	 */
	public double fitness = 0; // Fitness of the individual

	public Chromosome(long seed, GeneratorCharacteristics ch) {

		this.seed = seed;

		// Initialize variables for encoding
		tree = new ArrayList<String[]>();
		ctc = new ArrayList<String[]>();
		positions = new HashMap<String, Integer>();
		position = 1;
		
		this.characteristics = ch;
	}
	

	public Chromosome(List<String[]> tree, List<String[]> ctc, long seed, GeneratorCharacteristics ch) {
		this.tree = tree;
		this.ctc = ctc;
		this.seed = seed;
		this.characteristics = ch;
	}
	
	@Override
	public void encode(FAMAFeatureModel fm) {
		// Depth-first traversal encoding
		encodeDFT(fm.getRoot());

		// Encode CTC
		encodeCTC(fm);
	}


	/**
	 * Tree encoding (depth-first traversal)
	 * @param root the root feature of the tree
	 */
	private void encodeDFT(Feature root) {

		Iterator<Relation> itr = root.getRelations();
		while (itr.hasNext()) {
			Relation r = itr.next();

			// Binary relationships
			if (r.getNumberOfDestination() == 1) {
				String[] gene = new String[2];
				if (r.isMandatory()) // Mandatory
					gene[0] = "M";
				else
					gene[0] = "O"; // Optional

				gene[1] = Integer.toString(getNumberOfChildren(r
						.getDestinationAt(0))); // Number of children
				tree.add(gene);
				positions.put(r.getDestinationAt(0).getName(), position++); // Save the position of the
																			// feature
																			// for
																			// CTC
																			// encoding
				encodeDFT(r.getDestinationAt(0));

				// Set relationships
			} else {
				int card = r.getCardinalities().next().getMax();
				Iterator<Feature> itf = r.getDestination();
				while (itf.hasNext()) {
					String[] gene = new String[2];
					Feature f = itf.next();
					if (card > 1) // Or
						gene[0] = "Or";
					else
						// Alternative
						gene[0] = "Alt";

					gene[1] = Integer.toString(getNumberOfChildren(f)); // Number
																		// of
																		// children
					tree.add(gene);
					positions.put(f.getName(), position++);
					encodeDFT(f);
				}
			}
		}
	}

	/**
	 *  Return the number of children of a given feature
	 * @param f The feaure
	 * @return the number of children of a given feature
	 */
	private int getNumberOfChildren(Feature f) {
		int result = 0;

		Iterator<Relation> itr = f.getRelations();
		while (itr.hasNext()) {
			Relation r = itr.next();
			result += r.getNumberOfDestination();
		}

		return result;
	}

	/**
	 *  CTC encoding
	 * @param fm the FM to be encoded
	 */
	
	private void encodeCTC(FAMAFeatureModel fm) {

		String[] gene = new String[3];
		Iterator<Dependency> itd = fm.getDependencies();
		while (itd.hasNext()) {
			Dependency d = itd.next();
			gene = new String[3];
			if (d instanceof RequiresDependency)
				gene[0] = "R";
			else if (d instanceof ExcludesDependency)
				gene[0] = "E";

			gene[1] = Integer.toString(positions.get(d.getOrigin().getName()));
			gene[2] = Integer.toString(positions.get(d.getDestination()
					.getName()));
			ctc.add(gene);
		}
	}

	/**
	 *  Decode the chromosome
	 */
	public FAMAFeatureModel decode() {

		// Initialize variables
		featureId = 0;
		relationId = 0;
		features = new HashMap<String, Feature>();
		orRelationships = new HashMap<Feature, List<Feature>>();
		altRelationships = new HashMap<Feature, List<Feature>>();
		stats = new FMStatistics();
		rand = new Random();
		rand.setSeed(seed);

		// Create root
		FAMAFeatureModel fm = new FAMAFeatureModel();
		Feature root = new Feature("root");
		fm.setRoot(root);
		stats.setNoFeatures(stats.getNoFeatures()+1);

		// Decode tree
		if (!decodeTree(root))
			return null;

		// Decode CTC
		if (!decodeCTC(fm))
			return null;

		// Print statistics
		// stats.printStatistics();

		return fm;
	}

	/**
	 *  Tree decoding
	 * @param root The root of the tree
	 * @return a boolean telling is the decoding was succeful
	 */
	private boolean decodeTree(Feature root) {

		// Decode tree
		Iterator<String[]> it = tree.iterator();
		decodeTreeRecursive(root, it);

		// Fix tree
		fixTree();

		// Validate tree
		return validateTree(root);

	}

	/**
	 *  Recursive method for decoding the tree
	 * @param current_feature The actual feature
	 * @param treeIterator The Iterator of the tree to be decoded
	 */
	private void decodeTreeRecursive(Feature current_feature, Iterator<String[]> treeIterator) {

		if (treeIterator.hasNext()) {
			String[] gene = treeIterator.next();
			Feature child = null;
			
			if (gene[0].equalsIgnoreCase("M")) {					// Mandatory
				child = new Feature(getFeatureId());
				features.put(child.getName(), child);
				createCardinality(current_feature, child, 1, 1);
				stats.setNoMandatory(stats.getNoMandatory()+1);
				
			} else if (gene[0].equalsIgnoreCase("O")) {				// Optional
				child = new Feature(getFeatureId());
				features.put(child.getName(), child);
				createCardinality(current_feature, child, 0, 1);
				stats.setNoOptional(stats.getNoOptional()+1);
				
			} else if (gene[0].equalsIgnoreCase("Or")) {			// Or
				
				child = new Feature(getFeatureId());
				features.put(child.getName(), child);
				if (orRelationships.containsKey(current_feature))
					orRelationships.get(current_feature).add(child);
				else {
					List<Feature> orChildren = new LinkedList<Feature>();
					orChildren.add(child);
					orRelationships.put(current_feature, orChildren);
				}	
				
			} else if (gene[0].equalsIgnoreCase("Alt")) {			// Alternative
					
				child = new Feature(getFeatureId());
				features.put(child.getName(), child);
				if (altRelationships.containsKey(current_feature))
					altRelationships.get(current_feature).add(child);
				else {
					List<Feature> altChildren = new LinkedList<Feature>();
					altChildren.add(child);
					altRelationships.put(current_feature, altChildren);
				}
			}
			
			stats.setNoFeatures(stats.getNoFeatures()+1);
			int numberOfChildren = Integer.parseInt(gene[1]);
			for (int i=0;i<numberOfChildren;i++) {
					decodeTreeRecursive(child,treeIterator);
			}
		}
		

		if ((current_feature.getName().equalsIgnoreCase("Root")) && (stats.getNoFeatures() <= tree.size()))
			if (numberOfChildren(current_feature) < this.characteristics.getMaxBranchingFactor()) {
				decodeTreeRecursive(current_feature,treeIterator);
			} else {
				while (stats.getNoFeatures() <= tree.size()) {
					Iterator<Feature> itf = features.values().iterator();
					while (itf.hasNext()) {
						Feature f = itf.next();
						if (numberOfChildren(f) < this.characteristics.getMaxBranchingFactor()) {
							decodeTreeRecursive(f,treeIterator);
							break;
						}
					}
				}
			}
	}

	/**
	 * Fix the tree in case the decoding generates any inconsistency.
	 * Single children in set relationships are converted to optional
	 */
	private void fixTree() {
		
		Relation setRelation = null;

		// OR RELATIONSHIPS
		Iterator<Feature> it = orRelationships.keySet().iterator();
		while(it.hasNext()) {
			Feature f = it.next();
			List<Feature> orChildren = orRelationships.get(f);
			
	
			if (orChildren.size()==1) {						// Isolated or child
				Feature child = orChildren.get(0); 
				//features.put(child.getName(), child);
				createCardinality(f, child, 0, 1);
				stats.setNoOptional(stats.getNoOptional()+1);
				
			}else {
				setRelation = new Relation(getRelationId());
				f.addRelation(setRelation);
				setRelation.addCardinality(new Cardinality(1, orChildren.size()));
				stats.setNoOr(stats.getNoOr()+1);
				
				// Link children
				Iterator<Feature> itc = orChildren.iterator();
				while (itc.hasNext()) {
					Feature c = itc.next();
					setRelation.addDestination(c);
					//features.put(c.getName(),c);
					stats.setNoOrChildren(stats.getNoOrChildren()+1);
				}
			}
		}
		
		
		// ALTERNATIVE RELATIONSHIPS
		it = altRelationships.keySet().iterator();
		while(it.hasNext()) {
			Feature f = it.next();
			List<Feature> altChildren = altRelationships.get(f);
			
			// Create relation
			if (altChildren.size()==1) {					// Isolated alternative child
				Feature child = altChildren.get(0); 
				//features.put(child.getName(), child);
				createCardinality(f, child, 0, 1);
				stats.setNoOptional(stats.getNoOptional()+1);
				
			}else {
				setRelation = new Relation(getRelationId());
				f.addRelation(setRelation);
				setRelation.addCardinality(new Cardinality(1, 1));
				stats.setNoAlternative(stats.getNoAlternative()+1) ;
				
				// Link children
				Iterator<Feature> itc = altChildren.iterator();
				while (itc.hasNext()) {
					Feature c = itc.next();
					setRelation.addDestination(c);
					//features.put(c.getName(), c);
					stats.setNoAlternativeChildren(stats.getNoAlternativeChildren()+1);
				}
			}
		}
	}

	/**
	 *  This function makes sure that the decoded chromosome belong to the desired input domain
	 * @param root The root of the tree
	 */
	private boolean validateTree(Feature root) {
		return true;
	}

	/**
	 *  Fixing: Erroneous and redundant constraints are discarded
	 */
	private boolean decodeCTC(FAMAFeatureModel fm) {
		Iterator<String[]> it = ctc.iterator();
		while (it.hasNext()) {
			String[] gene = it.next();
			Feature origin = features.get("F" + gene[1]);
			Feature destination = features.get("F" + gene[2]);

			// We discard erroneous and redundant constraints
			if (!areDirectFamily(origin, destination)
					&& !areDirectFamily(destination, origin)
					&& !existsRelation(fm, origin, destination)) {
				if (gene[0].equalsIgnoreCase("R")) {
					fm.addDependency(new RequiresDependency(getRelationId(),
							origin, destination));
					stats.setNoRequires(stats.getNoRequires()+1);
				} else {
					fm.addDependency(new ExcludesDependency(getRelationId(),
							origin, destination));
					stats.setNoExcludes(stats.getNoExcludes()+1);
				}
			}

			stats.setNoCrossTree(stats.getNoCrossTree()+1);
		}

		return true;
	}

	/**
	 * Check if f and g are direct family
	 * @param f A feature
	 * @param g A feature
	 * @return a boolean telling if are Direct Family
	 */
	protected boolean areDirectFamily(Feature f, Feature g) {
		boolean res = false;

		if (f == g)
			res = true;
		else {
			Iterator<Relation> itr = f.getRelations();
			while (itr.hasNext() && !res) {
				Iterator<Feature> itf = itr.next().getDestination();
				while (itf.hasNext() && !res) {
					res = areDirectFamily(itf.next(), g);
				}
			}
		}

		return res;
	}

	/**
	 *  Check if already exist a constraints between two features
	 */
	protected boolean existsRelation(FAMAFeatureModel fm, Feature f, Feature g) {
		boolean res = false;
		Iterator<Dependency> itd = fm.getDependencies();

		while (itd.hasNext() && !res) {
			Dependency dep = itd.next();
			if ((dep.getDestination() == f && dep.getOrigin() == g)
					|| dep.getDestination() == g && dep.getOrigin() == f) {
				res = true;
			}
		}

		return res;
	}

	/**
	 *  Generate and id for the following feature
	 * @return The name of the feature
	 */
	protected String getFeatureId() {
		return "F" + String.valueOf(++featureId);
	}

	/**
	 *  Generate an id for the following relation
	 * @return the name of the relation
	 */
	protected String getRelationId() {
		return "R-" + String.valueOf(++relationId);
	}


	protected void createCardinality(Feature parent, Feature child, int i, int j) {
		Relation rel = new Relation(getRelationId());
		rel.addCardinality(new Cardinality(i, j));
		rel.addDestination(child);
		parent.addRelation(rel);
	}

	// Return the tree list
	public List<String[]> getTree() {
		return tree;
	}

	// Return the CTC list
	public List<String[]> getCTC() {
		return ctc;
	}

	// Return decoding statistics
	public FMStatistics getStatistics() {
		return stats;
	}

	/**
	 *  Return a copy of the chromosome
	 */
	@Override
	public Chromosome clone() {

		List<String[]> treecpy = new LinkedList<String[]>();
		List<String[]> ctccpy = new LinkedList<String[]>();

		int nFeatures = this.tree.size();
		int nConstraints = this.ctc.size();

		// Copy tree
		for (int i = 0; i < nFeatures; i++)
			treecpy.add(i, ((String[]) this.tree.get(i)).clone());

		// Copy ctc
		for (int i = 0; i < nConstraints; i++)
			ctccpy.add(i, ((String[]) this.ctc.get(i)).clone());

		Chromosome chromosome = new Chromosome(treecpy, ctccpy,this.seed,this.characteristics);
		chromosome.fitness = this.fitness;

		return chromosome;
	}

	/**
	 * Output string
	 */
	public String toString() {
		String result = null;

		// Tree
		System.out.print("Tree (" + tree.size() + ") :");
		Iterator<String[]> it = tree.iterator();
		while (it.hasNext()) {
			String[] gene = it.next();
			System.out.print("[" + gene[0] + "," + gene[1] + "] ");
		}

		System.out.println();

		// CTC
		System.out.print("CTC (" + ctc.size() + ") :");
		it = ctc.iterator();
		while (it.hasNext()) {
			String[] gene = it.next();
			System.out.print("[" + gene[0] + "," + gene[1] + "," + gene[2]
					+ "] ");
		}

		System.out.println();

		return result;
	}

	@Override
	public void setFitnessNumber(double ff) {
		this.fitness = ff;

	}
	
	// Return the number of children of a feature
	private int numberOfChildren(Feature f) {
		int nChildren = 0;
		Iterator<Relation> itr = f.getRelations();
		while (itr.hasNext()) {
			Relation r = itr.next();
			nChildren += r.getNumberOfDestination();
		}
		
		// Sum alternative and or children to add
		if (orRelationships.containsKey(f))
			nChildren += orRelationships.get(f).size();
		
		if (altRelationships.containsKey(f))
			nChildren += altRelationships.get(f).size();
		
		return nChildren;
	}
}
