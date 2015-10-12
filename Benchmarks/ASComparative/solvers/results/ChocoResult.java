package solvers.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import choco.integer.IntVar;

import JaCoP.FDV;

import net.sf.javabdd.BDD;

import experiments.Experiment;


public class ChocoResult implements IResult {

	private Collection solutions = new ArrayList();
	private double numberOfSolutions= -1;
	private long timeAllSolutions= -1;
	private long timeOneSolution = -1;
	private long timeNumberOfSolutions=-1;
	private long memoryUsageOneSolution=-1;
	private long memoryUsageNumberOfSolutions=-1;
	private long memoryUsageAllSolutions=-1;
	private int backtracks = -1;
	private int searchdepth = -1;
	private int decisions = -1;
	private int nDependencies = -1;
	private int nFeatures = -1;
	private Experiment experiment = new Experiment();
	private long flushSeed = 0;

	
	public ChocoResult() {
		
	}
	
	public int getBacktracks() {
			return backtracks;
	}

	public void setBacktracks(int backtracks) {
		this.backtracks = backtracks;
	}

	public int getSearchdepth() {
			return this.searchdepth;
	}

	public void setSearchdepth(int searchdepth) {
		this.searchdepth = searchdepth;
	}

	public long getTimeOneSolution() {
			return this.timeOneSolution;
	}

	public void setTimeOneSolution(long timeOneSolution) {
		this.timeOneSolution = timeOneSolution;
	}
	
	public double getNumberOfSolutions() {
		return this.numberOfSolutions;
	}
	
	public void setNumberOfSolutions(double numberOfSolutions) {
		this.numberOfSolutions=numberOfSolutions;
	}
	
	public long getTimeAllSolutions() {
		return this.timeAllSolutions;
	}
	
	public void setTimeAllSolutions(long timeAllSolutions) {
		this.timeAllSolutions=timeAllSolutions;
	}
	
	public long getTimeNumberOfSolutions() {
		return this.timeAllSolutions;
	}
	
	public void setTimeNumberOfSolutions(long timeNumberOfSolutions) {
		this.timeNumberOfSolutions=timeNumberOfSolutions;
	}
	
	public long getMemoryUsageOneSolution() {
		return this.memoryUsageOneSolution;
	}
	
	public void setMemoryUsageOneSolution(long mem) {
		this.memoryUsageOneSolution=mem;
	}
	
	public long getMemoryUsageNumberOfSolutions() {
		return this.memoryUsageNumberOfSolutions;
	}
	
	public void setMemoryUsageNumberOfSolutions(long mem) {
		this.memoryUsageNumberOfSolutions=mem;
	}
	
	public long getMemoryUsageAllSolutions() {
		return this.memoryUsageAllSolutions;
	}
	
	public void setMemoryUsageAllSolutions(long mem) {
		this.memoryUsageAllSolutions=mem;
	}

	public Collection getSolutions() {
			return this.solutions;

	}

	public void setSolutions(Collection solutions) {
		this.solutions = solutions;
	}

	public int getDecisions() {
			return this.decisions;
	}

	public void setDecisions(int decisions) {
		this.decisions = decisions;
	}

	public int getNDependencies() {
		return nDependencies;
	}

	public void setNDependencies(int dependencies) {
		nDependencies = dependencies;
	}

	public int getNFeatures() {
		return nFeatures;
	}

	public void setNFeatures(int features) {
		nFeatures = features;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public long getFlushSeed() {
		return flushSeed;
	}

	public void setFlushSeed(long flushSeed) {
		this.flushSeed = flushSeed;
	}
	
	
	// Return the result as a String
	public String printResults() {
		
		String res;
		res="**************** Choco *******************\n";
		res+="NUMBER OF FEATURES: " + this.nFeatures + "\n";
		res+="NUMBER OF DEPENDENCIES: " + this.nDependencies + "\n";
		
		// One solution
		if (this.timeOneSolution!=-1)
		{
			Iterator it = this.solutions.iterator();
			if (!it.hasNext()) // Unsatisfiable
				res += "Unsatisfiable!";
			else {
				Collection solution=(Collection)it.next();  // We get the first solution
				res+="ONE SOLUTION: " + printSolutions(solution) + "\n";
				res+="NUMBER OF FEATURES IN SOLUTION: " + getNumberOfFeatureInSolution(solution) + "\n";
				if (this.decisions!=-1)
					res+="NUMBER OF DECISIONS: " + this.decisions + "\n";
				if (this.backtracks!=-1)
					res+="NUMBER OF BACKTRACKS: " + this.backtracks + "\n";
				if (this.searchdepth!=-1)
					res+="SEARCH DEPTH: " + this.searchdepth + "\n";
				res+="TIME TO GET ONE SOLUTION: " + this.timeOneSolution + "ms\n";
				res+="MEMORY USAGE TO GET ONE SOLUTION (Aprox): " + this.memoryUsageOneSolution/1024 + " KB\n";
			}
		}
		
		// Number of solutions
		if (this.numberOfSolutions!=-1)
		{
			res+="NUMBER OF SOLUTIONS: " + this.numberOfSolutions + "\n" ;
			res+="TIME TO GET THE NUMBER OF SOLUTIONS: " + this.timeNumberOfSolutions + "ms\n";
			res+="MEMORY USAGE TO GET THE NUMBER OF SOLUTIONS (Aprox): " + this.memoryUsageNumberOfSolutions/1024 + " KB\n";
		}
		
		//All solutions
		if (this.timeAllSolutions!=-1)
		{
			res+="TIME TO GET ALL SOLUTIONS " + this.timeAllSolutions + "ms \n";
			res+="MEMORY USAGE TO GET ALL SOLUTIONS (Aprox): " + this.memoryUsageAllSolutions/1024 + " KB\n";
		}
		
		res+="\n";
		return res;
	}
	
	// Return an ArrayList of strings
	public ArrayList<String> printCSVResults() {
		
		ArrayList<String> results=new ArrayList<String>();
		
		if (this.timeOneSolution!=-1) {
			Iterator it = this.solutions.iterator();
			if (!it.hasNext()) // Unsatisfiable
				results.add("Unsatisfiable!");
			else {
			Collection solution=(Collection)it.next();  // We get the first solution
			results.add(Long.toString(this.timeOneSolution));
			results.add(Long.toString(this.memoryUsageOneSolution/1024));
			results.add(Integer.toString(getNumberOfFeatureInSolution(solution)));
			}
		}
		
		if (this.numberOfSolutions!=-1) {
			results.add(Double.toString(this.getNumberOfSolutions()));
			results.add(Long.toString(this.timeNumberOfSolutions));
			results.add(Long.toString(this.memoryUsageNumberOfSolutions/1024));
		}
		
		if (this.timeAllSolutions!=-1) {
			results.add(Long.toString(this.timeAllSolutions));
			results.add(Long.toString(this.memoryUsageAllSolutions/1024));
		}
		
		return results;
	}
	
	
	private static int getNumberOfFeatureInSolution(Collection solution) {

		int res = 0;

		for (Iterator iterator = solution.iterator(); iterator.hasNext();) {
			IntVar element = (IntVar) iterator.next();
			if(element.getValue() > 0) {
				res = res + 1;
			}
		}

		return res;
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
