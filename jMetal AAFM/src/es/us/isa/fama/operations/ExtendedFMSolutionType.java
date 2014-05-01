//package es.us.isa.fama.operations;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.SortedSet;
//
//import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
//import es.us.isa.FAMA.models.domain.Domain;
//import es.us.isa.FAMA.models.domain.IntegerDomain;
//import es.us.isa.FAMA.models.domain.ObjectDomain;
//import es.us.isa.FAMA.models.domain.Range;
//import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
//import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
//import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
//import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;
//import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
//import jmetal.core.Problem;
//import jmetal.core.SolutionType;
//import jmetal.core.Variable;
//import jmetal.encodings.variable.ArrayInt;
//import jmetal.encodings.variable.Binary;
//
///**
// * Class representing an Extended Feature Model solution type.
// * It's composed by a Binary(features) and an ArrayInt (attributes)
// * @author jesus
// *
// */
//public class ExtendedFMSolutionType extends SolutionType {
//
//	protected FAMAAttributedFeatureModel fm;
//	//features arranged by index
//	protected GenericAttributedFeature[] features;
//	//attributes arranged by index
//	protected GenericAttribute[] attributes;
//	
//	protected double[] attLowerBounds;
//	protected double[] attUpperBounds;
//	private Map<String,Integer> featureSolutionIndex;
//	private Map<String,Integer> attributeSolutionIndex;
//	
//	
//	public ExtendedFMSolutionType(Problem problem, FAMAAttributedFeatureModel fm) {
//		super(problem);
//		this.fm = fm;
//		//init internal variables
//		fmToJMetalVars();
//	}
//	
//	public FAMAAttributedFeatureModel getFm() {
//		return fm;
//	}
//
////	public void setFm(FAMAAttributedFeatureModel fm) {
////		this.fm = fm;
////	}
//
//	@Override
//	public Variable[] createVariables() throws ClassNotFoundException {
//		Variable[] variables = new Variable[2];
//		//a binary of numberOfFeatures bits
//		
//		
//		variables[0] = new Binary(features.length);
//		variables[1] = new ArrayInt(attributes.length,attLowerBounds,attUpperBounds);
//		return variables;
//	}
//
//	
//	private void fmToJMetalVars() {
//		featureSolutionIndex = new HashMap<String, Integer>();
//		attributeSolutionIndex = new HashMap<String, Integer>();
//		// create the array of features
//		
//		
//		features = fm.getAttributedFeatures().toArray(new GenericAttributedFeature[1]);
//		Collection<GenericAttribute> temp = new LinkedList<GenericAttribute>();
//		for (int i = 0; i < features.length; i++){
//			featureSolutionIndex.put(features[i].getName(),i);
//			Collection<? extends GenericAttribute> atts = features[i].getAttributes();
//			temp.addAll(atts);
//		}//for
//		
//		attributes = temp.toArray(new GenericAttribute[1]);
//		attLowerBounds = new double[attributes.length];
//		attUpperBounds = new double[attributes.length];
//		for (int i = 0; i < attributes.length; i++){
//			attributeSolutionIndex.put(attributes[i].getFullName(), i);
//			Domain dom = attributes[i].getDomain();
//			if (dom instanceof IntegerDomain){
//				IntegerDomain idom = (IntegerDomain) dom;
//				if (idom instanceof RangeIntegerDomain){
//					RangeIntegerDomain ridom = (RangeIntegerDomain) idom;
//					Set<Range> ranges = ridom.getRanges();
//					Iterator<Range> it = ranges.iterator();
//					Range first = it.next();
//					Range last = first;
//					while (it.hasNext()){
//						last = it.next();
//					}//while
//					int min = first.getMin();
//					int max = last.getMax();
//					attLowerBounds[i] = min;
//					attUpperBounds[i] = max;
//				}//if
//				
//			}//if
//			else if (dom instanceof ObjectDomain){
//				ObjectDomain odom = (ObjectDomain) dom;
//				//make sure that the values are consecutive
//				Set<Integer> values = odom.getAllIntegerValues();
//				List<Integer> auxList = new ArrayList<Integer>(values);
//				Collections.sort(auxList);
//				attLowerBounds[i] = auxList.get(0);
//				attUpperBounds[i] = auxList.get(auxList.size() - 1);
//			}//else if
//		}//for
//		
//	}//fmToArray
//	
////	public double[] getUpperBounds(){
////		return attUpperBounds;
////	}
////	
////	public double[] getLowerBounds(){
////		return attLowerBounds;
////	}
////	
////	public Map<String, Integer> getFeatureSolutionIndex() {
////		return featureSolutionIndex;
////	}
////
////	public Map<String, Integer> getAttributeSolutionIndex() {
////		return attributeSolutionIndex;
////	}
//
//}
