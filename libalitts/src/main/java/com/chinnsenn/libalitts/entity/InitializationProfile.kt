package com.chinnsenn.libalitts.entity

import com.alibaba.idst.nui.Constants
import com.chinnsenn.libalitts.BuildConfig
import com.google.gson.annotations.SerializedName

/**
 * @author: 陈川
 * @date: 2022/9/8
 */
class InitializationProfile {
	var workspace: String? = null

	var token: String? = null

	@SerializedName("app_key")
	var appKey: String? = null

	var url : String = "wss://nls-gateway.cn-shanghai.aliyuncs.com:443/ws/v1"

	@SerializedName("device_id")
	var deviceId: String? = null

	@Transient
	var logLevel: Constants.LogLevel = if (BuildConfig.DEBUG) Constants.LogLevel.LOG_LEVEL_VERBOSE else Constants.LogLevel.LOG_LEVEL_NONE

	@Transient
	var saveLog = BuildConfig.DEBUG

	fun checkParameter(): Boolean {
		return token.isNullOrEmpty()
	}
}