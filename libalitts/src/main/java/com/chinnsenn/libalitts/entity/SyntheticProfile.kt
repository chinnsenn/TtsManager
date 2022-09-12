package com.chinnsenn.libalitts.entity

import com.google.gson.annotations.SerializedName

/**
 * @author: 陈川
 * @date: 2022/9/8
 */
class SyntheticProfile {
	var appKey: String? = null
	var token: String? = null

	@SerializedName("direct_host")
	var directHost: String? = null

	@SerializedName("font_name")
	var fontName: String? = null
	@SerializedName("encode_type")
	var encodeType: String? = null
	@SerializedName("sample_rate")
	var sampleRate: String? = null
	var volume: String? = null
	@SerializedName("speed_level")
	var speedLevel: String? = null
	@SerializedName("pitch_level")
	var pitchLevel: String? = null
	@SerializedName("enable_subtitle")
	var enableSubtitle: String? = null
}