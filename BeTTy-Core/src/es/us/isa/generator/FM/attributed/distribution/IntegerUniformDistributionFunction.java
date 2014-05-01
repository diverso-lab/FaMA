/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.generator.FM.attributed.distribution;

/**
 *
 * @author japarejo
 */
public class IntegerUniformDistributionFunction implements DistributionFunction<Integer> {

    private Integer min;
    private Integer max;
    
    public IntegerUniformDistributionFunction(Integer min, Integer max)
    {
        this.min=min;
        this.max=max;        
    }
    
    @Override
    public Integer getValue() {
        double value=getMin().doubleValue()+Math.random()*(getMax().doubleValue()-getMin().doubleValue());
        return (int)Math.round(value);
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @Override
    public String toString()
    {
        return "Uniform<Integer>(Min:"+min+",Max:"+max+")";
    }
}
