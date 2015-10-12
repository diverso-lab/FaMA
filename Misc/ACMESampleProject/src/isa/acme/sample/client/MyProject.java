package isa.acme.sample.client;

import isa.acme.toolkit.client.IO.IOModule;
import isa.acme.toolkit.client.ProyectTypes.Project;
import isa.acme.toolkit.client.bindings.Binding;
import isa.acme.toolkit.client.widgets.AddRemoveNode;
import isa.acme.toolkit.client.widgets.AttributeViewer;
import isa.acme.toolkit.client.widgets.ModelPostprocessing;
import isa.acme.toolkit.client.widgets.SearchForTreeViewer;
import isa.acme.toolkit.client.widgets.TreeViewer;
import isa.acme.toolkit.client.widgets.XMLDepthProcessingWidget;
import isa.acme.toolkit.client.widgets.XViewHeader;
import acme.client.core.Node.NodeController;
import acme.client.core.RestrictionModel.RestrictionModel;
import acme.client.core.Tree.TreeController;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * 
 * Clase de ejemplo que implemente Project.
 * 
 * Esto se prevee que cambiar‡ en breve. Por lo tanto no prestar demasiada atenci—n
 * a los mŽtodos que se han dejado sin implementar.
 * 
 * Lo importante es entender que en el constructor solo debemos realizar incializaciones
 * b‡sicas. Es en el init() donde se debe incluir el grueso de la inicializaci—n.
 * 
 * El mŽtodo init() se llama automaticamente por otras clases (IO, Importaci—n) cuando
 * todos los recursos (ficheros, modelo...) para inicializar el proyecto est‡n listos.
 *
 */
public class MyProject implements Project {
	
	private RestrictionModel model = null;		/** Modelo de restricci—n. En nuestro caso
	 												ser’a donde se almacenar‡ el modelo
	 												"arbol" una vez pocesados los ficheros
	 												structure y grammar. */
	
	private String name = "Mi Proyecto";		/** Un nombre para nuestro proyecto **/
	
	private TreeController tree = null;			/** Aqu’ se almacenar‡ el ‡rbol que vamos
													a editar. **/
	
	private IOModule io = null;					/** Modulo de E/S **/
	
	
	/** En nuestro ejemplo no es necesario hacer nada en el constructor **/
	public MyProject() {

	}
	
