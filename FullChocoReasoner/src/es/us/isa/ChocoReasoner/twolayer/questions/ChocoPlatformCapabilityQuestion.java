package es.us.isa.ChocoReasoner.twolayer.questions;
/**
 * This file is not part of FaMa FW, and actually is not open source, the distribution of this piece of software is not allowed yet every rights owns to José Galindo, mail malawito@gmail.com for more info.
 */

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import static choco.Choco.*;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoValidAtributedConfigurationQuestion;
import es.us.isa.ChocoReasoner.twolayer.ChocoQuestion;
import es.us.isa.ChocoReasoner.twolayer.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormCapabilityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoPlatformCapabilityQuestion extends ChocoQuestion implements
		PlatFormCapabilityQuestion {
	Configuration conf;
	Set<Product> products;
	public Collection<GenericFeature> fmust = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fremove = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fOpenOptional = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fGroupOptional = new ArrayList<GenericFeature>();
	public ChocoPlatformCapabilityQuestion() {
		this.products = new HashSet<Product>();
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner reasoner = (es.us.isa.ChocoReasoner.twolayer.ChocoReasoner) r;
		PerformanceResult res = new ChocoResult();
		
		// 1ï¿½ mirar que la configuraciï¿½n sea valida con el choco normal.
		es.us.isa.ChocoReasoner.attributed.questions.ChocoValidAtributedConfigurationQuestion cvcq = new ChocoValidAtributedConfigurationQuestion();
		cvcq.setConfiguration(conf);
		
		res.addFields(cvcq.answer(reasoner.topLayerReasoner));
		if (true) {
			reasoner.applyStagedConfiguration(conf);
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
				Collection<GenericFeature> optionalFeats = new LinkedList<GenericFeature>();

				Collection<IntegerVariable> optionalsVars = new LinkedList<IntegerVariable>();
				solver.propagate();
				for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
					IntegerVariable intVar = chocoProblem.getIntVar(i);
					IntDomainVar aux = solver.getVar(intVar);
					
						GenericFeature f = getFeature(aux, reasoner);
						if (f != null) {
//							System.out.println(f.getName()+" domain :"+aux.getDomain());
							if(aux.getVal()==0&&aux.getDomain().getInf()==aux.getDomain().getSup()){
//								System.out.println(": "+f.getName());
								notP.add(aux);
								fremove.add(f);
								
							}else if(aux.getVal()==1&&aux.getDomain().getInf()==aux.getDomain().getSup()){
//								System.out.println("Must feature: "+f.getName());
								must.add(aux);
								fmust.add(f);
							}else{
//								System.out.println("Not defined_The feature :"+ f.getName());
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
//						System.out.println("The var "+aux+" is not a real option");

						fOpenOptional.add(f);
					}else if(optionalFeats.contains(f)&&aux.getVal()==0){
//						System.out.println("The var "+aux+" is a real option");
						fGroupOptional.add(f);
					}
				}
			}} catch (ContradictionException e) {}
//			solver.read(chocoProblem);
//			if (solver.solve() == Boolean.TRUE && solver.isFeasible()) {
//				do {
//					Product p = new Product();
//					for (int i = 0; i < chocoProblem.getNbIntVars(); i++) {
//						IntDomainVar aux = solver.getVar(chocoProblem
//								.getIntVar(i));
//						if (aux.getVal() > 0) {// add the condition that mmust be part of the bottom layer
//							GenericFeature f = getFeature(aux, reasoner);
//							if (f != null) {
//								p.addFeature(f);
//							}
//						}
//					}
//					products.add(p);
//				} while (solver.nextSolution() == Boolean.TRUE);
//			}
//			// res.fillFields(solver);
//			// TODO
//			for(Product p : products){
//				System.out.println(p);
//			}
		}
		System.out.println("Must features");
		for(GenericFeature f : fmust){
			System.out.println(f.getName());
//			System.out.println("\n");
		}
		System.out.println("Remove features");
		for(GenericFeature f : fremove){
			System.out.println(f.getName());
//			System.out.println("\n");
		}
		System.out.println("Open optional features");
		for(GenericFeature f : fOpenOptional){
			System.out.println(f.getName());
//			System.out.println("\r\n");
		}
		System.out.println("Grouped optional features");
		for(GenericFeature f : fGroupOptional){
			System.out.println(f.getName());
//			System.out.println("\r\n");
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

	@Override
	public Collection<Product> getBottomConfigurations() {
		return products;
	}

	@Override
	public void setConfTop(Configuration initConfig) {
		this.conf = initConfig;
	}

}
