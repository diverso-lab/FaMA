import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import solvers.ChocoFeatureModelSolver;
import solvers.JacopFeatureModelSolver;
import solvers.JacopFeatureModelSolverAS;
import solvers.JavaBDDFeatureModelSolver;
import solvers.JavaBDDFeatureModelSolverAS;
import solvers.SATFeatureModelSolver;
import solvers.SATFeatureModelSolverAS;
import solvers.results.BDDResult;
import solvers.results.BDDResultAS;
import solvers.results.CNFResult;
import solvers.results.JacopResult;
import solvers.results.JacopResultAS;
import experiments.Experiment;
import experiments.ExperimentSaver;
import featureModel.Feature;
import featureModel.FeatureModel;
import featureModelRepresentations.ASFeatureModel;
import featureModelRepresentations.CNFFeatureModel;
import featureModelRepresentations.CNFFeatureModelAS;
import featureModelRepresentations.ChocoFeatureModel;
import featureModelRepresentations.JacopFeatureModel;
import featureModelRepresentations.JacopFeatureModelAS;
import featureModelRepresentations.JavaBDDFeatureModel;
import featureModelRepresentations.JavaBDDFeatureModelAS;
import generators.CNFFeatureModelGenerator;
import generators.CNFFeatureModelGeneratorAS;
import generators.ChocoFeatureModelGenerator;
import generators.GraphFeatureModelGenerator;
import generators.JacopFeatureModelGenerator;
import generators.JacopFeatureModelGeneratorAS;
import generators.JavaBDDFeatureModelGenerator;
import generators.JavaBDDFeatureModelGeneratorAS;
import util.*;
import filters.Filter;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import com.yourkit.api.Controller;
import com.yourkit.api.ProfilingModes;

public class FMFacade {

	private boolean generate_bdd=false;
	private boolean generate_bdd_as=false;
	private boolean generate_sat=false;
	private boolean generate_sat_as=false;
	private boolean generate_jacop=false;
	private boolean generate_choco=false;
	private boolean generate_graph=false;
	private boolean generate_jacop_as=false;
	private boolean generate_as=false;
	
	private boolean monitor_memory = false;
	
	// Representations
	private JacopFeatureModel fm_jacop;
	private JacopFeatureModelAS fm_jacop_as;
	private ChocoFeatureModel fm_choco;
	private CNFFeatureModel fm_cnf;
	private CNFFeatureModelAS fm_cnf_as;
    private JavaBDDFeatureModel fm_bdd;
    private JavaBDDFeatureModelAS fm_bdd_as;
    private ASFeatureModel fm_as;
    private FeatureModel fm;
    
    // Solvers
    private JacopFeatureModelSolver fms_jacop;
    private JacopFeatureModelSolverAS fms_jacop_as;
    private ChocoFeatureModelSolver fms_choco;
    private SATFeatureModelSolver fms_sat;
    private SATFeatureModelSolverAS fms_sat_as;
    private JavaBDDFeatureModelSolver fms_bdd;
    private JavaBDDFeatureModelSolverAS fms_bdd_as;
    
    //Results
   private String jacop_r="";
   private String jacop_ras="";
   private String bdd_r="";
   private String bdd_ras="";
   private String cnf_r="";
   private String cnf_ras="";
   private String fm_as_r="";
   
   // CSV Results
   private ArrayList jacop_csv_r=null;
   private ArrayList jacop_csv_ras=null;
   private ArrayList bdd_csv_r=null;
   private ArrayList bdd_csv_ras=null;
   private ArrayList cnf_csv_r=null;
   private ArrayList cnf_csv_ras=null;
   private ArrayList fm_as_csv=null;
    
    private Experiment experiment;
    
    
    // Filter
    private Filter filter=null;
    private boolean create_random_filter=false;	// Flag activated when creating a random filter during the generation is required
    private int nincluded=-1;   // Number of features required in the random filer
    private double pincluded=0; // Percentage of features included in the random filter.
    
	public FMFacade() {
		
	}
	
