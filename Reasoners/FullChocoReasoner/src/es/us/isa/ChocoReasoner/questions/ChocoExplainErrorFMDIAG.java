package es.us.isa.ChocoReasoner.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.solver.Solver;
import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;

public class ChocoExplainErrorFMDIAG extends ChocoQuestion implements
		ExplainErrorsQuestion {

	private ChocoReasoner chReasoner;
	Collection<Error> errors;
	Map<String, Constraint> relations =null;
	public PerformanceResult answer(Reasoner r) throws FAMAException {
		
		ChocoResult res = new ChocoResult();
		chReasoner = (ChocoReasoner) r;
		

		if ((errors == null) || errors.isEmpty()) {
			errors = new LinkedList<Error>();
			return res;
		}
//		Model p = chReasoner.getProblem();
//		Solver s = new CPSolver();
//		s.read(p);
//		s.solve();
//		System.out.println(s.isFeasible());
//		
		
		relations = new HashMap<String, Constraint>();
		relations.putAll(chReasoner.getRelations());
		relations.putAll(chReasoner.getConfigConstraints());
		
		ArrayList<String> S = new ArrayList<String>(relations.keySet());		
		ArrayList<String> AC = new ArrayList<String>(relations.keySet());
		
		Collections.reverse(AC);Collections.reverse(S);
		if(errors.size()>0){
			List<String> fmdiag = fmdiag(S,AC);
			for(String str: fmdiag ){
				System.out.println("Relation "+str+" is causing the conflict");
			}
		}
		return null;
	}
	
	public List<String> fmdiag(List<String> S,List<String> AC){
		if(S.size()==0||!isConsistent(less(AC,S))){
			return new ArrayList<String>();
		}else{
			return diag(new ArrayList<String>(),S,AC);
		}
	} 
	
	public List<String> diag(List<String> D, List<String> S,List<String> AC){
		if(D.size()!=0&&isConsistent(AC)){
			return new ArrayList<String>();
		}
		if(S.size()==1){
			return S;
		}
		int k= S.size()/2;
		List<String> S1=S.subList(0, k);
		List<String> S2=S.subList(k, S.size());
		List<String> A1=diag(S2,S1,less(AC,S2));
		List<String> A2=diag(A1,S2,less(AC,A1));
		return plus(A1,A2);
	}
	
	private List<String> plus(List<String> a1, List<String> a2) {
		List<String> res=new ArrayList<String>();
		res.addAll(a1);
		res.addAll(a2);
		return res;
	}

	private List<String> less(List<String> aC, List<String> s2) {
		List<String> res=new ArrayList<String>();
		res.addAll(aC);
		res.removeAll(s2);
		return res; 
	}

	private boolean isConsistent(Collection<String> aC) {
		Model p = new CPModel();
		p.addVariables(chReasoner.getVars());

		for(String rel:aC){
			Constraint c = relations.get(rel);
			if(c==null){
				System.out.println("Error");
			}
			p.addConstraint(c);
		}
		Solver s = new CPSolver();
		s.read(p);
		s.solve();
		return s.isFeasible();
	}

	public void setErrors(Collection<Error> colErrors) {
		this.errors= colErrors;
	}

	public Collection<Error> getErrors() {
		return errors;
	}

}
