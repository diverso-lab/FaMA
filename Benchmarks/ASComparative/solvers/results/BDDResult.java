package solvers.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import experiments.Experiment;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDD.BDDIterator;


public class BDDResult implements IResult {

	private ArrayList<BDD> solutions = new ArrayList<BDD>();
	private double numberOfSolutions= -1;
	private long timeAllSolutions= -1;
	private long timeOneSolution = -1;
	private long timeNumberOfSolutions=-1;
	private long memoryUsage=-1;		    // Memory usage (Java profiler)
	private int memoryUsageJavaBDD=-1;		// Memory usage by counting GC times (John Whaley)
	private long memoryUsageOneSolution=-1;
	private long memoryUsageNumberOfSolutions=-1;
	private long memoryUsageAllSolutions=-1;
	private int numVar=-1;
	private int numNodes=-1;
	private int cache_size=-1;
	private int nodes_table_size=-1;


	public BDDResult() {
		
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
	
	public int getMemoryUsageJavaBDD() {
		return this.memoryUsageJavaBDD;
	}
	
	public void setMemoryUsageJavaBDD(int mem) {
		this.memoryUsageJavaBDD=mem;
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

	public ArrayList<BDD> getSolutions() {
			return this.solutions;
	}

	public void setSolutions(ArrayList<BDD> solutions) {
		this.solutions = solutions;
	}
	
	public int getNumVar() {
		return this.numVar;
	}
	
	public void setNumVar(int numVar) {
		this.numVar=numVar;
	}
	
	public int getNumNodes() {
		return this.numNodes;
	}
	
	public void setNumNodes(int numNodes) {
		this.numNodes=numNodes;
	}
	
	public int getCacheSize() {
		return this.cache_size;
	}
	
	public void setCacheSize(int cs) {
		this.cache_size=cs;
	}
	
	public int getNodesTableSize() {
		return this.nodes_table_size;
	}
	
	public void setNodesTableSize(int nts) {
		this.nodes_table_size=nts;
	}
	
	// Print results
	public String printResults() {
		
		String res;
		res="**************** JavaBDD *******************\n";
		res+="NUMBER OF VARIABLES: " + this.numVar + "\n";
		res+="NUMBER OF NODES: " + this.numNodes + "\n";
		res+="CACHE SIZE: " + this.cache_size + "\n";
		res+="TABLE NODES SIZE: " + this.nodes_table_size + "\n";
		res+="MEMORY USAGE (PROFILER): " + this.memoryUsage/1024 + " KB (" + (this.memoryUsage/1024)/1024 + " MB) \n";
		res+="MEMORY USAGE (JavaBDD): " + this.memoryUsageJavaBDD * 100 + " KB\n";
		
		// One solution
		if (this.timeOneSolution!=-1)
		{
			if (this.solutions.isEmpty())
				res += "Unsatisfiable!";
			else
			{
				BDD sol=(BDD)this.solutions.get(0);
				res+="ONE SOLUTION: " + sol.toString() + "\n" ;
			}
			
			res+="TIME TO GET ONE SOLUTION " + this.timeOneSolution + "ms\n";
			res+="MEMORY USAGE TO GET ONE SOLUTION (Aprox): " + this.memoryUsageOneSolution/1024 + " KB\n";
		}
		
		// Number of solutions
		if (this.numberOfSolutions!=-1)
		{
			res+="NUMBER OF SOLUTIONS: " + this.numberOfSolutions + "\n" ;
			res+="TIME TO GET THE NUMBER OF SOLUTIONS: " + this.timeNumberOfSolutions + "ms\n";
			res+="MEMORY USAGE TO GET THE NUMBER OF SOLUTIONS (Aprox): " + this.memoryUsageNumberOfSolutions/1024 + " KB\n";
		}
		
		// All solutions
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
		
		// Number of nodes
		results.add(Integer.toString(this.numNodes));
		
		// Nodes table size
		results.add(Integer.toString(this.nodes_table_size));
		
		// Memory usage
		results.add(Long.toString(this.memoryUsage/1024));
		
		if (this.timeOneSolution!=-1)
			results.add(Long.toString(this.timeOneSolution));
		
		
		if (this.numberOfSolutions!=-1)
			results.add(Double.toString(this.getNumberOfSolutions()));
			results.add(Long.toString(this.timeNumberOfSolutions));
		
		if (this.timeAllSolutions!=-1)
			results.add(Long.toString(this.timeAllSolutions));
		
		return results;
	}
}
