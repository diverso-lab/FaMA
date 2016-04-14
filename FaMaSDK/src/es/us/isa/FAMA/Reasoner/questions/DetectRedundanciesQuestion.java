package es.us.isa.FAMA.Reasoner.questions;

import java.util.Collection;

import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;

public interface DetectRedundanciesQuestion {
	public Collection<VariabilityElement> getRedundancies();
}
