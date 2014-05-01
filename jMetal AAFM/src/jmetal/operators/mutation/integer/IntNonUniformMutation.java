package jmetal.operators.mutation.integer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayIntSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XInt;

public class IntNonUniformMutation extends Mutation {
	 /**
	   * Valid solution types to apply this operator 
	   */
	  private static final List VALID_TYPES = Arrays.asList(IntSolutionType.class,
	  		                                            ArrayIntSolutionType.class) ;
	  /**
	   * perturbation_ stores the perturbation value used in the Non Uniform 
	   * mutation operator
	   */
	  private Double perturbation_ = null;
	  
	  /**
	   * maxIterations_ stores the maximun number of iterations. 
	   */
	  private Integer maxIterations_ = null;    
	  
	  /**
	   * currentIteration_ stores the iteration in which the operator is going to be
	   * applied
	   */
	  private Integer currentIteration_ = null;
	           
	  private Double mutationProbability_ = null;

	  /** 
	  * Constructor
	  * Creates a new instance of the non uniform mutation
	  */
	  public IntNonUniformMutation(HashMap<String, Object> parameters) {
	  	super(parameters) ;
	  	if (parameters.get("probability") != null)
	  		mutationProbability_ = (Double) parameters.get("probability") ;  		
	  	if (parameters.get("perturbation") != null)
	  		perturbation_ = (Double) parameters.get("perturbation") ;  		
	  	if (parameters.get("maxIterations") != null)
	  		maxIterations_ = (Integer) parameters.get("maxIterations") ;  		
	  } // NonUniformMutation
	            

	  /**
	  * Constructor
	  * Creates a new instance of the non uniform mutation
	  */
	  //public NonUniformMutation(Properties properties){
	  //   this();
	  //} // NonUniformMutation


	  /**
	  * Perform the mutation operation
	  * @param probability Mutation probability
	  * @param solution The solution to mutate
	   * @throws JMException 
	  */
	  public void doMutation(double probability, Solution solution) throws JMException {                
	  	XInt x = new XInt(solution) ; 
	    for (int var = 0; var < solution.getDecisionVariables().length; var++) {         
	      if (PseudoRandom.randDouble() < probability) {
	        double rand = PseudoRandom.randDouble();
	        double tmp;
	                
	        if (rand <= 0.5) {
	          tmp = delta(x.getUpperBound(var) - x.getValue(var),
	                      perturbation_.doubleValue());
	          tmp += x.getValue(var);
	        }
	        else {
	          tmp = delta(x.getLowerBound(var) - x.getValue(var),
	                      perturbation_.doubleValue());
	          tmp += x.getValue(var);
	        }
	                
	        if (tmp < x.getLowerBound(var))
	          tmp = x.getLowerBound(var);
	        else if (tmp > x.getUpperBound(var))
	          tmp = x.getUpperBound(var);
	                
	        x.setValue(var, (int)Math.round(tmp)) ;
	      }
	    }
	  } // doMutation
	    

	  /**
	   * Calculates the delta value used in NonUniform mutation operator
	   */
	  private double delta(double y, double bMutationParameter) {
	    double rand = PseudoRandom.randDouble();
	    int it,maxIt;
	    it    = currentIteration_.intValue();
	    maxIt = maxIterations_.intValue();
	        
	    return (y * (1.0 - 
	                Math.pow(rand,
	                         Math.pow((1.0 - it /(double) maxIt),bMutationParameter)
	                         )));
	  } // delta

	  /**
	  * Executes the operation
	  * @param object An object containing a solution
	  * @return An object containing the mutated solution
	   * @throws JMException 
	  */
	  public Object execute(Object object) throws JMException {
	    Solution solution = (Solution)object;
	    
			if (!VALID_TYPES.contains(solution.getType().getClass())) {
	      Configuration.logger_.severe("NonUniformMutation.execute: the solution " +
	      		solution.getType() + "is not of the right type");

	      Class cls = java.lang.String.class;
	      String name = cls.getName(); 
	      throw new JMException("Exception in " + name + ".execute()") ;
	    } // if  
	    
	  	if (getParameter("currentIteration") != null)
	  		currentIteration_ = (Integer) getParameter("currentIteration") ;  		

	    doMutation(mutationProbability_,solution);
	        
	    return solution;    
	  } // execute

}
