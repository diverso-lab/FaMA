package es.us.isa.FAMA.ws;

import java.util.Collection;

import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.ws.auxiliar.ErrorProxy;
import es.us.isa.FAMA.ws.auxiliar.ProductProxy;

public interface FaMaWS {
	
	public boolean isValid(byte[] model);
	
	public long getNumberOfProducts(byte[] model);
	
	public Collection<ProductProxy> getProducts(byte[] model);
	
	public Collection<ErrorProxy> getErrors(byte[] model);
	
	public Collection<String> getCoreFeatures(byte[] model);
	
	public Collection<String> getVariantFeatures(byte[] model);
	
	public float getVariability(byte[] mode);
	
	public long getCommonality(byte[] model, String feature);
	
	public boolean isValidProduct(byte[] model, ProductProxy p);
	
	public boolean isValidConfiguration(byte[] mode, Configuration c);
	
//	public Collection<ErrorProxy> explainErrors(byte[] model, Collection<Error> errors);
	
	public Collection<ErrorProxy> detectAndExplainErrors(byte[] model);
	
	public ProductProxy productRepair(byte[] model, ProductProxy p);
		
}
