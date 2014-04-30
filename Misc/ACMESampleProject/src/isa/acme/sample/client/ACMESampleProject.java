package isa.acme.sample.client;

import isa.acme.toolkit.client.IO.HybridIO;
import isa.acme.toolkit.client.IO.IOModule;
import isa.acme.toolkit.client.ProyectTypes.Project;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ACMESampleProject implements EntryPoint {
	
	/**
	 * MŽtodo principal
	 */
	public void onModuleLoad() {
		/*
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode("http://localhost:8080/soa4all/IntegratedRankingModule/IntegratedRanking/ServicesServlet"));
		
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
			    public void onError(Request request, Throwable exception) {
			    	Window.alert("ERROR: "+exception.getMessage());
			    }

				public void onResponseReceived(Request request, Response response) {
			      if (200 == response.getStatusCode()) {
			    	  

			    	
			      } else {
			    	  Window.alert("ERROR: "+response.getStatusCode()+" "+response.getStatusText()+" \n"+response.getHeadersAsString());
			      }
			    }       
			});
		} catch (RequestException e) {
			Window.alert("EXC: "+ e.getMessage());
		}*/
		
		
		
		/**
		 * Primero creamos un modulo de entrada salida.
		 * Hasta el momento el œnico disponile es HybridIO, que combina E/S con el applet
		 * para local y Rest online.
		 */
		IOModule io = new HybridIO();
		
		
		/**
		 * Creamos un proyecto nuevo. Para el ejemplo hemos creado una nueva clase MyProject
		 * que implemente la interfaz de proyectos.
		 */
		Project myProject = new MyProject();
		
		/**
		 * Asignamos el modulo de E/S que estamos usando al proyecto.
		 * 
		 * Podr’amos tener varios proyectos en una sola aplicaci—n, y a su vez un modulo
		 * de E/S distinto para cada uno de ellos, pero la filosofia que se plantea es que el
		 * modulos de E/S sea el mismo para toda la aplicaci—n.
		 */
		myProject.setIO(io);
		
	}
}
