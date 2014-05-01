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
package es.us.isa.utils;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;

/**
 * This class represents the statistics of a model
 */
public class FMStatistics {
	private int NoFeatures = 0; 				// Number of features
	private int NoCrossTree = 0; 				// Number of CTC
	private int NoMandatory = 0; 				// Number of mandatory features
	private int NoOptional = 0; 				// Number of optional features
	private int NoOr = 0; 						// Number of or-relationships
	private int NoOrChildren = 0; 				// Number of subfeatures in or-relationships
	private int NoAlternative = 0; 				// Number of alternative relationships
	private int NoAlternativeChildren = 0; 		// Number of subfeatures in alternative relationships
	private int NoRequires = 0; 				// Number of requires constraints
	private int NoExcludes = 0; 				// Number of excludes relationships
	private int MaxBranchingFactor =0 ;			// Maximum branching factor
	private int MaxSetChildren = 0;				// Maximum number of children in a set relationship
	private float CTCR = 0; 					// CTC Ratio

	public FMStatistics() {

	}

	// Get the statistics of a given model
	public FMStatistics(FAMAFeatureModel fm) {

		this.extractStatistics(fm);
	}

	public String toString() {

		String res = "";
		res += ("==================== STATISTICS OF THE FEATURE MODEL GENERATED ============================\n");
		res += ("== TREE STATISTICS ==\n");
		res += ("Number of features: " + NoFeatures + "\n");
		res += ("Mandatory features: " + NoMandatory + " (" + getPercentageMandatory() + "%)\n");
		res += ("Optinal features: " + NoOptional + " (" + getPercentageOptional() + "%)\n");
		res += ("Or-relationships: " + NoOr + " (" + getPercentageOr() + "%)\n");
		res += ("Alternative relationships: " + NoAlternative + " (" + getPercentageAlternative() + "%)\n");
		res += ("Subfeatures in or-relationships: " + NoOrChildren + " (" + getPercentageOrChildren() + "%)\n");
		res += ("Subfeatures in alternative relationships: " + NoAlternativeChildren + " (" + getPercentageAltChildren() + "%)\n");
		res += ("Maximum branching factor: " + MaxBranchingFactor + "\n");
		res += ("Maximum number of children in a set relationship: " + MaxSetChildren + "\n");
		res += ("== CTC ==\n");
		res += ("Cross-tree constraints: " + NoCrossTree + " (" + getPercentageCTC() + "%)\n");
		res += ("CTC Ratio: " + CTCR + "%" + "\n");
		res += ("Requires constraints: " + NoRequires + " (" + getPercentageRequires() + "%)\n");
		res += ("Excludes constraints: " + NoExcludes + " (" + getPercentageExcludes() + "%)\n");
		return res;
	}

	public Map<String, String> getStatisticsMap() {
		Map<String, String> statistics = new HashMap<String, String>();

		statistics.put("Features", Integer.toString(NoFeatures));
		statistics.put("Mandatory", Integer.toString(NoMandatory));
		statistics.put("Optional", Integer.toString(NoOptional));
		statistics.put("Or-relationships", Integer.toString(NoOr));
		statistics.put("Or subfeatures", Integer.toString(NoOrChildren));
		statistics.put("Alt relationships", Integer.toString(NoAlternative));
		statistics.put("Alt subfeatures", Integer.toString(NoAlternativeChildren));
		statistics.put("Max branching factor", Integer.toString(MaxBranchingFactor));
		statistics.put("Max set children", Integer.toString(MaxSetChildren));
		statistics.put("Number CTC", Integer.toString(NoCrossTree));
		statistics.put("CTC Ratio", Float.toString(CTCR));
		statistics.put("Requires", Integer.toString(NoRequires));
		statistics.put("Excludes", Integer.toString(NoExcludes));

		return statistics;
	}