	/** Este mŽtodo se llamar‡ cuando se quiera borrar el contenido del proyecto.
	 * 	Se utiliza si se quiere "resetear" para luego asignarle otro, por ejemplo,
	 *  importandolo desde disco.
	 */
	@Override
	public void clearProyect() {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * Aqu’ ir’a c—digo para la importaci—n.
	 */
	@Override
	public void deserialize(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RestrictionModel getModel() {
		return this.model;
	}

	@Override
	public void setModel(RestrictionModel model) {
		this.model = model;
		
	}

	@Override
	public void setIO(IOModule io) {
		this.io = io;
		io.setProject(this);
		io.loadModel("fama-deployer");
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Este es el mŽtodo m‡s importante. 
	 */
	@Override
	public void init() {
		
		//Este ser‡ el panel principal del proyecto. Le decimos las unidades que
		//vamos a usar -> Unit.PX = pixeles
		DockLayoutPanel projectPanel = new DockLayoutPanel(Unit.PX);
		
		//Este panel nos permite modificar el tama–o de sus columnas (y de sus filas).
		//Lo usaremos para el "editor" -> Visor de arbol + Visor de atributos/nodos
		SplitLayoutPanel editorPanel = new SplitLayoutPanel();
		
		//Al norte, con altura 30 pixeles, a–adimos lo que podr’a ser un logo. Por simplificar,
		//solo a–adimos una etiqueta.
		projectPanel.addNorth(new Label(name), 30);
		
		//Inicializamos nuestro ‡rbol, pas‡ndole al constructor un nombre por defecto
		//y el modelo en el que lo vamos a basar.
		this.tree = new TreeController("arbol", this.model);
		//metemos la raiz...
		this.tree.addNode(this.model.getRoot());
		//por cada uno de los hijos...
		NodeController controller = tree.getRoot().createNewChild("Node", this.model.getNodeTypeByName("Node"));
		controller.getAttributes().getAttribute("Name").getModel().setValue("FaMa");
		controller.notifyNodeChange();
		controller.getAttributes().getAttribute("Name").forceLabelUpdate();
		controller.getAttributes().getAttribute("Selected").getModel().setValue("True");
		
		
//		tree.model.
		
		//Para que inicialmente el ‡rbol no estŽ vacio, le a–adimos manualmente el nodo raiz.
//		this.tree.addNode(this.model.getRoot());

		//Una cabecera donde podemos a–adir botones, etiquetas, o como en nuestro caso,
		//una barra de bœsqueda instant‡nea.
		XViewHeader header = new XViewHeader(this.tree);
		
		//A–adimos la cabecera al panel de edici—n
		editorPanel.add(header);
		
		//Creamos un visor para el ‡rbol, pas‡ndole el ‡rbol que debe mostrar.
		TreeViewer treeviewer = new TreeViewer(this.tree.getRoot());
		//este constructor de aqui abajo te oculta el elemento raiz definido en el xml
//		TreeViewer treeviewer = new TreeViewer(this.tree.getRoot(),true,true);
		
		//Cremos un "NodeDebugger" (widget que hemos implementado para este ejemplo)
		//y que usaremos para sustituirlo por el visor de atributos.
		//NodeDebugger nodedebugger = new NodeDebugger();
		
		//Lo a–adimos en el editorPanel, a la derecha, con 300 pixeles de anchura por defecto.
		//Recordamos que el editorPanel permite modificar el tama–o.
		//editorPanel.addEast(nodedebugger, 300);
		
		
		//A–adimos el visor al panel de edici—n justo despuŽs de la cabecera.
		editorPanel.add(treeviewer);
		editorPanel.getElement().getStyle().setProperty("height", "100%");
		
		
		//Creamos un widget de bœsqueda isntant‡nea, pas‡ndole el visor de ‡rbol
		//sobre el que queremos que actœe.
		SearchForTreeViewer search = new SearchForTreeViewer(treeviewer);
		
		//Y se lo a–adimos a la cabecera, a la izquiera.
		header.setLeftInfo(search);
		
		//Ahora creamos un widget para a–adir y eliminar nodos, pas‡ndole el ‡rbol
		//sobre el que debe actuar.
//		AddRemoveNode ar = new AddRemoveNode(this.tree);
		
		//Y lo a–adimos al sur del panel del proyecto.
//		projectPanel.addSouth(ar, 30);
		
		
		
		
		
		//Equivalente a lo anterior pero con el visor de atributos incluido en el Toolkit
		//
		AttributeViewer av = new AttributeViewer();
		editorPanel.addEast(av, 300);
		
		//Creamos un widget para procesar el ‡rbol. En este caso es un widget que se ha
		//implementado para este ejemplo.
		//SimpleTreeProcessing processing = new SimpleTreeProcessing(this.tree.getRoot(), "Generar XML", this.io);
		
//		XMLDepthProcessingWidget processing = new XMLDepthProcessingWidget(this.tree.getRoot(), "Generar XML", this.io, true);
		
		//Lo a–adimos al norte del panel del proyecto, junto con el "logo".
//		projectPanel.addNorth(processing, 30);
		
		//A–adimos el editorPanel dentro del projectPanel
		projectPanel.add(editorPanel);
		
		//Y a–adimos el projectPanel al body.
		RootPanel.get().add(projectPanel);
		
		//Esto es para evitar un bug con el estilo de los paneles.
		projectPanel.getElement().getStyle().setProperty("height", "100%");
		
		
		/** Bindings */
		
		//Creamos un binding entre el treeviwer y el ar + nodedebugger:
		//		treeviewer ->o )-> ar
		//					   )->nodedebugger
		//
		Binding<NodeController> tv_ar_nd_binding = new Binding<NodeController>();
		
		//Vinculamos la salida del treeviwewer (nodo selecionado) al "ar" (para
		//que cuando seleccionemos un nodo, el widget "ar" sepa si se puede elminar o no,
		//y sea capaz de calcular que nodos puede a–adir)
//		tv_ar_nd_binding.bind(treeviewer, ar);
		
		//Vinculamos la salida del treeviewer al nodedebugger, para que cuando seleccionemos
		//un nodo en el treeviewer, avise al nodedebugger para que este muestre su informaci—n.
		//tv_ar_nd_binding.bind(treeviewer, nodedebugger);
		
		//Equivalente a lo anterior pero con el visor de atributos incluido en el Toolkit
		//
		Binding<NodeController> tv_ar_av_binding = new Binding<NodeController>();
//		tv_ar_av_binding.bind(treeviewer, ar);
		tv_ar_av_binding.bind(treeviewer, av);
		
		//De manera similar, creamos un binding entre el ar y el treeviewer
		//		ar ->o )->treeviewer
		//
		Binding<NodeController> ar_tv_binding = new Binding<NodeController>();
		
		//Vinculamos la salida del "ar" (nodo padre del creado o nodo eliminado), para que
		//el treeviewer sepa que porci—n del ‡rbol debe redibujar.
//		ar_tv_binding.bind(ar, treeviewer);
	
		
	}
	
	
	/** Si queremos carcar un ‡rbol desde fuera. */
	@Override
	public void loadTree(TreeController tree) {
		this.tree = tree;
		
		//Aqu’ habr’a que encaegarse de que la interfaz de usuario reflejara los cambios.
	}

	@Override
	public void proyectViewReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCase(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSerial(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
