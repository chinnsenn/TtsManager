package com.chinnsenn.libalitts.entity

import com.alibaba.idst.nui.Constants
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
	var logLevel: Constants.LogLevel = Constants.LogLevel.LOG_LEVEL_NONE

	@Transient
	var saveLog = false

	fun checkParameter(): Boolean {
		return token.isNullOrEmpty()
	}

	fun isPrintLog() = logLevel < Constants.LogLevel.LOG_LEVEL_NONE
}