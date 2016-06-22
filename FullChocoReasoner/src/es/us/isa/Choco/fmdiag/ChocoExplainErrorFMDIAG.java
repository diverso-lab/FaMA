package es.us.isa.Choco.fmdiag;

import static choco.Choco.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import es.us.isa.ChocoReasoner.ChocoQuestion;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public class ChocoExplainErrorFMDIAG extends ChocoQuestion implements
		ExplainErrorsQuestion {

	public boolean returnAllPossibeExplanations=false;
	private ChocoReasoner chReasoner;
	public List<String> explanations;

	Collection<Error> errors;
	Map<String, Constraint> relations =null;

	public boolean flexactive=false;
	public int m=1;
	
	public PerformanceResult answer(Reasoner r) throws FAMAException {
		
		ChocoResult res = new ChocoResult();
		chReasoner = (ChocoReasoner) r;

		if ((errors == null) || errors.isEmpty()) {
			errors = new LinkedList<Error>();
			return res;
		}
		
		Iterator<Error> itE = this.errors.iterator();
		Map<String, IntegerVariable> vars = chReasoner.getVariables();
		Map<String, IntegerExpressionVariable> setVars = chReasoner.getSetRelations();
		// mientras haya errores
		while (itE.hasNext()) {
			// crear una lista de constraints, que impondremos segun las
			// observaciones
			Error e = itE.next();

			//System.out.println("Explanations for "+e.toString());
			Map<String,Constraint> cons4obs = new HashMap<String,Constraint>();
			Observation obs = e.getObservation();
			Map<? extends VariabilityElement, Object> values = obs.getObservation();
			Iterator<?> its = values.entrySet().iterator();

			// mientras haya observations
			// las imponemos al problema como restricciones
			while (its.hasNext()) {
				int i=0;
				try {
					Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) its.next();
					Constraint cn;
					int value = (Integer) entry.getValue();
					VariabilityElement ve = entry.getKey();
					if (ve instanceof GenericFeature) {
						IntegerVariable arg0 = vars.get(ve.getName());
						cn = eq(arg0, value);
					} else {
						IntegerExpressionVariable arg0 = setVars.get(ve.getName());
						cn = eq(arg0, value);
					}
					cons4obs.put("Temporary"+i,cn);
					i++;
				} catch (ClassCastException exc) {
				}
			}
			
			//solve the problem  y fmdiag
			relations = new HashMap<String, Constraint>();
			relations.putAll(cons4obs);
			relations.putAll(chReasoner.getRelations());
			
			ArrayList<String> S = new ArrayList<String>(chReasoner.getRelations().keySet());		
			ArrayList<String> AC = new ArrayList<String>(relations.keySet());
			if(returnAllPossibeExplanations==false){
				List<String> fmdiag = fmdiag(S,AC);
				//System.out.println("Relation "+fmdiag.get(0)+" is causing the conflict");
				explanations=fmdiag;
			}else{
				List<String> allExpl= new LinkedList<String>();
				List<String> fmdiag = fmdiag(S,AC);
				while(fmdiag.size()!=0){
					allExpl.addAll(fmdiag);
					S.removeAll(fmdiag);
					AC.removeAll(fmdiag);
					fmdiag = fmdiag(S,AC);
				}
				explanations=fmdiag;
//				for(String str:allExpl){
//					System.out.println("Relation "+str+" is causing the conflict");
//				}
			}
	
		}
		return new ChocoResult();

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
		if(flexactive){
			if(S.size()<=m){
				return S;
			}
		}else{
			if(S.size()==1){
				return S;
			}
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
