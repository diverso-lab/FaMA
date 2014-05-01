//  NSGAIIPermutation_Settings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;

/**
 * Settings class of algorithm NSGA-II (permutation encoding)
 */
public class NSGAIIPermutation_Settings extends Settings {

  int populationSize_  ;
  int maxEvaluations_  ;

  double mutationProbability_  ;
  double crossoverProbability_ ;

  /**
   * Constructor
   */
  public NSGAIIPermutation_Settings(String problem) {
    super(problem) ;
    
    Object [] problemParams = {"Permutation"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    e.printStackTrace();
    }      
    
    // Default experiments.settings
    populationSize_ = 100   ;
    maxEvaluations_ = 25000 ;

    mutationProbability_  = 1.0/problem_.getNumberOfVariables();
    crossoverProbability_ = 0.9 ; 
  } // NSGAIIPermutation_Settings
  
  /**
   * Configure NSGAII with user-defined parameter experiments.settings
   * @return A NSGAII algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator  selection ;
    Operator  crossover ;
    Operator  mutation  ;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new NSGAII(problem_) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);

    
    // Mutation and Crossover Permutation codification
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", parameters);

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    mutation = MutationFactory.getMutationOperator("SwapMutation",parameters);
    
    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;   
    
    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    return algorithm ;
  } // configure
} // NSGAIIPermutation_Settings
