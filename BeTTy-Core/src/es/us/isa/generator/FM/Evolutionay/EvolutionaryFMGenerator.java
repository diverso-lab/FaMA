/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.generator.FM.Evolutionay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMStatistics;
import es.us.isa.utils.InverseFitnessFunction;

/**
 * A novel evolutionary algorithm for solving optimization problems on feature
 * models. The algorithm takes several size parameters and an objective function
 * as input and returns a feature model of the given size maximizing or
 * minimizing the optimization criteria defined by the function.
 */
public class EvolutionaryFMGenerator extends AbstractFMGenerator {
	public SortedMap<Double, List<Chromosome>> population;

	/**
	 * Cross over probability, 70% by default
	 */
	private float crossoverProb;

	/**
	 * Mutation probability, 1% by default
	 */
	private float mutationProb;

	/**
	 * Population size, 200 by default
	 */
	private int populationSize;

	/**
	 * Number of extra chromosomes generated on each generation to replace wrong
	 * chromosomes. 0 by default (repairing algorithm used)
	 */
	private int extraChromosomes;

	/**
	 * Stop criteria. Maximum number of populations without improvement. 25 by default.
	 */
	private int maxIterations; // 

	/**
	 * Maximum number of generations. 25 by default
	 */
	private int maxGenerations;

	/**
	 * Fitness to beat (optional stop criteria)
	 */
	private double fitnessToBeat;

	/**
	 * Improvement factor for stop criteria (i.e. stop when fitness obtained is
	 * n times better than the fitnessToBeat)
	 */
	private int improvementFactor;

	/**
	 * Random generator for the initial population
	 */
	private AbstractFMGenerator generator; // 

	/**
	 * Choose Between Roulete Wheel Selection (true) or Tournament (false)
	 */
	private boolean isRolueteSelection;

	/**
	 * Number of opponents per tournament
	 */
	private int opponentsPerTournament;

	/**
	 * Variable to sum the fitnesses and then save the average on each
	 * generation
	 */
	double fitnesses;

	/**
	 * 
	 */
	int individuals;

	/**
	 * Execution times
	 */
	private int executionTimes;

	/**
	 * Generation in which the best fitness is found
	 */
	private int bestFitnessGeneration;

	/**
	 * Number of generations
	 */
	private int generations;

	/**
	 * Maximum global fitness
	 */
	private double bestFitness;

	/**
	 * Best solution
	 */
	private Chromosome solution;

	/**
	 * Save the average fitness of each generation
	 */
	List<Double> AvFitnesses;

	/**
	 * 
	 */
	private boolean maximize;

	/**
	 * 
	 */
	FitnessFunction ff;

	/**
	 * 
	 */
	Chromosome chr;

	public EvolutionaryFMGenerator(float crossoverProb, float mutationProb,
			int populationSize, int extraChromosomes, int maxIterations,
			int maxGenerations, double fitnessToBeat, int improvementFactor,
			AbstractFMGenerator generator, boolean isRolueteSelection,
			int opponentsPerTournament) throws BettyException {
		super();
		this.setCrossoverProb(crossoverProb);
		this.setMutationProb(mutationProb);
		this.setPopulationSize(populationSize); // Default: 200
		this.setExtraChromosomes(extraChromosomes);
		this.setMaxIterations(maxIterations); // Default: 25
		this.setMaxGenerations(maxGenerations); // Default: 25
		this.setfitnessToBeat(fitnessToBeat);
		this.setImprovementFactor(improvementFactor);
		this.setGenerator(generator);
		this.setRolueteSelection(isRolueteSelection);
		this.setOpponentsPerTournament(opponentsPerTournament);

		// Initialize variables
		population = new TreeMap<Double, List<Chromosome>>();
		fitnesses = 0;
		individuals = 0;
		executionTimes = 0;
		bestFitnessGeneration = 0;
		generations = 0;
		solution = null;
		AvFitnesses = new ArrayList<Double>();
		maximize = true;
		generator = new FMGenerator();
	}

