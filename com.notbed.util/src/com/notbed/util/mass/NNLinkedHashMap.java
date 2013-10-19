/**
 *
 */
package com.notbed.util.mass;

import java.util.LinkedHashMap;

/**
 * Never Null Linked Hash Map
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class NNLinkedHashMap<K, X, V extends X> extends NNMap<K, X, K, V> {

	/**
	 * @param initializer
	 */
	public NNLinkedHashMap(IEvaluator<K, X> initializer) {
		super(new LinkedHashMap<K, V>(), initializer);
	}

}
