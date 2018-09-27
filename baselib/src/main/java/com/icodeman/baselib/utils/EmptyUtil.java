package com.icodeman.baselib.utils;

import java.util.Collection;

public class EmptyUtil {
	
	/**
	 * 判断一个字符串是否为null
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * 判断一个数组是否为null
	 * @param array
	 * @return
	 */
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}
	
	/**
	 * 判断一个集合是否为null
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.size() == 0;
	}
}