	public void reset() {
		generate_bdd=false;
		generate_bdd_as=false;
		generate_sat=false;
		generate_sat_as=false;
		generate_jacop=false;
		generate_choco=false;
		generate_graph=false;
		generate_jacop_as=false;
		generate_as=false;
		
		monitor_memory = false;
		
		// Representations
		fm_jacop = null;
		fm_jacop_as = null;
		fm_choco = null;
		fm_cnf = null;
		fm_cnf_as = null;
	    fm_bdd = null;
	    fm_bdd_as = null;
	    fm_as = null;
	    fm = null;
	    
	    // Solvers
	    fms_jacop=null;
	    fms_jacop_as=null;
	    fms_choco=null;
	    fms_sat=null;
	    fms_sat_as=null;
	    fms_bdd=null;
	    fms_bdd_as=null;
	    
	    // Filter
	    filter=null;
	    create_random_filter=false;	// Flag activated when creating a random filter during the generation is required
	    nincluded=-1;   // Number of features required in the random filer
	    pincluded=0; // Percentage of features included in the random filter.
	}
	
	public void monitorMemory(boolean m) {
		this.monitor_memory = m;
	}
	
	public boolean getGenerateBDD() {
		return this.generate_bdd;
	}
	
	public void generateBDD(boolean generate_bdd) {
		this.generate_bdd=generate_bdd;
	}
	
	public boolean getGenerateBDDAS() {
		return this.generate_bdd_as;
	}
	
	public void generateBDDAS(boolean generate_bdd_as) {
		this.generate_bdd_as=generate_bdd_as;
	}
	
	public boolean getGenerateSAT() {
		return this.generate_sat;
	}
	
	public void generateSAT(boolean generate_sat) {
		this.generate_sat=generate_sat;
	}
	
	public boolean getGenerateSATAS() {
		return this.generate_sat_as;
	}
	
	public void generateSATAS(boolean generate_sat_as) {
		this.generate_sat_as=generate_sat_as;
	}
	
	public boolean getGenerateJacop() {
		return this.generate_jacop;
	}
	
	public void generateJacop(boolean generate_jacop) {
		this.generate_jacop=generate_jacop;
	}
	
	public boolean getGenerateJacopAS() {
		return this.generate_jacop_as;
	}
	
	public void generateJacopAS(boolean generate_jacop_as) {
		this.generate_jacop_as = generate_jacop_as;
	}
	
	public boolean getGenerateChoco() {
		return this.generate_choco;
	}
	
	public void generateChoco(boolean generate_choco) {
		this.generate_choco=generate_choco;
	}
	
	public boolean getGenerateGraph() {
		return this.generate_graph;
	}
	
	public void generateGraph(boolean generate_graph) {
		this.generate_graph=generate_graph;
	}
	
	public boolean getGenerateAS() {
		return this.generate_as;
	}
	
	public void generateAS(boolean as) {
		this.generate_as=as;
	}
	
