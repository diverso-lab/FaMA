//  MOCell_Settings.java 
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

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.mocell.MOCell;
import jmetal.metaheuristics.mocell.MOCellAAFM;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;

import es.us.isa.fama.operations.AAFMProblem;

/**
 * Settings class of algorithm MOCell
 */
public class MOCellAAFM_Settings extends AAFMSettings{

  private int populationSize_                ;
  private int maxEvaluations_                ;
  private int archiveSize_                   ;
  private int feedback_                      ;
  private double mutationProbability_        ;
  private double crossoverProbability_       ;
  private double crossoverDistributionIndex_ ;
  private double mutationDistributionIndex_  ;

  /**
   * Constructor
   */
  public MOCellAAFM_Settings(AAFMProblem problem) {
    super(problem) ;

    // Default experiments.settings
    populationSize_             = 100   ;
    maxEvaluations_             = 25000 ;
    archiveSize_                = 100   ;
    feedback_                   = 20    ;
    mutationProbability_        = 1.0/problem_.getNumberOfVariables();
    crossoverProbability_       = 0.9   ;
    crossoverDistributionIndex_ = 20.0  ;
    mutationDistributionIndex_  = 20.0  ;
  } // MOCell_Settings

  /**
   * Configure the MOCell algorithm with default parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;

    Crossover crossover ;
    Mutation  mutation  ;
    Operator  selection ;

    HashMap  parameters ; // Operator parameters

    algorithm = new MOCellAAFM(problem_) ;

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
    algorithm.setInputParameter("archiveSize",archiveSize_ );
    algorithm.setInputParameter("feedBack",feedback_);


    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", crossoverDistributionIndex_) ;
    crossover = CrossoverFactory.getCrossoverOperator("IntSBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", mutationDistributionIndex_) ;
    mutation = MutationFactory.getMutationOperator("IntPolynomialMutation", parameters);                         

    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;   

    // Add the operators to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    return algorithm ;
  } // configure
} // MOCell_Settings