	/**
	 * Constructor using defaults parameters
	 * 
	 * @throws BettyException
	 */
	public EvolutionaryFMGenerator() throws BettyException {
		this(0.7f, 0.01f, 200, 0, 25, 25, 0, -1, new FMGenerator(), true, 2);
	};

	@Override
	public VariabilityModel generateFM(Characteristics ch)
			throws BettyException {

		/**
		 * Number of iterations without improvement
		 */
		int iterations = 0;
		this.generations = 1;

		/**
		 * Variable to sum the fitnesses and then save the average on each
		 * generation
		 */
		fitnesses = 0;

		/**
		 * Reset statistics
		 */

		bestFitness = Integer.MIN_VALUE;

		// INITIAL POPULATION: GENERATION AND EVALUATION
		for (int i = 0; i < populationSize; i++) {
			super.resetGenerator(ch);
			generator.resetGenerator(ch);

			// Generation
			FAMAFeatureModel model = (FAMAFeatureModel) generator
					.generateFM(ch);
			((GeneratorCharacteristics) ch)
					.setSeed(((GeneratorCharacteristics) ch).getSeed() + 10);
			FMStatistics stads = new FMStatistics(model);
			System.out.println(stads);

			// Evaluation
			double fitness = ff.fitness(model);
			fitnesses += fitness;
			Chromosome chromosome = null;

			// Save chromosome
			if (chr == null) {
				chromosome = new Chromosome(this.characteristics.getSeed(),this.characteristics);
			} else {
				try {
					chromosome = chr.getClass().newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			chromosome.encode(model);
			chromosome.setFitnessNumber(fitness);

			this.insertElement(fitness, chromosome);
			System.out.println("Fitness: " + fitness);

			// Check if we have a new maximum
			if (fitness > bestFitness && maximize)
				bestFitness = fitness;

			// Increment the number of executions
			executionTimes++;
		}

		// Print information of current generation
		System.out.println("---------------- GENERATION 1 -------------------");
		System.out.println("Max fitness: " + bestFitness);

		// Update statistics
		this.bestFitnessGeneration = 1;
		this.solution = population.get(population.lastKey()).get(0).clone();
		this.AvFitnesses.add(fitnesses / populationSize);

		while ((iterations < maxIterations) && !stopCriteria()) // Stop
		// Criteria
		{

			generations++;

			// NEW GENERATION
			List<Chromosome> chromosomes = new LinkedList<Chromosome>();

			// Sum the fitness of the whole population (rank fitness)
			int sumFitnesses = 0;
			for (int i = 1; i <= population.size(); i++)
				sumFitnesses += i;

			// REPRODUCTION
			while (chromosomes.size() != populationSize + extraChromosomes) {

				// SELECTION
				Chromosome mum = null;
				Chromosome dad = null;
				if (isRolueteSelection) {
					mum = rouletteWheelSelection(sumFitnesses);
					dad = rouletteWheelSelection(sumFitnesses);
					while (mum == dad)
						// We make sure that mum and dad are different
						// chromosomes
						dad = rouletteWheelSelection(sumFitnesses);
				} else {
					mum = tournamentSelection(opponentsPerTournament);
					dad = tournamentSelection(opponentsPerTournament);
					while (mum == dad)
						// We make sure that mum and dad are different
						// chromosomes
						dad = tournamentSelection(opponentsPerTournament);
				}

				// CROSSOVER
				if (random.nextFloat() <= crossoverProb)
					crossover1P(mum, dad, chromosomes);
				// discreteRecombination(mum,dad,chromosomes);
				else {
					chromosomes.add(mum.clone());
					chromosomes.add(dad.clone());
				}
			}

			// MUTATION
			Iterator<Chromosome> itc = chromosomes.iterator();
			while (itc.hasNext())
				mutation(itc.next(), (GeneratorCharacteristics) ch);

			// EVALUATION
			population.clear();
			itc = chromosomes.iterator();
			double bestLocalFitness;
			bestLocalFitness = Double.NEGATIVE_INFINITY;

			individuals = 0;
			fitnesses = 0; // Variable to compute average fitness on each
			// generation
			while (individuals < populationSize && itc.hasNext()
					&& !stopCriteriaFitness(bestLocalFitness)) {
				Chromosome chromosome = itc.next();

				double fitness = evaluate(chromosome);

				if (fitness == -1) {
					System.err.println("Time out exceeded!");
					return null;
				}

				if (fitness != Double.NaN) {
					fitnesses += fitness;
					chromosome.setFitnessNumber(fitness);
					insertElement(fitness, chromosome);
					System.out.println("Fitness: " + fitness);

					if ((fitness > bestLocalFitness)){
					bestLocalFitness = fitness;
					}
					// Increment the number of executions
					executionTimes++;
					individuals++;
				}
			}

			// Check if we have a new maximum
			if (bestLocalFitness > bestFitness){
				this.bestFitness = bestLocalFitness;
				iterations = 0;
				this.bestFitnessGeneration = generations;
				this.solution = population.get(population.lastKey()).get(0).clone();
				System.out.println("New best fitness: " + this.bestFitness + " in generation " + this.bestFitnessGeneration);
			} else {
				iterations++;

				// Print information of current generation
				System.out.println("---------------- GENERATION " + generations
						+ " -------------------");
				System.out.println("Best fitness in this generation: " + bestLocalFitness);
				System.out.println("Best fitness until now: " + bestFitness);

				// Save average fitness for this generation
				this.AvFitnesses.add(fitnesses / populationSize);
			}
		}

		// Print statistics

		return this.solution.decode();
	}

	/**
	 * In this methods, random changes are applied to the chromosomes to prevent
	 * the algorithm from getting stuck prematurely at a locally optimal
	 * solution. Mutation operators must be specifically designed for the type
	 * of encoding used.
	 * 
	 * @param c
	 *            The chromosome
	 * @param chars
	 *            The characteristics of the generator
	 */
	public void mutation(Chromosome c, GeneratorCharacteristics chars) {

		int nFeatures = c.getTree().size();
		int nConstraints = c.getCTC().size();

		// Tree mutation
		for (int i = 0; i < nFeatures; i++) {
			if (random.nextFloat() <= mutationProb)
				if (random.nextBoolean())
					mutantOperator1(c.getTree(), i);
				else
					mutantOperator2(c.getTree(), i, (int) chars
							.getMaxBranchingFactor());
		}

		// CTC mutation
		for (int i = 0; i < nConstraints; i++) {
			if (random.nextFloat() <= mutationProb)
				if (random.nextBoolean())
					mutantOperator3(c.getCTC(), i);
				else
					mutantOperator4(c.getCTC(), i, nFeatures);
		}
	}

	/**
	 * Operator 1. It changes randomly the type of a relationship in the tree
	 * array, e.g. from mandatory, [M, 3] , to optional,[ O, 3 ].
	 * 
	 * @param tree
	 *            The tree containing the relationship
	 * @param position
	 *            The position to do the change
	 */
	public void mutantOperator1(List<String[]> tree, int position) {

		String[] gene = tree.get(position);

		int relation = random.nextInt(4);
		switch (relation) {
		case 0: // Mandatory
			gene[0] = "M";
			break;
		case 1: // Optional
			gene[0] = "O";
			break;
		case 2: // Or
			gene[0] = "Or";
			break;
		case 3: // Alternative
			gene[0] = "Alt";
			break;
		}
	}

	/**
	 * It changes randomly the number of children of a feature in the tree, e.g.
	 * from [ M, 3 ] to [ M, 5 ]. The new number of children is the range [0, BF
	 * ] where BF is the maximum branching factor indicated as input.
	 * 
	 * The tree containing the children The position to do the change
	 * 
	 * @param maxChildren
	 *            The new max number of Children
	 */
	public void mutantOperator2(List<String[]> tree, int position,
			int maxChildren) {

		String[] gene = tree.get(position);
		int numberOfChildren = random.nextInt(maxChildren + 1);

		while (gene[1].equalsIgnoreCase(Integer.toString(numberOfChildren)))
			numberOfChildren = random.nextInt(maxChildren + 1);

		gene[1] = Integer.toString(numberOfChildren);
	}

	/**
	 * Operator 3. It changes the type of a cross-tree constraint in the CTC
	 * array, e.g. from excludes [ E, 3, 6 ] to requires [ R, 3, 6 ].
	 * 
	 * @param ctc
	 *            The ctc to be changed
	 * @param position
	 *            The position where it is
	 */
	public void mutantOperator3(List<String[]> ctc, int position) {

		String[] gene = ctc.get(position);
		if (gene[0].equalsIgnoreCase("R"))
			gene[0] = "E";
		else
			gene[0] = "R";
	}

	/**
	 * Operator 4. It changes randomly (with equal probability) the origin or
	 * destination feature of a constraint in the CTC array, e.g. from [ E, 3, 6
	 * ] to requires [ E, 1, 6 ]. Origin and destination features are ensured to
	 * be different.
	 * 
	 * @param ctc
	 *            The ctc to be changed
	 * @param position
	 *            The position to do the change
	 * @param numberOfFeatures
	 *            the number of features of the model
	 */
	public void mutantOperator4(List<String[]> ctc, int position,
			int numberOfFeatures) {

		String[] gene = ctc.get(position);

		if (random.nextBoolean()) // Move origin
			gene[1] = Integer.toString(random.nextInt(numberOfFeatures - 1) + 1);
		else
			// Move destination
			gene[2] = Integer.toString(random.nextInt(numberOfFeatures - 1) + 1);

		while (gene[1].equalsIgnoreCase(gene[2]))
			gene[2] = Integer.toString(random.nextInt(numberOfFeatures - 1) + 1);
	}

	/**
	 * Insert a chromosome into the population
	 * 
	 * @param key
	 *            Check if the populaition contains the chromosome
	 * @param chromosome
	 *            the chromosome
	 */
	private void insertElement(double key, Chromosome chromosome) {
		if (!population.containsKey(key)) {
			List<Chromosome> chromosomeList = new LinkedList<Chromosome>();
			chromosomeList.add(chromosome);
			population.put(key, chromosomeList);
		} else
			population.get(key).add(chromosome);
	}

	/**
	 * Print population in a readable format
	 * 
	 * @return The String in a readable format
	 */
	public String printPopulation() {
		String result = "";
		int i = 1;

		Iterator<Double> it = population.keySet().iterator();
		while (it.hasNext()) {
			double fitness = it.next();
			Iterator<Chromosome> itc = population.get(fitness).iterator();
			while (itc.hasNext()) {
				Chromosome c = itc.next();
				System.out.println(i++ + ". Fitness: " + fitness);
				c.toString();
			}
		}
		return result;
	}

	/**
	 * Determines how the individuals of one generation are selected to be
	 * combined and produce new offspring. Roulette Wheel Selection (with rank
	 * based selection).
	 * 
	 * @param sumFitnesses
	 *            The fitnesses of the retrieved population
	 * @return A chromosome with updated info
	 */
	private Chromosome rouletteWheelSelection(int sumFitnesses) {

		int r, s = 0, i = 1;
		boolean selected = false;
		Chromosome c = null;

		r = random.nextInt(sumFitnesses);
		// System.out.print("Size: " + population.size() + " Sum Fitnesses: " +
		// sumFitnesses + " r=" + r);
		Iterator<Double> it = population.keySet().iterator();
		while (it.hasNext() && !selected) {
			double fitness = it.next();
			s += i;
			if (s > r) {
				List<Chromosome> chromosomes = population.get(fitness);
				int position = random.nextInt(chromosomes.size());
				// System.out.println(" Position:" + position);
				c = chromosomes.get(position);
				selected = true;
			}
			i++;
		}

		return c;
	}

	/**
	 * Determines how the individuals of one generation are selected to be
	 * combined and produce new offspring. Tournament Selection
	 * 
	 * @param numberOfOpponents
	 *            The number of opponents used in the tournament
	 * @return A chromosome with updated info
	 */
	public Chromosome tournamentSelection(int numberOfOpponents) {
		Chromosome result = null;
		// List<Chromosome> opponents = new
		// ArrayList<Chromosome>(numberOfOpponents);
		int index = 0;
		int candidateIndex = 0;

		/**
		 * Select the index of the chosen chromosme: As we use a sorted Map an
		 * it is a maximization problem we chose the solution with a smaller
		 * index.
		 */
		for (int i = 0; i < numberOfOpponents; i++) {
			candidateIndex = random.nextInt(populationSize - 1);
			if (candidateIndex >= index)
				index = candidateIndex;
		}
		/**
		 * Get the chosen chromosme from population map:
		 */
		int currentIndex = 0;
		double fitness = 0;
		List<Chromosome> chromosomes = null;
		Iterator<Double> it = population.keySet().iterator();
		while (it.hasNext() && currentIndex <= index) {
			fitness = it.next();
			chromosomes = population.get(fitness);
			/**
			 * If the chosen chromosome is not in this list we go to to next
			 * list.
			 */
			if (currentIndex + chromosomes.size() <= index)
				currentIndex += chromosomes.size();
			else {
				/**
				 * The chosen chormosome is in this list, so we get it:
				 */
				result = chromosomes.get(index - currentIndex);
				currentIndex += chromosomes.size();
			}
		}
		return result;
	}

	/**
	 * These are the techniques used to combine chromosomes in some way and
	 * produce new individuals in an analogous way to biological reproduction.
	 * Single point crossover
	 */
	public void crossover1P(Chromosome mum, Chromosome dad,
			List<Chromosome> population) {

		int numFeatures = mum.getTree().size();
		int numCTC = mum.getCTC().size();

		/**
		 * TREE CROSSOVER
		 */
		List<String[]> tree1 = new LinkedList<String[]>();
		List<String[]> tree2 = new LinkedList<String[]>();
		int position = random.nextInt(numFeatures);

		for (int i = 0; i < numFeatures; i++) {
			if (i <= position) {
				tree1.add(i, ((String[]) mum.getTree().get(i)).clone());
				tree2.add(i, ((String[]) dad.getTree().get(i)).clone());
			} else {
				tree1.add(i, ((String[]) dad.getTree().get(i)).clone());
				tree2.add(i, ((String[]) mum.getTree().get(i)).clone());
			}
		}

		// CTC CROSSOVER
		List<String[]> ctc1 = new LinkedList<String[]>();
		List<String[]> ctc2 = new LinkedList<String[]>();
		if(numCTC>0){
		position = random.nextInt(numCTC);
		for (int i = 0; i < numCTC; i++) {
			if (i <= position) {
				ctc1.add(i, ((String[]) mum.getCTC().get(i)).clone());
				ctc2.add(i, ((String[]) dad.getCTC().get(i)).clone());
			} else {
				ctc1.add(i, ((String[]) dad.getCTC().get(i)).clone());
				ctc2.add(i, ((String[]) mum.getCTC().get(i)).clone());
			}
		}
		}
		// System.out.println("Crossover position: " + position);
		population.add(new Chromosome(tree1, ctc1, this.characteristics.getSeed(),this.characteristics));
		population.add(new Chromosome(tree2, ctc2, this.characteristics.getSeed(),this.characteristics));
	}

	public double evaluate(Chromosome chromosome) {
		FAMAFeatureModel fm = chromosome.decode();
		double fitness = Double.NaN;
		if (fm != null)
			fitness = ff.fitness(fm);
		return fitness;
	}

	/*
	 * ================================================== STOP CRITERIA
	 * ===================================================
	 */

	/**
	 * Global stop criteria for the genetic algorithm
	 */
	private boolean stopCriteria() {
		if ((this.generations < this.maxGenerations)
				&& ((improvementFactor == -1) || ( (bestFitness < improvementFactor
						* fitnessToBeat)))) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Local stop criteria for the genetic algorithm
	 */
	private boolean stopCriteriaFitness(double fitness) {
		if ((improvementFactor == -1)
				|| ((fitness < (improvementFactor * fitnessToBeat)))) {
			return false;
		} else {
			return true;
		}
	}

	// Setters
	public void setGenerator(AbstractFMGenerator generator)
			throws BettyException {
		if (generator == null) {
			throw new BettyException();
		} else {
			this.generator = generator;
		}
	}

	public void setFitnessFunction(FitnessFunction ff) throws BettyException {
		if (ff != null) {
			this.ff = ff;
			if (!maximize) {
				InverseFitnessFunction iff = new InverseFitnessFunction();
				iff.setFitnessFunction(ff);
				this.ff = iff;
			}
		} else {
			throw new BettyException();
		}
	}

	public void setCrossoverProb(float crossoverProb) throws BettyException {
		if (crossoverProb > 0) {
			this.crossoverProb = crossoverProb;
		} else {
			throw new BettyException();
		}
	}

	public void setMutationProb(float mutationProb) throws BettyException {
		if (!(mutationProb < 0)) {
			this.mutationProb = mutationProb;
		} else {
			throw new BettyException();
		}
	}

	public void setPopulationSize(int populationSize) throws BettyException {
		if (populationSize > 0) {
			this.populationSize = populationSize;
		} else {
			throw new BettyException();
		}
	}

	public void setExtraChromosomes(int extraChromosomes) throws BettyException {
		if (!(extraChromosomes < 0)) {
			this.extraChromosomes = extraChromosomes;
		} else {
			throw new BettyException();
		}
	}

	public void setMaxIterations(int maxIterations) throws BettyException {
		if (!(maxIterations <= 0)) {
			this.maxIterations = maxIterations;
		} else {
			throw new BettyException();
		}
	}

	public void setMaxGenerations(int maxGenerations) throws BettyException {
		if (maxGenerations > 0) {
			this.maxGenerations = maxGenerations;
		} else {
			throw new BettyException();
		}
	}

	public void setfitnessToBeat(double fitnessToBeat) {
		this.fitnessToBeat = fitnessToBeat;
	}

	public void setImprovementFactor(int improvementFactor) {
		this.improvementFactor = improvementFactor;
	}

	public void setRolueteSelection(boolean isRolueteSelection) {
		this.isRolueteSelection = isRolueteSelection;
	}

	public void setOpponentsPerTournament(int opponentsPerTournament)
			throws BettyException {
		if (opponentsPerTournament > 0) {
			this.opponentsPerTournament = opponentsPerTournament;
		} else {
			throw new BettyException();
		}
	}

	public void setMaximize(boolean max) {
		this.maximize = max;
		if (!max) {
			if (!(ff instanceof InverseFitnessFunction)) {
				InverseFitnessFunction iff = new InverseFitnessFunction();
				iff.setFitnessFunction(ff);
				this.ff = iff;
			}
		}
	}

	// Getters

	public int getBestFitnessGeneration() {
		return bestFitnessGeneration;
	}

	public void setBestFitnessGeneration(int bestFitnessGeneration) {
		this.bestFitnessGeneration = bestFitnessGeneration;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}

	public List<Double> getAvFitnesses() {
		return AvFitnesses;
	}

	public void setAvFitnesses(List<Double> avFitnesses) {
		AvFitnesses = avFitnesses;
	}

	public Chromosome getChr() {
		return chr;
	}

	public void setChr(Chromosome chr) {
		this.chr = chr;
	}

}