	public void generate(Experiment exp) {
		
		this.experiment=exp;
		
		try
		{
			
			// Monitor memory
			Controller mc = null;
			if (monitor_memory == true)
			{
				mc=new Controller();
				mc.forceGC();
			}
			
			if (this.generate_graph)
			{
				System.out.print("Generating graph...");
				GraphFeatureModelGenerator fmg_graph=new GraphFeatureModelGenerator();
				fmg_graph.setSeed(exp.getGeneratorSeed());
				fmg_graph.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				String graph=fmg_graph.getGraph();
				String graphPath=UtilProp.getProperty("graphsPath") + exp.getName();
				Dot.showGraph(graphPath, graph);
				System.out.println("OK");
			}
			
			if (this.generate_choco)
			{
				System.out.print("Generating choco...");
				ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
				fmg_choco.setSeed(exp.getGeneratorSeed());
				fmg_choco.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_choco=fmg_choco.getFm();
				this.fm=fm_choco.getFeatureModel();
				
				if (this.create_random_filter)	// If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_choco=new ChocoFeatureModelSolver(this.fm_choco,filter);
				this.fms_choco.setSeed(10);
				System.out.println("OK");
			}
			
			if (this.generate_jacop)
			{
				// Start monitoring memory
				if (mc!=null)
					mc.startAllocationRecording(true, 10, true, 100*1024);
				
				System.out.print("Generating jacop...");
				JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
				fmg_jacop.setSeed(exp.getGeneratorSeed());
				fmg_jacop.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_jacop=fmg_jacop.getFm();
				this.fm=fm_jacop.getFeatureModel();
				
				if (this.create_random_filter) // If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_jacop=new JacopFeatureModelSolver(this.fm_jacop,filter,mc);
				this.fms_jacop.setSeed(10);
				System.out.println("OK");
				
			}
			
			if (this.generate_jacop_as)
			{
				// Start monitoring memory
				if (mc!=null)
					mc.startAllocationRecording(true, 10, true, 100*1024);
				
				System.out.print("Generating JaCoP AS...");
				JacopFeatureModelGeneratorAS fmg_jacop_as=new JacopFeatureModelGeneratorAS();
				fmg_jacop_as.setSeed(exp.getGeneratorSeed());
				fmg_jacop_as.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_jacop_as=fmg_jacop_as.getFm();
				this.fm=fm_jacop_as.getFeatureModel();
				
				if (this.create_random_filter) // If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_jacop_as=new JacopFeatureModelSolverAS(this.fm_jacop_as,filter,mc);
				this.fms_jacop_as.setSeed(10);
				System.out.println("OK");
				
			}
			
			if (this.generate_bdd)
			{
				// Start monitoring memory
				if (mc!=null)
					mc.startAllocationRecording(true, 10, true, 1000*1024);
				System.out.print("Generating javaBDD...");
				JavaBDDFeatureModelGenerator fmg_bdd=new JavaBDDFeatureModelGenerator();
				fmg_bdd.setSeed(exp.getGeneratorSeed());
				fmg_bdd.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_bdd=fmg_bdd.getFm();
				this.fm=fm_bdd.getFeatureModel();
				
				if (this.create_random_filter)	// If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_bdd=new JavaBDDFeatureModelSolver(this.fm_bdd,exp.getName(),filter,mc);
				System.out.println("OK");
				
			}
			
			
			if (this.generate_bdd_as)
			{
				// Start monitoring memory
				if (mc!=null)
					mc.startAllocationRecording(true, 10, true, 100*1024);
				
				System.out.print("Generating javaBDD...");
				JavaBDDFeatureModelGeneratorAS fmg_bdd_as=new JavaBDDFeatureModelGeneratorAS();
				fmg_bdd_as.setSeed(exp.getGeneratorSeed());
				fmg_bdd_as.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_bdd_as=fmg_bdd_as.getFm();
				this.fm=fm_bdd_as.getFeatureModel();
				
				if (this.create_random_filter)	// If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_bdd_as=new JavaBDDFeatureModelSolverAS(this.fm_bdd_as,exp.getName(),filter,mc);
				System.out.println("OK");
				
			}
			
			if (this.generate_sat)
			{
				// Start monitoring memory
				if (mc!=null)
					mc.startAllocationRecording(true, 10, true, 100*1024);
				
				System.out.print("Generating SAT...");
				CNFFeatureModelGenerator fmg_cnf=new CNFFeatureModelGenerator();
				fmg_cnf.setSeed(exp.getGeneratorSeed());
				fmg_cnf.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_cnf=fmg_cnf.getFm();
				this.fm=fm_cnf.getFeatureModel();
				
				if (this.create_random_filter)	// If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_sat=new SATFeatureModelSolver(this.fm_cnf,exp.getName(),filter,mc);
				System.out.println("OK");
			}
			
			
			if (this.generate_sat_as)
			{
				// Start monitoring memory
				if (mc!=null)
					mc.startAllocationRecording(true, 10, true, 100*1024);
				
				System.out.print("Generating SAT AS...");
				CNFFeatureModelGeneratorAS fmg_cnf_as=new CNFFeatureModelGeneratorAS();
				fmg_cnf_as.setSeed(exp.getGeneratorSeed());
				fmg_cnf_as.generateFeatureModel(exp.getW(),exp.getH(),exp.getE(),exp.getD());
				this.fm_cnf_as=fmg_cnf_as.getFm();
				this.fm=fm_cnf_as.getFeatureModel();
				
				if (this.create_random_filter)	// If user want to apply a random filter, we create it now
					this.appRandomFilter();
				
				this.fms_sat_as=new SATFeatureModelSolverAS(this.fm_cnf_as,exp.getName(),filter,mc);
				System.out.println("OK");
				
			}
			
			if (this.generate_as)
			{
				if (this.fm!=null)
				{
					fm_as = new ASFeatureModel();
					fm_as.setFeatureModel(this.fm);
					fm_as.generateAS();
					this.fm_as_r = fm_as.printResults();
					this.fm_as_csv = fm_as.printCSVResults();
				}
			}
			
			
			}catch (Exception oops)
			{
				System.out.println("ERROR: " + oops.getMessage());
			}	
	}
	
