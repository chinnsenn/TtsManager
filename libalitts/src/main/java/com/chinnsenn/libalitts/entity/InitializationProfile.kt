package com.chinnsenn.libalitts.entity

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
	@SerializedName("device_id")
	var deviceId: String? = null
}