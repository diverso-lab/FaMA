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

package es.us.isa.FAMA.models.FAMAfeatureModel.OrderingHeuristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;


import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;

public class OrderingHeuristic {

	public static final int NATURALPREORDER=1;
	public static final int NATURALSORTEDPREORDER=2;
	public static final int NATURALCLUSTEREDPREORDER=3;
	
	int state=-1;
	public OrderingHeuristic(int state){
		this.state=state;
	}
	
	public ArrayList<Feature> orderFM(FAMAFeatureModel fm){
		
		if(state==NATURALPREORDER){
			return naturalPreOrder(fm);
		}else if(state==NATURALSORTEDPREORDER){
			return naturalSortedPreOrder(fm);
		}else if(state==NATURALCLUSTEREDPREORDER){
			return clusteredPreOrder(fm);
		}else {
			throw new IllegalStateException();
		}
	}

	private ArrayList<Feature> naturalPreOrder(FAMAFeatureModel fm) {
		ArrayList<Feature> res = new ArrayList<Feature>();

		Stack<Feature> stack = new Stack<Feature>();

		// The root at first
		Feature node = fm.getRoot();
		stack.push(node);

		// Traversal Pre-order using stack (no need for recursion)
		while (!stack.empty()) {
			System.out.println(node);
			res.add(node);
			Iterator<Relation> relations = node.getRelations();
			while (relations.hasNext()) {
				Relation relation = relations.next();
				Iterator<Feature> destinations = relation.getDestination();
				Feature next = destinations.next();
				stack.push(next);
			}
			node = stack.pop();
		}
		return res;

	}

	private ArrayList<Feature> naturalSortedPreOrder(FAMAFeatureModel fm) {
		ArrayList<Feature> res = new ArrayList<Feature>();

		Stack<Feature> stack = new Stack<Feature>();

		// The root at first
		Feature node = fm.getRoot();
		stack.push(node);

		// Traversal Pre-order using stack (no need for recursion)
		while (!stack.empty()) {
			System.out.println(node);
			res.add(node);
			Iterator<Relation> relations = node.getRelations();
			SortedMap<Integer, Feature> tempRelStore = new TreeMap<Integer, Feature>();
			while (relations.hasNext()) {
				Relation relation = relations.next();
				Iterator<Feature> destinations = relation.getDestination();
				Feature next = destinations.next();
				tempRelStore.put(getSize(next), next);
			}
			Iterator<Feature> orderedFeats = tempRelStore.values().iterator();
			while (orderedFeats.hasNext()) {
				stack.push(orderedFeats.next());
			}
			node = stack.pop();
		}
		return res;

	}

	private Integer getSize(Feature feat) {
		int i = 1;
		Stack<Feature> stack = new Stack<Feature>();

		// The root at first
		Feature node = feat;
		stack.push(node);

		// Traversal Pre-order using stack (no need for recursion)
		while (!stack.empty()) {
			i++;
			Iterator<Relation> relations = node.getRelations();
			while (relations.hasNext()) {
				Relation relation = relations.next();
				Iterator<Feature> destinations = relation.getDestination();
				Feature next = destinations.next();
				stack.push(next);
			}
			node = stack.pop();
		}
		return i;
	}

