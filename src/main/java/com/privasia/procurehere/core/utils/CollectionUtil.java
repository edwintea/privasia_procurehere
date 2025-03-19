package com.privasia.procurehere.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionUtil {
	private CollectionUtil() {
	}

	/**
	 * true if collection is null or empty.
	 * 
	 * @param collection
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || (collection != null && collection.isEmpty());
	}

	/**
	 * true if collection is not null and not empty.
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	/**
	 * Remove duplicate object base on hashcode
	 * 
	 * @param retList
	 */
	public static <T> List<T> removeDuplicateInList(List<T> retList) {
		Set<T> set = new HashSet<T>(retList);
		retList = new ArrayList<T>(set);
		return retList;
	}

	/**
	 * Convert Arrays to HashSet
	 * 
	 * @param <T>
	 * @param objects
	 * @return
	 */
	@SafeVarargs
	public static <T> Set<T> toSet(T... objects) {
		List<T> list = Arrays.asList(objects);

		return new HashSet<T>(list);
	}

	/**
	 * Concatenates two arrays.
	 * 
	 * @param array1 - first array
	 * @param array2 - second array
	 * @param <T> - object class
	 * @return array contatenation
	 */
	@SafeVarargs
	public static <T> T[] concatenate2Arrays(T[] array1, T... array2) {
		List<T> result = new ArrayList<T>();
		result.addAll(Arrays.asList(array1));
		result.addAll(Arrays.asList(array2));

		return result.toArray(array1);
	}

	/**
	 * Make the log more meanings
	 * 
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @return
	 */
	public static <K, V> String toLog(Map<K, V> map) {
		// using StringBuffer instead of String because expect there are many append operation
		StringBuffer sb = new StringBuffer();

		if (map == null) {
			return null;
		}
		if (map.isEmpty()) {
			return map.toString();
		}

		sb.append("{");

		for (Iterator<K> iterator = map.keySet().iterator(); iterator.hasNext();) {
			K key = iterator.next();
			Object value = map.get(key);
			sb.append(key).append("=");
			sb.append(toString4Log(value));
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	/**
	 * Make the log more meanings
	 * 
	 * @param <E>
	 * @param collec
	 * @return
	 */
	public static <E> String toLog(Collection<E> collec) {
		// using StringBuffer instead of String because expect there are many append operation
		StringBuffer sb = new StringBuffer();

		if (collec == null) {
			return null;
		}
		if (collec.isEmpty()) {
			return collec.toString();
		}
		sb.append("[");
		for (Iterator<E> iterator = collec.iterator(); iterator.hasNext();) {
			E value = iterator.next();
			sb.append(toString4Log(value));
			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("]");
		return sb.toString();
	}

	private static String toString4Log(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Short) {
			return "" + ((Short) value).shortValue();
		} else if (value instanceof Integer) {
			return "" + ((Integer) value).intValue();
		} else if (value instanceof Long) {
			return "" + ((Long) value).longValue();
		} else if (value instanceof Float) {
			return "" + ((Float) value).floatValue();
		} else if (value instanceof Double) {
			return "" + ((Double) value).doubleValue();
		} else if (value instanceof Object[]) {
			Object[] objs = (Object[]) value;
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			for (int i = 0; i < objs.length; i++) {
				sb.append(toString4Log(objs[i]));

				if (i != objs.length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]");
			return sb.toString();
		}

		else
			return value.toString();
	}
}
