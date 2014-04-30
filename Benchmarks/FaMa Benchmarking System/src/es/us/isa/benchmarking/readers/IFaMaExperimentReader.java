package es.us.isa.benchmarking.readers;


import java.util.Collection;

public interface IFaMaExperimentReader {
	Collection<String> getReasoners(String path);
	Collection<String> getQuestions(String path);
}
