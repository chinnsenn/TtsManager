package com.chinnsenn.ali_tts_manager

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.chinnsenn.ali_tts_manager.databinding.FragmentFirstBinding
import com.chinnsenn.libalitts.TtsManager
import com.chinnsenn.libalitts.entity.InitializationProfile
import com.chinnsenn.libalitts.listeners.ITokenProvider

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@Suppress("DEPRECATION")
class FirstFragment : Fragment() {

	private var ttsManager : TtsManager? = null

	private val sdkListener = object : TtsManager.SDKListener {
		override fun onInitSuccess() {
			requireActivity().runOnUiThread {
				showToast("SDK 初始化成功")
			}
		}

		override fun onInitializing() {
			requireActivity().runOnUiThread {
				showToast("SDK 初始化中")
			}
		}

		override fun onInitFailed() {
			requireActivity().runOnUiThread {
				showToast("SDK 初始化失败")
			}
		}

		override fun onError(error: String) {

		}
	}

	private fun showToast(text: String) {
		Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
	}

	private var _binding: FragmentFirstBinding? = null

	// This property is only valid between onCreateView and
	// onDestroyView.
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentFirstBinding.inflate(inflater, container, false)
		return binding.root

	}

	@SuppressLint("HardwareIds")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		ttsManager = TtsManager.Builder(requireContext())
			.setInitProfile(InitializationProfile().apply {
				this.workspace = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath
				this.deviceId = android.os.Build.SERIAL
			})
			.setTokenProvider(object : ITokenProvider {
				override fun getToken(callback: ITokenProvider.OnTokenResultCallback) {
				}
			})
			.setSdkListener(sdkListener)
			.build()

		binding.buttonFirst.setOnClickListener {
			ttsManager?.startTTS("本样例展示离线语音合成使用方法，1）设置鉴权信息：按照鉴权认证文档获取注册信息，并调用接口tts_initialize进行设置；2)将下载好的语音包设置给SDK；3）开始合成")
		}

	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
		ttsManager?.release()
	}
}