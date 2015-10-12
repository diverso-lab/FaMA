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
public abstract class Dependency {
	public final static int EXCLUDES = 1;
	public final static int DEPENDS  = 2;
	protected int depType;
	protected Feature origin,dest;
	
	public int getType() {
		return depType;
	}
	
	public Feature getOrigin() {
		return origin;
	}
	
	public void setOrigin (Feature f) {
		if ( f != null) {
			origin = f;
		}
	}
	
	public Feature getDest() {
		return dest;
	}
	
	public void setDestination (Feature f) {
		if ( f != null) {
			dest = f;
		}
	}

}
