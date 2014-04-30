package es.us.isa.FAMA.ws;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import es.us.isa.FAMA.Reasoner.QuestionTrader;

public class Activator implements BundleActivator {

	private ServiceRegistration registration;
	private ServiceReference sr = null;
	
	public static QuestionTrader qt = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		String className = QuestionTrader.class.getCanonicalName();
		sr = context.getServiceReference(className);
		qt = (QuestionTrader) context.getService(sr);
		
		
		Hashtable<String,String> t = new Hashtable<String, String>();
		t.put("id", "FaMa");
		t.put("osgi.remote.interfaces", "*");
		t.put("osgi.remote.configuration.type", "pojo");
	    t.put("osgi.remote.configuration.pojo.address", "http://localhost:8082/FaMaWS");  
	    FaMaWS fws = new FaMaWSImpl();
		registration = context.registerService(FaMaWS.class.getCanonicalName(), fws, t);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (sr != null){
			context.ungetService(sr);
		}
		registration.unregister();
	}

}
