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

public class X3DModel {
	private Group root;
	public X3DModel() {
		root = new Group();
	}
	
	public void setRoot(Group root) {
		this.root = root;
		root.setHeight(3.0d);
	}
	
	public Group getRoot() {
		return root;
	}
	
	public void layout() {
		root.layoutChildren();
	}
	
	public String toString() {
		String res = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		res += "<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.0//EN\" \"http://www.web3d.org/specifications/x3d-3.0.dtd\">\r\n";
		res += "<X3D profile='Immersive'>\r\n";
		res += "<head></head>\r\n<Scene>\r\n";
		res += "<WorldInfo title='Feature Model' info='\"Created by FAMA FW\"'/>\r\n";
		res += "<Background DEF='Background' containerField='children' skyAngle='' skyColor='.50196 .50196 1' groundAngle='' groundColor='0 0 0'/>\r\n";
		res += root.toString();
		res += "</Scene>\r\n</X3D>";
		
		return res;
	}
}
