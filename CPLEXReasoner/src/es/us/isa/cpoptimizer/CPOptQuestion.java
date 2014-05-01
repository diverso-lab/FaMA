package es.us.isa.cpoptimizer;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.Reasoner;

public abstract class CPOptQuestion implements Question {

	public Class<? extends Reasoner> getReasonerClass() {
		return CPOptReasoner.class;
	}
	
	public abstract PerformanceResult answer(Reasoner r);
	
	public void preAnswer(CPOptReasoner r){
		// TODO pre-process the reasoner
	}
	
	public void postAnswer(CPOptReasoner r){
		// TODO clean the solver
		
	}

}
