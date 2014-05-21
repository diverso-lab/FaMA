package es.us.isa.FAMA.ideas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import javax.naming.OperationNotSupportedException;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.transformations.IVariabilityModelTransform;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.ideas.common.AppResponse;
import es.us.isa.ideas.common.AppResponse.Status;

public class FAMAAnalyserDelegate {
	
	public static final String VALID_OP = "validFM";
	
	public static final String NUMBER_OF_CONFIGS_OP = "numberOfConfigurations";
	
	public static final String CONFIGS_OP = "configurationsList";
	
	public static final String OPTIMAL_OP = "optimalConfig";
	
	public static final String VALID_CONFIG_OP = "validConfig";
	
	private QuestionTrader qt;
	
	private static FAMAAnalyserDelegate instance = null;
	
	private FAMAAnalyserDelegate(){
		qt = new QuestionTrader("single-file");
	}
	
	public static FAMAAnalyserDelegate getInstance(){
		if (instance == null){
			instance = new FAMAAnalyserDelegate();
		}
		return instance;
	}
	
	public AppResponse analyseForIDEAS(String fmContent, String opId,
			String extension, String configContent) {
		AppResponse result = new AppResponse();
		File fmFile = this.createTempfile(fmContent, ".afm");
		File configFile = this.createTempfile(configContent, extension);
		String message = "";
		try {
			if (opId.equals(FAMAAnalyserDelegate.VALID_CONFIG_OP)) {
				boolean aux = this.isValidConfig(fmFile.getAbsolutePath(),
						configFile.getAbsolutePath());
				if (aux) {
					message = "This FM is valid";
				} else {
					message = "This FM is NOT valid";
				}
				result.setStatus(Status.OK);
			} else if (opId.equals(FAMAAnalyserDelegate.OPTIMAL_OP)) {
				Configuration conf = this.getOptimalConfig(
						fmFile.getAbsolutePath(), configFile.getAbsolutePath());
				message = conf.toString();
				result.setStatus(Status.OK);
			} else if (opId.equals(FAMAAnalyserDelegate.NUMBER_OF_CONFIGS_OP)) {
				double aux = this.getNumberOfConfigurations(
						fmFile.getAbsolutePath(), configFile.getAbsolutePath());
				message = "Total number of configurations: " + aux;
				result.setStatus(Status.OK);
			} else if (opId.equals(FAMAAnalyserDelegate.CONFIGS_OP)) {
				Collection<? extends GenericProduct> confs = this
						.getConfigurations(fmFile.getAbsolutePath(),
								configFile.getAbsolutePath());
				int i = 1;
				for (GenericProduct p : confs) {
					message += "Configuration " + i + ": " + p.toString()
							+ "\n";
					i++;
				}
				result.setStatus(Status.OK);
			} else {
				result.setStatus(Status.ERROR);
				message = "Operation " + opId + " not available";
			}
		} catch (OperationNotSupportedException exc) {
			result.setStatus(Status.ERROR);
			message = "Operation " + opId + " not available for the arguments";
		}
		finally{
			fmFile.delete();
			configFile.delete();
		}
		result.setMessage(message);
		return result;
	}
	
	public AppResponse analyseForIDEAS(String content, String opId,
			String extension) {
		AppResponse result = new AppResponse();
		File f = this.createTempfile(content, extension);
		String message = "";

		try {
			if (f != null) {
				if (opId.equals(FAMAAnalyserDelegate.CONFIGS_OP)) {
					Collection<? extends GenericProduct> confs = this
							.getConfigurations(f.getAbsolutePath());
					int i = 1;
					for (GenericProduct p : confs) {
						message += "Configuration " + i + ": " + p.toString()
								+ "\n";
						i++;
					}
					result.setStatus(Status.OK);
				} else if (opId
						.equals(FAMAAnalyserDelegate.NUMBER_OF_CONFIGS_OP)) {
					double aux = this.getNumberOfConfigurations(f
							.getAbsolutePath());
					message = "Total number of configurations: " + aux;
					result.setStatus(Status.OK);
				} else if (opId.equals(FAMAAnalyserDelegate.VALID_OP)) {
					boolean aux = this.isValidFM(f.getAbsolutePath());
					if (aux) {
						message = "This FM is valid";
					} else {
						message = "This FM is NOT valid";
					}
					result.setStatus(Status.OK);
				} else {
					result.setStatus(Status.ERROR);
					message = "Operation " + opId + " not available";
				}
			}
		} catch (OperationNotSupportedException exc) {
			result.setStatus(Status.ERROR);
			message = "Operation " + opId + " not available for the arguments";
		}
		f.delete();
		result.setMessage(message);
		return result;
	}

