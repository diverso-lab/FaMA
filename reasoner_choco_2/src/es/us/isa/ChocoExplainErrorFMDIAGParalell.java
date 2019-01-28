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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import es.us.isa.Choco.fmdiag.ChocoExplainErrorFMDIAGParalell.diagThreads;
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

public class ChocoExplainErrorFMDIAGParalell extends ChocoQuestion implements ExplainErrorsQuestion {

	public boolean returnAllPossibeExplanations = false;
	private ChocoReasoner chReasoner;
	public List<String> explanations;

	Collection<Error> errors;
	Map<String, Constraint> relations = null;
	//int numberOfThreads = Runtime.getRuntime().availableProcessors();
	int numberOfThreads = 4;
	Model base;

	ExecutorService executorService = Executors.newCachedThreadPool();

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

			// System.out.println("Explanations for "+e.toString());
			Map<String, Constraint> cons4obs = new HashMap<String, Constraint>();
			Observation obs = e.getObservation();
			Map<? extends VariabilityElement, Object> values = obs.getObservation();
			Iterator<?> its = values.entrySet().iterator();

			// mientras haya observations
			// las imponemos al problema como restricciones
			while (its.hasNext()) {
				int i = 0;
				try {
					Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) its
							.next();
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
					cons4obs.put("Temporary" + i, cn);
					i++;
				} catch (ClassCastException exc) {
				}
			}

			// solve the problem y fmdiag
			relations = new HashMap<String, Constraint>();
			relations.putAll(cons4obs);
			relations.putAll(chReasoner.getRelations());

		    //////////////////////////////*******************
			base = new CPModel();
		    base.addVariables(chReasoner.getVars());
		    /////////////////////////////********************

			ArrayList<String> S = new ArrayList<String>(chReasoner.getRelations().keySet());
			ArrayList<String> AC = new ArrayList<String>(relations.keySet());
			if (returnAllPossibeExplanations == false) {
				List<String> fmdiag = fmdiag(S, AC);
				// System.out.println("Relation "+fmdiag.get(0)+" is causing the
				// conflict");
				explanations = fmdiag;
			} else {
				List<String> allExpl = new LinkedList<String>();
				List<String> fmdiag = fmdiag(S, AC);
				while (fmdiag.size() != 0) {
					allExpl.addAll(fmdiag);
					S.removeAll(fmdiag);
					AC.removeAll(fmdiag);
					fmdiag = fmdiag(S, AC);
				}
				explanations = fmdiag;
				// for(String str:allExpl){
				// System.out.println("Relation "+str+" is causing the
				// conflict");
				// }
			}

		}
		return new ChocoResult();

	}

	public List<String> fmdiag(List<String> S, List<String> AC) {
		if (S.size() == 0 || !isConsistent(less(AC, S))) {
			return new ArrayList<String>();
		} else {
			diagThreads dt = new diagThreads(new ArrayList<String>(), S, AC, numberOfThreads, executorService);
			Future<List<String>> submit = executorService.submit(dt);
		
			
		//	executorService.shutdown();
			try {
				//executorService.awaitTermination(1, TimeUnit.MINUTES);
			//	System.out.println("RESULT:"+submit.get());
				return submit.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return new LinkedList<String>();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new LinkedList<String>();
			}
		}
	}

	public class diagThreads implements Callable<List<String>>{
		List<String> D, S, AC;
		int numberOfSplits;
		ExecutorService executorService;
		int consistencyDetails=0;		

		public diagThreads(List<String> D, List<String> S,List<String> AC,int numberOfSplits, ExecutorService executorService){
			this.D=D;
			this.S=S;
			this.AC=AC;
			this.executorService=executorService;
			this.numberOfSplits=numberOfSplits;
		}
		
		@Override
		public List<String> call() throws Exception {
			//System.out.println("Executando "+D+","+S+","+AC);
			//if(!isConsistent(D)&&isConsistent(AC)){
			this.consistencyDetails = 0;

			if(isConsistent(AC, this)){
				return new ArrayList<String>();
			}
			if(S.size()==1){
				return S;
			}
			List<List<String>> outLists= new LinkedList<List<String>>();
			//Hay una optimizacion a realizar si usamos algo m'as de memoria. Si almacenamos en un mapa los 
			//resultados que tengamos siempre podemos volver a usar D=0 como hacen en el paper
			List<List<String>> splitListToSubLists = splitListToSubLists(S, S.size()/this.numberOfSplits);
			//System.out.println("Number of splits "+splitListToSubLists.size());

			for(List<String> s: splitListToSubLists){
				List<String> rest= getRest(s,splitListToSubLists);	
				List<String> less = less(AC,rest);
				diagThreads dt = new diagThreads(rest, s,less , numberOfSplits, executorService);
				//System.out.println("Llamando "+rest+","+s+","+less);

				Future<List<String>> submit = executorService.submit(dt);
				outLists.add(submit.get());
				
			}
			return plus(outLists);
		}
		
		private List<String> getRest(List<String> s2, List<List<String>> splitListToSubLists) {
			LinkedList<String> res= new LinkedList<String>();
			for(List<String> c:splitListToSubLists){
				if(c!=s2){
					res.addAll(c);
				}
			}
			return res;
		}

		private List<String> plus(List<List<String>> outLists) {
			List<String> res=new ArrayList<String>();
			for(List<String> s:outLists){
				res.addAll(s);
			}
			return res;		}

		public <T> List<List<T>> splitListToSubLists(List<T> parentList, int subListSize) {
			  List<List<T>> subLists = new ArrayList<List<T>>();
			  if (subListSize > parentList.size()) {
			     subLists.add(parentList);
			  } else {
			     int remainingElements = parentList.size();
			     int startIndex = 0;
			     int endIndex = subListSize;
			     do {
			        List<T> subList = parentList.subList(startIndex, endIndex);
			        subLists.add(subList);
			        startIndex = endIndex;
			        if (remainingElements - subListSize >= subListSize) {
			           endIndex = startIndex + subListSize;
			        } else {
			           endIndex = startIndex + remainingElements - subList.size();
			        }
			        remainingElements -= subList.size();
			     } while (remainingElements > 0);

			  }
			  return subLists;
		
	}
//	public List<String> diag(List<String> D, List<String> S,List<String> AC){
//		if(D.size()!=0&&isConsistent(AC)){
//			return new ArrayList<String>();
//		}
//		if(S.size()==1){
//			return S;
//		}
//		int k= S.size()/2;//here is where we paralelize
//		List<String> S1=S.subList(0, k);
//		List<String> S2=S.subList(k, S.size());
//		List<String> A1=diag(S2,S1,less(AC,S2));
//		List<String> A2=diag(A1,S2,less(AC,A1));
//		return plus(A1,A2);
//	}
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
 	    Model p = base;

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


	private boolean isConsistent(Collection<String> aC, diagThreads currentThread) {
 	    try{
 	    	Model p = base;
 	    

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
 	    }catch(Exception ex){
            if (currentThread.consistencyDetails < 2){
            	currentThread.consistencyDetails++;
            	
            	return isConsistent(aC, currentThread);
            }
			return false;

 	    }
 	    	
	}


	public void setErrors(Collection<Error> colErrors) {
		this.errors= colErrors;
	}

	public Collection<Error> getErrors() {
		return errors;
	}
}