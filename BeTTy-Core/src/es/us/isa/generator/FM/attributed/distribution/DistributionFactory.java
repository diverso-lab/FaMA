/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.generator.FM.attributed.distribution;

import java.util.Map;

/**
 *
 * @author Alejandro
 */
public class DistributionFactory {

    protected static DistributionFactory singleton;
    protected static Map map;
/*
    public DistributionFactory() {
        map = new HashMap();
        map.put("Constant", new IntegerConstantDistribution());
        map.put("IntegerNormalDistribution", new IntegerNormalDistribution());
        map.put("IntegerUniformDistribution", new IntegerUniformDistribution());
    }

    public static DistributionFactory create() {
        if (singleton == null) {
            singleton = new DistributionFactory();
        }
        return singleton;
    }

    public Distribution distribution(String description, String attributeType) {
        String function = description.substring(0, description.indexOf("("));
        Distribution result = (Distribution) map.get(function);
        String params = description.substring(description.indexOf("(") + 1, description.lastIndexOf(")"));
        result.setParams(params);
        return result;
    }
 * */
}
