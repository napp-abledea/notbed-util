/**
 *
 */
package com.notbed.util.mass;

import java.util.Map;

import com.notbed.util.mass.MapUtil.LazyInit;

/**
 * Simple Never Null Map
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
class SNNMap<K, X, V extends X> extends NNMap<K, X, Object, V> {

	/**
	 * @param map
	 * @param initializer
	 */
	public SNNMap(Map<K, V> map, LazyInit<X> initializer) {
		super(map, initializer);
	}

}
