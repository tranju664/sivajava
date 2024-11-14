package com.internetarmy.aws.util;

import java.util.List;
import java.util.Map;

public class CoreUtilities {
	
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isEmpty() || value.isBlank();
	}
	
	public static boolean isNullOrEmpty(Object[] value) {
		return value == null || value.length ==0 ;
	}
	
	public static <T> boolean isNullOrEmpty(byte[] value) {
		return value == null || value.length == 0;
	}
	
	public static <T> boolean isNullOrEmpty(List<T> value) {
		return value == null || value.isEmpty();
	}
	
	public static <K,V> boolean isNullOrEmpty(Map<K,V> value) {
		return value == null || value.isEmpty();
	}
}
