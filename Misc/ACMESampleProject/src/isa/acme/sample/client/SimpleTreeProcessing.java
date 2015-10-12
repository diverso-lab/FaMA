package isa.acme.sample.client;

/**
 * 
 * Ejemplo de un widget de procesado. Este widget generar‡ y enviar‡ como salida al
 * modulo de E/S una cadena con todos lo nodos de un ‡rbol, siguiendo el formato:
 * 
 * 		Nodos: Nodo1, Nodo2, Nodo3, ... , NodoK
 * 
 */

import isa.acme.toolkit.client.IO.IOModule;
import isa.acme.toolkit.client.widgetInterfaces.AWidget;
import acme.client.core.Node.NodeController;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class SimpleTreeProcessing extends AWidget {
	
	private NodeController startingNode;	/** El nodo a partir del cual procesamos */
	private IOModule io;					/** El modulo de E/S que usaremos */

	public SimpleTreeProcessing(NodeController startingNode, String label, IOModule io) {

		this.startingNode = startingNode;
		this.io = io;
		
		//Creamos un bot—n, que en este caso ser‡ el aspecto de nuestro widget.
		Button button = new Button(label);
		
		//A–adimos un handler para el clic.
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//A–adimos el principio ("Nodos: ") y el nombre del nodo incial.
				String s = "Nodos: "+SimpleTreeProcessing.this.startingNode.getName();
				
				//Recorremos TODOS los nodos hijos del nodo inicial y vamos a–adiendolos
				//a la cadena.
				//
				//getAllChildren() devuelve todos los nodos hijos y los hijos de los hijos
				//en profundidad, hasta llegar a las hojas.
				for(NodeController node : SimpleTreeProcessing.this.startingNode.getAllChildren()) {
					s += ", "+node.getName();
				}
				
				//Enviamos el resultado a la salida (s) del modulo de E/S, con un nombre por defecto.
				//El tercer par‡metro es una URL, pero solo se usa en caso de salida para Rest, en
				//nuestro caso se ignorar‡.
				SimpleTreeProcessing.this.io.saveFile(s, "arbol.txt", "");
				
			}
		});
		
		//A–dimos el bot—n al panel del widget.
		//IMPORTANTE: todos las clases que extiendan a AWidget, heredan el campo "widgetPanel".
		//Es aqu’ donde se deben a–adir todos los Widget (me refiero a widgets de GWT).
		this.widgetPanel.add(button);
		
	}
	
	


}
