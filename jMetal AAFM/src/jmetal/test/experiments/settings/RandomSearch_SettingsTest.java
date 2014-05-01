package jmetal.test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.RandomSearch_Settings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 27/06/13
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
public class RandomSearch_SettingsTest {
  @Test
  public void testConfigure() throws Exception {
    Settings randomSettings = new RandomSearch_Settings("Fonseca");
    Algorithm algorithm = randomSettings.configure() ;

    assertEquals("RandomSearch_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

  }
}
