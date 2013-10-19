package com.notbed.util.mass;

/**
 * @author Alexandru Bledea
 * @since Jul 14, 2013
 */
public class DuplicateKeyException extends Exception {

	/**
	 * @param key
	 */
	public DuplicateKeyException(Object key) {
		super("Duplicate key found: ".concat(String.valueOf(key)));
	}

}
