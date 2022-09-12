package com.chinnsenn.libalitts.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * @author: 陈川
 * @date: 2022/9/9
 */
object JsonHelper {
	private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create()

	fun <T> string2Bean(json: String, clazz: Class<T>): T? {
		return try {
			gson.fromJson(json, clazz)
		} catch (e: JsonSyntaxException) {
			null
		}
	}

	fun <T> bean2Json(t: T): String? {
		return try {
			gson.toJson(t)
		} catch (e: JsonIOException) {
			null
		}
	}

	fun string2Map(json: String): Map<String, String>? {
		return try {
			gson.fromJson(json, object : TypeToken<Map<String, String>>() {}.type)
		} catch (e: JsonSyntaxException) {
			null
		}
	}

	fun <T> bean2Map(t: T): Map<String, String>? {
		return try {
			gson.fromJson(bean2Json(t), object : TypeToken<Map<String, String>>() {}.type)
		} catch (e: JsonSyntaxException) {
			null
		}
	}
}