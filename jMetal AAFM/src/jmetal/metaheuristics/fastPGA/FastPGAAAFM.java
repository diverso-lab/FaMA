package jmetal.metaheuristics.fastPGA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import es.us.isa.fama.operations.AlgorithmWithSeeds;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Distance;
import jmetal.util.FPGAFitness;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.FPGAFitnessComparator;

public class FastPGAAAFM extends AlgorithmWithSeeds {

	// private Collection<Solution> seeds;
	/**
	 * Constructor Creates a new instance of FastPGA
	 */
	public FastPGAAAFM(Problem problem) {
		super(problem);
		
		// this.seeds = new LinkedList<Solution>();
	} // FastPGA

	/**
	 * Runs of the FastPGA algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int maxPopSize, populationSize, offSpringSize, evaluations, maxEvaluations, initialPopulationSize;
		SolutionSet solutionSet, offSpringSolutionSet, candidateSolutionSet = null;
		double a, b, c, d;
		Operator crossover, mutation, selection;
		int termination;
		Distance distance = new Distance();
		Comparator fpgaFitnessComparator = new FPGAFitnessComparator();

		// Read the parameters
		maxPopSize = ((Integer) getInputParameter("maxPopSize")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations"))
				.intValue();
		initialPopulationSize = ((Integer) getInputParameter("initialPopulationSize"))
				.intValue();
		termination = ((Integer) getInputParameter("termination")).intValue();

		// Read the operators
		crossover = (Operator) operators_.get("crossover");
		mutation = (Operator) operators_.get("mutation");
		selection = (Operator) operators_.get("selection");

		// Read the params
		a = ((Double) getInputParameter("a")).doubleValue();
		b = ((Double) getInputParameter("b")).doubleValue();
		c = ((Double) getInputParameter("c")).doubleValue();
		d = ((Double) getInputParameter("d")).doubleValue();

		// Initialize populationSize and offSpringSize
		evaluations = 0;
		populationSize = initialPopulationSize;
		offSpringSize = maxPopSize;

		// in this solution set, we just store valid solutions (no constraints
		// violations)
		SolutionSet validSolutionSet = new SolutionSet(populationSize);

		// Build a solution set randomly
		solutionSet = new SolutionSet(populationSize);

		// -> Create the initial solutionSet and populate with seeds if we have
		// them
		int seedsSize = seeds.size();
		for (Solution aux : seeds) {
			Solution s = this.processSeed(aux);
			this.evaluate(s);
			evaluations++;
			solutionSet.add(s);
			if (s.getOverallConstraintViolation() == 0) {
				validSolutionSet.add(s);
			}
		}

		for (int i = seedsSize; i < populationSize; i++) {
			Solution solution = new Solution(problem_);
			this.evaluate(solution);
			evaluations++;
			solutionSet.add(solution);
			// if (solution.getOverallConstraintViolation() == 0){
			// validSolutionSet.add(solution);
			// }
		}

		// Begin the iterations
		Solution[] parents = new Solution[2];
		Solution[] offSprings;
		boolean stop = false;
		int reachesMaxNonDominated = 0;
		while (!stop) {

			// Create the candidate solutionSet
			offSpringSolutionSet = new SolutionSet(offSpringSize);
			for (int i = 0; i < offSpringSize / 2; i++) {
				parents[0] = (Solution) selection.execute(solutionSet);
				parents[1] = (Solution) selection.execute(solutionSet);
				offSprings = (Solution[]) crossover.execute(parents);
				mutation.execute(offSprings[0]);
				mutation.execute(offSprings[1]);
				this.evaluate(offSprings[0]);
				// problem_.evaluate(offSprings[0]);
				// problem_.evaluateConstraints(offSprings[0]);
				evaluations++;
				this.evaluate(offSprings[1]);
				// problem_.evaluate(offSprings[1]);
				// problem_.evaluateConstraints(offSprings[1]);
				evaluations++;
				offSpringSolutionSet.add(offSprings[0]);
				offSpringSolutionSet.add(offSprings[1]);
			}

			// Merge the populations
			candidateSolutionSet = solutionSet.union(offSpringSolutionSet);

			// Rank
			Ranking ranking = new Ranking(candidateSolutionSet);
			distance.crowdingDistanceAssignment(ranking.getSubfront(0),
					problem_.getNumberOfObjectives());
			FPGAFitness fitness = new FPGAFitness(candidateSolutionSet,
					problem_);
			fitness.fitnessAssign();

			// Count the non-dominated solutions in candidateSolutionSet
			int count = ranking.getSubfront(0).size();

			// Regulate
			populationSize = (int) Math.min(a + Math.floor(b * count),
					maxPopSize);
			offSpringSize = (int) Math.min(c + Math.floor(d * count),
					maxPopSize);

			candidateSolutionSet.sort(fpgaFitnessComparator);
			solutionSet = new SolutionSet(populationSize);

			for (int i = 0; i < populationSize; i++) {
				solutionSet.add(candidateSolutionSet.get(i));
			}

			// just keep the valid solutions
			validSolutionSet = updateValidSolutionSet(validSolutionSet,
					offSpringSolutionSet, fpgaFitnessComparator, a, b,
					maxPopSize);

			// Termination test
			if (termination == 0) {
				ranking = new Ranking(solutionSet);
				count = ranking.getSubfront(0).size();
				if (count == maxPopSize) {
					if (reachesMaxNonDominated == 0) {
						reachesMaxNonDominated = evaluations;
					}
					if (evaluations - reachesMaxNonDominated >= maxEvaluations) {
						stop = true;
					}
				} else {
					reachesMaxNonDominated = 0;
				}
			} else {
				if (evaluations >= maxEvaluations) {
					stop = true;
				}
			}
		}

		setOutputParameter("evaluations", evaluations);

		//XXX now we return just the valid solution set
		Ranking ranking = new Ranking(validSolutionSet);
		return ranking.getSubfront(0);
		
//		Ranking ranking = new Ranking(solutionSet);
//		return ranking.getSubfront(0);
	} // execute

	// TODO be careful. maybe you're copying the same results all the times
	private SolutionSet updateValidSolutionSet(SolutionSet validSolutionSet,
			SolutionSet workingSet, Comparator fpgaFitnessComparator, double a,
			double b, int maxPopSize) {

		Collection<Solution> validOnes = new ArrayList<Solution>();
		int size = workingSet.size();
		for (int i = 0; i < size; i++) {
			Solution s = workingSet.get(i);
			if (s.getOverallConstraintViolation() == 0) {
				validOnes.add(s);
			}
		}
		
		int validOnesSize = validOnes.size();
		if (validOnesSize > 0){
			// create a solution set with the new valid solutions
			SolutionSet candidateSolutionSet = new SolutionSet(validOnesSize);
			for (Solution s : validOnes) {
				candidateSolutionSet.add(s);
			}

			// rank and keep the best solutions
			// Merge the populations
			candidateSolutionSet = validSolutionSet.union(candidateSolutionSet);

			// Rank
			Ranking ranking = new Ranking(candidateSolutionSet);
			Distance distance = new Distance();
			distance.crowdingDistanceAssignment(ranking.getSubfront(0),
					problem_.getNumberOfObjectives());
			FPGAFitness fitness = new FPGAFitness(candidateSolutionSet, problem_);
			fitness.fitnessAssign();

			// Count the non-dominated solutions in candidateSolutionSet
			int count = ranking.getSubfront(0).size();

			// Regulate
			int populationSize = (int) Math.min(a + Math.floor(b * count),
					maxPopSize);
			// offSpringSize = (int)Math.min(c + Math.floor(d * count),maxPopSize);

			candidateSolutionSet.sort(fpgaFitnessComparator);
			validSolutionSet = new SolutionSet(populationSize);

			int minSize = populationSize;
			if (minSize > candidateSolutionSet.size()){
				minSize = candidateSolutionSet.size();
			}
			
			for (int i = 0; i < minSize; i++) {
				validSolutionSet.add(candidateSolutionSet.get(i));
			}
		}
		
		return validSolutionSet;
	}


}
