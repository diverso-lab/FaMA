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
package es.us.isa.FAMA.models.FAMAAttributed.osgi;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedWriter;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;
import es.us.isa.FAMA.models.variabilityModel.parsers.IWriter;

public class Activator implements BundleActivator {

	private List<ServiceRegistration> regs;
	
	
	public void start(BundleContext context) throws Exception {

		regs = new LinkedList<ServiceRegistration>();
		ServiceRegistration sr;
		Hashtable<String,String> d = new Hashtable<String,String>();
		d.put("id", "FaMaAttributedReader");
		d.put("extensions", "afm,efm");
		d.put("famaType", "reader");
		sr = context.registerService(IReader.class.getCanonicalName(), 
				new AttributedReader(), d);
		regs.add(sr);
		
		d = new Hashtable<String,String>();
		d.put("id", "FaMaAttributedWriter");
		d.put("famaType", "writer");
		sr = context.registerService(IWriter.class.getCanonicalName(), 
				new AttributedWriter(), d);
		regs.add(sr);
		
		

	}

	
	public void stop(BundleContext context) throws Exception {
		Iterator<ServiceRegistration> it = regs.iterator();
		while (it.hasNext()){
			ServiceRegistration sr = it.next();
			sr.unregister();
		}
	}

}
