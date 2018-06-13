package es.us.isa.FAMA.Reasoner.questions.twolayer;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public interface TwoLayerQuestion extends Question {

	public void setTopLayer(VariabilityModel vm1);
	public void setBottomLayer(VariabilityModel vm2);
	public void setInterModelRelationships(String path);
	public VariabilityModel getTopLayer();
	public VariabilityModel getBottomLayer();
	public String getInterModelRelationsips();
}