	// Get one solution for all FM representations generated previously
	public void getOneSolution() {
		
		if (this.generate_jacop)
		{
			JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
			fmg_jacop.setSeed(experiment.getGeneratorSeed());
			fmg_jacop.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_jacop.getOneSolutionFlush(fmg_jacop.getFm(),false,filter);
			this.jacop_r=this.fms_jacop.getResults().printResults();
			this.jacop_csv_r=this.fms_jacop.getResults().printCSVResults();
		}
		
		if (this.generate_jacop_as)
		{
			JacopFeatureModelGeneratorAS fmg_jacop_as=new JacopFeatureModelGeneratorAS();
			fmg_jacop_as.setSeed(experiment.getGeneratorSeed());
			fmg_jacop_as.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_jacop_as.getOneSolutionFlush(fmg_jacop_as.getFm(),false,filter);
			this.jacop_ras=this.fms_jacop_as.getResults().printResults();
			this.jacop_csv_ras=this.fms_jacop_as.getResults().printCSVResults();
		}
		
		if (this.generate_choco)
		{
			ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
			fmg_choco.setSeed(experiment.getGeneratorSeed());
			fmg_choco.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_choco.getOneSolutionFlush(fmg_choco.getFm(),false,filter);
		}
		
		if (this.generate_bdd)
		{
			this.fms_bdd.getOneSolution();
			this.bdd_r=this.fms_bdd.getResults().printResults();
			this.bdd_csv_r=this.fms_bdd.getResults().printCSVResults();
		}
		
		
		if (this.generate_bdd_as)
		{
			this.fms_bdd_as.getOneSolution();
			this.bdd_ras=this.fms_bdd_as.getResults().printResults();
			this.bdd_csv_ras=this.fms_bdd_as.getResults().printCSVResults();
		}

		
		if (this.generate_sat)
		{
			this.fms_sat.getOneSolution();
			this.cnf_r=this.fms_sat.getResults().printResults();
			this.cnf_csv_r=this.fms_sat.getResults().printCSVResults();
		}
		
		if (this.generate_sat_as)
		{
			this.fms_sat_as.getOneSolution();
			this.cnf_ras=this.fms_sat_as.getResults().printResults();
			this.cnf_csv_ras=this.fms_sat_as.getResults().printCSVResults();
		}
	}
	
	
	// Get the number of solutions for all FM representations generated previously
	public void getNumberOfSolutions() {
		
		if (this.generate_jacop)
		{
			JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
			fmg_jacop.setSeed(experiment.getGeneratorSeed());
			fmg_jacop.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_jacop.getNumberOfSolutions(fmg_jacop.getFm(),filter);
			this.jacop_r=this.fms_jacop.getResults().printResults();
			this.jacop_csv_r=this.fms_jacop.getResults().printCSVResults();
		}
		
		if (this.generate_jacop_as)
		{
			JacopFeatureModelGeneratorAS fmg_jacop_as=new JacopFeatureModelGeneratorAS();
			fmg_jacop_as.setSeed(experiment.getGeneratorSeed());
			fmg_jacop_as.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_jacop_as.getNumberOfSolutions(fmg_jacop_as.getFm(),filter);
			this.jacop_ras=this.fms_jacop_as.getResults().printResults();
			this.jacop_csv_ras=this.fms_jacop_as.getResults().printCSVResults();
		}
		
		if (this.generate_choco)
		{
			ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
			fmg_choco.setSeed(experiment.getGeneratorSeed());
			fmg_choco.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_choco.getNumberOfSolutions(fmg_choco.getFm(),filter);
		}
		
		if (this.generate_bdd)
		{
			this.fms_bdd.getNumberOfSolutions();
			this.bdd_r=this.fms_bdd.getResults().printResults();
			this.bdd_csv_r=this.fms_bdd.getResults().printCSVResults();
		}
		
		if (this.generate_bdd_as)
		{
			this.fms_bdd_as.getNumberOfSolutions();
			this.bdd_ras=this.fms_bdd_as.getResults().printResults();
			this.bdd_csv_ras=this.fms_bdd_as.getResults().printCSVResults();
		}
		
		if (this.generate_sat)
		{
			this.fms_sat.getNumberOfSolutions();
			this.cnf_r=this.fms_sat.getResults().printResults();
			this.cnf_csv_r=this.fms_sat.getResults().printCSVResults();
		}
		
		if (this.generate_sat_as)
		{
			this.fms_sat_as.getNumberOfSolutions();
			this.cnf_ras=this.fms_sat_as.getResults().printResults();
			this.cnf_csv_ras=this.fms_sat_as.getResults().printCSVResults();
		}
	}
		
