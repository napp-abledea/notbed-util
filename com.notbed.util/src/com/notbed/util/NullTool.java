package com.notbed.util;

/**
 * @author Alexandru Bledea
 * @since Sep 21, 2013
 */
public class NullTool {

	/**
	 * @param o
	 * @param message
	 */
	public static void forbidNull(Object o, String message) {
		if (o == null) {
			throw new NullPointerException(message);
		}
	}

	/**
	 * @param o
	 */
	public static void forbidNull(Object... o) {
		for (Object object : o) {
			forbidNull(object, "Missing property");
		}
	}

}
