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

public class IntUniformMutation extends Mutation {

	  /**
	   * Valid solution types to apply this operator 
	   */
	  private static final List VALID_TYPES = Arrays.asList(IntSolutionType.class,
	  		                                            ArrayIntSolutionType.class) ;
	  /**
	   * Stores the value used in a uniform mutation operator
	   */
	  private Double perturbation_;
	  
	  private Double mutationProbability_ = null;

	  /** 
	   * Constructor
	   * Creates a new uniform mutation operator instance
	   */
	  public IntUniformMutation(HashMap<String, Object> parameters) {
	  	super(parameters) ;
	  	
	  	if (parameters.get("probability") != null)
	  		mutationProbability_ = (Double) parameters.get("probability") ;  		
	  	if (parameters.get("perturbation") != null)
	  		perturbation_ = (Double) parameters.get("perturbation") ;  		

	  } // UniformMutation


	  /**
	   * Constructor
	   * Creates a new uniform mutation operator instance
	   */
	  //public UniformMutation(Properties properties) {
	  //  this();
	  //} // UniformMutation


	  /**
	  * Performs the operation
	  * @param probability Mutation probability
	  * @param solution The solution to mutate
	   * @throws JMException 
	  */
	  public void doMutation(double probability, Solution solution) throws JMException {  
	  	XInt x = new XInt(solution) ; 

	    for (int var = 0; var < solution.getDecisionVariables().length; var++) {
	      if (PseudoRandom.randDouble() < probability) {
	        double rand = PseudoRandom.randDouble();
	        double tmp = (rand - 0.5)*perturbation_.doubleValue();
	                                
	        tmp += x.getValue(var);
	                
	        if (tmp < x.getLowerBound(var))
	          tmp = x.getLowerBound(var);
	        else if (tmp > x.getUpperBound(var))
	          tmp = x.getUpperBound(var);
	                
	        x.setValue(var, (int)Math.round(tmp)) ;
	      } // if
	    } // for
	  } // doMutation
	  
	  /**
	  * Executes the operation
	  * @param object An object containing the solution to mutate
	   * @throws JMException 
	  */
	  public Object execute(Object object) throws JMException {
	    Solution solution = (Solution )object;
	    
			if (!VALID_TYPES.contains(solution.getType().getClass())) {
	      Configuration.logger_.severe("UniformMutation.execute: the solution " +
	          "is not of the right type. The type should be 'Real', but " +
	          solution.getType() + " is obtained");

	      Class cls = java.lang.String.class;
	      String name = cls.getName(); 
	      throw new JMException("Exception in " + name + ".execute()") ;
	    } // if 
	    
	    doMutation(mutationProbability_,solution);
	        
	    return solution;
	  } // execute            
}