	// Get all solutions for all FM representations generated previously
	public void getAllSolutions() {
		
		if (this.generate_jacop)
		{
			JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
			fmg_jacop.setSeed(experiment.getGeneratorSeed());
			fmg_jacop.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_jacop.getAllSolutions(fmg_jacop.getFm(),filter);
		}
		
		if (this.generate_choco)
		{
			ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
			fmg_choco.setSeed(experiment.getGeneratorSeed());
			fmg_choco.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
			this.fms_choco.getAllSolutions(fmg_choco.getFm(),filter);
		}
		
		if (this.generate_bdd)
			this.fms_bdd.getAllSolutions();
		
		if (this.generate_sat)
			this.fms_sat.getAllSolutions();
	}
	
	
	// Jacop

	public void getOneSolutionJacop() {
		JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
		fmg_jacop.setSeed(experiment.getGeneratorSeed());
		fmg_jacop.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_jacop.getOneSolutionFlush(fmg_jacop.getFm(),false,filter);
		this.jacop_r=this.fms_jacop.getResults().printResults();
		this.jacop_csv_r=this.fms_jacop.getResults().printCSVResults();
		
	}
	
	public void getNumberOfSolutionJacop() {
		JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
		fmg_jacop.setSeed(experiment.getGeneratorSeed());
		fmg_jacop.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_jacop.getNumberOfSolutions(fmg_jacop.getFm(),filter);
		this.jacop_r=this.fms_jacop.getResults().printResults();
		this.jacop_csv_r=this.fms_jacop.getResults().printCSVResults();
	}
	
	public void getAllSolutionsJacop() {
		JacopFeatureModelGenerator fmg_jacop=new JacopFeatureModelGenerator();
		fmg_jacop.setSeed(experiment.getGeneratorSeed());
		fmg_jacop.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_jacop.getAllSolutions(fmg_jacop.getFm(),filter);
	}
	
	// Jacop AS

	public void getOneSolutionJacopAS() {
		JacopFeatureModelGeneratorAS fmg_jacop_as=new JacopFeatureModelGeneratorAS();
		fmg_jacop_as.setSeed(experiment.getGeneratorSeed());
		fmg_jacop_as.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_jacop_as.getOneSolutionFlush(fmg_jacop_as.getFm(),false,filter);
		this.jacop_ras=this.fms_jacop_as.getResults().printResults();
		this.jacop_csv_ras=this.fms_jacop_as.getResults().printCSVResults();
	}
	
	public void getNumberOfSolutionJacopAS() {
		JacopFeatureModelGeneratorAS fmg_jacop_as=new JacopFeatureModelGeneratorAS();
		fmg_jacop_as.setSeed(experiment.getGeneratorSeed());
		fmg_jacop_as.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_jacop_as.getNumberOfSolutions(fmg_jacop_as.getFm(),filter);
		this.jacop_ras=this.fms_jacop_as.getResults().printResults();
		this.jacop_csv_ras=this.fms_jacop_as.getResults().printCSVResults();
	}
	
	
	// Choco
	
	public void getOneSolutionChoco() {
		ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
		fmg_choco.setSeed(experiment.getGeneratorSeed());
		fmg_choco.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_choco.getOneSolutionFlush(fmg_choco.getFm(),false,filter);
	}
	
	public void getNumberOfSolutionChoco() {
		ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
		fmg_choco.setSeed(experiment.getGeneratorSeed());
		fmg_choco.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_choco.getNumberOfSolutions(fmg_choco.getFm(),filter);
	}
	
