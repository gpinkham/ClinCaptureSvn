package com.clinovo.util;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Collection utility.
 */
public final class CollectionUtil {

	private CollectionUtil() {
	}

	/**
	 * Returns comma-separated string representation of a collection.
	 *
	 * @param collection collection to transform.
	 * @return String
	 */
	public static String collectionToString(Collection<? extends Serializable> collection) {
		return collection.toString().replaceAll("\\[|\\]|\\s*", "");
	}

	/**
	 * Parses input comma-separated string to a set of enum constants of specified enum class.
	 *
	 * @param strRepresentation comma-separated enum constant names.
	 * @param enumClass         required enum class.
	 * @return Set a set of enum constants.
	 */
	public static <E extends Enum<E>> Set<E> stringToEnumSet(Class<E> enumClass, String strRepresentation) {

		Set<E> enumSet = new HashSet<E>();
		if (StringUtils.isNotEmpty(strRepresentation)) {
			for (String constName: Arrays.asList(strRepresentation.split(","))) {
				constName = constName.trim();
				if (constName.length() > 0) {
					enumSet.add(E.valueOf(enumClass, constName));
				}
			}
		}
		return enumSet;
	}

	/**
	 * Parses input comma-separated string to a set of strings.
	 *
	 * @param strRepresentation comma-separated values.
	 * @return Set a set of strings.
	 */
	public static Set<String> stringToStringSet(String strRepresentation) {

		Set<String> stringSet = new HashSet<String>();
		if (StringUtils.isNotEmpty(strRepresentation)) {
			for (String strValue: Arrays.asList(strRepresentation.split(","))) {
				strValue = strValue.trim();
				if (strValue.length() > 0) {
					stringSet.add(strValue);
				}
			}
		}
		return stringSet;
	}
}
