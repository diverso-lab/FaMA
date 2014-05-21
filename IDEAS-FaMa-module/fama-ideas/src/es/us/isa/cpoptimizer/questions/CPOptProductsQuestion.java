package es.us.isa.cpoptimizer.questions;

import java.util.Collection;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.cpoptimizer.CPOptQuestion;

public class CPOptProductsQuestion extends CPOptQuestion implements
		ProductsQuestion {

	public long getNumberOfProducts() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Collection<? extends GenericProduct> getAllProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		// TODO Auto-generated method stub
		return null;
	}

}
