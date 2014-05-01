/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.generator.FM.attributed;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.ComplexConstraint;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
import es.us.isa.FAMA.models.featureModel.Constraint;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.benchmarking.writers.PlainWriter;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.IGenerator;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.AbstractFMGeneratorDecorator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.attributed.distribution.DistributionFunction;
import es.us.isa.generator.FM.attributed.distribution.DoubleConstantDistribution;
import es.us.isa.generator.FM.attributed.distribution.DoubleGaussianDistributionFunction;
import es.us.isa.generator.FM.attributed.distribution.DoubleUniformDistributionFunction;
import es.us.isa.generator.FM.attributed.distribution.IntegerConstantDistribution;
import es.us.isa.generator.FM.attributed.distribution.IntegerGausssianDistributionFunction;
import es.us.isa.generator.FM.attributed.distribution.IntegerUniformDistributionFunction;
import es.us.isa.utils.BettyException;

public class AttributedFMGenerator extends AbstractFMGeneratorDecorator {

	private DistributionFunction defaultValueDistribution;
	private DistributionFunction nullValueDistribution;
	public AttributedFMGenerator(IGenerator gen) {
		super(gen);
	}

	/**
	 * Return an attributed feature model with the characteristics received as
	 * input.
	 * 
	 * @param ch
	 *            User's preferences for the generation. It must be an
	 *            AttributetdCharacteristic
	 * 
	 * @return the attributed feature model generated.
	 */
	@Override
	public VariabilityModel generateFM(Characteristics ch) {
		return this.generateFM(ch, new FMGenerator());
	}

	public VariabilityModel generateFM(Characteristics ch, AbstractFMGenerator gen ) {
		if (!(ch instanceof AttributedCharacteristic)) {
			throw new BettyException(
					"The characteristic must be AttributedCharacteristic");
		}
		AttributedCharacteristic ac = (AttributedCharacteristic) ch;

		initializeDefaultValueDistribution(ac);
		initializeNullValueDistribution(ac);
		// Feature model generation
		FAMAFeatureModel fm = (FAMAFeatureModel) gen.generateFM(ch);

		FAMAAttributedFeatureModel afm = FMToAttributedFM(fm);

		// Adding attributes
		addAttributes(ac, afm.getRoot());

		addExtendedContraints(afm, ch);
		return afm;
	}
	
	private void initializeNullValueDistribution(AttributedCharacteristic ac) {
		if (ac.getAttributeType() == AttributedCharacteristic.INTEGER_TYPE) {
			switch (ac.getNullValueDistributionFunction()) {
			case 0://CONSTANT_DIST
				this.nullValueDistribution = new IntegerConstantDistribution();
				((IntegerConstantDistribution)nullValueDistribution).setParams(ac.getDistributionFunctionArguments()[0]);
				break;
			case 1://GAUSIAN_DISTRIBUTION
				this.nullValueDistribution = new IntegerGausssianDistributionFunction(Integer.parseInt(ac.getDistributionFunctionArguments()[0]),Double.parseDouble(ac.getDistributionFunctionArguments()[1]));
				break;
			case 2://UNIFORM_DISTRIBUTION
				this.nullValueDistribution = new IntegerUniformDistributionFunction(Integer.parseInt(ac.getDistributionFunctionArguments()[0]),Integer.parseInt(ac.getDistributionFunctionArguments()[1]));
				break;
			default:
				break;
			}
		} else if (ac.getAttributeType() == AttributedCharacteristic.DOUBLE_TYPE) {
			switch (ac.getDefaultValueDistributionFunction()) {
			case 0://CONSTANT_DIST
				this.nullValueDistribution = new DoubleConstantDistribution();
                ((DoubleConstantDistribution) nullValueDistribution).setParams(ac.getDistributionFunctionArguments()[0]);
				break;
			case 1://GAUSIAN_DISTRIBUTION
				this.nullValueDistribution = new DoubleGaussianDistributionFunction(new Integer(ac.getDistributionFunctionArguments()[0]), new Double(ac.getDistributionFunctionArguments()[1]));
				break;
			case 2://UNIFORM_DISTRIBUTION
				this.nullValueDistribution = new DoubleUniformDistributionFunction(new Double(ac.getDistributionFunctionArguments()[0]), new Double(ac.getDistributionFunctionArguments()[1]));
				break;
			default:
				break;
			}
			
			// NOT implemented
			throw new BettyException(
					"Attributed Generator with double not implemented yet");
		}
	}

	
	private void initializeDefaultValueDistribution(AttributedCharacteristic ac) {
		if (ac.getAttributeType() == AttributedCharacteristic.INTEGER_TYPE) {
			switch (ac.getDefaultValueDistributionFunction()) {
			case 0://CONSTANT_DIST
				this.defaultValueDistribution = new IntegerConstantDistribution();
				((IntegerConstantDistribution)defaultValueDistribution).setParams(ac.getDistributionFunctionArguments()[0]);
				break;
			case 1://GAUSIAN_DISTRIBUTION
				this.defaultValueDistribution = new IntegerGausssianDistributionFunction(Integer.parseInt(ac.getDistributionFunctionArguments()[0]),Double.parseDouble(ac.getDistributionFunctionArguments()[1]));
				break;
			case 2://UNIFORM_DISTRIBUTION
				this.defaultValueDistribution = new IntegerUniformDistributionFunction(Integer.parseInt(ac.getDistributionFunctionArguments()[0]),Integer.parseInt(ac.getDistributionFunctionArguments()[1]));
				break;
			default:
				break;
			}
		} else if (ac.getAttributeType() == AttributedCharacteristic.DOUBLE_TYPE) {
			switch (ac.getDefaultValueDistributionFunction()) {
			case 0://CONSTANT_DIST
				this.defaultValueDistribution = new DoubleConstantDistribution();
                ((DoubleConstantDistribution) defaultValueDistribution).setParams(ac.getDistributionFunctionArguments()[0]);
				break;
			case 1://GAUSIAN_DISTRIBUTION
				this.defaultValueDistribution = new DoubleGaussianDistributionFunction(new Integer(ac.getDistributionFunctionArguments()[0]), new Double(ac.getDistributionFunctionArguments()[1]));
				break;
			case 2://UNIFORM_DISTRIBUTION
				this.defaultValueDistribution = new DoubleUniformDistributionFunction(new Double(ac.getDistributionFunctionArguments()[0]), new Double(ac.getDistributionFunctionArguments()[1]));
				break;
			default:
				break;
			}
			
			// NOT implemented
			throw new BettyException(
					"Attributed Generator with double not implemented yet");
		}
	}

