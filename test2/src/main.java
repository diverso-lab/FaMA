import java.util.Iterator;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FAMAAttributedFeatureModel afm = new FAMAAttributedFeatureModel();
		
		
		AttributedFeature r = afm.getRoot();
		Iterator<Relation> relations = r.getRelations();
		
	}

}
