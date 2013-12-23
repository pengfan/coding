/**
 * @author WangWenbin
 * @date 2013-11-28
 * @TODO 
 */
package com.codingPower.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JSONBeanUtil {

	public static <T> T getObjectFromJson(String json, Class<T> valueType) {
		T bean = null;

		try {
			Gson gson = new Gson();
			bean = gson.fromJson(json, valueType);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return bean;
	}

	public static String getJsonFromObject(Object valueType) {

		Gson gson = new Gson();
		return gson.toJson(valueType);
	}
}