	/**
	 * This function add all atributes related to leaf features
	 * 
	 * @param ch
	 * @param f
	 */
	private void addAttributes(AttributedCharacteristic ch, AttributedFeature f) {
		Iterator<Relation> it = f.getRelations();
		while (it.hasNext()) {
			Relation r = it.next();
			Iterator<AttributedFeature> it2 = r.getDestination();
			while (it2.hasNext()) {
				AttributedFeature f2 = it2.next();
				// Check if it is a leaf, then we add the attributes
				if (f2.getNumberOfRelations() == 0) {

					for (GenericAttribute att : generateAttributes(ch)) {
						f2.addAttribute(att);
					}
				}
				addAttributes(ch, f2);
			}
		}
	}

	private Collection<GenericAttribute> generateAttributes(AttributedCharacteristic ch) {
		// TODO Auto-generated method stub
		Collection<GenericAttribute> attributes2return = new ArrayList<GenericAttribute>();
		for(int i=0;i<ch.getNumberOfAttibutesPerFeature();i++){
			if (ch.getAttributeType()==AttributedCharacteristic.INTEGER_TYPE) {
				attributes2return.add(new GenericAttribute(ch.getHeadAttributeName()+i, new RangeIntegerDomain(ch.getRanges()) , nullValueDistribution.getValue(), defaultValueDistribution.getValue()));
			}else{
				//not yet supported
			}
		}
		return attributes2return;
	}

	private void addExtendedContraints(FAMAAttributedFeatureModel afm,
			Characteristics ch) {
		AttributedCharacteristic ach = (AttributedCharacteristic) ch;
		Random random = new Random();
		int i = 0;
		while (i < ach.getNumberOfExtendedCTC()) {
			Collection<AttributedFeature> attributedFeatures = afm
					.getAttributedFeatures();
			boolean atts = false;
			AttributedFeature af1 = null;
			AttributedFeature af2 = null;
			while (!atts) {
				af1 = (AttributedFeature) attributedFeatures.toArray()[random
						.nextInt(afm.getFeaturesNumber())];
				af2 = (AttributedFeature) attributedFeatures.toArray()[random
						.nextInt(afm.getFeaturesNumber())];
				if (af1.getAttributes().size() > 0
						&& af2.getAttributes().size() > 0) {
					atts = true;
				}
			}
			if (af1 != af2) {

				GenericAttribute a1 = (GenericAttribute) af1.getAttributes()
						.toArray()[random.nextInt(af1.getNumberOfAttributes())];
				GenericAttribute a2 = (GenericAttribute) af2.getAttributes()
						.toArray()[random.nextInt(af2.getNumberOfAttributes())];
				// Get an int in range , to extend we also need to check if we
				// are in range or integer domain, double ... (ttf aprox 50
				// hours)

				Range maxA1R = ((Range) ((RangeIntegerDomain) a1.getDomain())
						.getRanges().toArray()[0]);
				int maxA1 = maxA1R.getMax();
				Range maxA2R = ((Range) ((RangeIntegerDomain) a1.getDomain())
						.getRanges().toArray()[0]);
				int maxA2 = maxA2R.getMax();

				String cStr = a1.toString() + getRandomConstraint(true)
						+ random.nextInt(maxA1) + getRandomConstraint(false)
						+ a2.toString() + getRandomConstraint(true)
						+ random.nextInt(maxA2) + ";";
				// System.out.println(cStr);
				Constraint c = new ComplexConstraint(cStr);

				afm.addConstraint(c);
				i++;
			}
		}

	}

	private String getRandomConstraint(boolean b) {
		Random random = new Random();

		String[] booleanC = { " > ", " < ", " == ", " != "," >= "," <= "  };
		String[] advancedC = { " IMPLIES "," IFF "," OR "," AND " };
		return b ? booleanC[random.nextInt(booleanC.length)] : advancedC[random
				.nextInt(advancedC.length)];
	}

	// Reuses the generators available for FAMAFeatureModel to a
	// FAMAAttributedFeatureMode
	private FAMAAttributedFeatureModel FMToAttributedFM(FAMAFeatureModel fm) {

		FAMAAttributedFeatureModel model = null;
		PlainWriter writer = new PlainWriter();
		String tempdir = System.getProperty("java.io.tmpdir");
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}

		// Write the model to a temporary file
		try {
			writer.writeFile(tempdir + "out.afm", fm);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Read the model from the temporary file as an attributed FM
		AttributedReader reader = new AttributedReader();
		try {
			model = (FAMAAttributedFeatureModel) reader.parseFile(tempdir
					+ "out.afm");
		} catch (Exception e) {
			throw new BettyException(
					"Error when parsing the model from a temporary file");
		}
		
		File f = new File(tempdir+"out.afm");
		f.delete();
		return model;
	}
}
