package solvers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import solvers.results.ChocoResult;

import featureModelRepresentations.*;

import com.sun.org.apache.regexp.internal.RESyntaxException;

import choco.*;
import choco.integer.*;
import choco.integer.var.IntDomainVar;
import choco.Solution;

import filters.Filter;

public class ChocoFeatureModelSolver {

	private Random rand;
	private String graph;
	private long seed;
	private Problem problem;

	private ChocoResult results = new ChocoResult();

	public ChocoFeatureModelSolver(ChocoFeatureModel fm, Filter filter) {
		graph = new String("digraph G {");
		rand = new Random();
		this.createCSP(fm,filter);
	}

	public void setSeed(long seed) {
		this.seed = seed;
		this.rand.setSeed(seed);

	}

	public long getSeed() {
		return this.seed;

	}

	public void getOneSolution(ChocoFeatureModel fm, Filter filter) {
		this.getOneSolutionFlush(fm, false,filter);
	}

	public void getOneSolutionFlush(ChocoFeatureModel fm, boolean flush, Filter filter) {

		ArrayList<IntVar> fdvList = new ArrayList<IntVar>();

		if (flush) {
			fdvList = this.flush(fm.getFeatures());
			System.out.println("flush seed: " + this.seed);
			this.results.setFlushSeed(this.seed);

		} else {
			fdvList = fm.getFeatures();

		}

		// Create problem
		this.createCSP(fm,filter);
		

		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before = System.currentTimeMillis();
		boolean found=problem.solve();
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		Collection solution = new ArrayList();
		
		// Save results
		if (found == Boolean.TRUE) {
			for (Iterator iter = fdvList.iterator(); iter.hasNext();) {
				IntVar element = (IntVar) iter.next();
				solution.add(element);
				graph += element.toString().replace('=', '_') + ";";
			}
			this.results.getSolutions().add(solution);

		}
		
		this.results.setTimeOneSolution(time);
		this.results.setMemoryUsageOneSolution(memory);
		
		// Show results
		System.out.println("=========== Choco:One Solution ===========");
		System.out.println("SOLUTION: " + printSolutions(solution));
		System.out.println("TIME TO GET ONE SOLUTION: " + time);

		graph += "}";

	}
	
	
	public void getNumberOfSolutions(ChocoFeatureModel fm, Filter filter) {
		
		int res=0;
		
		// Create problem
		this.createCSP(fm,filter);
		
		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before=System.currentTimeMillis();
		problem.solveAll();
		long time=System.currentTimeMillis() - before;
		res = problem.getSolver().getNbSolutions();
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		// Save results
		this.results.setNumberOfSolutions(res);
		this.results.setTimeNumberOfSolutions(time);
		this.results.setMemoryUsageNumberOfSolutions(memory);
		
		// Show results
		System.out.println("=========== Choco :Number of Solutions ===========");
		System.out.println("NUMBER OF SOLUTIONS: " + res);
		System.out.println("TIME TO GET THE NUMBER OF SOLUTIONS: " + time);
	}
	
	
	public void  getAllSolutions(ChocoFeatureModel fm, Filter filter) {
		long res = 0;

		// Create problem
		this.createCSP(fm,filter);
		
		int nf=fm.getFeatures().size();
		
		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before=System.currentTimeMillis();
		Solver solver=problem.getSolver();
		if (problem.solve()) {
			  do {
				  Solution solution=(Solution)solver.getSearchSolver().solutions.get(0);
				  ArrayList<Integer> sol=new ArrayList<Integer>();
				  for (int i=0;i<nf;i++)
					  sol.add(solution.getValue(i));
				  this.results.getSolutions().add(sol);
				  // Show Solution
				 System.out.println("CHOCO SOLUTION: " + sol.toString());
			  } while (problem.nextSolution());
			}
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		// Save results
		this.results.setTimeAllSolutions(time);
		this.results.setMemoryUsageAllSolutions(memory);
		
		// Show results
		System.out.println("=========== Choco :All Solutions ===========");
		System.out.println("TIME TO GET ALL SOLUTIONS: " + time);
		
	}

	private ArrayList flush(ArrayList list) {

		ArrayList copia = (ArrayList) list.clone();
		list.clear();

		while (!copia.isEmpty()) {
			int i = rand.nextInt(copia.size());
			list.add(copia.remove(i));
		}
		return list;
	}
	
	private void createCSP(ChocoFeatureModel fm, Filter filter) {
		
		ArrayList<IntVar> fdvList = fm.getFeatures();
		ArrayList<Constraint> constraintList = fm.getConstraints();

		// Add all the constraints related to the normal relations
		problem = fm.getProblem();

		for (Iterator iter = constraintList.iterator(); iter.hasNext();) {
			Constraint element = (Constraint) iter.next();
			problem.post(element);
		}

		// Add all the constraints related to DEPENDENCIES
		Map<String,Constraint> dependencyList = fm.getDependencies();
		
		//for (Iterator iter = dependencyList.values().iterator(); iter.hasNext();) {
		int numberOfDependenciesHandled = dependencyList.size(); 
		for (int i = 0; i < numberOfDependenciesHandled  ; i++) {
			int number = dependencyList.values().size() - (i + 1);
			Constraint c = dependencyList.get(new String(""+number)); 
			problem.post(c);
		}
		
		// Apply filter
		if (filter!=null)
		{
			// Included features
			Iterator it=filter.getIncludedFeatures().iterator();
			while (it.hasNext())
			{
				IntVar var=fdvList.get((Integer) it.next());
				Constraint c=fm.getProblem().eq(var,1);
				problem.post(c);
			}
				
			// Excludes features
			it=filter.getExcludedFeatures().iterator();
			while (it.hasNext())
			{
				IntVar var=fdvList.get((Integer) it.next());
				Constraint c=fm.getProblem().eq(var,0);
				problem.post(c);
			}
		}
		
		// Save results
		this.results.setNDependencies(numberOfDependenciesHandled);
		this.results.setNFeatures(fm.getFeatureModel().getFeaturesNumber());
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public ChocoResult getResults() {
		return results;
	}
	
	private static String printSolutions(Collection solution) {
		
		String solutions="";
		IntVar element=null;
		Iterator itElements=null;
		
		solutions += "[";
		itElements=solution.iterator();
		while (itElements.hasNext()) {
			element=(IntVar)itElements.next();
			solutions += element.toString() + "=" + element.getValue() + ", ";
		}
		solutions += "]";
		return solutions;
	}
}
