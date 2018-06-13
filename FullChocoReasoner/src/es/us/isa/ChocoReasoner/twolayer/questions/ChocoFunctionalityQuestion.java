package es.us.isa.ChocoReasoner.twolayer.questions;
/**
 * This file is not part of FaMa FW, and actually is not open source, the distribution of this piece of software is not allowed yet every rights owns to José Galindo, mail malawito@gmail.com for more info.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoValidAtributedConfigurationQuestion;
import es.us.isa.ChocoReasoner.twolayer.ChocoQuestion;
import es.us.isa.ChocoReasoner.twolayer.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.twolayer.PlatFormFunctionalityQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class ChocoFunctionalityQuestion extends ChocoQuestion implements
		PlatFormFunctionalityQuestion {
	Configuration conf;
	Collection<Product> products;
	public Collection<GenericFeature> fmust = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fremove = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fOpenOptional = new ArrayList<GenericFeature>();
	public Collection<GenericFeature> fGroupOptional = new ArrayList<GenericFeature>();
	public ChocoFunctionalityQuestion() {
		this.products = new LinkedList<Product>();
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner reasoner = (es.us.isa.ChocoReasoner.twolayer.ChocoReasoner) r;

		PerformanceResult res = new ChocoResult();
		
		// 1ï¿½ mirar que la configuraciï¿½n sea valida con el choco normal.
		es.us.isa.ChocoReasoner.attributed.questions.ChocoValidAtributedConfigurationQuestion cvcq = new ChocoValidAtributedConfigurationQuestion();
		cvcq.setConfiguration(conf);
		
		res.addFields(cvcq.answer(reasoner.bottomLayerReasoner));
		if (true) {
			reasoner.unapplyStaged();
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
				Collection<GenericFeature> optionalFeats = new LinkedList<GenericFeature>();

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
								optionalFeats.add(f);
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
				System.out.println("Must features");
				for(GenericFeature f : fmust){
					System.out.println(f.getName());
//					System.out.println("\n");
				}
				System.out.println("Remove features");
				for(GenericFeature f : fremove){
					System.out.println(f.getName());
//					System.out.println("\n");
				}
				System.out.println("Open optional features");
				for(GenericFeature f : optionalFeats){
					System.out.println(f.getName());
//					System.out.println("\r\n");
				}
				
			} catch (ContradictionException e) {
				System.err.println(e);}		
			}
		reasoner.unapplyStaged();

		return res;

	}

	private GenericFeature getFeature(IntDomainVar aux, ChocoReasoner reasoner) {
		String temp = new String(aux.toString().substring(0,
				aux.toString().indexOf(":")));
		// solo me valen las features de abajo
		GenericFeature f = reasoner.topLayerReasoner.searchFeatureByName(temp);
		return f;
	}

	@Override
	public Collection<Product> getTopConfigurations() {
		return this.products;
	}

	@Override
	public void setBottom(Configuration confBottom) {
		this.conf = confBottom;
	}

}
