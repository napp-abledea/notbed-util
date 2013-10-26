/**
 *
 */
package com.notbed.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Alexandru Bledea
 * @since Sep 22, 2013
 */
public class UString {

	/**
	 * @param s
	 * @return
	 */
	public static boolean empty(String s) {
		return s == null || s.isEmpty();
	}

	/**
	 * @param collection
	 * @param separator
	 * @return
	 */
	public static String join(Collection<String> collection, String separator) {
		StringBuilder sb = new StringBuilder();
		if (!collection.isEmpty()) {
			Iterator<String> iterator = collection.iterator();
			boolean hasNext = true;
			while (hasNext) {
				sb.append(iterator.next());
				hasNext = iterator.hasNext();
				if (hasNext) {
					sb.append(separator);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * @param string
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String pad(String string, String prefix, String suffix) {
		string = getNotNull(string);
		if (!empty(prefix) && !string.startsWith(prefix)) {
			string = prefix.concat(string);
		}
		if (!empty(suffix) && !string.endsWith(suffix)) {
			string = string.concat(suffix);
		}
		return string;
	}

	/**
	 * @param string
	 * @return
	 */
	public static String getNotNull(String string) {
		if (empty(string)) {
			return "";
		}
		return string;
	}

	/**
	 * @param string
	 * @param separator
	 * @return
	 */
	public static String[] splitAndTrim(String string, String separator) {
		String[] split = string.split(separator);
		int length = split.length;
		for (int i = 0; i < length; i++) {
			String trim = split[0].trim();
			if (!trim.equals(split[0])) {
				split[0] = trim;
			}
		}
		return split;
	}

	/**
	 * @param string
	 * @param count
	 * @param separator
	 * @return
	 */
	public static String join(String string, int count, String separator) {
		return join(createCollection(string, count), separator);
	}

	/**
	 * @param string
	 * @param count
	 * @return
	 */
	public static Collection<String> createCollection(String string, int count) {
		Collection<String> strings = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			strings.add(string);
		}
		return strings;
	}

	/**
	 * @param string
	 * @param separator
	 * @param returnEmptyLines
	 * @return
	 */
	public static List<String> breakAndTrimLines(String string, String separator, boolean returnEmptyLines) {
		if (string == null) {
			string = "";
		}
		List<String> nonEmptyStrings = new ArrayList();
		String[] pieces = string.split(separator);
		for (String piece : pieces) {
			piece = piece.trim();
			if (returnEmptyLines || !piece.isEmpty()) {
				nonEmptyStrings.add(piece);
			}
		}
		return nonEmptyStrings;
	}

	/**
	 * @param string
	 * @param separator
	 * @return
	 */
	public static List<String> breakAndTrimLines(String string, String separator) {
		return breakAndTrimLines(string, separator, false);
	}

}