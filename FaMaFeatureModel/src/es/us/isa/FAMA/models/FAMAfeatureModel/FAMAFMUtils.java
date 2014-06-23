package es.us.isa.FAMA.models.FAMAfeatureModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public class FAMAFMUtils {

	/**
	 * This method obtains the name of all the leaf features of the model
	 * 
	 * @param fm A FAMa Feature Model
	 * @return Collection<String> the names of all the leaf features
	 */
	public static Collection<String> getLeaves(FAMAFeatureModel fm){
		Collection<String> result = new ArrayList<String>();
		Collection<Feature> features = fm.getFeatures();
		for (Feature f: features)
		{
			if (f.getNumberOfRelations() == 0)
			{
				//leaf features
				result.add(f.getName());
			}
		}
		return result;
	}
	
	public static Product product2SimpleProduct(Product p, Collection<String> leaves){
		Collection<GenericFeature> productFeatures = p.getFeatures();
		Product simpleProduct = new Product();
		for (GenericFeature f:productFeatures)
		{
			if (leaves.contains(f.getName()))
			{
				simpleProduct.addFeature(f);
			}
		}
		return simpleProduct;
	}
	
	public static Collection<Product> products2SimpleProducts(Collection<Product> c, FAMAFeatureModel fm){
		Collection<String> leaves = FAMAFMUtils.getLeaves(fm);
		Collection<Product> products = new LinkedList<Product>();
		for (Product p:c){
			Product aux = FAMAFMUtils.product2SimpleProduct(p, leaves);
			products.add(aux);
		}
		return products;
	}
	
}