	private ArrayList<Feature> clusteredPreOrder(FAMAFeatureModel fm) {
		Collection<Collection<Feature>> clusters = new ArrayList<Collection<Feature>>();

		Map<Feature, Feature> visited = new HashMap<Feature, Feature>();
		Iterator<Dependency> dependencies = fm.getDependencies();
		while (dependencies.hasNext()) {
			Dependency next = dependencies.next();
			if ((visited.containsKey(next.getDestination()) && visited.get(
					next.getDestination()).equals(next.getOrigin()))
					|| (visited.containsKey(next.getOrigin()) && visited.get(
							next.getOrigin()).equals(next.getDestination()))) {
				System.err.println("Docuble relation between dependencies");
			} else {
				visited.put(next.getOrigin(), next.getDestination());
				clusters.add(getChildsPreorder(calculateLCA(next.getOrigin(),
						next.getDestination())));
			}
		}

		ArrayList<Feature> res = new ArrayList<Feature>();
		Stack<Feature> stack = new Stack<Feature>();

		// The root at first
		Feature node = fm.getRoot();
		stack.push(node);

		// Traversal Pre-order using stack (no need for recursion)
		while (!stack.empty()) {
			System.out.println(node);
			res.add(node);
			Iterator<Relation> relations = node.getRelations();
			Collection<Feature> tempColection = new ArrayList<Feature>();
			while (relations.hasNext()) {
				Relation relation = relations.next();
				Iterator<Feature> destinations = relation.getDestination();
				Feature next = destinations.next();
				tempColection.add(next);
			}
			tempColection = clusterOrdering(tempColection, fm, clusters);
			for (Feature feat : tempColection) {
				stack.push(feat);
			}
			node = stack.pop();
		}
		return res;
	}

	private Collection<Feature> clusterOrdering(Collection<Feature> col,
			FAMAFeatureModel fm, Collection<Collection<Feature>> clusters) {
		
		Collection<Collection<Feature>> orderedClusters = new ArrayList<Collection<Feature>>();
		Collection<Feature> addedFeatures= new ArrayList<Feature>();
		Collection<Feature> res = new ArrayList<Feature>();
		
		
		for (Collection<Feature> cluster : clusters) {
			Collection<Feature> clusterTmp= new ArrayList<Feature>();
			for (Feature feat : col) {
				if (cluster.contains(feat)&&!addedFeatures.contains(feat)){
					clusterTmp.add(feat);
					addedFeatures.add(feat);
				}
			}
			if(clusterTmp.size()>0){
				orderedClusters.add(clusterTmp);
			}
		}
		
		//Add the singles
		col.removeAll(addedFeatures);
		res.addAll(col);
		//add the clustereds
		while (orderedClusters.size()>0){
			Collection<Feature> minCluster = getMinCluster(orderedClusters);
			orderedClusters.remove(minCluster);
			res.addAll(minCluster);
		}
		

		return res;
	}

	

	private Collection<Feature> getMinCluster(Collection<Collection<Feature>> orderedClusters) {
		Collection<Feature> minCluster = null;
		int minSize=999;
		for (Collection<Feature> cluster:orderedClusters){
			if(cluster.size()<minSize){
				minSize=cluster.size();
				minCluster=cluster;
			}
		}
		return minCluster;
	}

	private Feature calculateLCA(Feature feat1, Feature feat2) {
		Feature LCAFeat = feat1;
		Stack<Feature> stack = new Stack<Feature>();
		boolean enc = false;
		// si el o algunos de sus hijos es padre de feat2 devuelve

		while (!enc) {

			Feature node = LCAFeat.getParent().getParent();
			stack.push(node);
			// Traversal Pre-order using stack (no need for recursion)
			while (!stack.empty()) {
				if (node.equals(feat2)) {
					enc = true;
				}
				Iterator<Relation> relations = node.getRelations();
				while (relations.hasNext()) {
					Relation relation = relations.next();
					Iterator<Feature> destinations = relation.getDestination();
					Feature next = destinations.next();
					stack.push(next);
				}

				node = stack.pop();
			}
			if (enc) {
				LCAFeat = node;
			}
		}

		return LCAFeat;
	}

	private ArrayList<Feature> getChildsPreorder(Feature feat) {
		ArrayList<Feature> res = new ArrayList<Feature>();

		Stack<Feature> stack = new Stack<Feature>();

		// The root at first
		Feature node = feat;
		stack.push(node);

		// Traversal Pre-order using stack (no need for recursion)
		while (!stack.empty()) {
			System.out.println(node);
			res.add(node);
			Iterator<Relation> relations = node.getRelations();
			while (relations.hasNext()) {
				Relation relation = relations.next();
				Iterator<Feature> destinations = relation.getDestination();
				Feature next = destinations.next();
				stack.push(next);
			}
			node = stack.pop();
		}
		return res;

	}
}
