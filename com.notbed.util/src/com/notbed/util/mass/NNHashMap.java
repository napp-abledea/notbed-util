/**
 *
 */
package com.notbed.util.mass;

import java.util.HashMap;


/**
 * Never Null Hash Map
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class NNHashMap<K, X, V extends X> extends NNMap<K, X, K, V> {

	/**
	 * @param initializer
	 */
	public NNHashMap(IEvaluator<K, X> initializer) {
		super(new HashMap<K, V>(), initializer);
	}

}
