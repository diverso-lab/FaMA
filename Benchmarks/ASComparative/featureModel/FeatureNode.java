/*
 * Created on 04-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package featureModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author trinidad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FeatureNode extends Feature {
	public static final int UNKNOWN = -1;
	public static final int ROOT = 0;
	public static final int MANDATORY = 1;
	public static final int OPTIONAL = 2;
	public static final int ALTERNATIVE = 3;
	public static final int OR = 4;
	public static final int GROUPED = 6;
	public static final int CARDINALITY = 5;
	
	protected ArrayList subfeaturesList;
	protected List groupsList;
	protected int relationType;
	protected FeatureNode parentFeature;
	protected int cardMin, cardMax;

	protected FeatureNode()
	{
		super();
		this.relationType = UNKNOWN;
		this.parentFeature = null;
		this.subfeaturesList = new ArrayList();
		this.groupsList = new ArrayList();
	}
	
	public FeatureNode(String name)
	{
		super(name);
		this.relationType = UNKNOWN;
		this.parentFeature = null;
		this.subfeaturesList = new ArrayList();
		this.groupsList = new ArrayList();
	}
	
	public int getRelationType (){
		return relationType;
	}
	
	public void setRelationType (int relationType) throws IllegalArgumentException{
		if ( relationType >= ROOT && relationType <= GROUPED)
			this.relationType = relationType;
		else
			throw new IllegalArgumentException("Setting an unknown feature relation type");
		
		//MOD para que si es grouped también ponga 1..1
		switch ( relationType ) {
			case MANDATORY:
				this.cardMin = 1;
				this.cardMax = 1;
				break;
			case OPTIONAL:
				this.cardMin = 0;
				this.cardMax = 1;
				break;
			case GROUPED:
				this.cardMin = 1;
				this.cardMax = 1;
				break;

		}
	}

	public void setRelationType (String relationType) throws IllegalArgumentException{
		
		//MOD para que cuando sea grouped la cardinalidad sea también 1 1
		if ( relationType.equalsIgnoreCase("root") )
			this.relationType = FeatureNode.ROOT;
		else if ( relationType.equalsIgnoreCase("mandatory") ) {
			this.cardMin = 1; this.cardMax = 1;
			this.relationType = FeatureNode.MANDATORY;
		}
		else if ( relationType.equalsIgnoreCase("optional") ) {
			this.cardMin = 0; this.cardMax = 1;
			this.relationType = FeatureNode.OPTIONAL;
		}
		else if ( relationType.equalsIgnoreCase("alternative") ) {
			this.relationType = FeatureNode.ALTERNATIVE;
		}
		else if ( relationType.equalsIgnoreCase("or") )
			this.relationType = FeatureNode.OR;
		else if ( relationType.equalsIgnoreCase("grouped") ){
			this.cardMin = 1; this.cardMax = 1;
			this.relationType = FeatureNode.GROUPED;
		}
		else
			throw new IllegalArgumentException("Setting an unknown feature relation type");
	}

	public void setCardinality (int min, int max) {
		this.relationType = FeatureNode.CARDINALITY;
		this.cardMin = min;
		this.cardMax = max;
	}
	
	public void setGroupedCardinality (int min, int max) {
		this.relationType = FeatureNode.CARDINALITY;
	}
	/**
	 * @param f
	 * @param relationType
	 */
	public void setChild(FeatureNode f, int relationType){
		f.setParent(this);
		f.setRelationType(relationType);
		subfeaturesList.add(f);
	}
	
	public void setChild(FeatureNode f){
		f.setParent(this);
		subfeaturesList.add(f);
	}
	
	public FeatureNode getChild ( int pos ){
		return (FeatureNode)subfeaturesList.get(pos);
	}
	
	public int getChildNum (){
		return subfeaturesList.size(); 
	}
	
	public FeatureNode getParent() {
		return parentFeature;
	}
	
	public List<FeatureNode> getSubfeatures() {
		return this.subfeaturesList;
	}
	
	public void setParent (FeatureNode parent) {
		this.parentFeature = parent;
	}
	
	public static int getSubfeaturesNumber(FeatureNode f) {
		int res = 1; // feature f should be considered
		for ( int i = 0; i < f.getChildNum(); i++) {
			res += getSubfeaturesNumber(f.getChild(i));
		}
		return res;
	}

	public int getSubfeaturesNumber() {
		int res = 1; // this feature should be considered
		for ( int i = 0; i < getChildNum(); i++) {
			res += getSubfeaturesNumber(getChild(i));
		}
		return res;
	}
	
	public void getFeatures(FeatureNode f,Collection col) {
		int res = 1; // feature f should be considered
		col.add(f);
		for ( int i = 0; i < f.getChildNum(); i++) {
			getFeatures(f.getChild(i),col);
		}
	}

	public Collection getFeatures() {
		Collection res = new HashSet();
		
		getFeatures(this, res);
		
		return res;
	}
	
	public int getMinCardinality() {
		return cardMin;
	}
	
	public int getMaxCardinality() {
		return cardMax;
	}
	
	
	public String toString(){
		String res;
		
		res = "(" + featureName + "-";
		switch(this.relationType)
		{
			case FeatureNode.ROOT: 			res += "root"; break;
			case FeatureNode.MANDATORY: 	res += "mandatory"; break;
			case FeatureNode.OPTIONAL: 		res += "optional"; break;
			case FeatureNode.ALTERNATIVE: 	res += "alternative"; break;
			case FeatureNode.OR: 			res += "or"; break;
			case FeatureNode.CARDINALITY:	res += "[" + cardMin + "," + cardMax + "]"; break;
			case FeatureNode.GROUPED:		res += "grouped"; break;
			default: 						res += "unknown"; break;
		}
		
		if ( subfeaturesList.size() > 0 ) 
			res += ",(";

		int i;
		for ( i = 0; i < subfeaturesList.size() - 1; i++)
		{
			res += subfeaturesList.get(i);
		}
		if ( subfeaturesList.size() > 0)
			res += subfeaturesList.get(i) + ")";

		res += ")";
		return res;
	}

	public List getGroupsList() {
		return groupsList;
	}

	public void addGroup(FeatureGroup group) {
		
//		for (Iterator iter = group.getFeatures().iterator(); iter.hasNext();) {
//			FeatureNode element = (FeatureNode) iter.next();
//			subfeaturesList.add(element);
//		}
		groupsList.add(group);
		
	}
}
