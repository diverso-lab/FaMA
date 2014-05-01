/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.generator.FM.attributed.distribution;

/**
 *
 * @author japarejo
 */
public class DoubleUniformDistributionFunction implements DistributionFunction<Double> {

    private Double min;
    private Double max;
    
    public DoubleUniformDistributionFunction(Double min, Double max)
    {
        this.min=min;
        this.max=max;
    }
    
    @Override
    public Double getValue() {
        return getMin()+Math.random()*(getMax()-getMin());
                
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
    
    @Override
     public String toString()
    {
        return "Uniform<Double>(Min:"+min+",Max:"+max+")";
    }

}
