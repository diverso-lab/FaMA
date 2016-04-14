package es.us.isa.Sat4j.fmdiag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

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
import es.us.isa.FAMA.Reasoner.questions.DetectRedundanciesQuestion;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.Sat4jReasoner.Sat4jQuestion;
import es.us.isa.Sat4jReasoner.Sat4jReasoner;
import es.us.isa.Sat4jReasoner.Sat4jResult;

public class ChocoDetectRedundanciesFMCORE extends Sat4jQuestion implements
		DetectRedundanciesQuestion {

	private Sat4jReasoner chReasoner;
	Collection<Error> errors;
	Collection<String> relations = null;

	public PerformanceResult answer(Reasoner r) throws FAMAException {

		Sat4jResult res = new Sat4jResult();
		chReasoner = (Sat4jReasoner) r;

		// solve the problem y fmdiag
		relations = new ArrayList<String>();
		relations.addAll(chReasoner.getClauses());

		ArrayList<String> S = new ArrayList<String>(relations);
		
		List<String> fmcore = fmcore(S,new ArrayList<String>(relations));
		for(String red: fmcore){
			System.out.println("The relation "+red+" is redundant");
		}
		return res;

	}

	public List<String> fmcore(List<String> S,List<String> AC) {
		List<String> Stemp = new LinkedList<String>();
		Stemp.addAll(S);
		
		List<String> NotS = new LinkedList<String>();
		for(String Smember:S){
			NotS.add(negate(Smember));
		}
		
		for(String ci:S){
			if(!isConsistent(less(Stemp,ci),NotS)){
				Stemp=less(Stemp,ci);
			}
		}
		return Stemp;
	}

	private String negate(String in){
		String res="";
		StringTokenizer tokenizer = new StringTokenizer(in, " ");
		while(tokenizer.hasMoreElements()){
			String nextToken = tokenizer.nextToken();
			if(!nextToken.equals("0")){
				res+="-"+nextToken+" ";
			}else{
				res+=nextToken;
			}
		}
		return res;
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
	


	private boolean isConsistent(Collection<String> aC, List<String> notS) {
		
		
		//First we create the content of the cnf
		String cnf_content = "c CNF file\n";

		// We show as comments the variables's number
		Iterator<String> it = chReasoner.variables.keySet().iterator();
		while (it.hasNext()) {
			String varName = it.next();
			cnf_content += "c var " + chReasoner.variables.get(varName) + " = " + varName
					+ "\n";
		}

		// Start the problem
		cnf_content += "p cnf " + chReasoner.variables.size() + " " + (aC.size() + notS.size())
				+ "\n";
		// Clauses
		it = aC.iterator();
		while (it.hasNext()) {
			cnf_content += (String) it.next() + "\n";
		}

		it = notS.iterator();
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

	public Collection<VariabilityElement> getRedundancies() {
		return null;
	}

}
