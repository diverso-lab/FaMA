package es.us.isa.cpoptimizer.questions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cp.IloCP;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.OptimisingConfigurationQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.cpoptimizer.CPOptQuestion;
import es.us.isa.cpoptimizer.CPOptReasoner;
import es.us.isa.cpoptimizer.CPOptResult;

public class CPOptOptimisingQuestion extends CPOptQuestion implements
		OptimisingConfigurationQuestion {

	private ExtendedConfiguration result;
	private ExtendedConfiguration config;
	private String optVar;
	private boolean maximise;
	private int timeLimit;

	public ExtendedConfiguration getOptimalConfiguration() {
		return result;
	}

	public void minimise(String attName) {
		optVar = attName;
		maximise = false;
	}

	public void maximise(String attName) {
		optVar = attName;
		maximise = true;
	}

	public void setConfiguration(ExtendedConfiguration config) {
		this.config = config;

	}

	public void setTimeLimit(int miliseconds) {
		timeLimit = miliseconds;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		CPOptReasoner reasoner = (CPOptReasoner) r;
		IloCP cp = reasoner.getCp();
		CPOptResult perfResult = new CPOptResult();

		//0. add the attributes
		reasoner.addAttributedElements();
		
		// 1. map the config
		// we invoke staged config method. it considers also complex constraints
		reasoner.applyStagedConfiguration(config);

		// 2. maximise/minimise
		IloNumVar optIloVar = reasoner.getAttVars().get(optVar);
		IloObjective obj;
		
		try {
			
			if (maximise) {
				obj = cp.maximize(optIloVar);
			} else {
				obj = cp.minimize(optIloVar);
			}
			cp.add(obj);
			cp.propagate();
			long initTime = System.currentTimeMillis();
			boolean b = cp.solve();
			long time = System.currentTimeMillis() - initTime;
			perfResult.setTime(time);
		} catch (IloException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// cp.getModelImpl().getEnv().

		// 3. obtain the values
		result = new ExtendedConfiguration();

		Map<String, IloIntVar> featVars = reasoner.getFeatureVars();
		Map<String, IloNumVar> attVars = reasoner.getAttVars();
		Map<String, GenericFeature> features = reasoner.getFeatures();
		Map<String, GenericAttribute> atts = reasoner.getAtts();

		Set<Entry<String, IloIntVar>> featEntrySet = featVars.entrySet();
		for (Entry<String, IloIntVar> e : featEntrySet) {
			try {
				int val = (int) cp.getValue(e.getValue());
				VariabilityElement v = features.get(e.getKey());
				result.addElement(v, val);
			} catch (Exception exception) {
				System.err.println(e.getKey()+" => "+exception.getMessage());
			}
		}

		Set<Entry<String, IloNumVar>> attEntrySet = attVars.entrySet();
		for (Entry<String, IloNumVar> e : attEntrySet) {
			try {
				double val = cp.getValue(e.getValue());
				GenericAttribute v = atts.get(e.getKey());
				result.addAttValue(v, val);
			} catch (Exception exception) {
				System.err.println(e.getKey()+" => "+exception.getMessage());
			}
		}

		return perfResult;
	}

}
