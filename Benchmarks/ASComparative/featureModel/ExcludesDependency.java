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
public class ExcludesDependency extends Dependency {
	public ExcludesDependency(Feature orig, Feature dest) {
		this.depType = Dependency.EXCLUDES;
		this.origin = orig;
		this.dest = dest;
	}
	
	public String toString() {
		String res = "{" + origin.getName() + " excludes " + dest.getName() + "}\n";
		return res;
	}
}
