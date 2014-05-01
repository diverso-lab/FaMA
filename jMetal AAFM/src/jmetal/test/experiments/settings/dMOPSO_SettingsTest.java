package jmetal.test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.dMOPSO_Settings;
import jmetal.problems.Fonseca;
import jmetal.util.JMException;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 16/06/13
 * Time: 00:28
 */
public class dMOPSO_SettingsTest {

  @Test
  public void test() throws JMException {
    double epsilon = 0.000000000000001;
    Settings dMOPSOSettings = new dMOPSO_Settings("Fonseca");
    Algorithm algorithm = dMOPSOSettings.configure();
    Problem problem = new Fonseca("Real");

    int swarmSize = (Integer)algorithm.getInputParameter("swarmSize");

    String dataDirectory = (String) algorithm.getInputParameter("dataDirectory");
    System.out.println(dataDirectory);
    File experimentDirectory = new File(dataDirectory);

    assertEquals("dMOPSO_SettingsTest", 100, ((Integer) algorithm.getInputParameter("swarmSize")).intValue());
    assertEquals("dMOPSO_SettingsTest", 250, ((Integer) algorithm.getInputParameter("maxIterations")).intValue());

    assertTrue("cMOEAD_SettingsTest", experimentDirectory.exists());
  }
}
