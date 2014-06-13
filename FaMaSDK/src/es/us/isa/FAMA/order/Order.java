package es.us.isa.FAMA.order;

import java.util.ArrayList;
import java.util.Collection;

import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public class Order {

	private Collection<Configuration> configurations;
	private ExtendedConfiguration orderConfiguration;
	
	public Order(){
		configurations = new ArrayList<Configuration>();
	}
	
	public void addConfiguration(Configuration c){
		configurations.add(c);
	}
	
	public Collection<Configuration> getConfigurations(){
		return configurations;
	}

	public ExtendedConfiguration getOrderConfiguration() {
		return orderConfiguration;
	}

	public void setOrderConfiguration(ExtendedConfiguration orderConfiguration) {
		this.orderConfiguration = orderConfiguration;
	}
	
	
}
