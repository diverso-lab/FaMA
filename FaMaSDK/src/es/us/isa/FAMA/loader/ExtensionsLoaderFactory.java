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
package es.us.isa.FAMA.loader;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.loader.impl.JarJarExtensionsLoader;
import es.us.isa.FAMA.loader.impl.OSGIExtensionsLoader;
import es.us.isa.FAMA.loader.impl.SingleFileExtensionsLoader;

/**
 * This is mandated to instantiate the extension loaders classes.
 */
public class ExtensionsLoaderFactory {
	
	public ExtensionsLoader createExtensionsLoader(String str, QuestionTrader qt){
		ExtensionsLoader extLoad;
		if (str.equals("OSGI")){
			extLoad = OSGIExtensionsLoader.getInstance();
		}
		else if (str.equals("single-file")){
			//no se carga nada, todo esta en el propio jar
			extLoad = new SingleFileExtensionsLoader();
		}
		else{
			extLoad = new JarJarExtensionsLoader(str,qt);
		}
		return extLoad;
	}

}
