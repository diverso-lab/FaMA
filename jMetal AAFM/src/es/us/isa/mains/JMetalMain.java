package es.us.isa.mains;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.metaheuristics.fastPGA.FastPGAAAFM;
import jmetal.metaheuristics.ibea.IBEAAAFM;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.BinaryTournament;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.comparators.FPGAFitnessComparator;
import jmetal.util.comparators.FitnessComparator;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.fama.operations.AAFMProblem;
import es.us.isa.fama.operations.PreferencesConfigurationProblem;
import es.us.isa.fama.operations.PreferencesREconfigurationProblem;
import es.us.isa.fama.solvers.Solver;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;
import es.us.isa.soup.preferences.User;
import es.us.isa.utils.FMUtils;
import es.us.isa.utils.PreferenceUtils;

public class JMetalMain {

	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	public static void main(String[] args) throws JMException, IOException,
			ClassNotFoundException {

		JMetalMain main = new JMetalMain();
		FAMAAttributedFeatureModel model = main.loadModel("./inputs/LeroDaaSIntegers.afm");
//		FAMAAttributedFeatureModel model = main.loadModel("./inputs/LeroAtomicSets.afm");
		Collection<User> prefs = main.loadDefaultPreferences1(model);
		AAFMProblem problem;

		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("PreferencesConfiguration.log");
		logger_.addHandler(fileHandler_);
		QualityIndicator indicators = null;

//		problem = new PreferencesConfigurationProblem(model, prefs, 5);
		problem = new PreferencesConfigurationProblem(model, prefs, 0);
		long initTime = System.currentTimeMillis();
		SolutionSet population = main.executeFastPGA(problem, indicators);
		long estimatedTime = System.currentTimeMillis() - initTime;
		
		Map<Solution,Double> feasibleSols = PreferenceUtils.processSolutionSet(population, prefs);
		//XXX s1 is null, no solution without violations :s
		Solution s1 = main.obtainBestSolution(feasibleSols);
		
		prefs = main.loadDefaultPreferences2(model);
//		problem = new PreferencesConfigurationProblem(model, prefs, 5);
		problem = new PreferencesREconfigurationProblem(model, prefs, s1);
		initTime = System.currentTimeMillis();
		population = main.executeFastPGA(problem, indicators);
		long estimatedTime2 = System.currentTimeMillis() - initTime;
		estimatedTime += estimatedTime2;
		
		Map<Solution,Double> feasibleSols2 = PreferenceUtils.processSolutionSet(population, prefs);
		
		Solution s2 = main.obtainBestSolution(feasibleSols2);
		
		//XXX and evaluate s1 again
		Solution s11 = Solution.getNewSolution(problem);
		Variable[] decisionVariables1 = s1.getDecisionVariables();
		Variable[] decisionVariables11 = new Variable[decisionVariables1.length];
		for (int i = 0; i < decisionVariables1.length; i++){
			decisionVariables11[i] = decisionVariables1[i].deepCopy();
		}
		s11.setDecisionVariables(decisionVariables11);
		problem.evaluate(s11);
		
		Solver s = problem.getSolver();
		
		SolutionSet newSolSet = new SolutionSet(3);
//		newSolSet.add(s1);
		newSolSet.add(s11);
		newSolSet.add(s2);

		// Print the results
		logger_.info("Total execution time: " + estimatedTime + "ms");
		logger_.info("Variables values have been writen to file VAR");
		population.printVariablesToFile("VAR");
		population.printFeasibleVAR("FeasibleVAR");
		logger_.info("Objectives values have been writen to file FUN");
		population.printObjectivesToFile("FUN");
		population.printFeasibleFUN("FeasibleFUN");

		main.printInconsistencies("INCONSISTENCIES.txt", population);
		
//		newSolSet.printObjectivesToFile("ANALYSISSOLS");
		
		//now we have solutions and nash product
		Map<Solution,Double> analysisSols = PreferenceUtils.processSolutionSet(newSolSet, prefs);
		analysisSols.put(s1, feasibleSols.get(s1));
		Set<Entry<Solution,Double>> entries = analysisSols.entrySet();
		
		for (Entry<Solution,Double> e:entries){
			String name = null;
			Solution aux = e.getKey();
			if (aux == s1){
				name = "s1";
			}
			else if (aux == s11){
				name = "s11";
			}
			else{
				name = "s2";
			}
			System.out.println(name);
			ExtendedConfiguration conf = s.solution2Configuration(aux);
			System.out.println(FMUtils.config2String(conf));
			int objSize = prefs.size();
			for (int i = 0; i < objSize; i++){
				System.out.print(aux.getObjective(i)+", ");
			}
			//and the nash product
			System.out.print(e.getValue());
			System.out.println();
		}

	}// main

