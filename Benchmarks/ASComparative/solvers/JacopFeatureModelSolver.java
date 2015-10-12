package solvers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import solvers.results.JacopResult;

import featureModelRepresentations.*;

import JaCoP.Constraint;
import JaCoP.Credit;
import JaCoP.Delete;
import JaCoP.FDV;
import JaCoP.FDstore;
import JaCoP.FirstFail;
import JaCoP.Indomain;
import JaCoP.IndomainMin;
import JaCoP.MostConstrainedDynamic;
import JaCoP.MostConstrainedStatic;
import JaCoP.Search;
import JaCoP.SearchOne;
import JaCoP.SelectFDV;
import JaCoP.Solver;
import JaCoP.XeqC;

import filters.Filter;

import com.yourkit.api.Controller;
import com.yourkit.api.MemorySnapshot;

public class JacopFeatureModelSolver {

	private Random rand;
	private String graph;
	private long seed;
	private FDstore store;

	private JacopResult results = new JacopResult();

	public JacopFeatureModelSolver(JacopFeatureModel fm, Filter filter, Controller c) {
		graph = new String("digraph G {");
		rand = new Random();
		this.createCSP(fm,filter,c);
	}

	public void setSeed(long seed) {
		this.seed = seed;
		this.rand.setSeed(seed);
	}

	public long getSeed() {
		return this.seed;
	}

	public void getOneSolution(JacopFeatureModel fm, Filter filter) {
		this.getOneSolutionFlush(fm, false,filter);
	}

	public void getOneSolutionFlush(JacopFeatureModel fm, boolean flush,Filter filter) {

		ArrayList<FDV> fdvList = new ArrayList<FDV>();
		
		if (flush) {
			fdvList = this.flush(fm.getFeatures());
			System.out.println("flush seed: " + this.seed);
			this.results.setFlushSeed(this.seed);

		} else {
			fdvList = fm.getFeatures();
		}
		
		// Create Store
		this.createCSP(fm,filter,null);
		
		Indomain indomain = new IndomainMin();
		// I put a heuristic to the solver in order to have good results
		//SelectFDV heuristic = new Delete(new MostConstrainedDynamic(), new MostConstrainedStatic());
		SelectFDV heuristic = new Delete();
		//SelectFDV heuristic = new Delete(new FirstFail());		
		ArrayList<ArrayList<Integer>> solutions = new ArrayList<ArrayList<Integer>>();
		Search labeling = new SearchOne();
		// to limitate the number of backtracks
		//Search labeling = new Credit(30,10000);
		
		
		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before = System.currentTimeMillis();
		boolean found = Solver.searchOne(store, fdvList, labeling, indomain,
				heuristic);
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		// Save results
		
		Collection solution=new ArrayList();
		if (found) {
			for (Iterator iter = fdvList.iterator(); iter.hasNext();) {
				FDV element = (FDV) iter.next();
				solution.add(element);
				graph += element.toString().replace('=', '_') + ";";
			}
			this.results.getSolutions().add(solution);

		}
		
		this.results.setTimeOneSolution(time);
		this.results.setMemoryUsageOneSolution(memory);
		this.results.setBacktracks(labeling.getBacktracks());
		this.results.setSearchdepth(labeling.getDepth());
		this.results.setDecisions(labeling.getDecisions());

		// Show results
		System.out.println("=========== Jacop:One Solution ===========");
		System.out.println("SOLUTION: " + solution);
		System.out.println("TIME TO GET ONE SOLUTION: " + time);

		graph += "}";

	}
	
