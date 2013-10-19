/**
 *
 */
package com.notbed.util.mass;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Never Null Tree Map
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class NNTreeMap<K, X, V extends X> extends NNMap<K, X, K, V> {

	/**
	 * @param initializer
	 * @param comparator
	 */
	public NNTreeMap(IEvaluator<K, X> initializer, Comparator<? super K> comparator) {
		super(new TreeMap<K, V>(comparator), initializer);
	}


}
