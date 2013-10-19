/**
 *
 */
package com.notbed.util.mass;

import java.util.Comparator;
import java.util.TreeMap;

import com.notbed.util.mass.MapUtil.LazyInit;

/**
 * Simple Never Null Tree Map
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class SNNTreeMap<K, X, V extends X> extends SNNMap<K, X, V> {

	/**
	 * @param initializer
	 * @param comparator
	 */
	public SNNTreeMap(LazyInit<X> initializer, Comparator<? super K> comparator) {
		super(new TreeMap<K, V>(comparator), initializer);
	}

}