	public void getNumberOfSolutions(JacopFeatureModel fm, Filter filter) {
		
		int res=0;
		ArrayList<FDV> fdvList = fm.getFeatures();

		// Create Store
		this.createCSP(fm,filter,null);
		
		
		Indomain indomain = new IndomainMin();
		SelectFDV heuristic = new Delete();
		
		ArrayList<ArrayList<Integer>> solutions = new ArrayList<ArrayList<Integer>>();
		System.out.println(fm.getStore());
	
		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before=System.currentTimeMillis();
		boolean result = Solver.searchAll(store, fdvList, indomain, heuristic,
				solutions);
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		res = solutions.size();
		
		// Save results
		this.results.setNumberOfSolutions(res);
		this.results.setTimeNumberOfSolutions(time);
		this.results.setMemoryUsageNumberOfSolutions(memory);
		
		// Show results
		System.out.println("=========== Jacop:Number of Solutions ===========");
		System.out.println("NUMBER OF SOLUTIONS: " + res);
		System.out.println("TIME TO GET THE NUMBER OF SOLUTIONS: " + time);
	}
	
	
	public void  getAllSolutions(JacopFeatureModel fm, Filter filter) {

		ArrayList<FDV> fdvList = fm.getFeatures();
		
		// Create Store
		this.createCSP(fm,filter,null);
		
		Indomain indomain = new IndomainMin();
		SelectFDV heuristic = new Delete();
		ArrayList<ArrayList<Integer>> solutions = new ArrayList<ArrayList<Integer>>();

		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before = System.currentTimeMillis();
		boolean result = Solver.searchAll(store, fdvList, indomain, heuristic,solutions);
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		Iterator it=solutions.iterator();
		while(it.hasNext())
			this.results.getSolutions().add((Collection)it.next());
		
		this.results.setTimeAllSolutions(time);
		this.results.setMemoryUsageAllSolutions(memory);
		
		// Show results
		System.out.println("=========== Jacop: All Solutions ===========");
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
	
	// Create the problem
	private void createCSP(JacopFeatureModel fm,Filter filter, Controller mc) {

		ArrayList<FDV> fdvList = fm.getFeatures();
		
		//add all the constraints related to the normal relations
		ArrayList<Constraint> constraintList = fm.getConstraints();
		store = fm.getStore();

		for (Iterator iter = constraintList.iterator(); iter.hasNext();) {
			Constraint element = (Constraint) iter.next();
			store.impose(element);
		}

		//add all the constraints related to DEPENDENCIES
		Map<String,Constraint> dependencyList = fm.getDependencies();
		
		//for (Iterator iter = dependencyList.values().iterator(); iter.hasNext();) {
		int numberOfDependenciesHandled = dependencyList.size(); 
		for (int i = 0; i < numberOfDependenciesHandled  ; i++) {
			int number = dependencyList.values().size() - (i + 1);
			Constraint c = dependencyList.get(new String(""+number)); 
			store.impose(c);
		}
		
		// Apply filter
		if (filter!=null)
		{
			// Included features
			Iterator it=filter.getIncludedFeatures().iterator();
			while (it.hasNext())
			{
				FDV var=fdvList.get((Integer) it.next());
				Constraint c=new XeqC(var,1);
				store.impose(c);
			}
				
			// Excludes features
			it=filter.getExcludedFeatures().iterator();
			while (it.hasNext())
			{
				FDV var=fdvList.get((Integer) it.next());
				Constraint c=new XeqC(var,0);
				store.impose(c);
			}
		}
		
		// Save results
		this.results.setNDependencies(numberOfDependenciesHandled);
		this.results.setNFeatures(fm.getFeatureModel().getFeaturesNumber());
		this.results.setNVariables(fm.getFeatures().size());
		this.results.setNConstraints(fm.getConstraints().size());

		
		// The first time is generated we save memory results
		if (mc!=null)
		{
			// MemoryUsage
			try
			{
				// SECOND: Get Memory
				String fileCaptured= mc.captureMemorySnapshot();
				MemorySnapshot m=new MemorySnapshot(new File(fileCaptured),null,null);
				
				long mem1=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"JaCoP.FDstore\"/>" +
				        "</retained-objects>");
				
				long mem2=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"JaCoP.FDV\"/>" +
				        "</retained-objects>");
				
				long mem3=m.getShallowSize(
						"<retained-objects>" +
							"<objects class=\"JaCoP.Constraint\"/>" +
						"</retained-objects>");
				
				this.results.setMemoryUsage(mem1+mem2+mem3);
				
				// Stop monitoring memory
				mc.stopAllocationRecording();
				m.dispose();
				
			}catch (Exception oops)
			{
				System.out.println("ERROR: Error while analyzing memory snapshot: " + oops.getMessage());
			}
			
		}
		
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public JacopResult getResults() {
		return results;
	}

}
