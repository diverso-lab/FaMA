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

public class TextBillboard {
	private String text;
	
	public TextBillboard () {
		text = "Unnamed";
	}
	
	public TextBillboard (String text) {
		this.text = text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public String toString() {
		String res = "<Billboard DEF='Billboard_" + text + "' axisOfRotation='0 0 0'>\r\n";
		res += "<Shape DEF='" + text + "_Text' containerField='children'>\r\n";
		res += "<Appearance containerField='appearance'>\r\n";
		res += "<Material DEF='White' containerField='material' ambientIntensity='0.200' shininess='0.200' diffuseColor='1 1 1'/>";
		res += "</Appearance>\r\n";
		res += "<Text DEF='Geo_" + text + "' containerField='geometry' string='\"" + text + "\"' maxExtent='0.000'>\r\n";
		res += "<FontStyle containerField='fontStyle' family='ARIAL' style='PLAIN' justify='\"MIDDLE\"\"MIDDLE\"' size='0.500' spacing='1.000'/>";
		res += "</Text>\r\n</Shape>\r\n</Billboard>\r\n";
		return res;
	}
}
