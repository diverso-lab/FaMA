/*
 * Created on 15-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package featureModel;

/**
 * @author trinidad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequiresDependency extends Dependency {
	public RequiresDependency (Feature from, Feature to) {
		this.depType = Dependency.DEPENDS;
		this.origin = from;
		this.dest = to;
	}
	
	public String toString() {
		String res = "{" + origin.getName() + " requires " + dest.getName() +  "}\n";;
		return res;
	}
}
