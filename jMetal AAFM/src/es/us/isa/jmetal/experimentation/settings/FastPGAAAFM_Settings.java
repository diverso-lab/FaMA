package es.us.isa.jmetal.experimentation.settings;

import java.util.HashMap;

import es.us.isa.fama.operations.AAFMProblem;
import jmetal.core.Algorithm;
import jmetal.metaheuristics.fastPGA.FastPGA;
import jmetal.metaheuristics.fastPGA.FastPGAAAFM;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.BinaryTournament;
import jmetal.operators.selection.Selection;
import jmetal.util.JMException;
import jmetal.util.comparators.FPGAFitnessComparator;

public class FastPGAAAFM_Settings extends AAFMSettings {

	private double crossOverProbability;
	private double crossOverDistributionIndex;
	private double mutationProbability;
	private double mutationDistributionIndex;
	private int maxPopSize;
	private int initialPopulationSize;
	private int maxEvaluations;
    private double a;
    private double b;
    private double c;
    private double d;
    private int termination;
	
	public FastPGAAAFM_Settings(AAFMProblem problem){
		super(problem);
		
		// Default experiments.settings
		maxPopSize = 100;
		initialPopulationSize = 100;
		maxEvaluations = 25000;
		
//		maxPopSize = 200;
//		initialPopulationSize = 200;
//		maxEvaluations = 50000;
		
		a = 20.0;
		b = 1.0;
		c = 20.0;
		d = 0.0;
		termination = 1;
		crossOverProbability = 0.9;
		crossOverDistributionIndex = 20.0;
		mutationProbability = 1.0/problem_.getNumberOfVariables();
		mutationDistributionIndex = 20.0;
	}
	
	@Override
	public Algorithm configure() throws JMException {
		Algorithm algorithm = new FastPGAAAFM(problem_);

	    algorithm.setInputParameter("maxPopSize",maxPopSize);
	    algorithm.setInputParameter("initialPopulationSize",initialPopulationSize);
	    algorithm.setInputParameter("maxEvaluations",maxEvaluations);
	    algorithm.setInputParameter("a",a);
	    algorithm.setInputParameter("b",b);
	    algorithm.setInputParameter("c",c);
	    algorithm.setInputParameter("d",d);

	    // Parameter "termination"
	    // If the preferred stopping criterium is PPR based, termination must 
	    // be set to 0; otherwise, if the algorithm is intended to iterate until 
	    // a give number of evaluations is carried out, termination must be set to 
	    // that number
	    algorithm.setInputParameter("termination",termination);

	    // Mutation and Crossover for Real codification 
	    HashMap parameters = new HashMap() ;
	    parameters.put("probability", crossOverProbability) ;
	    parameters.put("distributionIndex", crossOverDistributionIndex) ;
	    Crossover crossover = CrossoverFactory.getCrossoverOperator("IntSBXCrossover", parameters);                   
	    //crossover.setParameter("probability",0.9);                   
	    //crossover.setParameter("distributionIndex",20.0);

	    parameters = new HashMap() ;
	    parameters.put("probability", mutationProbability) ;
	    parameters.put("distributionIndex", mutationDistributionIndex) ;
	    Mutation mutation = MutationFactory.getMutationOperator("IntPolynomialMutation", parameters);         
	    // Mutation and Crossover for Binary codification
	    
	    parameters = new HashMap() ; 
	    parameters.put("comparator", new FPGAFitnessComparator()) ;
	    Selection selection = new BinaryTournament(parameters);
	    
	    algorithm.addOperator("crossover",crossover);
	    algorithm.addOperator("mutation",mutation);
	    algorithm.addOperator("selection",selection);
	    
	    return algorithm;
	}

}
