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
package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Group {
	private Point3D position;
	private double maxChildrenRadius;
	private List<Group> childGroups;
	private List<Cylinder> childCylinders;
	private Sphere rootSphere;
	private static double epsilon = 0.5d;
	private static double height;
	private String text;
	public static final int MANDATORY_TYPE = 0;
	public static final int OPTIONAL_TYPE = 1;
	public static final int CARDINALITY_NODE_TYPE = 2;
	public static final int SET_TYPE = 3;
	
	public Group () {
		position = new Point3D();
		childGroups = new LinkedList<Group>();
		childCylinders = new LinkedList<Cylinder>();
		rootSphere = new Sphere();
		rootSphere.getRadius();
		maxChildrenRadius = 0.0d;
		height = 1.0d;
	}
	
	public Group (String text) {
		this();
		this.text = text;
		rootSphere.setText(text);
	}
	
	@SuppressWarnings("static-access")
	public void setHeight (double height) {
		this.height = height;
	}
	
	public void setPosition(double parentRadius,double angle, double height) {
		double x,y,z;
		x = parentRadius*Math.cos(angle);
		y = -height;
		z = parentRadius*Math.sin(angle);
		position.setPosition(x, y, z);
	}
	
	public double getSphereRadius() {
		return rootSphere.getRadius();
	}
	
	public double getRadius() {
		return getRadius(false);
	}
	
	public double getRadius(boolean forceRadiusCalculus) {
		double res;
		if (forceRadiusCalculus) {
		maxChildrenRadius = 0.0d;
			for (Group g: childGroups) {
				double childRadius = g.getRadius(true);
				if (childRadius > maxChildrenRadius)
					maxChildrenRadius = childRadius;
			}
		}
		if (childGroups.size() > 1)
			res = rootSphere.getRadius() + 2 * maxChildrenRadius + epsilon;
		else if (childGroups.size() == 1)
			res = childGroups.get(0).getRadius();
		else
			res = rootSphere.getRadius();
		return res;
	}
	
	public void addChild (Group child) {
		if (!childGroups.contains(child))
		{
			childGroups.add(child);
			if (child.getRadius() > maxChildrenRadius)
				maxChildrenRadius = child.getRadius();
			childCylinders.add(new Cylinder(this,child));
		}
	}
	
	public void layoutChildren () {
		double sumRadius = 0.0d;
		for (Group g: childGroups) {
			g.layoutChildren();
			sumRadius += g.getRadius(true);
		}
		sumRadius /= 3.1416d;
		
		double angle = 0.0d;
		double parentRadius = 0.0d;
		if (childGroups.size()>1)
			parentRadius = rootSphere.getRadius() + maxChildrenRadius + epsilon;
		Iterator<Group> itg = childGroups.iterator();
		Iterator<Cylinder> itc = childCylinders.iterator();
		if (itg.hasNext() && itc.hasNext()) {
			Group currentGroup = itg.next();
			Cylinder currentCylinder = itc.next();
			currentGroup.setPosition(parentRadius, angle, height);
			currentCylinder.setLayout(parentRadius, angle, height);
			while (itg.hasNext() && itc.hasNext()) {
				Group previousGroup = currentGroup;
				currentGroup = itg.next();
				currentCylinder = itc.next();
				// DivisionByZero exception is never thrown because this piece of code is only
				// executed whenever there are at least 2 child groups.
				double incRadius = (previousGroup.getRadius()+currentGroup.getRadius())/sumRadius;//parentRadius;
				angle += incRadius;
				currentGroup.setPosition(parentRadius, angle, height);
				currentCylinder.setLayout(parentRadius, angle, height);
			}
			// TODO: update cylinders
		}
	}
	
	public void setText(String text) {
		this.text = text;
		rootSphere.setText(text);
	}
	
	public String getText() {
		return text;
	}
	
	public String toString() {
		String res = "<Transform DEF='dad_Group_" +  text +"' translation='" + position + "'>\r\n";
		res += rootSphere.toString();
		for (Group g: childGroups)
			res += g.toString();
		for (Cylinder c: childCylinders)
			res += c.toString();
		res += "</Transform>\r\n";
		return res;
	}
	
	public void setType(int type) {
		if (type == MANDATORY_TYPE) {
			rootSphere.setColour(192, 192, 192);
		} else if (type == OPTIONAL_TYPE || type == SET_TYPE) {
			rootSphere.setColour(128, 128, 128);
		} else if (type == CARDINALITY_NODE_TYPE) {
			rootSphere.setColour(255, 255, 0);
		}
	}
	
	public Point3D getPosition() {
		return this.position;
	}
}