	public Solution obtainBestSolution(Map<Solution,Double> pairs){
		Set<Entry<Solution,Double>> entries = pairs.entrySet();
		Solution result = null;
		double max = 0;
		for (Entry<Solution,Double> e:entries){
			if (e.getValue() > max){
				max = e.getValue();
				result = e.getKey();
			}
		}
		return result;
	}
	
	public SolutionSet executeIBEA(AAFMProblem problem,
			QualityIndicator indicators) throws JMException,
			ClassNotFoundException {
		IBEAAAFM algorithm = new IBEAAAFM(problem);
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator
		algorithm.setSeeds(problem.getSeeds());

		// Algorithm parameters
		algorithm.setInputParameter("populationSize", 100);
		algorithm.setInputParameter("archiveSize", 100);
		algorithm.setInputParameter("maxEvaluations", 25000);
		
//		algorithm.setInputParameter("populationSize", 100);
//		algorithm.setInputParameter("archiveSize", 100);
//		algorithm.setInputParameter("maxEvaluations", 50000);

		// Mutation and Crossover for Real codification
		HashMap parameters = new HashMap();
		parameters.put("probability", 0.9);
		parameters.put("distributionIndex", 20.0);
		crossover = CrossoverFactory.getCrossoverOperator("IntSBXCrossover",
				parameters);

		parameters = new HashMap();
		parameters.put("probability", 1.0 / problem.getNumberOfVariables());
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("IntPolynomialMutation",
				parameters);

		/* Selection Operator */
		parameters = new HashMap();
		parameters.put("comparator", new FitnessComparator());
		selection = new BinaryTournament(parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		// Execute the Algorithm
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;

		return population;
	}

	public SolutionSet executeFastPGA(AAFMProblem problem, QualityIndicator indicators)
			throws JMException, ClassNotFoundException {
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator
		FastPGAAAFM algorithm = new FastPGAAAFM(problem);
		// XXX setting seeds
		algorithm.setSeeds(problem.getSeeds());

		algorithm.setInputParameter("maxPopSize", 100);
		algorithm.setInputParameter("initialPopulationSize", 100);
		algorithm.setInputParameter("maxEvaluations", 25000);
		// algorithm.setInputParameter("maxPopSize", 200);
		// algorithm.setInputParameter("initialPopulationSize", 100);
		// algorithm.setInputParameter("maxEvaluations", 50000);
		algorithm.setInputParameter("a", 20.0);
		algorithm.setInputParameter("b", 1.0);
		algorithm.setInputParameter("c", 20.0);
		algorithm.setInputParameter("d", 0.0);

		// Parameter "termination"
		// If the preferred stopping criterium is PPR based, termination must
		// be set to 0; otherwise, if the algorithm is intended to iterate until
		// a give number of evaluations is carried out, termination must be set
		// to
		// that number
		algorithm.setInputParameter("termination", 1);

		// Mutation and Crossover for Real codification
		HashMap parameters = new HashMap();
		parameters.put("probability", 0.9);
		parameters.put("distributionIndex", 20.0);
		// crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover",
		// parameters);
		crossover = CrossoverFactory.getCrossoverOperator("IntSBXCrossover",
				parameters);
		// crossover.setParameter("probability",0.9);
		// crossover.setParameter("distributionIndex",20.0);

		parameters = new HashMap();
		parameters.put("probability", 1.0 / problem.getNumberOfVariables());
		parameters.put("distributionIndex", 20.0);
		// mutation = MutationFactory.getMutationOperator("PolynomialMutation",
		// parameters);
		mutation = MutationFactory.getMutationOperator("IntPolynomialMutation",
				parameters);
		// Mutation and Crossover for Binary codification

		parameters = new HashMap();
		parameters.put("comparator", new FPGAFitnessComparator());
		selection = new BinaryTournament(parameters);

		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		SolutionSet population = algorithm.execute();
		return population;
	}

//	public void executeDMOPSOInteger(Problem problem,
//			QualityIndicator indicators) throws JMException,
//			ClassNotFoundException {
//		Algorithm algorithm = new DMOPSOInteger(problem);
//		// Algorithm parameters
//		algorithm.setInputParameter("swarmSize", 100);
//		algorithm.setInputParameter("maxAge", 2);
//		algorithm.setInputParameter("maxIterations", 250);
//		algorithm.setInputParameter("functionType", "_AGG");
//
//		HashMap parameters = new HashMap();
//		// Execute the Algorithm
//		long initTime = System.currentTimeMillis();
//		SolutionSet population = algorithm.execute();
//		long estimatedTime = System.currentTimeMillis() - initTime;
//
//		// Result messages
//		logger_.info("Total execution time: " + estimatedTime + "ms");
//		logger_.info("Objectives values have been writen to file FUN");
//		population.printObjectivesToFile("FUN");
//		logger_.info("Variables values have been writen to file VAR");
//		population.printVariablesToFile("VAR");
//		if (indicators != null) {
//			logger_.info("Quality indicators");
//			logger_.info("Hypervolume: "
//					+ indicators.getHypervolume(population));
//			logger_.info("GD         : " + indicators.getGD(population));
//			logger_.info("IGD        : " + indicators.getIGD(population));
//			logger_.info("Spread     : " + indicators.getSpread(population));
//			logger_.info("Epsilon    : " + indicators.getEpsilon(population));
//		} // if
//	}
//
//	public void executeSMPSO(Problem problem, QualityIndicator indicators)
//			throws JMException, ClassNotFoundException {
//		Algorithm algorithm = new SMPSOInteger(problem);
//
//		// Algorithm parameters
//		algorithm.setInputParameter("swarmSize", 100);
//		algorithm.setInputParameter("archiveSize", 100);
//		algorithm.setInputParameter("maxIterations", 250);
//
//		HashMap parameters = new HashMap();
//		parameters.put("probability", 1.0 / problem.getNumberOfVariables());
//		parameters.put("distributionIndex", 20.0);
//		// Operator mutation =
//		// MutationFactory.getMutationOperator("PolynomialMutation",
//		// parameters);
//		Operator mutation = MutationFactory.getMutationOperator(
//				"IntPolynomialMutation", parameters);
//
//		algorithm.addOperator("mutation", mutation);
//
//		// Execute the Algorithm
//		long initTime = System.currentTimeMillis();
//		SolutionSet population = algorithm.execute();
//		long estimatedTime = System.currentTimeMillis() - initTime;
//
//		// Result messages
//		logger_.info("Total execution time: " + estimatedTime + "ms");
//		logger_.info("Objectives values have been writen to file FUN");
//		population.printObjectivesToFile("FUN");
//		logger_.info("Variables values have been writen to file VAR");
//		population.printVariablesToFile("VAR");
//
//		if (indicators != null) {
//			logger_.info("Quality indicators");
//			logger_.info("Hypervolume: "
//					+ indicators.getHypervolume(population));
//			logger_.info("GD         : " + indicators.getGD(population));
//			logger_.info("IGD        : " + indicators.getIGD(population));
//			logger_.info("Spread     : " + indicators.getSpread(population));
//			logger_.info("Epsilon    : " + indicators.getEpsilon(population));
//		} // if
//	}

	public FAMAAttributedFeatureModel loadModel(String path) {
		AttributedReader reader = new AttributedReader();
		FAMAAttributedFeatureModel fm = null;
		try {
			fm = (FAMAAttributedFeatureModel) reader.parseFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// QuestionTrader qt = new QuestionTrader();
		// FAMAAttributedFeatureModel fm = (FAMAAttributedFeatureModel)
		// qt.openFile(path);
		return fm;
	}

	public List<User> loadDefaultPreferences1(FAMAAttributedFeatureModel fm) {
		List<User> result = new LinkedList<User>();

		// XXX add the other 2 tenants
		Collection<Preference> prefs = new LinkedList<Preference>();
		Preference p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		Preference p2 = new LikesPreference(
				fm.searchFeatureByName("OfficeUpdt"));
		Preference p3 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Backup.period"), 2.0);
		Preference p4 = new LowestPreference(FMUtils.searchAttribute(fm,
				"Antivirus.frequency"));
		Preference p5 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Firewall.level"), 2.0);
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u1 = new User(prefs, "Tenant 1");
		u1.setWeight(45);

		prefs = new LinkedList<Preference>();
		p1 = new DislikesPreference(fm.searchFeatureByName("Classic"));
		p2 = new LikesPreference(fm.searchFeatureByName("Indexing"));
		p3 = new LikesPreference(fm.searchFeatureByName("Defragmenter"));
		p4 = new LowestPreference(FMUtils.searchAttribute(fm, "Firewall.level"));
//		p5 = new DislikesPreference(fm.searchFeatureByName("Classic"));
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
//		prefs.add(p5);
		User u2 = new User(prefs, "Tenant 2");
		u2.setWeight(60);

		prefs = new LinkedList<Preference>();
//		p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		p2 = new AroundPreference(FMUtils.searchAttribute(fm,
				"OfficeUpdt.period"), 2.0);
//		p3 = new AroundPreference(FMUtils.searchAttribute(fm, "Backup.period"),
//				2.0);
		p4 = new HighestPreference(FMUtils.searchAttribute(fm,
				"Antivirus.frequency"));
		p5 = new HighestPreference(FMUtils.searchAttribute(fm, "Firewall.level"));
//		prefs.add(p1);
		prefs.add(p2);
//		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u3 = new User(prefs, "Tenant 3");
		u3.setWeight(31);

//		prefs = new LinkedList<Preference>();
//		p1 = new LikesPreference(fm.searchFeatureByName("Classic"));
//		p2 = new AroundPreference(FMUtils.searchAttribute(fm,
//				"LatexUpdt.period"), 3.0);
//		p3 = new AroundPreference(FMUtils.searchAttribute(fm,
//				"OfficeUpdt.period"), 3.0);
//		p4 = new AroundPreference(FMUtils.searchAttribute(fm,
//				"Antivirus.frequency"), 3.0);
//		p5 = new HighestPreference(FMUtils.searchAttribute(fm, "Firewall.level"));
////		p5 = new AroundPreference(FMUtils.searchAttribute(fm, "Firewall.level"),
////				2.0);
//		prefs.add(p1);
//		prefs.add(p2);
//		prefs.add(p3);
//		prefs.add(p4);
//		prefs.add(p5);
//		User u4 = new User(prefs, "Tenant 4");

		result.add(u1);
		result.add(u2);
		result.add(u3);
//		result.add(u4);

		return result;

	}
	
	
	public List<User> loadDefaultPreferences2(FAMAAttributedFeatureModel fm) {
		List<User> result = new LinkedList<User>();

		// XXX add the other 2 tenants
		Collection<Preference> prefs = new LinkedList<Preference>();
		Preference p1 = new LikesPreference(fm.searchFeatureByName("Aero"));
		Preference p2 = new LikesPreference(
				fm.searchFeatureByName("OfficeUpdt"));
		Preference p3 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Backup.period"), 2.0);
		Preference p4 = new LowestPreference(FMUtils.searchAttribute(fm,
				"Antivirus.frequency"));
		Preference p5 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Firewall.level"), 2.0);
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u1 = new User(prefs, "Tenant 1");
		u1.setWeight(49);

		prefs = new LinkedList<Preference>();
		p2 = new LikesPreference(fm.searchFeatureByName("Indexing"));
		p3 = new LikesPreference(fm.searchFeatureByName("Defragmenter"));
		p4 = new AroundPreference(FMUtils.searchAttribute(fm, "Firewall.level"),2.0);
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		User u2 = new User(prefs, "Tenant 2");
		u2.setWeight(53);

		prefs = new LinkedList<Preference>();
		p2 = new AroundPreference(FMUtils.searchAttribute(fm,
				"OfficeUpdt.period"), 2.0);
		p3 = new AroundPreference(FMUtils.searchAttribute(fm, "Backup.period"),
				2.0);
		p4 = new HighestPreference(FMUtils.searchAttribute(fm,
				"Antivirus.frequency"));
		p5 = new LikesPreference(fm.searchFeatureByName("Defragmenter"));
		prefs.add(p2);
		prefs.add(p3);
		prefs.add(p4);
		prefs.add(p5);
		User u3 = new User(prefs, "Tenant 3");
		u3.setWeight(40);

		prefs = new LinkedList<Preference>();
		p1 = new LikesPreference(fm.searchFeatureByName("Classic"));
		p2 = new AroundPreference(FMUtils.searchAttribute(fm,
				"JavaUpdt.period"), 3.0);
		p4 = new AroundPreference(FMUtils.searchAttribute(fm,
				"Antivirus.frequency"), 3.0);
		p5 = new HighestPreference(FMUtils.searchAttribute(fm, "Firewall.level"));
		prefs.add(p1);
		prefs.add(p2);
		prefs.add(p4);
		prefs.add(p5);
		User u4 = new User(prefs, "Tenant 4");
		u4.setWeight(23);

		result.add(u1);
		result.add(u2);
		result.add(u3);
		result.add(u4);

		return result;

	}

	public void printInconsistencies(String path, SolutionSet sols) {
		try {
			PrintWriter writer = new PrintWriter(path);
			Iterator<Solution> it = sols.iterator();
			while (it.hasNext()) {
				Solution s = it.next();
				writer.write("" + s.getOverallConstraintViolation() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
