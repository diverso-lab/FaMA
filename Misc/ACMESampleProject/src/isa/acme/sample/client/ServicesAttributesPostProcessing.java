package isa.acme.sample.client;

import java.util.List;

import acme.client.core.Attribute.AttributeTypeModel;
import acme.client.core.RestrictionModel.RestrictionModel;
import acme.client.core.Tree.TreeController;
import isa.acme.toolkit.client.widgets.ModelPostprocessing;

public class ServicesAttributesPostProcessing extends ModelPostprocessing {

	private String services;

	public ServicesAttributesPostProcessing(TreeController tree, boolean processAtInit, String services) {
		super(tree, false);
		this.services = services;
		this.process();
		
	}

	@Override
	public void process() {
		RestrictionModel rModel = tree.getModel();
		List<AttributeTypeModel> atms = rModel.getAllAttributeTypeModels();
		
		for(AttributeTypeModel atm : atms) {
			if(atm.getKey().equals("Densidad")){
				atm.setValue(this.services);
			}
		}
	}

}
