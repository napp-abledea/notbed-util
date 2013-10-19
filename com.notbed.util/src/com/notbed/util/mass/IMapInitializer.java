package com.notbed.util.mass;

import java.util.Map;

/**
 * @author Alexandru Bledea
 * @since Jul 21, 2013
 */
public interface IMapInitializer<K, V> {

	/**
	 * @param map
	 */
	public void initMap(Map<K, V> map);

}