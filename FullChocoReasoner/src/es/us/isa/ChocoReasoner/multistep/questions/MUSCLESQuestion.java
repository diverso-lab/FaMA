package es.us.isa.ChocoReasoner.multistep.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.multistep.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class MUSCLESQuestion extends ChocoQuestion {

	public Configuration initConfiguration, finalConfiguration;
	private Collection<String[]> modds = new ArrayList<String[]>();
	public PerformanceResult answer(Reasoner r) throws FAMAException {
		ChocoReasoner reasoner = (ChocoReasoner) r;
		reasoner.applyStagedConfiguration(initConfiguration, 0);
		reasoner.applyStagedConfiguration(finalConfiguration, 3);
		reasoner.model_drift=modds;
		reasoner.applyChanges();
		ChocoResult res = new ChocoResult();
		Model chocoProblem = reasoner.problem;
		Solver solver = new CPSolver();
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		solver.read(chocoProblem);
		// this.heuristic = new MinDomain(solver,solver.getVar(
		// reasoner.getVars()));
		// this.heuristic.setVars(solver.getVar( r.getVars()));
		// solver.setVarIntSelector(heuristic);

		Map<Integer, Collection<GenericFeature>> step_features = new HashMap<Integer, Collection<GenericFeature>>();
		if (solver.solve() == Boolean.TRUE && solver.isFeasible()) {
			do {
				for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
					IntDomainVar aux = solver.getVar(chocoProblem.getIntVar(i));
					if (aux.getName().contains("~")) {
						String name = aux.getName().substring(0,
								aux.getName().indexOf('~'));
						String step = aux.getName().substring(
								aux.getName().indexOf('~') + 1);
						if (aux.getVal() > 0) {
							int parsedStep = Integer.parseInt(step);
							Collection<GenericFeature> feats;
							if (step_features.get(parsedStep) == null) {
								feats = new ArrayList<GenericFeature>();
							} else {
								feats = step_features.get(parsedStep);
							}
							if (!feats.contains(getFeature(name, reasoner))) {
								feats.add(getFeature(name, reasoner));
								step_features.put(parsedStep, feats);
							}
						}
					}
				}
			} while (solver.nextSolution() == Boolean.TRUE);
		}

		for (int i = 0; i < step_features.size(); i++) {
			System.out.println("Features in step " + i);
			for (GenericFeature f : step_features.get(i)) {
				System.out.println(f);
			}
		}
		res.fillFields(solver);
		return res;

	}

	private GenericFeature getFeature(String name, ChocoReasoner r) {
		for (GenericFeature f : r.feature_allVariables.keySet()) {
			if (f.getName().equals(name)) {
				return f;
			}
			;
		}
		return null;
	}

	public void addMod(String[] conf){
		this.modds.add(conf);
	}
	public void setInitConf(Configuration conf) {
		this.initConfiguration = conf;
	}

	public void setEndConf(Configuration conf) {
		this.finalConfiguration = conf;
	}

}
