/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.generator.FM.attributed.distribution;

/**
 *
 * @author Alejandro
 */
public class DoubleConstantDistribution implements DistributionFunction<Double>{

    public Double value;

    public DoubleConstantDistribution(){
        value = null;
    }

    public DoubleConstantDistribution(Double value){
        this.value = value;
    }

    public void setValue(Double value){
        this.value = value;
    }

    public Double getValue(){
        return value;
    }

    public void setParams(String params) {
        setValue(new Double(params));
    }
}
