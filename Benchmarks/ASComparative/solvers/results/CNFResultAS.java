package solvers.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.javabdd.BDD;

public class CNFResultAS implements IResult {

	private ArrayList<int[]> solutions = new ArrayList<int[]>();
	private double numberOfSolutions= -1;
	private long timeAllSolutions= -1;
	private long timeOneSolution = -1;
	private long timeNumberOfSolutions=-1;
	private long memoryUsage=-1;
	private long memoryUsageOneSolution=-1;
	private long memoryUsageNumberOfSolutions=-1;
	private long memoryUsageAllSolutions=-1;
	private int numVar=-1;
	private int numClauses=-1;
	private Map<String,List> as;
	
	public long getTimeOneSolution() {
		return this.timeOneSolution;
	}

	public void setTimeOneSolution(long timeOneSolution) {
		this.timeOneSolution=timeOneSolution;
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
		return this.timeNumberOfSolutions;
	}

	public void setTimeNumberOfSolutions(long timeNumberOfSolutions) {
		this.timeNumberOfSolutions=timeNumberOfSolutions;
	}
	
	public long getMemoryUsage() {
		return this.memoryUsage;
	}
	
	public void setMemoryUsage(long mem) {
		this.memoryUsage=mem;
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

	
	public ArrayList<int[]> getSolutions() {
		return this.solutions;
	}
	
	public void setSolutions(ArrayList<int[]> solutions) {
		this.solutions=solutions;
	}
	
	public int getNumVar() {
		return this.numVar;
	}
	
	public void setNumVar(int numVar) {
		this.numVar=numVar;
	}
	
	public int getNumClauses() {
		return this.numClauses;
	}
	
	public void setNumClauses(int numClauses) {
		this.numClauses=numClauses;
	}
	
	public void setAtomicSets(Map<String,List> nas) {
		this.as = nas;
	}

	// Return results as String
	public String printResults() {
		
		String res;
		res="**************** SAT *******************\n";
		res+="NUMBER OF VARIABLES " + this.numVar + "\n";
		res+="NUMBER OF CLAUSES " + this.numClauses + "\n";
		res+="ATOMIC SETS: " + this.as.toString() + "\n";
		res+="NUMBER OF ATOMIC SETS: " + this.as.size() + "\n";
		res+="MEMORY USAGE (PROFILER): " + this.memoryUsage/1024 + " KB (" + (this.memoryUsage/1024)/1024 + " MB) \n";
		
		// One solution
		if (this.timeOneSolution!=-1)
		{
			if (this.solutions.isEmpty())
				res += "Unsatisfiable!";
			else {
				int[] sol=(int[])this.solutions.get(0);
				res+="ONE SOLUTION: " + printSolution(sol) + "\n";
				res+="NUMBER OF FEATURES IN SOLUTION: " + getNumberOfFeatureInSolution(sol) + "\n";
			}
			
			res+="TIME TO GET ONE SOLUTION: " + this.timeOneSolution + "ms\n";
			res+="MEMORY USAGE TO GET ONE SOLUTION (Aprox): " + this.memoryUsageOneSolution/1024 + " KB\n";
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
		
		// Satisfiable ?
/*		if (this.solutions.isEmpty())
			results.add("Unsatisfiable");
		else
			results.add("Satisfiable");*/
		
		// Number of variables
		results.add(Integer.toString(this.numVar));
		
		// Number of clauses
		results.add(Integer.toString(this.numClauses));
		
		// Memory usage
		results.add(Long.toString(this.memoryUsage/1024));
		
		// Number of atomic sets
		results.add(Integer.toString(this.as.size()));
		
		if (this.timeOneSolution!=-1)
			results.add(Long.toString(this.timeOneSolution));

		
		if (this.numberOfSolutions!=-1) {
			results.add(Double.toString(this.getNumberOfSolutions()));
			results.add(Long.toString(this.timeNumberOfSolutions));
		}
		
		if (this.timeAllSolutions!=-1)
			results.add(Long.toString(this.timeAllSolutions));

		return results;
	}
	
	// Print a solution in an atractive format
	private String printSolution(int[] solution)
	{
		String sol="[";
		for (int i=0;i<solution.length;i++)
		{
			sol+="F" + i + "=";
			if (solution[i]>0)
				sol+="1";
			else
				sol+="0";
			sol+=", ";
		}
		sol+="]";
		
		return sol;
	}
	
	// Count the number of variables with value=1;
	private int getNumberOfFeatureInSolution(int[] solution)
	{
		int n=0;
		for (int i=0;i<solution.length;i++)
		{
			if (solution[i]>0)
				n++;
		}
		return n;
	}
}
