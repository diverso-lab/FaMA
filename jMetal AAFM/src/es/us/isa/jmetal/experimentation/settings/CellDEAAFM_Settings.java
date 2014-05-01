//  CellDE_Settings.java 
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
import jmetal.metaheuristics.cellde.CellDE;
import jmetal.metaheuristics.cellde.CellDEAAFM;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;

import es.us.isa.fama.operations.AAFMProblem;

/**
 * Settings class of algorithm CellDE
 */
public class CellDEAAFM_Settings extends AAFMSettings{
  
  private double CR_           ;
  private double F_            ;
  
  private int populationSize_  ;
  private int archiveSize_     ;
  private int maxEvaluations_  ;
  private int archiveFeedback_ ;
 
  /**
   * Constructor
   */
  public CellDEAAFM_Settings(AAFMProblem problem) {
    super(problem);
    
//    Object [] problemParams = {"Real"};
//    try {
//	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
//    } catch (JMException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//    }      

    // Default experiments.settings
    CR_          = 0.5;
    F_           = 0.5    ;
    
    populationSize_ = 100   ;
    archiveSize_    = 100   ;
    maxEvaluations_ = 25000 ;
    archiveFeedback_= 20    ;
  } // CellDE_Settings
  
  /**
   * Configure the algorithm with the specified parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm ;
    Operator  selection ;
    Operator  crossover ;

    HashMap  parameters ; // Operator parameters

    // Creating the problem   
    algorithm = new CellDEAAFM(problem_) ;
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_);
    algorithm.setInputParameter("archiveSize", archiveSize_);
    algorithm.setInputParameter("maxEvaluations",maxEvaluations_);
    algorithm.setInputParameter("feedBack", archiveFeedback_);
    
    // Crossover operator 
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    crossover = CrossoverFactory.getCrossoverOperator("IntDifferentialEvolutionCrossover", parameters);                   
    
    // Add the operators to the algorithm
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ; 

    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("selection",selection);
    
    return algorithm ;
  } // configure
} // CellDE_Settings
