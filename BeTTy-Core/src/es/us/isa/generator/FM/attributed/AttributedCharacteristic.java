/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.us.isa.generator.FM.attributed;

import java.util.ArrayList;
import java.util.Collection;

import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.generator.FM.GeneratorCharacteristics;
import es.us.isa.utils.BettyException;

/**
 * 
 * @author Alejandro, jagalindo
 */
public class AttributedCharacteristic extends GeneratorCharacteristics {

	public static int INTEGER_TYPE = 0;
	public static int DOUBLE_TYPE = 1;

	public static int CONSTANT_DISTRIBUTION = 0;
	public static int GAUSIAN_DISTRIBUTION = 1;
	public static int UNIFORM_DISTRIBUTION = 2;

	// ---------------------------------

	private int numberOfExtendedCTC;
	private int numberOfAttibutesPerFeature;
	private int attributeType = INTEGER_TYPE;
	private int defaultValueDistributionFunction = UNIFORM_DISTRIBUTION;
	private int nullValueDistributionFunction = CONSTANT_DISTRIBUTION;

	private String[] distributionFunctionArguments;
	private Collection<Range> attributeRange;
	private String headAttributeName;

	public AttributedCharacteristic() {
		super();
		this.attributeRange = new ArrayList<Range>();
	}

	public AttributedCharacteristic(String modelName, int seed,
			long maxProducts, int maxBranchingFactor, int height,
			int maxSetChildren, int numberOfFeatures,
			int probabilityOptionalANDMandatory, int probabilityMandatory,
			int probabilityOptional, int probabilityOr,
			int probabilityAlternative, int percentageCTC,
			int numberOfExtendedCTC, int numberOfAttibutesPerFeature,
			int attributeType, int defaultValueDistributionFunction,
			int nullValueDistributionFunction,
			String[] distributionFunctionArguments,
			Collection<Range> attributeRange, String headAttributeName)
			throws BettyException {
		super(modelName, seed, maxProducts, maxBranchingFactor, height,
				maxSetChildren, numberOfFeatures,
				probabilityOptionalANDMandatory, probabilityMandatory,
				probabilityOptional, probabilityOr, probabilityAlternative,
				percentageCTC);
		this.numberOfExtendedCTC = numberOfExtendedCTC;
		this.numberOfAttibutesPerFeature = numberOfAttibutesPerFeature;
		this.attributeType = attributeType;
		this.defaultValueDistributionFunction = defaultValueDistributionFunction;
		this.nullValueDistributionFunction = nullValueDistributionFunction;
		this.distributionFunctionArguments = distributionFunctionArguments;
		this.attributeRange = attributeRange;
		this.headAttributeName = headAttributeName;
	}

	public AttributedCharacteristic(int numberOfExtendedCTC,
			int numberOfAttibutesPerFeature, int attributeType,
			int defaultValueDistributionFunction,
			int nullValueDistributionFunction,
			String[] distributionFunctionArguments,
			Collection<Range> attributeRange, String headAttributeName)
			throws BettyException {

		this.numberOfExtendedCTC = numberOfExtendedCTC;
		this.numberOfAttibutesPerFeature = numberOfAttibutesPerFeature;
		this.attributeType = attributeType;
		this.defaultValueDistributionFunction = defaultValueDistributionFunction;
		this.nullValueDistributionFunction = nullValueDistributionFunction;
		this.distributionFunctionArguments = distributionFunctionArguments;
		this.attributeRange = attributeRange;
		this.headAttributeName = headAttributeName;
	}

	public String[] getDistributionFunctionArguments() {
		return distributionFunctionArguments;
	}

	public void setDistributionFunctionArguments(
			String[] distributionFunctionArguments) {
		this.distributionFunctionArguments = distributionFunctionArguments;
	}

	public int getNumberOfExtendedCTC() {
		return numberOfExtendedCTC;
	}

	public void setNumberOfExtendedCTC(int numberOfExtendedCTC) {
		this.numberOfExtendedCTC = numberOfExtendedCTC;
	}

	public int getNumberOfAttibutesPerFeature() {
		return numberOfAttibutesPerFeature;
	}

	public void setNumberOfAttibutesPerFeature(int numberOfAttibutesPerFeature) {
		this.numberOfAttibutesPerFeature = numberOfAttibutesPerFeature;
	}

	public int getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(int attributeType) {
		this.attributeType = attributeType;
	}

	public int getDefaultValueDistributionFunction() {
		return defaultValueDistributionFunction;
	}

	public void setDefaultValueDistributionFunction(
			int defaultValueDistributionFunction) {
		this.defaultValueDistributionFunction = defaultValueDistributionFunction;
	}

	public int getNullValueDistributionFunction() {
		return nullValueDistributionFunction;
	}

	public void setNullValueDistributionFunction(
			int nullValueDistributionFunction) {
		this.nullValueDistributionFunction = nullValueDistributionFunction;
	}

	public String getHeadAttributeName() {
		return headAttributeName;
	}

	public void setHeadAttributeName(String headAttributeName) {
		this.headAttributeName = headAttributeName;
	}

	public Collection<Range> getRanges() {
		return this.attributeRange;
	}

	public void addRange(Range r) {
		this.attributeRange.add(r);
	}

	public AttributedCharacteristic clone() {
		// FIXME
		AttributedCharacteristic ch = new AttributedCharacteristic(
				numberOfExtendedCTC, numberOfAttibutesPerFeature,
				attributeType, defaultValueDistributionFunction,
				nullValueDistributionFunction, distributionFunctionArguments,
				attributeRange, headAttributeName);
		ch.setMaxBranchingFactor(maxBranchingFactor);
		if (maxProducts != -1) {
			ch.setMaxProducts(maxProducts);
		}
		ch.setMaxSetChildren(maxSetChildren);
		ch.setModelName(modelName);
		ch.setNumberOfFeatures(numberOfFeatures);
		ch.setPercentageCTC(percentageCTC);
		ch.setProbabilityAlternative(probabilityAlternative);
		ch.setProbabilityMandatory(probabilityMandatory);
		ch.setProbabilityOptional(probabilityOptional);
		ch.setProbabilityOr(probabilityOr);
		ch.setSeed(seed);
		return ch;
	}

}
