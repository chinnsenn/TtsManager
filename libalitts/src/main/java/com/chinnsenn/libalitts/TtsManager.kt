@file:JvmName("TtsManager")

package com.chinnsenn.libalitts

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import com.alibaba.idst.nui.Constants
import com.alibaba.idst.nui.Constants.ModeType
import com.alibaba.idst.nui.INativeTtsCallback
import com.alibaba.idst.nui.NativeNui
import com.chinnsenn.libalitts.entity.InitializationProfile
import com.chinnsenn.libalitts.listeners.ITokenProvider
import com.chinnsenn.libalitts.task.InitializationTask
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

	private var mHandler: TtsHandler? = null

	private var mContext: Context? = null

	private var mNativeNui: NativeNui? = NativeNui(ModeType.MODE_TTS)

	private var mInitProfile: InitializationProfile? = null

	private var mExecutor: ExecutorService? = null

	private var mTokenProvider: ITokenProvider? = null

	private var mInitializationTask: InitializationTask? = null

	private var mCustomCallback: TtsNuiNativeCallback? = null

	private var isInit = false

	init {
		this.mContext = builder.context
		this.mInitProfile = builder.initializationProfile
		this.mTokenProvider = builder.tokenProvider
		this.mExecutor = Executors.newSingleThreadExecutor()
		mHandler = TtsHandler(mContext!!, mContext?.mainLooper!!)
		this.mCustomCallback = TtsNuiNativeCallback(this)
		mTokenProvider?.getToken(object : ITokenProvider.OnTokenResultCallback {
			override fun onSuccess(token: String) {
				mInitProfile?.token = token
				mInitializationTask = InitializationTask(mNativeNui, mInitProfile!!, mCustomCallback!!)
				mExecutor?.execute(mInitializationTask)
			}

			override fun onFailed(msg: String) {
				Message.obtain().apply {
					what = TtsHandler.MSG_TYPE_MESSAGE
					obj = msg
				}
			}
		})
	}

	fun getContext() = mContext

	fun getHandler() = mHandler

	fun isInit() = isInit

	fun startTTS(text: String) {
		mNativeNui?.startTts(priority, text.hashCode().toString(), text)
	}

	fun pauseTTS() {
		mNativeNui?.pauseTts()
	}

	fun resumeTTS() {
		mNativeNui?.resumeTts()
	}

	fun cancelTTS(text: String) {
		mNativeNui?.cancelTts(text.hashCode().toString())
	}

	fun releaseTTS() {
		mNativeNui?.tts_release()
	}

	fun release() {
		mNativeNui = null
		mInitProfile = null
		mExecutor?.shutdownNow()
		mTokenProvider = null
		mInitializationTask = null
		mCustomCallback = null
		isInit = false
		mContext = null
		mHandler = null
	}

	class Builder(context: Context) {
		var context: Context? = context
		var initializationProfile: InitializationProfile? = null
		var tokenProvider: ITokenProvider? = null

		fun setInitProfile(initializationProfile: InitializationProfile) =
			apply { this.initializationProfile = initializationProfile }

		fun setTokenProvider(provider: ITokenProvider) =
			apply { this.tokenProvider = provider }

		fun build(): TtsManager {
			return TtsManager(this)
		}
	}

	class TtsHandler(context: Context, looper: Looper) : Handler(looper) {

		private val contextWeakReference = WeakReference(context)

		companion object {
			const val MSG_TYPE_MESSAGE = 1
			const val MSG_INIT_SUCCESS = 2
		}

		override fun handleMessage(msg: Message) {
			contextWeakReference.get()?.also {
				when (msg.what) {
					MSG_TYPE_MESSAGE -> {
						Toast.makeText(it, msg.obj as String, Toast.LENGTH_LONG).show()
					}
				}
			}
		}
	}

	class TtsNuiNativeCallback(tsManager: TtsManager) : InitializationTask.CustomNuiNativeCallback {

		private var weakReference = WeakReference(tsManager)

		override fun onInitResult(retCode: Int) {
			weakReference.get()?.also {
				when (retCode) {
					Constants.NuiResultCode.SUCCESS -> {
						it.isInit = true
						println("初始化 TTS 成功")
					}
				}
			}
		}

		override fun onTtsEventCallback(p0: INativeTtsCallback.TtsEvent?, p1: String?, p2: Int) {

		}

		override fun onTtsDataCallback(p0: String?, p1: Int, p2: ByteArray?) {

		}

		override fun onTtsVolCallback(p0: Int) {

		}
	}


}