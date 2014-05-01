/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.generator.FM.attributed.distribution;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 *
 * @author japarejo
 */
public class DoubleGaussianDistributionFunction implements DistributionFunction<Double> {

    protected Double mean;
    protected Double standarDeviation;
    RandomData dataGenerator;

    public DoubleGaussianDistributionFunction(double mean, double standardDeviation) {
        this.mean = mean;
        this.standarDeviation = standardDeviation;
        dataGenerator = new RandomDataImpl();
    }

    @Override
    public Double getValue() {
        Double result = mean;
        if(standarDeviation!=0)
            result = dataGenerator.nextGaussian(mean, standarDeviation);
        return result;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandarDeviation() {
        return standarDeviation;
    }

    public void setStandarDeviation(double standardDeviation) {
        this.standarDeviation = standardDeviation;
    }
    
    @Override
     public String toString()
    {
        return "Gaussian<Double>(Mean:"+mean+",StandardDeviation:"+standarDeviation+")";
    }
}
