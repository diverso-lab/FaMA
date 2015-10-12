package solvers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import solvers.results.BDDResult;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import featureModelRepresentations.JavaBDDFeatureModel;
import util.Memory;
import util.UtilProp;
import filters.Filter;

import com.yourkit.api.Controller;
import com.yourkit.api.MemorySnapshot;

public class JavaBDDFeatureModelSolver {

	private BDDResult results=new BDDResult();    // Results
	private JavaBDDFeatureModel fm;
	private BDD bdd;							  // BDD
	private String experimentName;		 		  // Experiment Name
	
	public JavaBDDFeatureModelSolver(JavaBDDFeatureModel fm, String experimentName, Filter filter,Controller c) {
		this.fm=fm;
		this.experimentName=experimentName;
		this.createBDD(filter,c); // Create the BDD
	}
	
	public void getOneSolution() {

		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before=System.currentTimeMillis();
		BDD sol=this.bdd.satOne();
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		// Save results
		this.results.getSolutions().add(sol);
		this.results.setTimeOneSolution(time);
		this.results.setMemoryUsageOneSolution(memory);
		
		// Show results
		System.out.println("=========== BDD:One Solution ===========");
		System.out.println("SOLUTION " + sol.toString());
		System.out.println("TIME TO GET ONE SOLUTION " + time + " ms");
	}
	
	public void getNumberOfSolutions() {

		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before=System.currentTimeMillis();
		double nsols=this.bdd.satCount();
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		// Save results
		this.results.setNumberOfSolutions(nsols);
		this.results.setTimeNumberOfSolutions(time);
		this.results.setMemoryUsageNumberOfSolutions(memory);
		
		// Show results
    	System.out.println("=========== BDD:Number of Solutions ===========");
        System.out.println("NUMBER OF SOLUTIONS " + nsols);
        System.out.println("TIME TO GET THE NUMBER OF SOLUTIONS " + time + " ms");

	}
	
	public void getAllSolutions() {

		System.gc();
		long memoryBefore=Runtime.getRuntime().freeMemory();
		long before=System.currentTimeMillis();
		List sols=this.bdd.allsat();
		long time=System.currentTimeMillis() - before;
		long memory=memoryBefore - Runtime.getRuntime().freeMemory();
		
		// Save results
		/*
		Iterator it=sols.iterator();
		while(it.hasNext())
			this.results.getSolutions().add((BDD)it.next());
			
		*/
		
		// Save results
		this.results.setTimeAllSolutions(time);
		this.results.setMemoryUsageAllSolutions(memory);
		
    	// Show results
    	System.out.println("=========== BDD:All Solutions ===========");
        System.out.println("TIME TO GET ALL SOLUTIONS " + time + " ms");
 
	}
	
	private void createBDD(Filter filter, Controller c) {

		// Get Root (In this case we know it is "F0")
		this.bdd = this.fm.getVariables().get("F0");

		Iterator it = this.fm.getNodes().iterator();
		while (it.hasNext()) {
			BDD aux = (BDD) it.next();
			this.bdd = this.bdd.apply(aux, BDDFactory.and);
		}

		// Apply filter
		if (filter != null) {
			
		 // Included features			
			it = filter.getIncludedFeatures().iterator();
			while (it.hasNext()) {
				BDD one = this.fm.getFactory().one();
				BDD var=fm.getVariables().get("F" + (Integer)it.next());
				BDD var2=one.apply(var, this.fm.getFactory().biimp);
				this.bdd = this.bdd.apply(var2, BDDFactory.and);
			}	
		}
		
		// Save results
		this.results.setNumVar(this.fm.getFactory().varNum());
		this.results.setNumNodes(fm.getFactory().getNodeNum());
		this.results.setCacheSize(fm.getFactory().getCacheSize());
		this.results.setNodesTableSize(fm.getFactory().getNodeTableSize());
		this.results.setMemoryUsageJavaBDD(fm.getFactory().getGCStats().num + 1);
		
		// Print BDD Representation
		String path="";
		try {
			// Read path from properties file
			path=UtilProp.getProperty("bddPath") + experimentName + ".bdd";
			
			// Save BDD
			fm.getFactory().save(path, this.bdd);
		} catch (IOException e) {
			System.out.println("ERROR: Error while saving BDD representation in " + path + ": " + e.getMessage());
		}

		// MemoryUsage
		
		if (c!=null)
		{
			try
			{
				String fileCaptured=c.captureMemorySnapshot();
				
				// Get memory
				MemorySnapshot m=new MemorySnapshot(new File(fileCaptured),null,null);
				long mem1=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"net.sf.javabdd.BDD\"/>" +
				        "</retained-objects>");
				
				long mem2=m.getShallowSize(
					 	"<retained-objects>" +
				      		"<objects class=\"net.sf.javabdd.JFactory\"/>" +
				        "</retained-objects>");
				
				this.results.setMemoryUsage(mem1 + mem2);
				
			// Stop monitoring memory
			c.stopAllocationRecording();
			m.dispose();
				
			}catch (Exception oops)
			{
				System.out.println("ERROR: Error while analyzing memory snapshot: " + oops.getMessage());
			}
		}
	}
	
	public BDDResult getResults() {
		return this.results;
	}
}
