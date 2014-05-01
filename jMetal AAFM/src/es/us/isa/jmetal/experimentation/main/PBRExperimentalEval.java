package es.us.isa.jmetal.experimentation.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.fama.csv.CSVExperimentWriter;
import es.us.isa.fama.experiments.generators.AbstractPreferencesREconfigAssignMethod;
import es.us.isa.fama.experiments.generators.ScenarioREconfigAssignMethod;
import es.us.isa.fama.operations.AAFMProblem;
import es.us.isa.fama.operations.PreferencesConfigurationProblem;
import es.us.isa.fama.operations.PreferencesREconfigurationProblem;
import es.us.isa.jmetal.experimentation.settings.AAFMSettings;
import es.us.isa.soup.preferences.User;
import es.us.isa.utils.PreferenceUtils;

public class PBRExperimentalEval {

	private FAMAAttributedFeatureModel fm;
	// private Collection<User>

//	private PreferencesConfigurationProblem configProblem;
//	private PreferencesREconfigurationProblem reconfigProblem;
	private AbstractPreferencesREconfigAssignMethod assignMethod;
	private String folder;
	private String fmName;
	private CSVExperimentWriter writer;
	
	private final static int MIN_INITIAL_TENANTS = 3;
	private final static int MIN_INITIAL_PREFS = 3;

	public PBRExperimentalEval(FAMAAttributedFeatureModel afm, String path) {
		folder = path;
//		path = folder;
		this.fm = afm;
		assignMethod = new ScenarioREconfigAssignMethod(fm);
		fmName = fm.getRoot().getName();
//		writer = new CSVExperimentWriter(path);
//		writer.setFmName(fm.getRoot().getName());
	}

//	private void createProblem() {
//		assignMethod = new ScenarioREconfigAssignMethod(fm);
//
////		configProblem = new PreferencesConfigurationProblem(fm,
////				assignMethod.getInitialUsers(), 0);
//	}

