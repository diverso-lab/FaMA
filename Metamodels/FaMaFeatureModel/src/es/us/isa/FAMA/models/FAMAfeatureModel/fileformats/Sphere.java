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

public class Sphere {
	private double radius;
	private Color color;
	private TextBillboard textBillboard;
	
	public Sphere() {
		radius = 1.0d;
		textBillboard = new TextBillboard();
		color = new Color(255,0,0);
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius (double radius) {
		this.radius = radius;
	}
	
	public void setColour(int r, int g, int b) {
		color = new Color(r,g,b);
	}
	
	public void setText(String text) {
		this.textBillboard.setText(text);
	}
	
	public String toString() {
		String res = "<Shape DEF='Sphere_" + textBillboard.getText() + "'>\r\n";
		res += "<Appearance containerField='appearance'>\r\n";
		res += "<Material DEF='Colour' containerField='material' ambientIntensity='0.200' shininess='0.200' diffuseColor='" 
			+ color.toString() +"' transparency='0.600'/>\r\n";
		res += "</Appearance>\r\n";
		res += "<Sphere DEF='GeoSphere_" + textBillboard.getText() 
			+ "' containerField='geometry' radius='" + radius + "'/>\r\n";
		res += "</Shape>\r\n";
		res += textBillboard.toString();
		
		return res;
	}
}
