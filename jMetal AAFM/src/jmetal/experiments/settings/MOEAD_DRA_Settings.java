//  MOEAD_DRA_Settings.java 
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

package jmetal.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.experiments.Settings;
import jmetal.metaheuristics.moead.MOEAD_DRA;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

import java.util.HashMap;

/**
 * Settings class of algorithm MOEA/D-DRA
 */
public class MOEAD_DRA_Settings extends Settings {
  private double CR_ ;
  private double F_  ;
  private int populationSize_ ;
  private int maxEvaluations_ ;
  private int finalSize_      ;

  private double mutationProbability_          ;
  private double distributionIndexForMutation_ ;

  private int T_        ;
  private double delta_ ;
  private int nr_    ;

  private String dataDirectory_  ;

  /**
   * Constructor
   */
  public MOEAD_DRA_Settings(String problem) {
    super(problem);
    
    Object [] problemParams = {"Real"};
    try {
	    problem_ = (new ProblemFactory()).getProblem(problemName_, problemParams);
    } catch (JMException e) {
	    e.printStackTrace();
    }      

    // Default experiments.settings
    CR_ = 1.0 ;
    F_  = 0.5 ;
    populationSize_ = 600;
    maxEvaluations_ = 300000;
    
    finalSize_ = 300 ;
   
    mutationProbability_ = 1.0/problem_.getNumberOfVariables() ;
    distributionIndexForMutation_ = 20;

    T_ = (int) (0.1 * populationSize_);
    delta_ = 0.9;
    nr_ = (int) (0.01 * populationSize_);

    // Directory with the files containing the weight vectors used in 
    // Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of MOEA/D 
    // on CEC09 Unconstrained MOP Test Instances Working Report CES-491, School 
    // of CS & EE, University of Essex, 02/2009.
    // http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
    //dataDirectory_ =  "/Users/antonio/Softw/pruebas/data/MOEAD_parameters/Weight" ;
    dataDirectory_ =  "/Users/antelverde/Dropbox/jMetal/data/MOEAD_parameters/Weight" ;

  } // MOEAD_DRA_Settings

  /**
   * Configure the algorithm with the specified parameter experiments.settings
   * @return an algorithm object
   * @throws jmetal.util.JMException
   */
  public Algorithm configure() throws JMException {
    Algorithm algorithm;
    Operator crossover;
    Operator mutation;

    HashMap  parameters ; // Operator parameters

    // Creating the problem
    algorithm = new MOEAD_DRA(problem_);
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize", populationSize_) ;
    algorithm.setInputParameter("maxEvaluations", maxEvaluations_) ;
    algorithm.setInputParameter("dataDirectory", dataDirectory_)   ;
    algorithm.setInputParameter("finalSize", finalSize_)           ;

    algorithm.setInputParameter("T", T_) ;
    algorithm.setInputParameter("delta", delta_) ;
    algorithm.setInputParameter("nr", nr_) ;

    // Crossover operator 
    parameters = new HashMap() ;
    parameters.put("CR", CR_) ;
    parameters.put("F", F_) ;
    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);                   
    
    // Mutation operator
    parameters = new HashMap() ;
    parameters.put("probability", mutationProbability_) ;
    parameters.put("distributionIndex", distributionIndexForMutation_) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);         
    
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);

    return algorithm;
  } // configure
} // MOEAD_DRA_Settings