	public void runExperiment(HashMap<String, Integer> experimentParameters,
			List<String> algorithms) {
//		Solution emergenceSol = null;
		/*
		 * Params: - time per algorithm run - number of problems (different
		 * initial tenants) - number of change rounds
		 */
		// int time = experimentParameters.get("time");
		int replays = experimentParameters.get("replays");
		int changeRounds = experimentParameters.get("changeRounds");
		int numberOfInitialTenants = experimentParameters.get("maxInitialTenants");
		int numberOfInitialPrefs = experimentParameters.get("maxInitialPrefs");

		writer = new CSVExperimentWriter(folder+"/"+fmName+".csv");
		writer.setFmName(fmName);
		
		for (int i = 0; i < replays; i++) {
			Random random = new Random(System.nanoTime());
			int ntenants = random.nextInt(numberOfInitialTenants - MIN_INITIAL_TENANTS) 
					+ MIN_INITIAL_TENANTS;
			int nprefs = random.nextInt(numberOfInitialPrefs - MIN_INITIAL_PREFS) 
					+ MIN_INITIAL_PREFS;
			int nrounds = random.nextInt(changeRounds - 5) + 5;
			
			executeScenario(ntenants, nprefs, nrounds, algorithms.toArray(new String[1]));

		}
		writer.finishWriting();
	}
	
	
	private void executeScenario(int initialTenants, int initalPrefs, int changeRounds,
			String[] algNames){
		
		assignMethod.setInitialPreferences(initalPrefs);
		Collection<User> prefs = assignMethod.getRandomUsers(initialTenants);
		
		// XXX for now, we use 0 seeds at the beginning
//		AAFMProblem problem1 = new PreferencesConfigurationProblem(
//				fm, prefs, 0);
		
		AAFMProblem problem1 = new PreferencesConfigurationProblem(
				fm, prefs, 1);
		
		for (int i = 0; i < algNames.length; i++){
			String algName = algNames[i];
			
			Class clazz;
			AAFMSettings settings = null;
			Constructor constructor = null;
			try {
				clazz = Class.forName(algName + "AAFM_Settings");
				constructor = clazz
						.getConstructor(AAFMProblem.class);
				settings = (AAFMSettings) constructor
						.newInstance(problem1);
				
				Algorithm alg = settings.configure();
				long init = System.currentTimeMillis();
				SolutionSet solSet = alg.execute();
				long end = System.currentTimeMillis();
				long elapsedTime = end - init;

				// XXX rank the feasible solutions by the
				// weighted nash product
				Map<Solution, Double> processedSols1 = PreferenceUtils
						.processSolutionSet(solSet, prefs);
				Solution ct1 = PreferenceUtils
						.obtainBestSolution(processedSols1,prefs);

				for (int l = 0; l < changeRounds; l++) {
					Random r = new Random(System.nanoTime());
					// TODO maybe should we group these parameters into
					// blocks?

					// XXX these parameters are simulated. in real
					// environments
					// we cannot control them, so they are non
					// controllable factors
//					assignMethod.setPreferencesChangeProbability(r
//							.nextDouble());
					assignMethod.setPreferencesChangeProbability(0.5);
					assignMethod.setWeightChange(r.nextDouble());
//					assignMethod.setWeightChangeProbability(r
//							.nextDouble());
					assignMethod.setWeightChangeProbability(0.5);

					// XXX change weights
					prefs = assignMethod.changeUsersWeight(prefs);
					// processSolutionSet(solSet, prefs);

					// XXX change preferences
					prefs = assignMethod.changeUsersPreferences(prefs);
					
					// XXX add or remove tenants
					prefs = assignMethod.changeUsers(prefs);

//					problem1 = new PreferencesConfigurationProblem(fm,
//							prefs, 0);
					problem1 = new PreferencesREconfigurationProblem(fm, prefs, ct1);
					settings = (AAFMSettings) constructor
							.newInstance(problem1);
					alg = settings.configure();
					init = System.currentTimeMillis();
					solSet = alg.execute();
					end = System.currentTimeMillis();
					elapsedTime = end - init;
					// XXX here, we have to evaluate the fitness of the
					// previous solution Ck,
					// and then compare it with the fitness of the new
					// solution Ck+1
					Map<Solution, Double> processedSols2 = PreferenceUtils
							.processSolutionSet(solSet, prefs);
					Solution ct2 = PreferenceUtils
							.obtainBestSolution(processedSols2,prefs);
					Solution ct12 = PreferenceUtils.cloneSolution(ct1,
							problem1);
					problem1.evaluate(ct12);
					
					Solution randomSol = problem1.computeRandomValidSolution();
					problem1.evaluate(randomSol);

					// XXX now we get all the Ft-1(Ct-1), Ft(Ct-1) and
					// Ft(Ct)
					SolutionSet auxSolSet = new SolutionSet(3);
					// Ft(Ct-1)
					auxSolSet.add(ct12);
					auxSolSet.add(randomSol);
					Map<Solution, Double> solutionsSt = PreferenceUtils
							.processSolutionSet(auxSolSet, prefs);
					// Ft-1(Ct-1)
					solutionsSt.put(ct1, processedSols1.get(ct1));
					// Ft(Ct)
					solutionsSt.put(ct2, processedSols2.get(ct2));
//					if (ct2 == null){
//						ct2 = ct12;
//						
//					}
//					else{
//						solutionsSt.put(ct2, processedSols2.get(ct2));
//					}
					
					//XXX to avoid a NullPointerException
//					emergenceSol = ct2;
					
					Double ct12val = solutionsSt.get(ct12);
					Double ct2val = solutionsSt.get(ct2);
					Double randomSolVal = solutionsSt.get(randomSol);
					
					int[] wi = this.getTenantWi(prefs);
					int[] pi = this.getNumberOfPrefs(prefs);
					double[] m12i = this.getSatisfactionPercentage(ct12,prefs);
					double[] m2i = this.getSatisfactionPercentage(ct2,prefs);
					double[] mRandomi = this.getSatisfactionPercentage(randomSol, prefs);
					double mean12 = this.roundDouble(this.getMean(m12i));
					double mean2 = this.roundDouble(this.getMean(m2i));
					double meanRandomSol = this.roundDouble(this.getMean(mRandomi));
					boolean improves = ct2val > ct12val;
					
					writer.writeLine(initialTenants, initalPrefs, l, prefs.size(), elapsedTime, ct12val,
							ct2val,randomSolVal,wi,pi,improves,
							m12i,m2i,mRandomi,mean12,mean2,meanRandomSol);
					
					ct1 = ct2;
					if (processedSols2.size() > 0){
						processedSols1 = processedSols2;
					}
					
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (JMException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private double[] getSatisfactionPercentage(Solution s,
			Collection<User> prefs) {
		double[] result = new double[prefs.size()];
		int i = 0;
		for (User u:prefs){
			double satisfied = s.getObjective(i);
			double total = u.getPreferences().size();
			double aux = satisfied/total;
			if (aux < 0){
				result[i] = - this.roundDouble(aux);
			}else{
				result[i] = this.roundDouble(aux);
			}
			
			i++;		
		}
		return result;
	}

	private double getMean(double[] values){
		double result = 0;
		double counter = 0;
		for (int i = 0; i < values.length; i++){
			counter += values[i];
		}
		result = counter/values.length;
		return result;
	}
	
	private int[] getNumberOfPrefs(Collection<User> prefs) {
		int[] result = new int[prefs.size()];
		int i = 0;
		for (User u:prefs){
			result[i] = u.getPreferences().size();
			i++;
		}
		return result;
	}

	public static void main(String[] args){
		
//		String[] models = {"inputs/LeroDaaSIntegers.afm",
//				"inputs/candidates/attributedModel1.afm",
//				"inputs/candidates/attributedModel2.afm",
//				"inputs/candidates/attributedModel3.afm"};
		
		String[] models = {"inputs/candidates/attributedModel204.afm",
				"inputs/candidates/attributedModel205.afm"};
		
		for (int i = 0; i < models.length; i++){
//			FAMAAttributedFeatureModel afm = loadModel("inputs/LeroDaaSIntegers.afm");
			FAMAAttributedFeatureModel afm = loadModel(models[i]);
			PBRExperimentalEval eval = new PBRExperimentalEval(afm, "./outputs");
			
			HashMap<String, Integer> options = new HashMap<String, Integer>();
			
//			options.put("replays", 2);
//			options.put("changeRounds", 2);
//			options.put("maxInitialTenants", 5);
//			options.put("maxInitialPrefs", 5);
			
			
			options.put("replays", 25);
			options.put("changeRounds", 10);
			options.put("maxInitialTenants", 5);
			options.put("maxInitialPrefs", 6);
			
			List<String> algorithms = new LinkedList<String>();
			algorithms.add("es.us.isa.jmetal.experimentation.settings.FastPGA");
			eval.runExperiment(options, algorithms);
		}
		
		
	}
	
	public static FAMAAttributedFeatureModel loadModel(String path) {
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
	
	private int[] getTenantWi(Collection<User> tenants){
		int[] result = new int[tenants.size()];
		int i = 0;
		for (User u:tenants){
			result[i] = u.getWeight();
			i++;
		}
		return result;
	}
	
	private double roundDouble(double n){
		double number = Math.round(n * 100);
		number = number/100;
		return number;
	}

}
