package es.us.isa.FAMA.ws;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.CommonalityQuestion;
import es.us.isa.FAMA.Reasoner.questions.CoreFeaturesQuestion;
import es.us.isa.FAMA.Reasoner.questions.DetectErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ExplainInvalidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariantFeaturesQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.ws.auxiliar.ErrorProxy;
import es.us.isa.FAMA.ws.auxiliar.ProductProxy;

public class FaMaWSImpl implements FaMaWS {
	
	private QuestionTrader qt;
	
	public FaMaWSImpl(){
		qt = Activator.qt;
	}

	@Override
	public Collection<ErrorProxy> detectAndExplainErrors(byte[] model) {
		VariabilityModel vm = setModel(model);
		DetectErrorsQuestion deq = (DetectErrorsQuestion) qt.createQuestion("DetectErrors");
		deq.setObservations(vm.getObservations());
		qt.ask(deq);
		Collection<Error> errors = deq.getErrors(); 
		ExplainErrorsQuestion eeq = (ExplainErrorsQuestion) qt.createQuestion("Explanations");
		eeq.setErrors(errors);
		qt.ask(eeq);
		Collection<Error> explainedErrors = eeq.getErrors();
		Collection<ErrorProxy> res = new LinkedList<ErrorProxy>();
		for (Error e:explainedErrors){
			ErrorProxy ep = new ErrorProxy();
			ep.setExplanations(e.getExplanations());
			ep.setObservations(e.getObservation().getObservation());
			res.add(ep);
		}
		return res;
	}

//	@Override
//	public Collection<ErrorProxy> explainErrors(byte[] model,
//			Collection<Error> errors) {
//		setModel(model);
//		ExplainErrorsQuestion eeq = (ExplainErrorsQuestion) qt.createQuestion("Explanations");
//		eeq.setErrors(errors);
//		qt.ask(eeq);
//		Collection<Error> explainedErrors = eeq.getErrors();
//		Collection<ErrorProxy> res = new LinkedList<ErrorProxy>();
//		for (Error e:explainedErrors){
//			ErrorProxy ep = new ErrorProxy();
//			ep.setExplanations(e.getExplanations());
//			ep.setObservations(e.getObservation().getObservation());
//			res.add(ep);
//		}
//		return res;
//	}

	@Override
	public long getCommonality(byte[] model, String feature) {
		VariabilityModel vm = setModel(model);
		CommonalityQuestion cq = (CommonalityQuestion) qt.createQuestion("Commonality");
		GenericFeatureModel fm = (GenericFeatureModel) vm;
		GenericFeature f = fm.searchFeatureByName(feature);
		if (f != null){
			cq.setFeature(f);
			qt.ask(cq);
			long res = cq.getCommonality(); 
			return res;
		}
		else{
			return 0;
		}
		
	}

	@Override
	public Collection<String> getCoreFeatures(byte[] model) {
		setModel(model);
		CoreFeaturesQuestion cfq = (CoreFeaturesQuestion) qt.createQuestion("CoreFeatures");
		qt.ask(cfq);
		Collection<GenericFeature> feats = cfq.getCoreFeats();
		Collection<String> res = new LinkedList<String>();
		for (GenericFeature f:feats){
			res.add(f.getName());
		}
		return res;
	}

	@Override
	public Collection<ErrorProxy> getErrors(byte[] model) {
		VariabilityModel vm = setModel(model);
		DetectErrorsQuestion deq = (DetectErrorsQuestion) qt.createQuestion("DetectErrors");
		deq.setObservations(vm.getObservations());
		qt.ask(deq);
		Collection<Error> errors = deq.getErrors(); 
		Collection<ErrorProxy> res = new LinkedList<ErrorProxy>();
		for (Error e:errors){
			ErrorProxy ep = new ErrorProxy();
			ep.setObservations(e.getObservation().getObservation());
			res.add(ep);
		}
		return res;
	}

	@Override
	public long getNumberOfProducts(byte[] model) {
		setModel(model);
		NumberOfProductsQuestion npq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		qt.ask(npq);
		long res = (long) npq.getNumberOfProducts();
		return res;
	}

