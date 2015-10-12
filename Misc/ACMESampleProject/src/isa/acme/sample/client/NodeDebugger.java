package isa.acme.sample.client;

import isa.acme.toolkit.client.IOtypes.INtype;
import isa.acme.toolkit.client.widgetInterfaces.AWidget;
import acme.client.core.Node.NodeController;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * 
 * Ejemplo de un widget para mostrar información de un nodo. Se mostrará, con un
 * estilo mínimo, información del nodo (elegida aleatoriamente a modo de ilustración).
 * 
 */

public class NodeDebugger extends AWidget implements INtype<NodeController>{
	
	/** El panel que englobará los elementos de información que queramos añadir. */
	private Panel debuggerPanel = new AbsolutePanel();
	
	public NodeDebugger() {
		
		//Podemos asignar aquí el estilo por defecto del widget. Se recomienda, si se
		//está creando un widget genérico para ACME, nombrar el estilo como:
		//	ACME-<nombre de la clase>
		setStyleName("ACME-NodeDebugger");
		
		//IMPORTANTE: todos las clases que extiendan a AWidget, heredan el campo "widgetPanel".
		//Es aquí donde se deben añadir todos los Widget (me refiero a widgets de GWT).
		
		//Añadimos una etiqueta que hará las veces de título para el widget.
		this.widgetPanel.add(new Label("Debugger panel"));
		
		//Después de la etiqueta añadimos el panel que irá cambiando de contenido
		//cuando el nodo que se observa se modifique.
		this.widgetPanel.add(debuggerPanel);
	}
	
	/** Método privado para ayudarnos a rellenar el widget.*/
	private void showNodeContents(NodeController target) {
		
		//Borramos el contenido actual (o anterior, según se vea) del panel.
		this.debuggerPanel.clear();
		
		//Añadimos 3 etiquetas con información elegida al azar (número de hijos,
		//número de instancia de ese tipo de nodo en el árbol, y el nombre del tipo de nodo).
		this.debuggerPanel.add( new Label("Children all count: "+target.getAllChildren().size()) );
		this.debuggerPanel.add( new Label("Instance num: "+target.getInst()) );
		this.debuggerPanel.add( new Label("Type: "+target.getNodeType().getDisplayName()) );
	}

	
	/** Este es el método que se ejecutará cuando el observado cambie. En nuestro caso
	 * tenemos que actualizar la interfaz con la información del nodo "target". Por lo tanto
	 * llamamos al método privado que hemos definido anteriormente, con el nodo "target" como
	 * parámetro. */
	@Override
	public void updateObserver(NodeController target) {
		this.showNodeContents(target);	
	}

	

}