	public void getAllSolutionsChoco() {
		ChocoFeatureModelGenerator fmg_choco=new ChocoFeatureModelGenerator();
		fmg_choco.setSeed(experiment.getGeneratorSeed());
		fmg_choco.generateFeatureModel(experiment.getW(),experiment.getH(),experiment.getE(),experiment.getD());
		this.fms_choco.getAllSolutions(fmg_choco.getFm(),filter);
	}
	
	
	// JavaBDD
	
	public void getOneSolutionJavaBDD() {
		if (this.fms_bdd!=null)
		{
			this.fms_bdd.getOneSolution();
			this.bdd_r=this.fms_bdd.getResults().printResults();
			this.bdd_csv_r=this.fms_bdd.getResults().printCSVResults();
		}
	}
	
	public void getNumberOfSolutionsJavaBDD() {
		if (this.fms_bdd!=null)
		{
			this.fms_bdd.getNumberOfSolutions();
			this.bdd_r=this.fms_bdd.getResults().printResults();
			this.bdd_csv_r=this.fms_bdd.getResults().printCSVResults();
		}
	}
	
	public void getAllSolutionsJavaBDD() {
		if (this.fms_bdd!=null)
			this.fms_bdd.getAllSolutions();
	}
	

	// JavaBDD AS
	
	public void getOneSolutionJavaBDDAS() {
		if (this.fms_bdd_as!=null)
		{
			this.fms_bdd_as.getOneSolution();
			this.bdd_ras=this.fms_bdd_as.getResults().printResults();
			this.bdd_csv_ras=this.fms_bdd_as.getResults().printCSVResults();
		}
	}
	
	public void getNumberOfSolutionsJavaBDDAS() {
		if (this.fms_bdd_as!=null)
		{
			this.fms_bdd_as.getNumberOfSolutions();
			this.bdd_ras=this.fms_bdd_as.getResults().printResults();
			this.bdd_csv_ras=this.fms_bdd_as.getResults().printCSVResults();
		}
	}
	
	
	// SAT
	
	public void getOneSolutionSAT() {
		if (this.fms_sat!=null)
		{
			this.fms_sat.getOneSolution();
			this.cnf_r=this.fms_sat.getResults().printResults();
			this.cnf_csv_r=this.fms_sat.getResults().printCSVResults();
		}
	}
	
	public void getNumberOfSolutionsSAT() {
		if (this.fms_sat!=null)
		{
			this.fms_sat.getNumberOfSolutions();
			this.cnf_r=this.fms_sat.getResults().printResults();
			this.cnf_csv_r=this.fms_sat.getResults().printCSVResults();
		}
	}
	
	public void getAllSolutionsJavaSAT() {
		if (this.fms_sat!=null)
			this.fms_sat.getAllSolutions();
	}
	
	
	// SAT AS
	
	public void getOneSolutionSATAS() {
		if (this.fms_sat_as!=null)
		{
			this.fms_sat_as.getOneSolution();
			this.cnf_ras=this.fms_sat_as.getResults().printResults();
			this.cnf_csv_ras=this.fms_sat_as.getResults().printCSVResults();
		}
	}
	
	public void getNumberOfSolutionsSATAS() {
		if (this.fms_sat_as!=null)
		{
			this.fms_sat_as.getNumberOfSolutions();
			this.cnf_ras=this.fms_sat_as.getResults().printResults();
			this.cnf_csv_ras=this.fms_sat_as.getResults().printCSVResults();
		}
	}
	
	
	// Apply a filter
	public void applyFilter(Filter filter) {
		this.filter=filter;
	}
	
	// Comprueba si los atomic set generados automáticamente en los generadores y los calculados
	//  desde el FM son los mismos.
	public boolean checkAS() {
		boolean res= false;
		if (fm_as!=null && fm_jacop_as!=null)
			if (fm_as.getAS().equals(fm_jacop_as.getAtomicSets()))
				res=true;
		
		return res;
	}
	
	
	//Results
	public String getJacopResults() {
		return this.jacop_r;
	}
	
	public void setJacopResults(String r) {
		this.jacop_r=r;
	}
	
	public String getJacopResultsAS() {
		return this.jacop_ras;
	}
	
