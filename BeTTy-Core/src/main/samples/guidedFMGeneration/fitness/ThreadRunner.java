/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package main.samples.guidedFMGeneration.fitness;


import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;


public class ThreadRunner implements Runnable {

    	private long maxTime;				// Time out
    	private double executionTime;			// Execution time
    	private FAMAFeatureModel fm = null;		// Input model
		private Thread thread;				// Thread
		private double fitness;				// Fitness
		private QuestionTrader qt=null;
	    	
    	
	public ThreadRunner(QuestionTrader qt, FAMAFeatureModel fm) {
		this.thread = new Thread(this);
		this.thread.setPriority(Thread.MAX_PRIORITY);
		this.fm = fm;
		this.maxTime = 0;
		this.fitness = -1;
		this.qt = qt;
	}

	public void execute() {
		
	    	try {
			// Start the thread
			thread.start();

			// Wait until the thread end or maxTime
			thread.join(maxTime);

			// If the thread didn't end, destroy it
			if (fitness == -1)
			    thread.stop();
	    	} catch (InterruptedException e) {
			   System.err.println("Error when executing the thread: " + e.getMessage() + e.getStackTrace());
		 } finally {
			thread = null;
		 }
	}

	
//	// JaCoP: voidFM
//	public void run() {
//
//		try {
//			// Perform question
////			qt.setCriteriaSelector("selected");
//			qt.setVariabilityModel(fm);
//			ValidQuestion q = (ValidQuestion) qt.createQuestion("Valid");
//			double timeBefore = System.currentTimeMillis();
//			PerformanceResult res = qt.ask(q);
//			this.executionTime = System.currentTimeMillis() - timeBefore;
//			this.fitness = Double.parseDouble(res.getResults().get("JaCoP:backtracks"));
//		
//		} catch (Exception e) {
//			System.err.println("Error when running the thread: " + e.toString());
//			e.printStackTrace();
//		}
//	}
	
	
	// JaCoP: voidFM
	public void run() {

		try {
			// Perform question
//			qt.setCriteriaSelector("selected");
			qt.setVariabilityModel(fm);
			NumberOfProductsQuestion q = (NumberOfProductsQuestion) qt.createQuestion("#Products");
			double timeBefore = System.currentTimeMillis();
			PerformanceResult res = qt.ask(q);
			this.executionTime = System.currentTimeMillis() - timeBefore;
//			this.fitness = Double.parseDouble(res.getResults().get("JaCoP:#Products"));
			this.fitness = q.getNumberOfProducts();

		} catch (Exception e) {
			System.err.println("Error when running the thread: " + e.toString());
			e.printStackTrace();
		}
	}
	/*
	
	// JaCoP: Dead Features
	public void run() {

		try {
			// Perform question
			qt.setCriteriaSelector("selected");
			qt.setVariabilityModel(fm);
			qt.setSelectedReasoner("JaCoP");
			DetectErrorsQuestion dfq= (DetectErrorsQuestion) qt.createQuestion("DetectErrors");
			dfq.setObservations(fm.getObservations());
			PerformanceResult res = qt.ask(dfq);
			this.fitness = Double.parseDouble(res.getResults().get("JaCoP:backtracks"));
		
		} catch (Exception e) {
			System.err.println("Error when running the thread: " + e.toString());
			e.printStackTrace();
		}
	}

	


	// Sat4j: Void FM
	public void run() {

		try {
			// Perform question
			qt.setCriteriaSelector("selected");
			qt.setVariabilityModel(fm);
			ValidQuestion q = (ValidQuestion) qt.createQuestion("Valid");
			double timeBefore = System.currentTimeMillis();
			PerformanceResult res = qt.ask(q);
			this.executionTime = System.currentTimeMillis() - timeBefore;
			if (res==null)
				this.fitness=0;
			else
				this.fitness=Double.parseDouble(res.getResults().get("Sat4j:decisions"));
		
		} catch (Exception e) {
			System.err.println("Error when running the thread: " + e.toString());
			e.printStackTrace();
		}
	}
	
*/
	
