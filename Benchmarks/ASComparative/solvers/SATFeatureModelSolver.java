package solvers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.sat4j.reader.*;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.*;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.tools.*;
import filters.Filter;
import solvers.results.CNFResult;
import util.UtilProp;
import featureModelRepresentations.CNFFeatureModel;

import com.yourkit.api.Controller;
import com.yourkit.api.MemorySnapshot;

public class SATFeatureModelSolver {

	private CNFResult results = new CNFResult(); // Results
	private CNFFeatureModel fm;
	private String cnfFilePath;			   // .cnf file path	
	private String experimentName;		   // Experiment Name

	public SATFeatureModelSolver(CNFFeatureModel fm, String expName, Filter filter, Controller c) {
		this.fm = fm;
		this.experimentName = expName;
		
		// Read CNF file Path from properties file
		this.cnfFilePath=UtilProp.getProperty("satPath") + expName + ".cnf";
		
		// Generate CNF file
		this.createCNFFile(filter,c);
	}

	public void getOneSolution() {
			
		SolverFactory factory = new SolverFactory();
		ISolver solver = factory.defaultSolver();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);

		try {
			IProblem problem = reader.parseInstance(this.cnfFilePath);
			System.gc();
			long memoryBefore=Runtime.getRuntime().freeMemory();
			long before = System.currentTimeMillis();
			if (problem.isSatisfiable()) {
				int[] solution=problem.model();

				// Save results
				this.results.getSolutions().add(solution);

				// Show results
				System.out.println("=========== SAT:One Solution ===========");
				System.out.println("SOLUTION: " + printSolution(solution));			
			} else {
				System.out.println("SAT One Solution: No solutions found: Unsatisfiable !");
			}
			
			long time = System.currentTimeMillis() - before;
			long memory=memoryBefore - Runtime.getRuntime().freeMemory();
			
			// Save results
			this.results.setTimeOneSolution(time);
			this.results.setMemoryUsageOneSolution(memory);
			
			System.out.println("TIME TO GET ONE SOLUTION " + time + " ms");
			
		} catch (FileNotFoundException e) {
			System.out.println("SAT One Solution: The file " + this.cnfFilePath + " wasn´t found.");
		} catch (ParseFormatException e) {
			System.out.println("SAT One Solution: Parse error in " + this.cnfFilePath + ": " + e.getMessage() + ". Check the sintax, please");
		} catch (IOException e) {
			System.out.println("SAT One Solution: IOException: " + e.getMessage());
		} catch (ContradictionException e) {
			System.out.println("SAT One Solution: Unsatisfiable (trivial)!");
		} catch (TimeoutException e) {
			System.out.println("SAT One Solution: Timeout, sorry!");
		}
	}
	
	public void getNumberOfSolutions() {

		SolverFactory factory = new SolverFactory();
		ISolver solver = factory.defaultSolver();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		
		try {

        	// Number of solutions
            IProblem problem = reader.parseInstance(this.cnfFilePath);
        	SolutionCounter solutionCounter = new SolutionCounter(solver);
        	System.gc();
        	long memoryBefore=Runtime.getRuntime().freeMemory();
        	long before=System.currentTimeMillis();
        	long nsol=solutionCounter.countSolutions();
        	long time=System.currentTimeMillis() - before;
        	long memory=memoryBefore - Runtime.getRuntime().freeMemory();
        	
        	// Save results
        	this.results.setNumberOfSolutions(nsol);
        	this.results.setTimeNumberOfSolutions(time);
        	this.results.setMemoryUsageNumberOfSolutions(memory);
        	
        	// Show results
        	System.out.println("=========== SAT:Number of Solutions ===========");
            System.out.println("NUMBER OF SOLUTIONS " + nsol);
            System.out.println("TIME TO GET THE NUMBER OF SOLUTIONS " + time + " ms");
            
            
		} catch (FileNotFoundException e) {
			System.out.println("SAT Number of Solutions: The file " + this.cnfFilePath + " wasn´t found.");
		} catch (ParseFormatException e) {
			System.out.println("SAT Number of Solutions: Parse error in " + this.cnfFilePath + ": " + e.getMessage() + ".Check the sintax, please");
		} catch (IOException e) {
			System.out.println("SAT Number of Solutions: IOException: " + e.getMessage());
		} catch (ContradictionException e) {
			System.out.println("SAT Number of Solutions: Unsatisfiable (trivial)!");
		} catch (TimeoutException e) {
			System.out.println("SAT Number of Solutions: Timeout, sorry!");
		}

	}
	
	public void getAllSolutions() {
		
		SolverFactory factory = new SolverFactory();
		ISolver solver = factory.defaultSolver();
		solver.setTimeout(3600); // 1 hour timeout
		Reader reader = new DimacsReader(solver);
		
		try {

            // All solutions
            IProblem problem = reader.parseInstance(this.cnfFilePath);
            System.gc();
            long memoryBefore=Runtime.getRuntime().freeMemory();
            long before=System.currentTimeMillis();
            while (problem.isSatisfiable()) {
            	int[] solution=problem.model();
            	// Save solution
            	this.results.getSolutions().add(solution);
            	// Show solution
            	System.out.println("SAT SOLUTION: " + reader.decode(solution));
            }
            long time=System.currentTimeMillis() - before;
            long memory=memoryBefore - Runtime.getRuntime().freeMemory();
        	
        	// Save results
            this.results.setTimeAllSolutions(time);
            this.results.setMemoryUsageAllSolutions(memory);
        	
        	// Show results
        	System.out.println("=========== SAT:All Solutions ===========");
            System.out.println("TIME TO GET ALL SOLUTIONS " + time + " ms");
            
            
		} catch (FileNotFoundException e) {
			System.out.println("SAT All Solutions: The file " + this.cnfFilePath + " wasn´t found.");
		} catch (ParseFormatException e) {
			System.out.println("SAT All Solutions: Parse error in " + this.cnfFilePath + ": " + e.getMessage() + ".Check the sintax, please");
		} catch (IOException e) {
			System.out.println("SAT All Solutions: IOException: " + e.getMessage());
		} catch (ContradictionException e) {
			System.out.println("SAT All Solutions: Unsatisfiable (trivial)!");
		} catch (TimeoutException e) {
			System.out.println("SAT All Solutions: Timeout, sorry!");
		}
	}
	
	private void createCNFFile(Filter filter, Controller c) {

		String cnf_content = "c " + this.experimentName + " cnf file\n";

		// We show as comments the variables's number
		Iterator it = this.fm.getVariables().keySet().iterator();
		while (it.hasNext()) {
			String varName = (String) it.next();
			cnf_content += "c var " + this.fm.getVariable(varName) + " = "
					+ varName + "\n";
		}

		// Clauses size
		int clauses_size=this.fm.getClauses().size();
		if (filter!=null)
			clauses_size += filter.getExcludedFeatures().size() + filter.getIncludedFeatures().size();
		
		// Start the problem
		cnf_content += "p cnf " + this.fm.getVariables().size() + " " + clauses_size + "\n";

		// Clauses
		it = this.fm.getClauses().iterator();
		while (it.hasNext()) {
			cnf_content += (String) it.next() + "\n";
		}
		
		// Apply filter
		if (filter!=null)
		{
			// Included features
			it=filter.getIncludedFeatures().iterator();
			while (it.hasNext())
				cnf_content += Integer.toString((Integer) it.next() + 1) + " 0 \n";
			
			// Excludes features
			it=filter.getExcludedFeatures().iterator();
			while (it.hasNext())
				cnf_content += "-" + Integer.toString((Integer) it.next() + 1) + " 0 \n";
		}
		
		// End file
		cnf_content +="0";

		// Create the .cnf file
		File outputFile = null;
		outputFile = new File(this.cnfFilePath);
		FileWriter out;
		try {
			out = new FileWriter(outputFile);
			out.write(cnf_content);
			out.close();

		} catch (IOException e) {
			System.out.println("SAT: Error creating .cnf file in " + this.cnfFilePath);
		}
		
		// Save results
		this.results.setNumVar(this.fm.getVariables().size());
		this.results.setNumClauses(this.fm.getClauses().size());

		// MemoryUsage
		if (c!=null)
		{
			try
			{
				
				// FIRST: Load CNF file in memory
				SolverFactory factory = new SolverFactory();
				ISolver solver = factory.defaultSolver();
				solver.setTimeout(3600); // 1 hour timeout
				Reader reader = new DimacsReader(solver);
				IProblem problem = reader.parseInstance(this.cnfFilePath);
				
				// SECOND: Get Memory
				String fileCaptured=c.captureMemorySnapshot();
				MemorySnapshot m=new MemorySnapshot(new File(fileCaptured),null,null);
				
				long mem1=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"org.sat4j.reader.Reader\"/>" +
				        "</retained-objects>");
				
				long mem2=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"org.sat4j.minisat.SolverFactory\"/>" +
				        "</retained-objects>");
				
				long mem3=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"org.sat4j.specs.IProblem\"/>" +
				        "</retained-objects>");
				
				long mem4=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"org.sat4j.specs.ISolver\"/>" +
				        "</retained-objects>");
				
				this.results.setMemoryUsage(mem1 + mem2 + mem3 + mem4);
				
				// Stop monitoring memory
				c.stopAllocationRecording();
				m.dispose();
				
			}catch (Exception oops)
			{
				System.out.println("ERROR: Error while analyzing memory snapshot: " + oops.getMessage());
			}
		}
	}
	
	public CNFResult getResults() {
		return this.results;
	}
	
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
}
