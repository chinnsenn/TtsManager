package com.chinnsenn.libalitts.task

import com.alibaba.idst.nui.INativeTtsCallback
import com.alibaba.idst.nui.NativeNui
import com.chinnsenn.libalitts.entity.InitializationProfile
import com.chinnsenn.libalitts.util.JsonHelper

/**
 * @author: 陈川
 * @date: 2022/9/12
 */
class InitializationTask(
	private var nativeNui: NativeNui?,
	private var profile: InitializationProfile,
	private var callBack: CustomNuiNativeCallback
) : Runnable {

	override fun run() {
		val ticket = JsonHelper.bean2Json(profile)
		nativeNui?.also {
			val ret: Int = it.tts_initialize(
				/*object : INativeTtsCallback {
					*//**
					 * 事件回调
					 * @param event：回调事件，参见如下事件列表。
					 * @param taskId：请求的任务ID。
					 * @param retCode：参见错误码，出现TTS_EVENT_ERROR事件时有效。
					 * 名称 说明
					 * TTS_EVENT_START 语音合成开始，准备播放。
					 * TTS_EVENT_END 语音合成播放结束
					 * TTS_EVENT_CANCEL 取消语音合成
					 * TTS_EVENT_PAUSE 语音合成暂停
					 * TTS_EVENT_RESUME 语音合成恢复
					 * TTS_EVENT_ERROR 语音合成发生错误
					 *//*
					override fun onTtsEventCallback(event: INativeTtsCallback.TtsEvent?, taskId: String?, retCode: Int) {
						callBack.onTtsEventCallback(event, taskId, retCode)
					}

					*//**
					 * 合成数据回调
					 * @param info：使用时间戳功能时，返回JSON格式的时间戳结果。
					 * @param infoLen：info字段的数据长度，暂不使用。
					 * @param data：合成的音频数据，写入播放器。
					 *//*
					override fun onTtsDataCallback(info: String?, infoLen: Int, data: ByteArray?) {
						callBack.onTtsDataCallback(info, infoLen, data)
					}

					override fun onTtsVolCallback(volume: Int) = Unit
				}.also { callBack ->
					mCallback = callBack
				}*/
				callBack,
				ticket,
				profile.logLevel,
				profile.saveLog
			)
			callBack.onInitResult(ret)
		}

	}

	interface CustomNuiNativeCallback : INativeTtsCallback {
		fun onInitResult(retCode: Int)
	}
}
