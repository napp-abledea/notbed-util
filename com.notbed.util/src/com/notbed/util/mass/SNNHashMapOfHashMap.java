/**
 *
 */
package com.notbed.util.mass;

import java.util.Map;



/**
 * Simple Never Null Hash Map of HashMaps. how convenient
 * @author Alexandru Bledea
 * @since Oct 19, 2013
 */
public class SNNHashMapOfHashMap<K, V extends Map> extends SNNHashMap<K, Map, V> {

	/**
	 * @param initializer
	 */
	public SNNHashMapOfHashMap() {
		super(LazyInitializers.MAP_HASHMAP);
	}

}
