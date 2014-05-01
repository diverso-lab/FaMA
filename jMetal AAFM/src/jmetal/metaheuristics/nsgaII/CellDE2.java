/**
 * CellDE.java
 * @author Juan J. Durillo, Antonio J. Nebro
 * @version 1.0
 */
package jmetal.metaheuristics.nsgaII;

import jmetal.core.*;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.*;
import jmetal.util.archive.CrowdingArchive;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.offspring.Offspring;
import jmetal.util.offspring.PolynomialMutationOffspring;

import java.io.*;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing the MoCell algorithm
 */
public class CellDE2 extends Algorithm {

  //->fields
  int[] contributionCounter_; // contribution per crossover operator
  double[] contribution_; // contribution per crossover operator
  int currentCrossover_;
  int[] contributionArchiveCounter_;

  final boolean TRAZA = false ;
  
  public CellDE2(Problem problem) {
    super(problem) ;
  }

  /** Execute the algorithm
   * @throws JMException 
   * @throws ClassNotFoundException */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    //Init the param
    int populationSize, archiveSize, maxEvaluations, evaluations;
    Operator mutationOperator, crossoverOperator, selectionOperator;
    SolutionSet currentPopulation;
    SolutionSet archive;
    SolutionSet[] neighbors;
    Neighborhood neighborhood;

    QualityIndicator indicators; // QualityIndicator object

    Comparator dominance = new DominanceComparator();
    Comparator crowdingComparator = new CrowdingComparator();
    Distance distance = new Distance();

    //Init the params

    //Read the params
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

    //Init the variables
    currentPopulation = new SolutionSet(populationSize);
    archive = new CrowdingArchive(archiveSize, problem_.getNumberOfObjectives());
    //archive            = new jmetal.base.archive.StrengthRawFitnessArchive(archiveSize);
    evaluations = 0;
    neighborhood = new Neighborhood(populationSize);
    neighbors = new SolutionSet[populationSize];

		Offspring[] getOffspring;
		int N_O; // number of offpring objects

		getOffspring = ((Offspring[]) getInputParameter("offspringsCreators"));
		N_O = getOffspring.length;

		contribution_               = new double[N_O];
		contributionCounter_        = new int[N_O];
		contributionArchiveCounter_        = new int[N_O];

		contribution_[0] = (double) (populationSize / (double) N_O) / (double) populationSize;
		for (int i = 1; i < N_O; i++) {
			contribution_[i] = (double) (populationSize / (double) N_O) / (double) populationSize + (double) contribution_[i - 1];
		}
    
		for (int i = 0; i < N_O; i++) {
			System.out.println(getOffspring[i].configuration()) ;
			System.out.println("Contribution: " + contribution_[i]) ;
		}
		
    int iterationsWithMinimumContribution = 0;

