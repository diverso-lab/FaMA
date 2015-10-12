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

package es.us.isa.FAMA.models.FAMAfeatureModel.osgi;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.FMPlainTextReader;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.GraphVizWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.X3DWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLReader;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLWriter;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain.FaMaPlainTextParser;
import es.us.isa.FAMA.models.FAMAfeatureModel.transformations.AtomicSet;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;


public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		registerReadersAndWriters(context);
		registerTransformations(context);
	}

	private void registerReadersAndWriters(BundleContext context) {
		Hashtable<String,String> d = new Hashtable<String,String>();
		d.put("id", "famaReader");
		d.put("extensions", "xml,fama");
		d.put("famaType", "reader");
		context.registerService(IReader.class.getCanonicalName(), 
				new XMLReader(), d);
		
		d = new Hashtable<String, String>();
		d.put("id", "famaPlainReader");
		d.put("extensions", "fm,fmf");
		d.put("famaType", "reader");
		context.registerService(IReader.class.getCanonicalName(), 
				new FMPlainTextReader(), d);
		
		d = new Hashtable<String,String>();
		d.put("id", "famaWriter");
		d.put("famaType", "writer");
		context.registerService(IWriter.class.getCanonicalName(),
				new XMLWriter(), d);
		d = new Hashtable<String, String>();
		d.put("id", "graphvizWriter");
		d.put("famaType", "writer");
		context.registerService(IWriter.class.getCanonicalName(), new GraphVizWriter(), d);
	
		d = new Hashtable<String, String>();
		d.put("id", "X3dWriter");
		d.put("famaType", "writer");
		context.registerService(IWriter.class.getCanonicalName(), new X3DWriter(), d);
		
		d = new Hashtable<String,String>();
		d.put("id", "AtomicSet");
		context.registerService(IVariabilityModelTransform.class.getCanonicalName(), new AtomicSet(), d);
	
		
	}
	
	private void registerTransformations(BundleContext context) {
		Hashtable<String,String> d = new Hashtable<String,String>();
		d.put("id", "atomicSet");
		d.put("famaType", "transform");
		context.registerService(IVariabilityModelTransform.class.getCanonicalName(), 
				new AtomicSet(), d);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
