package es.us.isa.cpoptimizer.questions;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cp.IloCP;

import java.beans.Expression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.Reasoner.questions.OptimisingPreferencesQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.stagedConfigManager.ExtendedConfiguration;
import es.us.isa.cpoptimizer.CPOptQuestion;
import es.us.isa.cpoptimizer.CPOptReasoner;
import es.us.isa.cpoptimizer.CPOptResult;
import es.us.isa.cpoptimizer.utils.RankableItem;
import es.us.isa.soup.preferences.AroundPreference;
import es.us.isa.soup.preferences.DislikesPreference;
import es.us.isa.soup.preferences.HighestPreference;
import es.us.isa.soup.preferences.LikesPreference;
import es.us.isa.soup.preferences.LowestPreference;
import es.us.isa.soup.preferences.Preference;

public class CPOptOptimisingPrefsQuestion extends CPOptQuestion implements
		OptimisingPreferencesQuestion {

	private Collection<Preference> preferences;
	private ExtendedConfiguration configuration;
	private List<ExtendedConfiguration> result;
	
	public List<ExtendedConfiguration> getRankedConfigurations() {
		return result;
	}

	public void setConfiguration(ExtendedConfiguration config) {
		configuration = config;

	}

	public void setPreferences(Collection<Preference> prefs) {
		preferences = prefs;
	}

	@Override
	public PerformanceResult answer(Reasoner r) {
		result = new LinkedList<ExtendedConfiguration>();
		List<RankableItem<ExtendedConfiguration>> auxList = 
				new ArrayList<RankableItem<ExtendedConfiguration>>();
		CPOptReasoner reasoner = (CPOptReasoner) r;
		IloCP cp = reasoner.getCp();
		CPOptResult perfResult = new CPOptResult();
		
		Map<String, IloIntVar> featVars = reasoner.getFeatureVars();
		Map<String, IloNumVar> attVars = reasoner.getAttVars();
		Map<String, GenericFeature> features = reasoner.getFeatures();
		Map<String, GenericAttribute> atts = reasoner.getAtts();

		//0. add the attributes
		reasoner.addAttributedElements();
		
		// 1. map the config
		// we invoke staged config method. it considers also complex constraints
		reasoner.applyStagedConfiguration(configuration);
		
		IloNumExpr prefsExpr = null;
		long initTime = 0;
		// 2. map the preferences to an expression
		try {
			prefsExpr = preferences2Expression(preferences, reasoner);
			IloObjective obj = cp.maximize(prefsExpr);
			cp.add(obj);
			cp.propagate();
			initTime = System.currentTimeMillis();
			cp.startNewSearch();
//			long time = System.currentTimeMillis() - initTime;
//			perfResult.setTime(time);
		} catch (IloException e) {
			e.printStackTrace();
		}
		
		while(cp.next()){
			ExtendedConfiguration ec = new ExtendedConfiguration();
			Set<Entry<String, IloIntVar>> featEntrySet = featVars.entrySet();
			for (Entry<String, IloIntVar> e : featEntrySet) {
				try {
					int val = (int) cp.getValue(e.getValue());
					VariabilityElement v = features.get(e.getKey());
					ec.addElement(v, val);
				} catch (Exception exception) {
					System.err.println(e.getKey()+" => "+exception.getMessage());
				}
			}

			Set<Entry<String, IloNumVar>> attEntrySet = attVars.entrySet();
			for (Entry<String, IloNumVar> e : attEntrySet) {
				try {
					double val = cp.getValue(e.getValue());
					GenericAttribute v = atts.get(e.getKey());
					ec.addAttValue(v, val);
				} catch (Exception exception) {
					System.err.println(e.getKey()+" => "+exception.getMessage());
				}
			}
			// XXX we get the preferences value, and add both config and value
			// to the aux list
			double rankingVal = cp.getValue(prefsExpr);
			auxList.add(new RankableItem<ExtendedConfiguration>(ec, rankingVal));
		}
		
		long time = System.currentTimeMillis() - initTime;
		perfResult.setTime(time);
		
		// XXX we sort by prefs value!!
		// it's natural order, so after that
		// we have to walk the list in the reverse order
		Collections.sort(auxList);
		
		result = rankedItems2Result(auxList);
		
		return perfResult;
	}
	
	private List<ExtendedConfiguration> rankedItems2Result(
			List<RankableItem<ExtendedConfiguration>> auxList) {
		List<ExtendedConfiguration> list = new LinkedList<ExtendedConfiguration>();
		for (int i = auxList.size() - 1; i > 0; i--){
			list.add(auxList.get(i).getItem());
		}
		return list;
	}

	private IloNumExpr preferences2Expression(Collection<Preference> prefs, CPOptReasoner r) throws IloException{
		IloNumExpr[] exprs = new IloNumExpr[prefs.size()];
		
		IloCP cp = r.getCp();
		Map<String,IloNumVar> atts = r.getAttVars();
		Map<String,IloIntVar> feats = r.getFeatureVars();
		
		int i = 0;
		for (Preference p:prefs){
			if (p instanceof LikesPreference){
				IloIntVar f = feats.get(p.getItem().getName());
				exprs[i] = cp.eq(f, 1);
			}
			else if(p instanceof DislikesPreference){
				IloIntVar f = feats.get(p.getItem().getName());
				exprs[i] = cp.eq(f, 0);
			}
			else if (p instanceof AroundPreference){
				AroundPreference ap = (AroundPreference) p;
				IloNumVar var = atts.get(((GenericAttribute)ap.getItem()).getFullName());
				exprs[i] = this.aroundExpression(var, ap.getValue(), cp);
			}
			else if (p instanceof LowestPreference){
				// TODO test it!
				IloNumVar var = atts.get(((GenericAttribute)p.getItem()).getFullName());
				exprs[i] = this.aroundExpression(var, var.getLB(), cp);
			}
			else if (p instanceof HighestPreference){
				// TODO test it!
				IloNumVar var = atts.get(((GenericAttribute)p.getItem()).getFullName());
				exprs[i] = this.aroundExpression(var, var.getUB(), cp);
			}
			i++;
		}
		
		IloNumExpr result = cp.sum(exprs);
		
		return result;
	}
	
	private IloNumExpr aroundExpression(IloNumVar var, double around, IloCP cp) throws IloException{
		// TODO test it!
		double upperBound = var.getUB(), lowerBound = var.getLB();
		double upperRange = upperBound - around, lowerRange = around - lowerBound, maxRange;
		if (upperRange > lowerRange){
			maxRange = upperRange;
		}
		else{
			maxRange = lowerRange;
		}
		
		IloNumExpr result = cp.quot(cp.min(maxRange,cp.abs(cp.min(var, around))),maxRange);
		return result;
	}

}
