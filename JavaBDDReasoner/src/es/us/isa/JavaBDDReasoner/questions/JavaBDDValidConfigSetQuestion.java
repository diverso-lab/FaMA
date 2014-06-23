// XXX NO FUNCIONA CORRECTAMENTE

//package es.us.isa.JavaBDDReasoner.questions;
//
//import java.util.Collection;
//import java.util.LinkedList;
//
//import net.sf.javabdd.BDD;
//import es.us.isa.FAMA.Benchmarking.PerformanceResult;
//import es.us.isa.FAMA.Reasoner.Reasoner;
//import es.us.isa.FAMA.Reasoner.questions.ValidConfigurationSetQuestion;
//import es.us.isa.FAMA.stagedConfigManager.Configuration;
//import es.us.isa.JavaBDDReasoner.JavaBDDQuestion;
//import es.us.isa.JavaBDDReasoner.JavaBDDReasoner;
//import es.us.isa.JavaBDDReasoner.JavaBDDResult;
//
//public class JavaBDDValidConfigSetQuestion extends JavaBDDQuestion implements
//		ValidConfigurationSetQuestion {
//
//	private Collection<Configuration> configs;
//	private Collection<Configuration> invalidConfigs;
//	
//	public void setConfigurationSet(Collection<Configuration> configs) {
//		this.configs = configs;
//	}
//
//	public Collection<Configuration> getInvalidConfigurations() {
//		return invalidConfigs;
//	}
//
//	public int getNumberOfInvalidConfigs() {
//		return invalidConfigs.size();
//	}
//	
//	@Override
//	public PerformanceResult answer(Reasoner r){
//		JavaBDDResult result = new JavaBDDResult();
//		JavaBDDReasoner bddr = (JavaBDDReasoner)r;
//		invalidConfigs = new LinkedList<Configuration>();
//		
//		BDD problem = bddr.getBDD();
//		BDD problemPropagated = problem.satOne();
//		
//		int i = 0;
//		for (Configuration c: configs){
//			i++;
//			long initTime = System.currentTimeMillis();
//			BDD constraint = bddr.parseConfiguration(c);
//			BDD union = problemPropagated.and(constraint);
//			BDD sol = union.satOne();
//			long time = System.currentTimeMillis() - initTime;
//			boolean b = !sol.isZero();
//			System.out.println("Config "+i+":"+c+". Valid: "+b+". Time: "+time);
//			if (!b){
//				invalidConfigs.add(c);
//			}
//		}
//		
//		return result;
//	}
//
//}
