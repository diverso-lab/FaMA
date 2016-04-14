package es.us.isa.Sat4j.fmdiag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Exceptions.FAMAException;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.ExplainErrorsQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Observation;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class Sat4jExplainErrorFMDIAG extends Sat4jQuestion implements
		ExplainErrorsQuestion {

	public boolean returnAllPossibeExplanations=false;
	private Sat4jReasoner reasoner;
	public List<String> explanations;

	Collection<Error> errors;
	Map<String, String> relations =null;

	
	public PerformanceResult answer(Reasoner r) throws FAMAException {
		
		Sat4jResult res = new Sat4jResult();
		reasoner = (Sat4jReasoner) r;

		if ((errors == null) || errors.isEmpty()) {
			errors = new LinkedList<Error>();
			return res;
		}
		
		Iterator<Error> itE = this.errors.iterator();

		// mientras haya errores
		while (itE.hasNext()) {
			// crear una lista de constraints, que impondremos segun las
			// observaciones
			Error e = itE.next();

			System.out.println("Explanations for "+e.toString());
			Map<String,String> cons4obs = new HashMap<String,String>();
			Observation obs = e.getObservation();
			Map<? extends VariabilityElement, Object> values = obs.getObservation();
			Iterator<?> its = values.entrySet().iterator();

			// mientras haya observations
			// las imponemos al problema como restricciones
			while (its.hasNext()) {
				int i=0;
				try {
					Entry<? extends VariabilityElement, Object> entry = (Entry<? extends VariabilityElement, Object>) its.next();
					String clause;
					int value = (Integer) entry.getValue();
					VariabilityElement ve = entry.getKey();
					if (ve instanceof GenericFeature) {
						clause=reasoner.getVariables().get(ve.getName())+" "+value;
						
					} else {
						clause=reasoner.getVariables().get(ve.getName())+" "+value;

					}
					cons4obs.put("Temporary"+i,clause);
					i++;
				} catch (ClassCastException exc) {
				}
			}
			
			//solve the problem  y fmdiag
			relations = new HashMap<String, String>();
			relations.putAll(cons4obs);
			int cindex= 0;
			for(String cl:reasoner.clauses){
				relations.put(cindex+"rel", cl);
			}
			ArrayList<String> S = reasoner.clauses;		
			ArrayList<String> AC = new ArrayList<String>(relations.keySet());
			if(returnAllPossibeExplanations==false){
				List<String> fmdiag = fmdiag(S,AC);
				System.out.println("Relation "+fmdiag.get(0)+" is causing the conflict");
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
				for(String str:allExpl){
					System.out.println("Relation "+str+" is causing the conflict");
				}
			}
	
		}
		return res;

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
		
		
		//First we create the content of the cnf
		String cnf_content = "c CNF file\n";

		// We show as comments the variables's number
		Iterator<String> it = reasoner.variables.keySet().iterator();
		while (it.hasNext()) {
			String varName = it.next();
			cnf_content += "c var " + reasoner.variables.get(varName) + " = " + varName
					+ "\n";
		}

		// Start the problem
		cnf_content += "p cnf " + reasoner.variables.size() + " " + (-1+ relations.values().size())
				+ "\n";
		// Clauses
		it = relations.values().iterator();
		while (it.hasNext()) {
			cnf_content += (String) it.next() + "\n";
		}

		// End file
		cnf_content += "0";
		ByteArrayInputStream stream= new ByteArrayInputStream(cnf_content.getBytes(StandardCharsets.UTF_8));
		
		
		
		ISolver s = SolverFactory.newDefault();
		Reader reader = new DimacsReader(s);
		try {
			reader.parseInstance(stream);
			return s.isSatisfiable();
		} catch (TimeoutException | ParseFormatException | ContradictionException | IOException e) {
			e.printStackTrace();
			
		}
		return false;
		
	}


	public void setErrors(Collection<Error> colErrors) {
		this.errors= colErrors;
	}


	public Collection<Error> getErrors() {
		return errors;
	}

}
