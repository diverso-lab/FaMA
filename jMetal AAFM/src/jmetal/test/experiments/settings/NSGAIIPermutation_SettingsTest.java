package jmetal.test.experiments.settings;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J.Nebro
 * Date: 29/06/13
 * Time: 16:58
 */
public class NSGAIIPermutation_SettingsTest {
  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    /*
    Settings nsgaIISettings = new NSGAIIPermutation_Settings("mQAP");
    Algorithm algorithm = nsgaIISettings.configure() ;
    Problem problem = new mQAP("Permutation") ;
    TwoPointsCrossover crossover = (TwoPointsCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;

    SwapMutation mutation = (SwapMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;

    assertEquals("NSGAIIPermutation_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("NSGAIIPermutation_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("NSGAIIPermutation_SettingsTest", 0.9, pc, epsilon);

    assertEquals("NSGAIIPermutation_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    */
  }
}