	@Override
	public Collection<ProductProxy> getProducts(byte[] model) {
		setModel(model);
		ProductsQuestion pq = (ProductsQuestion) qt.createQuestion("Products");
		qt.ask(pq);
		Collection<GenericProduct> products = new LinkedList<GenericProduct>();
		products.addAll(pq.getAllProducts());
		Collection<ProductProxy> res = new LinkedList<ProductProxy>();
		for (GenericProduct p:products){
			ProductProxy aux = new ProductProxy();
			Collection<VariabilityElement> feats = p.getElements();
			for (VariabilityElement e:feats){
				aux.addFeature(e.getName());
			}
			res.add(aux);
		}
		return res;
	}

	@Override
	public float getVariability(byte[] model) {
		setModel(model);
		VariabilityQuestion vq = (VariabilityQuestion) qt.createQuestion("Variability");
		qt.ask(vq);
		float res = vq.getVariability();
		return res;
	}

	@Override
	public Collection<String> getVariantFeatures(byte[] model) {
		setModel(model);
		VariantFeaturesQuestion vfq = (VariantFeaturesQuestion) qt.createQuestion("VariantFeatures");
		qt.ask(vfq);
		Collection<GenericFeature> feats = vfq.getVariantFeats();
		Collection<String> res = new LinkedList<String>();
		for (GenericFeature f:feats){
			res.add(f.getName());
		}
		return res;
	}

	@Override
	public boolean isValid(byte[] model) {
		setModel(model);
		ValidQuestion q = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(q);
		boolean res = q.isValid();
		return res;
	}

	@Override
	public boolean isValidConfiguration(byte[] model, Configuration c) {
		setModel(model);
		ValidConfigurationQuestion vpq = (ValidConfigurationQuestion) qt.createQuestion("ValidConfiguration");
		vpq.setConfiguration(c);
		qt.ask(vpq);
		boolean res = vpq.isValid();
		return res;
	}

	@Override
	public boolean isValidProduct(byte[] model, ProductProxy p) {
		GenericFeatureModel fm = (GenericFeatureModel) setModel(model);
		ValidProductQuestion vpq = (ValidProductQuestion) qt.createQuestion("ValidProduct");
		Product product = proxy2product(p, fm);
		vpq.setProduct(product);
		qt.ask(vpq);
		boolean res = vpq.isValid();
		return res;
	}

	@Override
	public ProductProxy productRepair(byte[] model, ProductProxy p) {
		GenericFeatureModel fm = (GenericFeatureModel)setModel(model);
		ExplainInvalidProductQuestion epq = (ExplainInvalidProductQuestion) qt.createQuestion("ExplainProduct");
		Product product = proxy2product(p, fm);
		epq.setInvalidProduct(product);
		qt.ask(epq);
		
		Product aux = epq.getFixedProduct();
		ProductProxy res = new ProductProxy();
		Collection<VariabilityElement> feats = aux.getElements();
		for (VariabilityElement e:feats){
			res.addFeature(e.getName());
		}
		return res;
	}

	private VariabilityModel setModel(byte[] model){
		String modelPath = toLocalFile(model);
		VariabilityModel vm = qt.openFile(modelPath);
		qt.setVariabilityModel(vm);
		removeLocalFile(modelPath);
		return vm;
	}
	
	private String toLocalFile(byte[] model){
		//solo sera modelo atribuido si es de texto
		//plano y tiene seccion %Attributes
		String path = null, extension = "";
		
		String text = new String(model);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(model);
			Document d = db.parse(is);
			//si no hay problemas, es que es un xml
			extension = ".xml";
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		if (extension.isEmpty()){
			//no es xml, por lo que es texto plano
			if (text.contains("%Attributes")){
				//modelo atribuido
				extension = ".afm";
			}
			else{
				//modelo simple
				extension = ".fm";
			}
		}
		
		try {
			//finalmente, guardamos con la extension
			File f = File.createTempFile("tmp", extension);
			FileOutputStream os = new FileOutputStream(f);
			os.write(model);
			os.flush();
			os.close();
			path = f.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	private void removeLocalFile(String path){
		File f = new File(path);
		f.delete();
	}
	
	private Product proxy2product(ProductProxy proxy, GenericFeatureModel fm){
		Product res = new Product();
		Collection<String> feats = proxy.getFeatures();
		for (String s: feats){
			GenericFeature f = fm.searchFeatureByName(s);
			if (f != null){
				res.addFeature(f);
			}
		}
		return res;
	}
	
}
