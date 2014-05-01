//  AbYSS_Settings.java 
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package es.us.isa.jmetal.experimentation.settings;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.metaheuristics.abyss.AbYSSAAFM;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.localSearch.MutationLocalSearch;
import jmetal.operators.mutation.MutationFactory;
import jmetal.util.JMException;
import es.us.isa.fama.operations.AAFMProblem;

/**
 * Settings class of algorithm AbYSS
 */
public class AbYSSAAFM_Settings extends AAFMSettings {

	private int populationSize_ ;
  private int maxEvaluations_ ;
  private int archiveSize_ ;
  private int refSet1Size_ ;
  private int refSet2Size_ ;
  private double mutationProbability_ ;
  private double crossoverProbability_ ;
  private double crossoverDistributionIndex_ ;
  private double mutationDistributionIndex_  ;
  private int improvementRounds_ ;
  
  /**
   * Constructor
   * @param problemName Problem to solve
   */
  public AbYSSAAFM_Settings(AAFMProblem problem) {
    super(problem);

//    Object [] problemParams = {"Real"};
//    try {
//	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
//    } catch (JMException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//    }      
    populationSize_ = 20;
    maxEvaluations_ = 25000;
    archiveSize_ = 100;
    refSet1Size_ = 10;
    refSet2Size_ = 10;
    mutationProbability_ = 1.0 / problem_.getNumberOfVariables();
    crossoverProbability_ = 1.0;
    crossoverDistributionIndex_ = 20.0  ;
    mutationDistributionIndex_  = 20.0  ;
    improvementRounds_ = 1;
    
  } // AbYSS_Settings

  /**
   * Configure the MOCell algorithm with default parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;
    Operator improvement; // Operator for improvement

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new AbYSSAAFM(problem_);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("refSet1Size", refSet1Size_);
    algorithm.setInputParameter("refSet2Size", refSet2Size_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", crossoverDistributionIndex_) ;
    crossover = CrossoverFactory.getCrossoverOperator("IntSBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("IntPolynomialMutation", parameters);    
    
    parameters = new HashMap() ;
    parameters.put("improvementRounds", improvementRounds_) ;
    parameters.put("problem",problem_) ;
    parameters.put("mutation",mutation) ;
    improvement = new MutationLocalSearch(parameters);

    // Adding the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("improvement", improvement);

    return algorithm;
  } // Constructor
} // AbYSS_Settings
