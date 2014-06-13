package es.us.isa.fama.order.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import es.us.isa.FAMA.order.OrderModel;
import es.us.isa.FAMA.order.parser.OrderModelParser;

public class OrderModelParsingTests {

	OrderModelParser parser;
	
	@Before
	public void setUp() throws Exception {
		parser = new OrderModelParser();
	}

	@Test
	public void test() {
		OrderModel result = parser.parseOrderModel("test/AzureOrderModel.om");
		int numProps = result.getAllProperties().size();
		int numConstraints = result.getConstraints().size();
		assertTrue(numProps > 0 && numConstraints > 0);
	}

}
