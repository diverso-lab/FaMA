package es.us.isa.ChocoReasoner.twolayer.questions;

/**
 * This file is not part of FaMa FW, and actually is not open source, the distribution of this piece of software is not allowed yet every rights owns to José Galindo, mail malawito@gmail.com for more info.
 */

import static choco.Choco.eq;
import static choco.Choco.ifOnlyIf;
import static choco.Choco.makeIntVar;
import static choco.Choco.neq;
import static choco.Choco.sum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.goals.choice.RemoveVal;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoValidAtributedConfigurationQuestion;
import es.us.isa.ChocoReasoner.twolayer.ChocoQuestion;
import es.us.isa.ChocoReasoner.twolayer.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormCompatibilityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeature;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoPlatformCompatibilityQuestion extends ChocoQuestion implements
		PlatFormCompatibilityQuestion {

	Collection<GenericFeature> deadFeatures;
	Collection<GenericFeature> aliveFeatures;
	Configuration inittConfiguration;
	Configuration initbConfiguration;

	
	
	public Collection<GenericFeature> fmust = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fremove = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fOpenOptional = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fGroupOptional = new ArrayList<GenericFeature>();
	Collection<GenericFeature> optionalFeats = new LinkedList<GenericFeature>();

	
	@Override
	public Collection<GenericFeature> deadFeatures() {
		return deadFeatures;
	}

	@Override
	public Collection<GenericFeature> aliveFeatures() {
		return aliveFeatures;
	}

	@Override
	public void setConfTop(Configuration inittConfig) {
		this.inittConfiguration = inittConfig;
	}

	@Override
	public void setConfBottom(Configuration initbConfig) {
		this.initbConfiguration = initbConfig;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner reasoner = (es.us.isa.ChocoReasoner.twolayer.ChocoReasoner) r;

		 
			PerformanceResult res = new ChocoResult();
			
			// 1ï¿½ mirar que la configuraciï¿½n sea valida con el choco normal.
			es.us.isa.ChocoReasoner.attributed.questions.ChocoValidAtributedConfigurationQuestion cvcq = new ChocoValidAtributedConfigurationQuestion();
			cvcq.setConfiguration(inittConfiguration);
			
			res.addFields(cvcq.answer(reasoner.topLayerReasoner));
			if (true) {
//				reasoner.applyStagedConfiguration(initbConfiguration);
				 reasoner.applyStagedConfiguration(inittConfiguration);
				 // BEFORE USE THE QUESTION WE MARK THE ATRIBUTES AS NO DECISION VAR
				Iterator<IntegerVariable> it = reasoner.getAttributesVariables()
						.values().iterator();
				while (it.hasNext()) {
					IntegerVariable var = it.next();
					var.addOption("cp:no_decision");
				}

				Model chocoProblem = reasoner.getProblem();
				Solver solver = new CPSolver();
				solver.read(chocoProblem);
				try {
					Collection<IntDomainVar> must = new LinkedList<IntDomainVar>();
					Collection<IntDomainVar> notP = new LinkedList<IntDomainVar>();
					Collection<IntDomainVar> optionals = new LinkedList<IntDomainVar>();

					Collection<IntegerVariable> optionalsVars = new LinkedList<IntegerVariable>();
					solver.propagate();
					for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
						IntegerVariable intVar = chocoProblem.getIntVar(i);
						IntDomainVar aux = solver.getVar(intVar);
						
							GenericFeature f = getFeature(aux, reasoner);
							if (f != null) {
//								System.out.println(f.getName()+" domain :"+aux.getDomain());
								if(aux.getVal()==0&&aux.getDomain().getInf()==aux.getDomain().getSup()){
//									System.out.println(": "+f.getName());
									notP.add(aux);
									fremove.add(f);
									
								}else if(aux.getVal()==1&&aux.getDomain().getInf()==aux.getDomain().getSup()){
//									System.out.println("Must feature: "+f.getName());
									must.add(aux);
									fmust.add(f);
								}else{
//									System.out.println("Not defined_The feature :"+ f.getName());
									optionals.add(aux);
									optionalFeats.add(f);
									optionalsVars.add(reasoner.variables.get(f.getName()));
								}
								//p.addFeature(f);
							}else{//is att
								if(intVar.getName().contains(".")){
									if(intVar.getDomainSize()!=aux.getDomainSize()&&aux.getDomain().getInf()!=aux.getDomain().getSup()){
										System.out.println("El atributo ha cambiado su dominio:"+aux+"["+aux.getDomain().getInf()+","+aux.getDomain().getSup()+"]");
									}
								}
								
							}
						
					}
					if(optionals.size()>0){
					IntegerVariable[] vars = new IntegerVariable[optionals.size()];
					int index=0;
					for(IntegerVariable v : optionalsVars){
						vars[index]=v;
						index++;
					}
					IntegerVariable sum = makeIntVar("sum_min",0,optionals.size());
					Constraint sumc= eq(sum,sum(vars));
					chocoProblem.addVariable(sum);
					chocoProblem.addConstraint(sumc);
					solver.read(chocoProblem);
					solver.solve();
					Solver solver2 = new CPSolver();
					solver2.read(chocoProblem);
					
					solver2.minimize(solver.getVar(sum), false);
					
					for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
						IntDomainVar aux = solver2.getVar(chocoProblem.getIntVar(i));
						GenericFeature f = getFeature(aux, reasoner);

						if(optionalFeats.contains(f)&&aux.getVal()==1){
//							System.out.println("The var "+aux+" is not a real option");

							fOpenOptional.add(f);
						}else if(optionalFeats.contains(f)&&aux.getVal()==0){
//							System.out.println("The var "+aux+" is a real option");
							fGroupOptional.add(f);
						}
					}
				}} catch (ContradictionException e) {}
//				solver.read(chocoProblem);
//				if (solver.solve() == Boolean.TRUE && solver.isFeasible()) {
//					do {
//						Product p = new Product();
//						for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
//							IntDomainVar aux = solver.getVar(chocoProblem
//									.getIntVar(i));
//							if (aux.getVal() > 0) {// add the condition that mmust be part of the bottom layer
//								GenericFeature f = getFeature(aux, reasoner);
//								if (f != null) {
//									p.addFeature(f);
//								}
//							}
//						}
//						products.add(p);
//					} while (solver.nextSolution() == Boolean.TRUE);
//				}
//				// res.fillFields(solver);
//				// TODO
//				for(Product p : products){
//					System.out.println(p);
//				}
			}
			
			for(Entry<VariabilityElement,Integer> pair:initbConfiguration.getElements().entrySet()){
				if(pair.getValue()==1&&fmust.contains(pair.getKey())||pair.getValue()==1&&optionalFeats.contains(pair.getKey())){
					System.out.println(pair.getKey()+" :is compatible");
				}
				if(pair.getValue()==1&&fremove.contains(pair.getKey())){
					System.out.println(pair.getKey()+" :is incompatible");
				}
				if(pair.getValue()==0&&fmust.contains(pair.getKey())){
					System.out.println(pair.getKey()+" :is missing");
				}
			}
			
			return res;		
		 
		
		

	}
	private GenericFeature getFeature(IntDomainVar aux, ChocoReasoner reasoner) {
		String temp = new String(aux.toString().substring(0,
				aux.toString().indexOf(":")));
		// solo me valen las features de abajo
		GenericFeature f = reasoner.bottomLayerReasoner
				.searchFeatureByName(temp);
		return f;
	}
}
