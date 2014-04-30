/*
 * Created on 04-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package featureModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author trinidad
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FeatureModel {
	protected FeatureNode root;

	protected List dependencies;

	public FeatureModel() {
		root = null;
		dependencies = new ArrayList();
	}

	public FeatureModel(FeatureNode root) {
		root.setRelationType(FeatureNode.ROOT);
		this.root = root;
	}

	public FeatureModel(FeatureModel fm) {
		this.root = fm.getRoot();
	}

	public FeatureNode getRoot() {
		return root;
	}

	public void setRoot(FeatureNode root) {
		this.root = root;
	}

	public int getFeaturesNumber() {
		return FeatureNode.getSubfeaturesNumber(root);
	}

	public Collection getFeatures() {
		return root.getFeatures();
	}

	public int getNumberOfLevels() {
		return getNumberOfLevels(root);
	}
	
	
	public int getNumberOfDependencies()
	{
		return dependencies.size();
	}

	private int getNumberOfLevels(FeatureNode fn) {
		int maxLevel = 0;
		for (int i = 0; i < fn.getChildNum(); i++) {
			int ChildLevel = getNumberOfLevels(fn.getChild(i));
			if (ChildLevel > maxLevel)
				maxLevel = ChildLevel;
		}
		return maxLevel + 1;
	}

	public long getTotalCardinality() {
		return getTotalCardinality(root);
	}

	private int getTotalCardinality(FeatureNode fn) {
		int total = getSetCardinalities(fn);

		for (int i = 0; i < fn.getChildNum(); i++) {
			total = total + getTotalCardinality(fn.getChild(i));

		}

		return total;
	}

	private int getSetCardinalities(FeatureNode fn) {
		int res = 0;

		for (Iterator iter = fn.getGroupsList().iterator(); iter.hasNext();) {
			FeatureGroup element = (FeatureGroup) iter.next();
			res = res + element.getCardMax();
		}
		return res;
	}

	public long getAvgNumberOfChildren() {
		return getAvgNumberOfChildren(root);
	}

	private long getAvgNumberOfChildren(FeatureNode fn) {
		long res;

		return 0;
	}

	public void addDependency(Dependency d) {
		dependencies.add(d);
	}

	public Iterator getDependencies() {
		return dependencies.iterator();
	}

	public Feature searchFeatureByName(String name) {
		return searchFeatureByName(name, root);
	}

	private Feature searchFeatureByName(String name, FeatureNode fn) {
		if (fn.getName().equalsIgnoreCase(name)) {
			return fn;
		} else {
			Feature res = null;
			for (int i = 0; i < fn.getChildNum() && res == null; i++) {
				res = searchFeatureByName(name, fn.getChild(i));
			}
			return res;
		}
	}

	public String toString() {
		String res = "Feature model: " + root;
		res += "\r\n" + dependencies;
		return res;
	}

	private boolean hasChild(FeatureNode p, FeatureNode c) {
		boolean res = false;

		if (p.getName().equals(c.getName())) {
			res = true;
		} else {
			for (int i = 0; i < p.getChildNum() && res == false; i++) {
				res = hasChild(p.getChild(i), c);
			}

		}

		return res;

	}

	public boolean areDirectFamily(FeatureNode p, FeatureNode c)
			throws IllegalArgumentException {
		boolean res = true;

		if (p != null && c != null) {
			res = hasChild(p, c) || hasChild(c, p);
			;

		} else {
			throw new IllegalArgumentException(
					"one or more of the features do not belongs to the Feature Model");
		}

		return res;
	}

	public boolean haveRelation(FeatureNode o, FeatureNode d) {
		boolean found = false;

		Iterator iter = dependencies.iterator();

		while (iter.hasNext() && !found) {
			Dependency element = (Dependency) iter.next();
			String origin = element.getOrigin().getName();
			String desti = element.getDest().getName();

			if ((origin.equals(o.getName()) && desti.equals(d.getName()))
					|| (origin.equals(d.getName()) && desti.equals(o.getName()))) {
				found = true;
			}

		}

		return found;

	}

}
