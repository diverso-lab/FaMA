package es.us.isa.jmetal.experimentation.settings;

import java.util.HashMap;
import java.util.logging.FileHandler;

import es.us.isa.fama.operations.AAFMProblem;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.metaheuristics.pesa2.PESA2;
import jmetal.metaheuristics.pesa2.PESA2AAFM;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.Kursawe;
import jmetal.problems.ProblemFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class PESA2AAFM_Settings extends AAFMSettings {

	private double crossOverProbability;
	private double crossOverDistributionIndex;
	private double mutationProbability;
	private double mutationDistributionIndex;
	private int archiveSize;
	private int populationSize;
	private int maxEvaluations;
	private int bisections;
	
	public PESA2AAFM_Settings(AAFMProblem problem){
		super(problem);
		
		archiveSize = 100;
		populationSize = 10;
		maxEvaluations = 25000;
		bisections = 5;
		crossOverProbability = 0.9;
		crossOverDistributionIndex = 20.0;
		mutationProbability = 1.0/problem_.getNumberOfVariables();
		mutationDistributionIndex = 20.0;
	}
	
	@Override
	public Algorithm configure() throws JMException {
		Algorithm algorithm ;         // The algorithm to use
	    Operator  crossover ;         // Crossover operator
	    Operator  mutation  ;         // Mutation operator    
	     
	    HashMap  parameters ; // Operator parameters

	    // Logger object and file to store log messages

	    
	    algorithm = new PESA2AAFM(problem_);
	    
	    // Algorithm parameters 
	    algorithm.setInputParameter("populationSize",populationSize);
	    algorithm.setInputParameter("archiveSize", archiveSize);
	    algorithm.setInputParameter("bisections",bisections);
	    algorithm.setInputParameter("maxEvaluations",maxEvaluations);
	    
	    // Mutation and Crossover for Real codification 
	    parameters = new HashMap() ;
	    parameters.put("probability", crossOverProbability) ;
	    parameters.put("distributionIndex", crossOverDistributionIndex) ;
	    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

	    parameters = new HashMap() ;
	    parameters.put("probability", mutationProbability) ;
	    parameters.put("distributionIndex", mutationDistributionIndex) ;
	    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);    
	    
	    // Mutation and Crossover Binary codification
	    /*
	    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover");                   
	    crossover.setParameter("probability",0.9);                   
	    mutation = MutationFactory.getMutationOperator("BitFlipMutation");                    
	    mutation.setParameter("probability",1.0/80);
	    */
	    
	    // Add the operators to the algorithm
	    algorithm.addOperator("crossover",crossover);
	    algorithm.addOperator("mutation",mutation);
	    
	    return algorithm;
	}

}