    FileOutputStream fos;
    BufferedWriter M1 = null, M2 = null, HV = null;
    BufferedWriter C1 = null, C2 = null ;
if (TRAZA) {
    try {
      fos = new FileOutputStream("DE");
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      M1 = new BufferedWriter(osw);
      fos = new FileOutputStream("SBX");
      OutputStreamWriter osw2 = new OutputStreamWriter(fos);
      M2 = new BufferedWriter(osw2);
      fos = new FileOutputStream("HV");
      OutputStreamWriter osw3 = new OutputStreamWriter(fos);
      HV = new BufferedWriter(osw3);
      fos = new FileOutputStream("C1");
      OutputStreamWriter osw4 = new OutputStreamWriter(fos);
      C1 = new BufferedWriter(osw4);
      fos = new FileOutputStream("C2");
      OutputStreamWriter osw5 = new OutputStreamWriter(fos);
      C2 = new BufferedWriter(osw5);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CellDE2.class.getName()).log(Level.SEVERE, null, ex);
    }
}
    //Create the initial population
    for (int i = 0; i < populationSize; i++) {
      Solution individual = new Solution(problem_);
      problem_.evaluate(individual);
      problem_.evaluateConstraints(individual);
      currentPopulation.add(individual);
      individual.setLocation(i);
      evaluations++;
    }

    System.out.println("eweaasfasfasfasdfasfdasfdsfdasfda");

    while (evaluations < maxEvaluations) {
      for (int i = 0; i < N_O; i++) {
        contributionCounter_[i] = 0;
        contributionArchiveCounter_[i] = 0;
      }

      for (int ind = 0; ind < currentPopulation.size(); ind++) {
        Solution individual = new Solution(currentPopulation.get(ind));

        Solution[] parents = new Solution[2];
        Solution offSpring = null;

        neighbors[ind] = neighborhood.getEightNeighbors(currentPopulation, ind);
        neighbors[ind].add(individual);

        boolean found = false;

        int selected = 0;
        for (selected = 0; selected < N_O; selected++) {
          double rnd = PseudoRandom.randDouble();
          
					if (!found && (rnd <= contribution_[selected])) {
						if ("DE".equals(getOffspring[selected].id())) {
							offSpring = getOffspring[selected].getOffspring(neighbors[ind], ind) ;
						} else if ("SBXCrossover".equals(getOffspring[selected].id())) {
							offSpring = getOffspring[selected].getOffspring(neighbors[ind]);
						} else if ("BLXAlphaCrossover".equals(getOffspring[selected].id())) {
							 offSpring = getOffspring[selected].getOffspring(neighbors[ind]);
						} else if ("PolynomialMutation".equals(getOffspring[selected].id())) {
							offSpring = ((PolynomialMutationOffspring)getOffspring[selected]).getOffspring(individual);
						} else {
							System.out.println("Error in CellDE2. Operator " + offSpring + " does not exist") ;
						}
          /*
          
          if (!found && (rnd <= contribution_[selected])) {
            if ("DE".equals(getOffspring[selected].id())) {
              //offSpring = getOffspring[selected].getOffspring(currentPopulation, neighbors[ind].size()-1) ;
              offSpring = getOffspring[selected].getOffspring(neighbors[ind], archive, neighbors[ind].size() - 1);
              //offSpring.setFitness(selected) ;
            } else if ("SBX_Polynomial".equals(getOffspring[selected].id())) {
              //offSpring = getOffspring[selected].getOffspring(currentSolutionSet) ;
              offSpring = getOffspring[selected].getOffspring(neighbors[ind], archive);
              //offSpring.setFitness(selected) ;
            } else {
              System.out.println("eweaasfasfasfasdfasfdasfdsfdasfda");
            }
           */
            offSpring.setFitness((int) selected);
            currentCrossover_ = selected;
            found = true;
          } // if
        } // for

        // Evaluate individual an his constraints
        problem_.evaluate(offSpring);
        problem_.evaluateConstraints(offSpring);
        evaluations++;

        int flag = dominance.compare(individual, offSpring);

        if (flag == 1) { //The new individual dominates
          offSpring.setLocation(individual.getLocation());
          currentPopulation.replace(offSpring.getLocation(), offSpring);
          archive.add(new Solution(offSpring));
        } else if (flag == 0) { //The new individual is non-dominated
          neighbors[ind].add(offSpring);
          offSpring.setLocation(-1);
          Ranking rank = new Ranking(neighbors[ind]);
          for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            distance.crowdingDistanceAssignment(rank.getSubfront(j),
              problem_.getNumberOfObjectives());
          }
          neighbors[ind].sort(crowdingComparator);
          Solution worst = neighbors[ind].get(neighbors[ind].size() - 1);

          if (worst.getLocation() == -1) { //The worst is the offspring
            archive.add(new Solution(offSpring));
          } else {
            offSpring.setLocation(worst.getLocation());
            currentPopulation.replace(offSpring.getLocation(), offSpring);
            archive.add(new Solution(offSpring));
          }
        }
      }

      for (int i = 0; i < N_O; i++) {
        contributionArchiveCounter_[i] = 0;
      }
      for (int i = 0; i < archive.size(); i++) {
        //System.out.println(i + " = " + (int)archive.get(i).getFitness()) ;
        if ((int) archive.get(i).getFitness() != -1) {
          contributionArchiveCounter_[(int) archive.get(i).getFitness()]++;
          //System.out.println(i + ": " + archive.get(i).getCrowdingDistance()) ;
        }
        archive.get(i).setFitness(-1);

        //if ((evaluations % 1000) == 0)
        //  if (PseudoRandom.randDouble() < 0.5)
        //    contributionArchiveCounter_[0] = 90 ;
        //  else
        //    contributionArchiveCounter_[1] = 90 ;
      }

      int minimumContribution = 2;

      int totalContributionCounter = 0;
      int totalcontributionArchiveCounter = 0;
      for (int i = 0; i < N_O; i++) {
        if (contributionCounter_[i] < minimumContribution) {
          contributionCounter_[i] = minimumContribution;
        }
        if (contributionArchiveCounter_[i] < minimumContribution) {
          contributionArchiveCounter_[i] = minimumContribution;
        }
        totalContributionCounter += contributionCounter_[i];
        totalcontributionArchiveCounter += contributionArchiveCounter_[i];
      }

      //contribution_[0] = contributionCounter_[0]*1.0 / (double)totalContributionCounter ;
      contribution_[0] = contributionArchiveCounter_[0] * 1.0 / totalcontributionArchiveCounter;
      for (int i = 1; i < N_O; i++) {
        contribution_[i] = contribution_[i - 1] + 1.0 * contributionArchiveCounter_[i] / totalcontributionArchiveCounter;
        //contribution_[i] = contribution_[i-1] + 1.0*contributionCounter_[i]/ (double)totalContributionCounter ;
      }

      //if ((contributionArchiveCounter_[0] == minimumContribution) && (contributionArchiveCounter_[1] == minimumContribution))
      //  iterationsWithMinimumContribution++ ;
      //else
      //  iterationsWithMinimumContribution = 0 ;
if (TRAZA) {
      for (int i = 0; i < N_O; i++) {
        System.out.print("i: " + i + "\t contCounter: " + contributionArchiveCounter_[i] +
          //System.out.println("i: " + i + "\t contributionCounter: " + contributionCounter_[i] +
          "\t contribution: " + contribution_[i] + "\t");
        //+ "\t No changes: " + iterationsWithMinimumContribution) ;
        System.out.println("");
        try {
          if (i == 0) {
            M1.write(evaluations + " " + contributionArchiveCounter_[0]);
            M1.newLine();
            HV.write(evaluations + " " + indicators.getHypervolume(archive));
            HV.newLine();
            C1.write(evaluations + " " + contribution_[0]);
            C1.newLine();
            } else {
            M2.write(evaluations + " " + contributionArchiveCounter_[1]);
            M2.newLine();
            C2.write(evaluations + " " + (1.0  - contribution_[0]));
            C2.newLine();
            }
        } catch (IOException ex) {
          Logger.getLogger(CellDE2.class.getName()).log(Level.SEVERE, null, ex);
        }
      } // for
      //System.out.println("HV: " + indicators.getHypervolume(archive));


      if (iterationsWithMinimumContribution > 80000) {
        System.out.println("500: terminating");
        return archive;
      }
}
    }
if (TRAZA) {
    try {
      M1.close();
      M2.close();
      C1.close();
      C2.close();
      HV.close();
    } catch (IOException ex) {
      Logger.getLogger(CellDE2.class.getName()).log(Level.SEVERE, null, ex);
    }
}
    //System.out.println(evaluations);
    return archive;
  }
} // CellDE2
