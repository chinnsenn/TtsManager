@file:JvmName("TtsManager")

package com.chinnsenn.libalitts

import com.alibaba.idst.nui.Constants.ModeType
import com.alibaba.idst.nui.NativeNui
import com.chinnsenn.libalitts.entity.InitializationProfile

/**
 * @author: 陈川
 * @date: 2022/9/8
 */
class TtsManager {

	companion object {
		@JvmStatic
		fun getInstance(): TtsManager {
			return Singleton.instance
		}
	}

	private var nativeNui = NativeNui(ModeType.MODE_TTS)

	fun initTTS(profile: InitializationProfile) {

	}

	private class Singleton {
		companion object {
			@JvmStatic
			val instance = TtsManager()
		}
	}
}