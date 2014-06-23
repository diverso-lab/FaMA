package es.us.isa.basic.cpoptimizer.questions;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloSolution;
import ilog.cp.IloCP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationSetQuestion;
import es.us.isa.FAMA.stagedConfigManager.Configuration;
import es.us.isa.basic.cpoptimizer.CPOptQuestion;
import es.us.isa.basic.cpoptimizer.CPOptReasoner;
import es.us.isa.basic.cpoptimizer.CPOptResult;


public class CPOptValidConfigSetQuestion extends CPOptQuestion implements
		ValidConfigurationSetQuestion {

	private Collection<Configuration> configs;
	private Collection<Configuration> invalidConfigs;

	public CPOptValidConfigSetQuestion() {
		invalidConfigs = new ArrayList<Configuration>();
	}

	public void setConfigurationSet(Collection<Configuration> configs) {
		this.configs = configs;
	}

	public Collection<Configuration> getInvalidConfigurations() {
		return invalidConfigs;
	}

	public int getNumberOfInvalidConfigs() {
		return invalidConfigs.size();
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		invalidConfigs = new ArrayList<Configuration>();
		CPOptReasoner reasoner = (CPOptReasoner) r;
		IloCP cp = reasoner.getCp();
		CPOptResult perfResult = new CPOptResult();

		// 0. add the attributes and propagate
		reasoner.addAttributedElements();
		try {
			cp.propagate();
			IloSolution auxSol = cp.solution();
			cp.store(auxSol);
			// 1. map the config
			// we invoke staged config method. it considers also complex
			// constraints
			
			int i = 0;
			for (Configuration config : configs) {
				i++;
				IloConstraint auxConstraint = reasoner.config2Constraint(config);
				
				long initTime = System.currentTimeMillis();
				cp.add(auxConstraint);
				boolean b = cp.propagate(auxConstraint);
//				boolean b = cp.solve();
//				boolean b = cp.propagate();
				cp.remove(auxConstraint);
				cp.restore(auxSol);
				long time = System.currentTimeMillis() - initTime;
				System.out.println("Config "+i+":"+config+". Valid: "+b+". Time: "+time);
				if (!b){
					invalidConfigs.add(config);
				}

				
				
			}
//			long time = System.currentTimeMillis() - initTime;
//			perfResult.setTime(time);
//			cp.prop
		} catch (IloException e) {
			e.printStackTrace();
		}

		return perfResult;
	}

}
