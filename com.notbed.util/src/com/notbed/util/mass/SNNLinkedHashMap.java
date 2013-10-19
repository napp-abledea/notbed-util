/**
 *
 */
package com.notbed.util.mass;

import java.util.LinkedHashMap;

import com.notbed.util.mass.MapUtil.LazyInit;

/**
 * Simple Never Null LinkedHashMap
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class SNNLinkedHashMap<K, X, V extends X> extends SNNMap<K, X, V> {

	/**
	 * @param initializer
	 */
	public SNNLinkedHashMap(LazyInit<X> initializer) {
		super(new LinkedHashMap<K, V>(), initializer);
	}

}
