package es.us.isa.Choco.fmdiag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static choco.Choco.not;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.solver.Solver;
import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.DetectRedundanciesQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class ChocoDetectRedundanciesFMCORE extends ChocoQuestion implements
		DetectRedundanciesQuestion {

	public boolean returnAllPossibeExplanations = false;
	private ChocoReasoner chReasoner;
	Collection<Error> errors;
	Map<String, Constraint> relations = null;

	public PerformanceResult answer(Reasoner r) throws FAMAException {

		ChocoResult res = new ChocoResult();
		chReasoner = (ChocoReasoner) r;

		// solve the problem y fmdiag
		relations = new HashMap<String, Constraint>();
		relations.putAll(chReasoner.getRelations());

		ArrayList<String> S = new ArrayList<String>(relations.keySet());
		
		List<String> fmcore = fmcore(S,new ArrayList<String>(relations.keySet()));
		for(String red: fmcore){
			System.out.println("The relation "+red+" is redundant");
		}
		return res;

	}

	public List<String> fmcore(List<String> S,List<String> AC) {
		List<String> Stemp = new LinkedList<String>();
		Stemp.addAll(S);
		
		List<Constraint> NotS = new LinkedList<Constraint>();
		for(String Smember:S){
			Constraint c=relations.get(Smember);
			NotS.add(not(c));
		}
		
		for(String ci:S){
			if(!isConsistent(less(Stemp,ci),NotS)){
				Stemp=less(Stemp,ci);
			}
		}
		return Stemp;
	}


	private List<String> plus(List<String> a1, List<String> a2) {
		List<String> res = new ArrayList<String>();
		res.addAll(a1);
		res.addAll(a2);
		return res;
	}

	private List<String> less(List<String> aC, String s2) {
		List<String> res = new ArrayList<String>();
		res.addAll(aC);
		res.remove(s2);
		return res;
	}

	private List<String> less(List<String> aC, List<String> s2) {
		List<String> res = new ArrayList<String>();
		res.addAll(aC);
		res.removeAll(s2);
		return res;
	}
	
	private boolean isConsistent(Collection<String> aC, List<Constraint> notS) {
		Model p = new CPModel();
		p.addVariables(chReasoner.getVars());
		for(Constraint c:notS){
			p.addConstraint(c);
		}
		
		for (String rel : aC) {
			Constraint c = relations.get(rel);

			if (c == null) {
				System.out.println("Error");
			}
			p.addConstraint(c);
		}
		Solver s = new CPSolver();
		s.read(p);
		s.solve();
		return s.isFeasible();
	}

	public Collection<VariabilityElement> getRedundancies() {
		return null;
	}

}
