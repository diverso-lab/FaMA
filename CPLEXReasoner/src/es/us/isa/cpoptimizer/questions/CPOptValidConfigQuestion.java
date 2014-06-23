package es.us.isa.cpoptimizer.questions;

import ilog.concert.IloException;
import ilog.cp.IloCP;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.cpoptimizer.CPOptQuestion;
import es.us.isa.cpoptimizer.CPOptReasoner;
import es.us.isa.cpoptimizer.CPOptResult;

public class CPOptValidConfigQuestion extends CPOptQuestion implements ValidConfigurationQuestion{

	private Configuration config;
	private boolean valid;
	
	public boolean isValid() {
		return valid;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		valid = false;
		CPOptReasoner reasoner = (CPOptReasoner) r;
		reasoner.restore();
		IloCP cp = reasoner.getCp();
		CPOptResult perfResult = new CPOptResult();

		
		// 1. map the config
		// we invoke staged config method. it considers also complex constraints
		reasoner.applyStagedConfiguration(config);
		try {
			long initTime = System.currentTimeMillis();
			valid = cp.propagate();
			long time = System.currentTimeMillis() - initTime;
			perfResult.setTime(time);
		} catch (IloException e) {
			e.printStackTrace();
		}
		reasoner.unapplyStagedConfigurations();
		
		return perfResult;
	}

	public void setConfiguration(Configuration c) {
		config = c;
	}

}
