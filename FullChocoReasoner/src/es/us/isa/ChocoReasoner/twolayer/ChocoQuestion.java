package es.us.isa.ChocoReasoner.twolayer;
/**
 * This file is not part of FaMa FW, and actually is not open source, the distribution of this piece of software is not allowed yet every rights owns to José Galindo, mail malawito@gmail.com for more info.
 */
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.twolayer.TwoLayerQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public abstract class ChocoQuestion implements TwoLayerQuestion {

	protected VariabilityModel topLayer,bottomLayer;
	protected String interModelRelationships;
	
	@Override
	public Class<? extends Reasoner> getReasonerClass() {
		return es.us.isa.ChocoReasoner.twolayer.ChocoReasoner.class;
	}

	@Override
	public void setTopLayer(VariabilityModel vm1) {
		this.topLayer=vm1;
	}

	@Override
	public void setBottomLayer(VariabilityModel vm2) {
		this.bottomLayer=vm2;
	}

	@Override
	public void setInterModelRelationships(String path) {
		this.interModelRelationships=path;
	}

	@Override
	public VariabilityModel getTopLayer() {
		return this.topLayer;
	}

	@Override
	public VariabilityModel getBottomLayer() {
		return this.bottomLayer;
	}

	@Override
	public String getInterModelRelationsips() {
		return this.interModelRelationships;
	}

	public abstract PerformanceResult answer(Reasoner r);
	
	
}
