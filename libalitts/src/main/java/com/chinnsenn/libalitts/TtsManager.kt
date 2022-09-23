@file:JvmName("TtsManager")

package com.chinnsenn.libalitts

import android.content.Context
import com.alibaba.idst.nui.CommonUtils
import com.alibaba.idst.nui.Constants
import com.alibaba.idst.nui.Constants.ModeType
import com.alibaba.idst.nui.INativeTtsCallback
import com.alibaba.idst.nui.NativeNui
import com.chinnsenn.libalitts.audio.AudioPlayer
import com.chinnsenn.libalitts.entity.InitializationProfile
import com.chinnsenn.libalitts.listeners.ITokenProvider
import com.chinnsenn.libalitts.task.InitializationTask
import com.chinnsenn.libalitts.util.toEventName
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author: 陈川
 * @date: 2022/9/8
 */
class TtsManager @Throws(Exception::class) private constructor(builder: Builder) {

	companion object {
		const val priority = "1"
	}

	private var mContext: Context? = null

	private var mNativeNui: NativeNui? = NativeNui(ModeType.MODE_TTS)

	private var mInitProfile: InitializationProfile? = null

	private var mExecutor: ExecutorService? = null

	private var mTokenProvider: ITokenProvider? = null

	private var mInitializationTask: InitializationTask? = null

	private var mCustomCallback: TtsNuiNativeCallback? = null

	private var mAudioPlayer: AudioPlayer? = null

	private var mSDKListener: SDKListener? = null

	private var isInit = false

	private var enable = true

	init {
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
		this.mContext = builder.getContext()
		this.mInitProfile = builder.getInitializationProfile()
		this.mSDKListener = builder.getSdkListener()
		this.mInitProfile?.workspace = CommonUtils.getModelPath(mContext);
		this.mTokenProvider = builder.getTokenProvider()
		this.mExecutor = Executors.newSingleThreadExecutor()
		this.mAudioPlayer = AudioPlayer()
		this.mCustomCallback = TtsNuiNativeCallback(this)
		mSDKListener?.onInitializing()
		if (CommonUtils.copyAssetsData(mContext)) {
			Timber.d("copy assets data done")
			mTokenProvider?.getToken(object : ITokenProvider.OnTokenResultCallback {
				override fun onSuccess(token: String) {
					mInitProfile?.token = token
					mInitializationTask = InitializationTask(mNativeNui, mInitProfile!!, mCustomCallback!!)
					mExecutor?.execute(mInitializationTask)
					Timber.d("获取token成功，token:%s", token)
				}

				override fun onFailed(msg: String) {
					Timber.d("获取token失败，错误信息:%s", msg)
					mSDKListener?.onInitFailed()
				}
			})
		}
	}

	fun getContext() = mContext

	private fun checkInit(block: () -> Unit) {
		if (!enable) {
			return
		}
		if (isInit) {
			block.invoke()
		} else {
			mSDKListener?.onError("请等待 SDK 初始化完成")
		}
	}

	fun startTTS(text: String) {
		checkInit {
			mNativeNui?.cancelTts("")
			mNativeNui?.startTts(priority, "", text)
		}
	}

	fun pauseTTS() {
		checkInit {
			mNativeNui?.pauseTts()
		}
	}

	fun resumeTTS() {
		checkInit {
			mNativeNui?.apply {
				resumeTts()
			}
		}
	}

	fun cancelTTS() {
		checkInit {
			mNativeNui?.cancelTts("")
			mAudioPlayer?.stop()
		}
	}

	fun releaseTTS() {
		checkInit {
			mNativeNui?.tts_release()
		}
	}

	fun setEnable(enable: Boolean) {
		checkInit {
			if (!enable) {
				cancelTTS()
			}
		}
		this.enable = enable
	}

	fun isEnable(): Boolean {
		return this.enable
	}

	fun release() {
		mNativeNui?.tts_release()
		mNativeNui = null
		mInitProfile = null
		mExecutor?.shutdownNow()
		mTokenProvider = null
		mInitializationTask = null
		mCustomCallback = null
		mAudioPlayer?.release()
		mAudioPlayer = null
		isInit = false
		mContext = null
	}

	class Builder(context: Context) {
		private var context: Context? = context
		private var initializationProfile: InitializationProfile? = null
		private var tokenProvider: ITokenProvider? = null
		private var sdkListener: SDKListener? = null

		fun setInitProfile(initializationProfile: InitializationProfile) =
			apply { this.initializationProfile = initializationProfile }

		fun setTokenProvider(provider: ITokenProvider) =
			apply { this.tokenProvider = provider }

		fun setSdkListener(listener: SDKListener) =
			apply { this.sdkListener = listener }

		fun build(): TtsManager {
			return TtsManager(this)
		}

		fun getContext() = context

		fun getInitializationProfile() = initializationProfile

		fun getTokenProvider() = tokenProvider

		fun getSdkListener() = sdkListener
	}

	class TtsNuiNativeCallback(tsManager: TtsManager) : InitializationTask.CustomNuiNativeCallback {

		private var weakReference = WeakReference(tsManager)

		override fun onInitResult(retCode: Int) {
			weakReference.get()?.also {
				when (retCode) {
					Constants.NuiResultCode.SUCCESS -> {
						it.mSDKListener?.onInitSuccess()
						it.isInit = true
						Timber.d("初始化 TTS 成功")
						it.mNativeNui?.apply {
							setparamTts("sample_rate", "16000")
							setparamTts("font_name", "siqi")
							setparamTts("enable_subtitle", "1")
							setparamTts("encode_type", "wav")
						}
					}
				}
			}
		}

		override fun onTtsEventCallback(event: INativeTtsCallback.TtsEvent?, taskId: String?, retCode: Int) {
			Timber.d("event:%s, taskId:%s, retCode:%d", event?.toEventName(), taskId, retCode)
			weakReference.get()?.also {
				when (event) {
					INativeTtsCallback.TtsEvent.TTS_EVENT_START -> {

					}
					INativeTtsCallback.TtsEvent.TTS_EVENT_RESUME -> {
						it.mAudioPlayer?.reseume()
					}
					INativeTtsCallback.TtsEvent.TTS_EVENT_END -> {
						it.mAudioPlayer?.finishSendData()
					}
					INativeTtsCallback.TtsEvent.TTS_EVENT_CANCEL -> {
						it.mAudioPlayer?.stop()
					}
					INativeTtsCallback.TtsEvent.TTS_EVENT_PAUSE -> {
						it.mAudioPlayer?.pause()
					}
					INativeTtsCallback.TtsEvent.TTS_EVENT_ERROR -> {
						it.mAudioPlayer?.stop()
					}
					else -> {}
				}
			}
		}

		override fun onTtsDataCallback(info: String?, infoLen: Int, data: ByteArray?) {
			Timber.d("info = [${info}], infoLen = [${infoLen}], data = [${data}]")
			if (data!!.isNotEmpty()) {
				weakReference.get()?.also {
					it.mAudioPlayer?.play(data)
				}
			}

		}

		override fun onTtsVolCallback(vol: Int) = Unit
	}

	interface SDKListener {
		fun onInitSuccess()

		fun onInitializing()

		fun onInitFailed()

		fun onError(error: String)
	}

}