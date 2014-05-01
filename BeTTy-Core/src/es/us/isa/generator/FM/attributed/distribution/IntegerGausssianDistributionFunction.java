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
public class IntegerGausssianDistributionFunction implements DistributionFunction<Integer>{

    Integer mean;
    Double standarDeviation;
    RandomData dataGenerator;
    
    public IntegerGausssianDistributionFunction(Integer mean, Double standarDeviation)
    {
        this.mean=mean;
        this.standarDeviation=standarDeviation;
        dataGenerator=new RandomDataImpl();
    }
    
    @Override
    public Integer getValue() {
        Integer result = mean;
        if(standarDeviation!=0)
            result = new Integer((int)Math.round((float)(dataGenerator.nextGaussian(mean, standarDeviation))));
        return result;
    }
    
    public Integer getMean() {
        return mean;
    }

    public void setMean(Integer mean) {
        this.mean = mean;
    }

    public double getStandarDeviation() {
        return standarDeviation;
    }

    public void setStandarDeviation(double standardDeviation) {
        this.standarDeviation = standardDeviation;
    }

    public String toString()
    {
        return "Gaussian<Integer>(Mean:"+mean+",StandardDeviation:"+standarDeviation+")";
    }
}
