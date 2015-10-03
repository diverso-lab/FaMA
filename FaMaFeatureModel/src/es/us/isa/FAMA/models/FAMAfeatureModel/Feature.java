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
/*
 * Created on 10-Jan-2005
 * Modified on 17-Apr-2006
 */
package es.us.isa.FAMA.models.FAMAfeatureModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

/**
 * @author trinidad, Manuel Nieto Ucl�s Feature have a name, some relations
 *         and attributes.
 */
public class Feature extends GenericFeature {

	/**
	 * @uml.property name="name"
	 */
	protected Relation parent_relation;
	protected List<Relation> relations;

	// protected List<Attribute> attributes;

	/* Constructors ****************************************************** */
	public Feature() {
		this.name = ("");
	}

	public Feature(String name) {
		this.name = name;
		this.parent_relation = null;
		this.relations = new ArrayList<Relation>();
		// this.attributes = new ArrayList<Attribute>();
	}

	/* Name ************************************************************** */
	/**
	 * @return
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* Parent ************************************************************ */
	public Relation getParent() {
		return parent_relation;
	}

	public void setParent(Relation r) {
		parent_relation = r;
	}

	public void removeParent() {
		this.parent_relation = null;
	}

	/* Relations ********************************************************* */
	public void addRelation(Relation r) {
		relations.add(r);
		r.setParent(this);
	}

	/**
	 * @return
	 * @uml.property name="relations"
	 */
	public Iterator<Relation> getRelations() {
		return relations.iterator();
	}

	public void removeRelation(Relation r) {
		relations.remove(r);
		r.removeParent();
	}

	public void removeAllRelations() {
		relations = new ArrayList<Relation>();
	}

	public int getNumberOfRelations() {
		return relations.size();
	}

	public Relation getRelationAt(int i) {
		return relations.get(i);
	}

	public int getIndexOf(Relation r) {
		return relations.indexOf(r);
	}

	/* Attributes ******************************************************** */
	// public void addAttribute (Attribute a) {
	// attributes.add(a);
	// }

	/**
	 * @return
	 * @uml.property name="attributes"
	 */
	// public Iterator<Attribute> getAttributes() {
	// return attributes.iterator();
	// }

	// public int getNumberOfAttributes() {
	// return attributes.size();
	// }

	/* Others ************************************************************ */
	public String toString() {
		return name;
	}

	/*
	 * @Override
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean res = false;
		if (obj instanceof VariabilityElement||obj instanceof Feature) {
			Feature f = (Feature) obj;
			return this.name.equalsIgnoreCase(f.getName());
		}
		return res;

	}

	public void remove() {
		if (getParent() != null)
			getParent().remove();
		Iterator<Relation> it = getRelations();
		while (it.hasNext()) {
			Relation r = it.next();
			r.remove();
		}
	}
}
