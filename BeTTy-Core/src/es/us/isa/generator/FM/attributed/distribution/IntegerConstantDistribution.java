/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.generator.FM.attributed.distribution;

/**
 *
 * @author Alejandro
 */
public class IntegerConstantDistribution implements DistributionFunction<Integer>{

    public Integer value;

    public IntegerConstantDistribution(){
        value = null;
    }

    public IntegerConstantDistribution(Integer value){
        this.value = value;
    }

    public void setValue(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }

    public void setParams(String params) {
        setValue(new Integer(params));
    }

}
