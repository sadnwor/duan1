package com.app.utils;

/**
 *
 * @author inuHa
 */
public class RouterUtils {

    public static String getPackageName(Class<?> clazz) { 
	return clazz.getPackage().getName() + "." + clazz.getSimpleName();
    }
    
}