	public void setJacopResultsAS(String r) {
		this.jacop_ras=r;
	}
	
	public String getJavaBDDResults() {
		return this.bdd_r;
	}
	
	public void setJavaBDDResults(String r) {
		this.bdd_r=r;
	}
	
	public String getJavaBDDResultsAS() {
		return this.bdd_ras;
	}
	
	public void setJavaBDDResultsAS(String r) {
		this.bdd_ras=r;
	}
	
	public String getSATResults() {
		return this.cnf_r;
	}
	
	public void setSATResults(String r) {
		this.cnf_r=r;
	}
	
	public String getSATResultsAS() {
		return this.cnf_ras;
	}
	
	public void setSATResultsAS(String ras) {
		this.cnf_ras=ras;
	}
	
	//CSV Results
	public ArrayList getJacopCSVResults() {
		return this.jacop_csv_r;
	}
	
	public void setJacopCSVResults(ArrayList r) {
		this.jacop_csv_r=r;
	}
	
	public ArrayList getJacopCSVResultsAS() {
		return this.jacop_csv_ras;
	}
	
	public void setJacopCSVResultsAS(ArrayList r) {
		this.jacop_csv_ras=r;
	}
	
	public ArrayList getJavaBDDCSVResults() {
		return this.bdd_csv_r;
	}
	
	public void setJavaBDDCSVResults(ArrayList r) {
		this.bdd_csv_r=r;
	}
	
	public ArrayList getJavaBDDCSVResultsAS() {
		return this.bdd_csv_ras;
	}
	
	public void setJavaBDDResultsAS(ArrayList r) {
		this.bdd_csv_ras=r;
	}
	
	public ArrayList getSATCSVResults() {
		return this.cnf_csv_r;
	}
	
	public void setSATCSVResults(ArrayList r) {
		this.cnf_csv_r=r;
	}
	
	public ArrayList getSATCSVResultsAS() {
		return this.cnf_csv_ras;
	}
	
	public void setSATCSVResultsAS(ArrayList r) {
		this.cnf_csv_ras=r;
	}
	

	// Create a valid random filter
	public void appRandomFilter() {
		
		int nincluded=this.nincluded;
		
		if (this.pincluded!=0) // Percentage
			nincluded=new Double(fm.getFeaturesNumber() * this.pincluded).intValue();
			
		try
		{
			if (fm.getFeaturesNumber()<nincluded)
				throw new Exception("Two many required features in the filter. FM=" + fm.getFeaturesNumber() + " Filter=" + nincluded);
		
			// Create the filter
			Filter filter=new Filter();
			
			int tmp=0;
			while (tmp!=nincluded) {
				int feature=(new Random()).nextInt(fm.getFeaturesNumber());
				if (!filter.getIncludedFeatures().contains(feature))
				{
					filter.includeFeature(feature);
					tmp++;
				}
			}
			
			this.filter=filter;
		}
		catch (Exception oops) {
			System.out.println("ERROR: " + oops.getMessage());
		}
		
		this.create_random_filter=false;
		this.pincluded=0;
		this.nincluded=-1;
	}
	
	// Apply a random filter (during generation). nincluded = Number of features included in the filter
	public void applyRandomFilter(int nincluded) {
		if (nincluded>0)
		{
			this.create_random_filter=true;
			this.nincluded=nincluded;
		}
		else
			System.out.println("ERROR: The filter could not be created. Please check parameter and try again.");
	}
	
	// Apply a random filter (during generation). pincluded= Per centage of features included in the filter.
	public void applyRandomFilter(double pincluded) {
		
		if (pincluded>0.0 && pincluded<=1.0)
		{
			this.create_random_filter=true;
			this.pincluded=pincluded;
		}	
		else
			System.out.println("ERROR: The filter could not be created. Please check parameter and try again.");
	}
	
