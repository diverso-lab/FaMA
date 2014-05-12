package es.us.isa.FAMA;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.util.Utils;

public class FAMAAnalyserDelegate {
	
	private QuestionTrader qt;
	
	public FAMAAnalyserDelegate(){
		qt = new QuestionTrader("single-file");
	}

	public double getNumberOfConfigurations(String fmPath){
		double result = 0.0;
		VariabilityModel vm = qt.openFile(fmPath);
		if (vm instanceof GenericAttributedFeatureModel){
			vm = transformToBasicModel(vm);
		}
		qt.setVariabilityModel(vm);
		NumberOfProductsQuestion nopq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		qt.ask(nopq);
		result = nopq.getNumberOfProducts();
		return result;
	}
	
	public double getNumberOfConfigurations(String fmPath, String configPath){
		//TODO
		return 0.0;
	}
	
	public Collection<Configuration> getConfigurations(String fmPath){
		Collection<Configuration> result = new LinkedList<Configuration>();
		VariabilityModel vm = qt.openFile(fmPath);
		if (vm instanceof GenericAttributedFeatureModel){
			vm = transformToBasicModel(vm);
		}
		qt.setVariabilityModel(vm);
		ProductsQuestion pq = (ProductsQuestion) qt.createQuestion("Products");
		qt.ask(pq);
		Collection<? extends GenericProduct> aux = pq.getAllProducts();
		result = Utils.products2Configurations(aux, (GenericFeatureModel) vm);
		return result;
	}
	
	public Collection<Configuration> getConfigurations(String fmPath, String configPath){
		//TODO
		return null;
	}
	
	public boolean isValidConfig(String fmPath, String configPath){
		VariabilityModel vm = qt.openFile(fmPath);
		loadConfig(vm, configPath);
		qt.setVariabilityModel(vm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		qt.ask(vq);
		boolean result = vq.isValid();
		return result;
	}
	
	public Configuration getOptimalConfig(String fmPath, String configPath){
		VariabilityModel vm = qt.openFile(fmPath);
		ExtendedConfiguration conf = (ExtendedConfiguration) qt.loadConfigurationFile(vm, configPath);
		qt.setVariabilityModel(vm);
		OptimisingConfigurationQuestion ocq = (OptimisingConfigurationQuestion) qt.createQuestion("Optimising");
		ocq.setConfiguration(conf);
		qt.ask(ocq);
		Configuration result = ocq.getOptimalConfiguration();
		return result;
	}
	
//	public Collection<String> checkEFMErrors(String path){
//		//TODO
//		return null;
//	}
//	
//	public Collection<String> checkFMErrors(String path){
//		//TODO
//		return null;
//	}
//	
//	public Collection<String> checkConfigErrors(String path){
//		//TODO
//		return null;
//	}

	private void loadConfig(VariabilityModel vm, String configFile){
		Configuration conf = qt.loadConfigurationFile(vm, configFile);
		qt.addStagedConfiguration(conf);
		
	}
	
	private VariabilityModel transformToBasicModel(VariabilityModel afm){
		IVariabilityModelTransform transform = qt.createTransform("Extended2Basic");
		VariabilityModel fm = transform.doTransform(afm);
		return fm;
	}
	
}

