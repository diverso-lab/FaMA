package jmetal.test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOEAD_DRA_Settings;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 21/06/13
 * Time: 07:48
 * To change this template use File | Settings | File Templates.
 */
public class MOEAD_DRA_SettingsTest {
  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings MOEAD_DRASettings = new MOEAD_DRA_Settings("Fonseca");
    Algorithm algorithm = MOEAD_DRASettings.configure() ;
    Problem problem = new Fonseca("Real") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;
    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    File experimentDirectory = new File(dataDirectory) ;
    int populationSize =  ((Integer)algorithm.getInputParameter("populationSize")).intValue() ;


    DifferentialEvolutionCrossover crossover = (DifferentialEvolutionCrossover)algorithm.getOperator("crossover") ;
    double CR = (Double)crossover.getParameter("CR") ;
    double F = (Double)crossover.getParameter("F") ;

    assertEquals("MOEAD_DRASettings", 600, populationSize);
    assertEquals("MOEAD_DRASettings", 300000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("MOEAD_DRASettings", 300, ((Integer)algorithm.getInputParameter("finalSize")).intValue());

    assertEquals("MOEAD_DRASettings", 0.9, ((Double)algorithm.getInputParameter("delta")).doubleValue(), epsilon) ;
    assertEquals("MOEAD_DRASettings", 60, ((Integer) algorithm.getInputParameter("T")).intValue());
    assertEquals("MOEAD_DRASettings", 6,   ((Integer)algorithm.getInputParameter("nr")).intValue());

    assertEquals("MOEAD_DRASettings", 1.0, CR, epsilon);
    assertEquals("MOEAD_DRASettings", 0.5, F, epsilon);
    assertEquals("MOEAD_DRASettings", 20.0, dim, epsilon);
    assertEquals("MOEAD_DRASettings", 1.0/problem.getNumberOfVariables(), pm, epsilon);

    assertTrue("MOEAD_DRASettings", experimentDirectory.exists());
  }
}