	public double getNumberOfConfigurations(String fmPath) throws OperationNotSupportedException{
		double result = 0.0;
		VariabilityModel vm = qt.openFile(fmPath);
		if (vm instanceof GenericAttributedFeatureModel){
			vm = transformToBasicModel(vm);
		}
		qt.setVariabilityModel(vm);
		NumberOfProductsQuestion nopq = (NumberOfProductsQuestion) qt.createQuestion("#Products");
		if (nopq == null){
			throw new OperationNotSupportedException();
		}
		qt.ask(nopq);
		result = nopq.getNumberOfProducts();
		return result;
	}
	
	public double getNumberOfConfigurations(String fmPath, String configPath) throws OperationNotSupportedException{
		//TODO
//		return 0.0;
		throw new OperationNotSupportedException();
	}
	
	public Collection<? extends GenericProduct> getConfigurations(String fmPath) throws OperationNotSupportedException{
//		Collection<Configuration> result = new LinkedList<Configuration>();
		VariabilityModel vm = qt.openFile(fmPath);
		if (vm instanceof GenericAttributedFeatureModel){
			vm = transformToBasicModel(vm);
		}
		qt.setVariabilityModel(vm);
		ProductsQuestion pq = (ProductsQuestion) qt.createQuestion("Products");
		if (pq == null){
			throw new OperationNotSupportedException();
		}
		qt.ask(pq);
		Collection<? extends GenericProduct> aux = pq.getAllProducts();
//		result = Utils.products2Configurations(aux, (GenericFeatureModel) vm);
		return aux;
	}
	
	public Collection<? extends GenericProduct> getConfigurations(String fmPath, String configPath) throws OperationNotSupportedException{
		//TODO
		throw new OperationNotSupportedException();
//		return null;
	}
	
	public boolean isValidFM(String fmPath) throws OperationNotSupportedException{
		VariabilityModel vm = qt.openFile(fmPath);
		qt.setVariabilityModel(vm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		if (vq == null){
			throw new OperationNotSupportedException();
		}
		qt.ask(vq);
		boolean result = vq.isValid();
		return result;
	}
	
	public boolean isValidConfig(String fmPath, String configPath) throws OperationNotSupportedException{
		VariabilityModel vm = qt.openFile(fmPath);
		loadConfig(vm, configPath);
		qt.setVariabilityModel(vm);
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		if (vq == null){
			throw new OperationNotSupportedException();
		}
		qt.ask(vq);
		boolean result = vq.isValid();
		return result;
	}
	
	public Configuration getOptimalConfig(String fmPath, String configPath) throws OperationNotSupportedException{
		VariabilityModel vm = qt.openFile(fmPath);
		ExtendedConfiguration conf = (ExtendedConfiguration) qt.loadConfigurationFile(vm, configPath);
		qt.setVariabilityModel(vm);
		OptimisingConfigurationQuestion ocq = (OptimisingConfigurationQuestion) qt.createQuestion("Optimising");
		if (ocq == null){
			throw new OperationNotSupportedException();
		}
		ocq.setConfiguration(conf);
		qt.ask(ocq);
		Configuration result = ocq.getOptimalConfiguration();
		return result;
	}


	private void loadConfig(VariabilityModel vm, String configFile){
		Configuration conf = qt.loadConfigurationFile(vm, configFile);
		qt.addStagedConfiguration(conf);
		
	}
	
	private VariabilityModel transformToBasicModel(VariabilityModel afm){
		IVariabilityModelTransform transform = qt.createTransform("Extended2Basic");
		VariabilityModel fm = transform.doTransform(afm);
		return fm;
	}
	
	private File createTempfile(String content, String extension){
		String prefix = "FaMaTempFile";
		String suffix = System.nanoTime() + extension;
		File f = null;
		try {
			f = File.createTempFile(prefix, suffix);
			FileOutputStream out = new FileOutputStream(f);
			StringReader r = new StringReader(content);
			int aux;
			while ((aux = r.read()) != -1){
				out.write(aux);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
		
	}
	
}

