package com.chinnsenn.ali_tts_manager

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chinnsenn.ali_tts_manager.databinding.FragmentFirstBinding
import com.chinnsenn.libalitts.TtsManager
import com.chinnsenn.libalitts.entity.InitializationProfile
import com.chinnsenn.libalitts.listeners.ITokenProvider

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@Suppress("DEPRECATION")
class FirstFragment : Fragment() {

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

		binding.buttonFirst.setOnClickListener {
			findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
		}

		val ttsManager = TtsManager.Builder(requireContext())
			.setInitProfile(InitializationProfile().apply {
				this.workspace = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath
				this.deviceId = "12839012890d"
			})
			.setTokenProvider(object : ITokenProvider{
				override fun getToken(callback: ITokenProvider.OnTokenResultCallback) {
					callback.onSuccess("7c64af37fe47463e84ca58e3c6b875ba")
				}

			})
			.build()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}