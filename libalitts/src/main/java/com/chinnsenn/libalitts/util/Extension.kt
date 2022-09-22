package com.chinnsenn.libalitts.util

import android.os.Looper
import com.alibaba.idst.nui.INativeTtsCallback

/**
 * @author: 陈川
 * @date: 2022/9/16
 */

fun Thread.isMainThread() = Looper.getMainLooper().thread == this

fun INativeTtsCallback.TtsEvent.toEventName() = when (this) {
	INativeTtsCallback.TtsEvent.TTS_EVENT_START -> {
		"TTS_EVENT_START"
	}
	INativeTtsCallback.TtsEvent.TTS_EVENT_RESUME -> {
		"TTS_EVENT_RESUME"
	}
	INativeTtsCallback.TtsEvent.TTS_EVENT_END -> {
		"TTS_EVENT_END"
	}
	INativeTtsCallback.TtsEvent.TTS_EVENT_CANCEL -> {
		"TTS_EVENT_CANCEL"
	}
	INativeTtsCallback.TtsEvent.TTS_EVENT_PAUSE -> {
		"TTS_EVENT_PAUSE"
	}
	INativeTtsCallback.TtsEvent.TTS_EVENT_ERROR -> {
		"TTS_EVENT_ERROR"
	}
}