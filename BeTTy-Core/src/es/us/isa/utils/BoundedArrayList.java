/**
 * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.us.isa.utils;

import java.util.ArrayList;
import java.util.Collection;

public class BoundedArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = -6371694213631010499L;
	long sizeLimit;

	public BoundedArrayList(long sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public boolean add(E o) {
		if (this.size() == (sizeLimit - 1)) {
			throw new ArrayIndexOutOfBoundsException("Incorrect number of products");
		} else {
			return super.add(o);
		}
	}

	public boolean addAll(Collection<? extends E> sublist){
		if ((this.size() + sublist.size()) > sizeLimit) {
			throw new ArrayIndexOutOfBoundsException("Incorrect number of products");
		}else{
			return super.addAll(sublist);}
	}

}
