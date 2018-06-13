package es.us.isa.ChocoReasoner.twolayer.questions;
/**
 * This file is not part of FaMa FW, and actually is not open source, the distribution of this piece of software is not allowed yet every rights owns to José Galindo, mail malawito@gmail.com for more info.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import es.us.isa.ChocoReasoner.twolayer.ChocoQuestion;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormMigrationQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoMigrationQuestion extends ChocoQuestion implements
		PlatFormMigrationQuestion {

	Collection<GenericFeature> discartedFeatures;
	Collection<GenericFeature> addedFeatures;
	Configuration confTop;
	Configuration confBottom1;
	Configuration confBottom2;

	@Override
	public Collection<GenericFeature> getDiscartedFeatures() {
		return discartedFeatures;
	}

	@Override
	public Collection<GenericFeature> getAddedFeatures() {
		return addedFeatures;
	}

	@Override
	public void setConfTop(Configuration ConfTop) {
		this.confTop = ConfTop;
	}

	@Override
	public void setConfBottom1(Configuration confBottom1) {
		this.confBottom1 = confBottom1;
	}

	@Override
	public void setConfBottom2(Configuration confBottom2) {
		this.confBottom2 = confBottom2;

	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		ChocoFunctionalityQuestion cfq = new ChocoFunctionalityQuestion();
		cfq.setBottom(this.confBottom1);
		PerformanceResult answer = cfq.answer(r);
		
		ChocoFunctionalityQuestion cfq2 = new ChocoFunctionalityQuestion();
		cfq2.setBottom(this.confBottom2);
		cfq2.answer(r);

		Collection<GenericFeature> unaffected = new ArrayList<GenericFeature>();
		Collection<GenericFeature> enable_new = new ArrayList<GenericFeature>();
		Collection<GenericFeature> incompatible = new ArrayList<GenericFeature>();
		for(GenericFeature f :cfq2.fmust){
			if(cfq.fmust.contains(f)){
				unaffected.add(f);
			}else{
				enable_new.add(f);
			}
		}
		
		for(GenericFeature f :cfq.fmust){
			if(cfq2.fremove.contains(f)){
				incompatible.add(f);
			}
		}
		System.out.println("Unaffected");
		for(GenericFeature f : unaffected){
			System.out.println(f);
		}
		System.out.println("Enabled, new");
		for(GenericFeature f : enable_new){
			System.out.println(f);
		}
		System.out.println("incompatible");
		for(GenericFeature f : incompatible){
			System.out.println(f);
		}
		
		return answer;
		
	}

	
}