	// Save the results
	public void saveResults(boolean append) {
			
		if (fm==null)
			System.out.println("ERROR: You must generate any representation before saving results.");
		
		ExperimentSaver exp_saver=new ExperimentSaver(this.experiment,fm);
		
		// Add filter information
		if (filter!=null)
			exp_saver.addResult(filter.print());
		
		// Add results
		
		if (this.fm_as!=null)
			exp_saver.addResult(this.fm_as.printResults());
		else if (this.fm_as_r!="")
			exp_saver.addResult(this.fm_as_r);
		
		
		if (this.fms_jacop!=null)
			exp_saver.addResult(this.fms_jacop.getResults().printResults());
		else if (this.jacop_r!="")
			exp_saver.addResult(this.jacop_r);
		
		if (this.fms_jacop_as!=null)
			exp_saver.addResult(this.fms_jacop_as.getResults().printResults());
		else if (this.jacop_ras!="")
			exp_saver.addResult(this.jacop_ras);
		
		if (this.fms_choco!=null)
			exp_saver.addResult(this.fms_choco.getResults().printResults());
		
		if (this.fms_bdd!=null)
			exp_saver.addResult(this.fms_bdd.getResults().printResults());
		else if (this.bdd_r!="")
			exp_saver.addResult(this.bdd_r);
			
		if (this.fms_bdd_as!=null)
			exp_saver.addResult(this.fms_bdd_as.getResults().printResults());
		else if (this.bdd_ras!="")
			exp_saver.addResult(this.bdd_ras);
		
		if (this.fms_sat!=null)
			exp_saver.addResult(this.fms_sat.getResults().printResults());
		else if (this.cnf_r!="")
			exp_saver.addResult(this.cnf_r);
		
		if (this.fms_sat_as!=null)
			exp_saver.addResult(this.fms_sat_as.getResults().printResults());
		else if (this.cnf_ras!="")
			exp_saver.addResult(this.cnf_ras);
		
		// Read path from Properties File
		String path=UtilProp.getProperty("experimentsPath");
		
		//Save
		exp_saver.save(path, append);
		
	}
	
	// Save results in CSV Format
	public void saveCSVResults() {
		
		ExperimentSaver exp_saver=new ExperimentSaver();
		exp_saver.setExperiment(this.experiment);
		
		if (fm==null)
			System.out.println("ERROR: You must generate any representation before saving results.");
		
		// Save FM data (Number of features and number of dependencies)
		exp_saver.addCSVData(Integer.toString(fm.getFeaturesNumber()));
		exp_saver.addCSVData(Integer.toString(fm.getNumberOfDependencies()));
		
		// Save results
		if (this.fm_as!=null)
			exp_saver.addCSVResult(this.fm_as.printCSVResults());
		else if (this.fm_as_csv!=null)
			exp_saver.addCSVResult(this.fm_as_csv);
		
		if (this.fms_jacop!=null)
			exp_saver.addCSVResult(this.fms_jacop.getResults().printCSVResults());
		else if (this.jacop_csv_r!=null)
			exp_saver.addCSVResult(this.jacop_csv_r);
		
		if (this.fms_jacop_as!=null)
			exp_saver.addCSVResult(this.fms_jacop_as.getResults().printCSVResults());
		else if (this.jacop_csv_ras!=null)
			exp_saver.addCSVResult(this.jacop_csv_ras);
		
		if (this.fms_choco!=null)
			exp_saver.addCSVResult(this.fms_choco.getResults().printCSVResults());
	
		if (this.fms_bdd!=null)
			exp_saver.addCSVResult(this.fms_bdd.getResults().printCSVResults());
		else if (this.bdd_csv_r!=null)
			exp_saver.addCSVResult(this.bdd_csv_r);
		
		if (this.fms_bdd_as!=null)
			exp_saver.addCSVResult(this.fms_bdd_as.getResults().printCSVResults());
		else if (this.bdd_csv_ras!=null)
			exp_saver.addCSVResult(this.bdd_csv_ras);
		
		if (this.fms_sat!=null)
			exp_saver.addCSVResult(this.fms_sat.getResults().printCSVResults());
		else if (this.cnf_csv_r!=null)
			exp_saver.addCSVResult(this.cnf_csv_r);
		
		if (this.fms_sat_as!=null)
			exp_saver.addCSVResult(this.fms_sat_as.getResults().printCSVResults());
		else if (this.cnf_csv_ras!=null)
			exp_saver.addCSVResult(this.cnf_csv_ras);
		
		
		// Read path from Properties File
		String path=UtilProp.getProperty("resultsCSVPath");
		
		// Save
		exp_saver.saveCSVResults(path);
	}
}
