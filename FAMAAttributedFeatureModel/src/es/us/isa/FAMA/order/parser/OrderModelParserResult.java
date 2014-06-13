package es.us.isa.FAMA.order.parser;

import java.util.Collection;

import es.us.isa.FAMA.order.OrderModel;

public class OrderModelParserResult {

	private OrderModel model;
	private Collection<String> errors;
	
	
	
	public OrderModelParserResult(OrderModel model, Collection<String> errors) {
		super();
		this.model = model;
		this.errors = errors;
	}
	public OrderModel getModel() {
		return model;
	}
	public void setModel(OrderModel model) {
		this.model = model;
	}
	public Collection<String> getErrors() {
		return errors;
	}
	public void setErrors(Collection<String> errors) {
		this.errors = errors;
	}
	
	
	
}