	public void extractStatistics(FAMAFeatureModel fm) {
		
		// TREE		
		NoFeatures = fm.getFeatures().size();
		Iterator <Feature> itf = fm.getFeatures().iterator();
		while(itf.hasNext()) {
			Feature f = itf.next();
			Iterator<Relation> itr = f.getRelations();
			int branches =0;
			while (itr.hasNext()) {
				Relation r = (Relation) itr.next();
				
				branches += r.getNumberOfDestination();
				
				if(r.getNumberOfDestination() > this.MaxSetChildren)
					this.MaxSetChildren = r.getNumberOfDestination();
				
				if (r.isMandatory()) { 									// Mandatory
					NoMandatory++;
				} else if (r.isOptional()) { 							// Optional
					NoOptional++;
				} else if (r.isAlternative()) { 						// Alternative
					NoAlternative++;
					NoAlternativeChildren += r.getNumberOfDestination();
				} else if (r.isOr()) { 									// Or
					NoOr++;
					NoOrChildren += r.getNumberOfDestination();
				}
			}
			
			if (branches > this.MaxBranchingFactor)
				this.MaxBranchingFactor = branches;
		}

		// CTC
		Set<Feature> featuresCTC = new HashSet<Feature>();
		NoCrossTree = fm.getNumberOfDependencies();
		Iterator<Dependency> itc = fm.getDependencies();
		while (itc.hasNext()) {
			Dependency d = itc.next();
			if (d instanceof RequiresDependency) {
				NoRequires++;
			} else if (d instanceof ExcludesDependency) {
				NoExcludes++;
			}
			if (!featuresCTC.contains(d.getOrigin())) {
				featuresCTC.add(d.getOrigin());
			}

			if (!featuresCTC.contains(d.getDestination())) {
				featuresCTC.add(d.getDestination());
			}
		}
		
		CTCR = (float) (featuresCTC.size() ) / NoFeatures;
		
	}

	public int getNoFeatures() {
		return NoFeatures;
	}

	public int getNoCrossTree() {
		return NoCrossTree;
	}

	public int getNoMandatory() {
		return NoMandatory;
	}

	public int getNoOptional() {
		return NoOptional;
	}

	public int getNoOr() {
		return NoOr;
	}

	public int getNoOrChildren() {
		return NoOrChildren;
	}

	public int getNoAlternative() {
		return NoAlternative;
	}

	public int getNoAlternativeChildren() {
		return NoAlternativeChildren;
	}

	public int getNoRequires() {
		return NoRequires;
	}

	public int getNoExcludes() {
		return NoExcludes;
	}

	public int getMaxBranchingFactor() {
		return MaxBranchingFactor;
	}

	public int getMaxSetChildren() {
		return MaxSetChildren;
	}

	public float getCTCR() {
		return CTCR;
	}
	
	public float getPercentageCTC() {
		return (this.NoCrossTree *  100) / this.NoFeatures;
	}
	
	public float getPercentageMandatory() {
		return (this.NoMandatory * 100) / this.NoFeatures;
	}
	
	public float getPercentageOptional() {
		return (this.NoOptional * 100) / this.NoFeatures;
	}
	
	public float getPercentageOrChildren() {
		return (this.NoOrChildren * 100) / this.NoFeatures;
	}
	
	public float getPercentageAltChildren() {
		return (this.NoAlternativeChildren * 100) / this.NoFeatures;
	}
	
	public float getPercentageOr() {
		return (this.NoOr * 100) / this.NoFeatures;
	}
	
	public float getPercentageAlternative() {
		return (this.NoAlternative * 100) / this.NoFeatures;
	}
	
	public float getPercentageRequires() {
		return (this.NoRequires * 100) / this.NoFeatures;
	}
	
	public float getPercentageExcludes() {
		return (this.NoExcludes * 100) / this.NoFeatures;
	}

	public void setNoFeatures(int noFeatures) {
		NoFeatures = noFeatures;
	}

	public void setNoCrossTree(int noCrossTree) {
		NoCrossTree = noCrossTree;
	}

	public void setNoMandatory(int noMandatory) {
		NoMandatory = noMandatory;
	}

	public void setNoOptional(int noOptional) {
		NoOptional = noOptional;
	}

	public void setNoOr(int noOr) {
		NoOr = noOr;
	}

	public void setNoOrChildren(int noOrChildren) {
		NoOrChildren = noOrChildren;
	}

	public void setNoAlternative(int noAlternative) {
		NoAlternative = noAlternative;
	}

	public void setNoAlternativeChildren(int noAlternativeChildren) {
		NoAlternativeChildren = noAlternativeChildren;
	}

	public void setNoRequires(int noRequires) {
		NoRequires = noRequires;
	}

	public void setNoExcludes(int noExcludes) {
		NoExcludes = noExcludes;
	}

	public void setMaxBranchingFactor(int maxBranchingFactor) {
		MaxBranchingFactor = maxBranchingFactor;
	}

	public void setMaxSetChildren(int maxSetChildren) {
		MaxSetChildren = maxSetChildren;
	}

	public void setCTCR(float cTCR) {
		CTCR = cTCR;
	}
	
	
}