	// SPLOT: #Products
//	public void run() {
//
//		try {
//			// Write model in SXFM format
//			SPLXWriter writer = new SPLXWriter();
//			writer.setName("SPLOTTest");
//			
//			// Feature model path
//			String featureModelPath = "models/model";
//			writer.writeFile(featureModelPath, fm);
//			
//			// Create feature model object from an XML file (SXFM format - see www.splot-research.org for details)	
//			// If an identifier is not provided for a feature use the feature name as id
//			FeatureModel featureModel = new XMLFeatureModel(featureModelPath, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
//			
//			// load feature model from 			
//			featureModel.loadModel();			
//
//			// create BDD variable order heuristic
//			new FTPreOrderSortedECTraversalHeuristic("Pre-CL-MinSpan", featureModel, FTPreOrderSortedECTraversalHeuristic.FORCE_SORT);		
//			VariableOrderingHeuristic heuristic = VariableOrderingHeuristicsManager.createHeuristicsManager().getHeuristic("Pre-CL-MinSpan");
//
//			// BDD construction parameters
//			// - Tuning this parameters can be tricky at times and may require playing a bit
//			// - For the purpose of this example let's assume "large enough" values 
//			int bddNodeNum = 1000000;  	// sets the initial size of the BDD table  
//			int bddCacheSize = 100000;  // sets the size of the BDD cache table
//			
//			// Creates the BDD reasoner
//			ReasoningWithBDD reasoner = new FMReasoningWithBDD(featureModel, heuristic, bddNodeNum, bddCacheSize, 1800000, "pre-order");
//			
//			// Initialize the reasoner (BDD is created at this moment)
//			reasoner.init();
//			
//			this.fitness = reasoner.getBDD().nodeCount();
//		
//		} catch (Exception e) {
//			System.err.println("Error when running the thread: " + e.toString());
//			e.printStackTrace();
//		}
//	}
//	
///*	
//	// SPLOT: Sat4j:Dead Features
//	public void run() {
//
//		try {
//			// Write model in SXFM format
//			SPLXWriter writer = new SPLXWriter();
//			writer.setName("SPLOTTest");
//			
//			// Feature model path
//			String featureModelPath = "models/model";
//			writer.writeFile(featureModelPath, fm);
//			
//			// Create feature model object from an XML file (SXFM format - see www.splot-research.org for details)	
//			// If an identifier is not provided for a feature use the feature name as id
//			FeatureModel featureModel = new XMLFeatureModel(featureModelPath, XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
//			// load feature model from 			
//			featureModel.loadModel();			
//
//			// SAT reasoner construction parameters
//			// - "MiniSAT" - name of the SAT4J solver used
//			// - Timeout parameter
//			int SATtimeout = 1800000;  	// 1 minute is given to the SAT solver to check the consistency of the feature model  
//			
//			FMReasoningWithSAT reasoner = new FMReasoningWithSAT("MiniSAT", featureModel, SATtimeout);
//
//			// Initialize the reasoner
//			reasoner.init();
//			
//			reasoner.isConsistent();
//			
//			Map<String,String> stats = new HashMap<String,String>();
//			Map<String,Boolean[]> domainTable = reasoner.allValidDomains(stats);
//			
//			this.fitness = Double.parseDouble(stats.get("sat-checks"));
//		
//		} catch (splar.core.fm.reasoning.FMReasoningException e) {
//		    this.fitness = 0;
//		} catch (org.sat4j.specs.ContradictionException e) {
//		    this.fitness = 0;
//		} catch (Exception e) {
//			System.err.println("Error when running the thread: " + e.toString());
//			e.printStackTrace();
//		}
//	}

	public void setTimeOut(long time) {
		maxTime = time;
	}
	
	public double getFitness() {
	    return this.fitness;
	}
	
	public double getExecutionTime() {
	    return this.executionTime;
	}
}
