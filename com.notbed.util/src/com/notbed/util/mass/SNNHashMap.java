/**
 *
 */
package com.notbed.util.mass;

import java.util.HashMap;

import com.notbed.util.mass.MapUtil.LazyInit;


/**
 * Simple Never Null Hash Map
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class SNNHashMap<K, X, V extends X> extends SNNMap<K, X, V> {

	/**
	 * @param initializer
	 */
	public SNNHashMap(LazyInit<X> initializer) {
		super(new HashMap<K, V>(), initializer);
	}

}
