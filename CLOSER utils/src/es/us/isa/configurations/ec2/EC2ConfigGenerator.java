package es.us.isa.configurations.ec2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;

public class EC2ConfigGenerator {

	private FAMAAttributedFeatureModel ec2FM;
	private Map<String,List[]> subtrees;
	
	public void setEC2Model(FAMAAttributedFeatureModel fm){
		ec2FM = fm;
		subtrees = new HashMap<String,List[]>();
		// obtener las features abstractas y concretas
		obtainSubtrees();
	}
	
	private void obtainSubtrees() {
		Collection<AttributedFeature> feats = new LinkedList<AttributedFeature>();
		feats.add(ec2FM.searchFeatureByName("OS"));
		feats.add(ec2FM.searchFeatureByName("INSTANCE_TYPE"));
		feats.add(ec2FM.searchFeatureByName("LOCATION"));
		feats.add(ec2FM.searchFeatureByName("CLOUD_WATCH"));
		feats.add(ec2FM.searchFeatureByName("RESERVED"));
		
		for (AttributedFeature f:feats){
			List<GenericFeature> abstractFeatures = new ArrayList<GenericFeature>();
			List<GenericFeature> concreteFeatures = new ArrayList<GenericFeature>();
//			classifyFeatures(f, abstractFeatures, concreteFeatures);
			List[] res = {abstractFeatures,concreteFeatures};
			subtrees.put(f.getName(), res);
		}
		
	}

	/**
	 * Classify features in two sets: abstract features and concrete features
	 * @param f
	 * @param abstractFeats
	 * @param concreteFeats
	 */
//	private void classifyFeatures(AttributedFeature f, List<GenericFeature> abstractFeats, 
//			List<GenericFeature> concreteFeats) {
//		Collection<Relation> rels = f.getRelations();
//		if (rels.isEmpty()){
//			concreteFeats.add(f);
//		}
//		else{
//			abstractFeats.add(f);
//			for (Relation r:rels){
//				Collection<AttributedFeature> feats = r.getDestination();
//				for (AttributedFeature child:feats){
//					classifyFeatures(child, abstractFeats, concreteFeats);
//				}
//			}
//		}
//	}

	public Collection<ExtendedConfiguration> generateConfigs(AWSConfigDescriptor descriptor, int n){
		Collection<ExtendedConfiguration> res = new LinkedList<ExtendedConfiguration>();
		for (int i = 0; i < n; i++){
			ExtendedConfiguration econf = generateConfig(descriptor);
			res.add(econf);
		}
		return res;
	}

	private ExtendedConfiguration generateConfig(AWSConfigDescriptor descriptor) {
		ExtendedConfiguration econf = new ExtendedConfiguration();
		Map<String,FeatureType> areas = descriptor.getConfigAreas();
		Set<Entry<String,FeatureType>> entryAreas = areas.entrySet();
		
		// for each feature entry in the ec2 configuration descriptor
		for (Entry<String,FeatureType> e:entryAreas){
			
			String area = e.getKey();
			FeatureType type = e.getValue();
			
			// we check if the area exists
			if (subtrees.containsKey(area)){
				List features;
				List[] value = subtrees.get(area);
				
				// we also check the feature type
				if (type == FeatureType.ABSTRACT){
					features = value[0];
				}
				else{
					features = value[1];
				}
				//and finally we get a random feature of the specified type
				int size = features.size();
				Random random = new Random(System.nanoTime());
				int randomInt = random.nextInt(size);
				GenericFeature selectedFeature = (GenericFeature) features.get(randomInt);
				econf.addElement(selectedFeature, 1);
			}
			
		}
		
		// TODO att entries
		
		return econf;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EC2ConfigGenerator generator = new EC2ConfigGenerator();
		System.out.println("#### Loading FaMa... ####");
		QuestionTrader qt = new QuestionTrader();
		System.out.println("#### Loading EC2 FM... ####");
		FAMAAttributedFeatureModel afm = (FAMAAttributedFeatureModel) 
				qt.openFile("AWS FMs/ec2/AmazonEC2AttsConstraints.afm");
//		AttributedFeature f1 = afm.searchFeatureByName("OS");
//		AttributedFeature f2 = afm.searchFeatureByName("INSTANCE_TYPE");
//		AttributedFeature f3 = afm.searchFeatureByName("LOCATION");
//		AttributedFeature f4 = afm.searchFeatureByName("CLOUD_WATCH");
//		AttributedFeature f5 = afm.searchFeatureByName("DEDICATED");
//		AttributedFeature f6 = afm.searchFeatureByName("SPECIAL_NEEDS");
		
		AWSConfigDescriptor descriptor = new AWSConfigDescriptor();
		Map<String,FeatureType> subtrees = new HashMap<String,FeatureType>();
		subtrees.put("OS", FeatureType.CONCRETE);
		subtrees.put("INSTANCE_TYPE", FeatureType.ABSTRACT);
		descriptor.setConfigAreas(subtrees);
		
		generator.setEC2Model(afm);
		System.out.println("#### Generating configurations... ####");
		Collection<ExtendedConfiguration> res = generator.generateConfigs(descriptor, 100);
		for (ExtendedConfiguration ec: res){
			System.out.println(generator.extendedConfig2String(ec));
			System.out.println();
		}
	}
	
	public String extendedConfig2String(ExtendedConfiguration config){
		Map<VariabilityElement,Integer> map = config.getElements();
		return map.toString();
	}

}
