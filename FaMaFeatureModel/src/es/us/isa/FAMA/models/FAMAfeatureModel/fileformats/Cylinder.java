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

public class Cylinder {
	private double coneRadius;
	private Group parentGroup;
	private Group childGroup;
	private Point3D traslation, pivot, rotation;
	private double alpha;
	private double length;
	private final static double cylinderRadius = 0.05d;
	
	public Cylinder (Group parent, Group child) {
		this.parentGroup = parent;
		this.childGroup = child;
	}
	
	public void setLayout( double radius,double parentAngle, double height) {
		this.coneRadius = radius;
		double parentRadius = parentGroup.getSphereRadius();
		double childRadius = childGroup.getSphereRadius();
		if (coneRadius != 0.0d){
			alpha = Math.atan2(coneRadius,height);
			length = Math.sqrt(coneRadius*coneRadius+height*height) - parentRadius - childRadius;
			traslation = new Point3D(parentRadius*Math.cos(parentAngle),
									 -length/2.0d-(parentRadius*Math.cos(alpha)),
									 parentRadius*Math.sin(parentAngle));
			pivot = new Point3D(0.0d,length/2.0d,0.0d);
			rotation = new Point3D(-Math.sin(parentAngle),0.0d,Math.cos(parentAngle));
		} else {
			alpha = 0.0d;
			length = height - parentRadius - childRadius;
			traslation = new Point3D(0.0d,-length/2.0d-parentRadius,0.0d);
			pivot = new Point3D(0.0d,length/2.0d,0.0d);
			rotation = new Point3D(1.0d,0.0d,0.0d);		
		}

	}
	
	public String toString() {
		String id = childGroup.getText();
		String res = "<Transform DEF='dad_Cylinder_" + id +"' containerField='children' "
				+ "translation='" + traslation + "' rotation='" + rotation + " " + alpha +
				"' center='" + pivot + "'>\r\n";
		res += "<Shape DEF='Cylinder" + id + "' containerField='children'>\r\n";
		res += "<Appearance containerField='appearance'>\r\n";
		res += "<Material DEF='Cylinder_Black' containerField='material' ambientIntensity='0.200' shininess='0.200' diffuseColor='0 0 0'/>\r\n";
		res += "</Appearance>\r\n";
		res += "<Cylinder DEF='GeoCylinder_" + id + "' containerField='geometry' height='" + length +
				"' radius='" + cylinderRadius + "'/>\r\n";
		res += "</Shape>\r\n";
		res += "</Transform>\r\n";

		return res;
	}
}
