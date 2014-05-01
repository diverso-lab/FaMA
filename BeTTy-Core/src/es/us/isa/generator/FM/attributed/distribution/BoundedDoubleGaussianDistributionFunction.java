/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.generator.FM.attributed.distribution;

/**
 *
 * @author japarejo
 */
public class BoundedDoubleGaussianDistributionFunction extends DoubleGaussianDistributionFunction {
    private double min;
    private double max;
    public BoundedDoubleGaussianDistributionFunction(double mean, double standardDeviation)
    {
        this(mean,standardDeviation,Double.MIN_VALUE,Double.MAX_VALUE);
    }
    
    public BoundedDoubleGaussianDistributionFunction(double mean, double standardDeviation, double min, double max)
    {
        super(mean,standardDeviation);
        this.min=min;
        this.max=max;
    }
    
    @Override
    public Double getValue() {
        Double result=super.getValue();
        if(result<getMin())
            result=getMin();
        else if(result>getMax())
            result=getMax();
        return result;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
    
    
}
