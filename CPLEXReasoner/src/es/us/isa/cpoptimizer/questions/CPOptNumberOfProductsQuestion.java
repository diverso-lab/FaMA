package es.us.isa.cpoptimizer.questions;

import ilog.cp.IloCP;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.cpoptimizer.CPOptQuestion;
import es.us.isa.cpoptimizer.CPOptReasoner;
import es.us.isa.cpoptimizer.CPOptResult;

public class CPOptNumberOfProductsQuestion extends CPOptQuestion implements
		NumberOfProductsQuestion {
	
	private double products;

	public double getNumberOfProducts() {
		return products;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		// TODO Auto-generated method stub
		CPOptReasoner reasoner = (CPOptReasoner) r;
		IloCP cp = reasoner.getCp();
		CPOptResult perfResult = new CPOptResult();
		
		
//		cp.propagate();
//		cp.solve();
		
		
		return perfResult;
	}

}
