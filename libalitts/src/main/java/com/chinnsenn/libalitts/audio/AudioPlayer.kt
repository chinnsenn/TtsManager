package com.chinnsenn.libalitts.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author: 陈川
 * @date: 2022/9/21
 */
class AudioPlayer {
	companion object {
		const val SAMPLE_RATE = 16000
		const val AUDIO_STREAM_TYPE = AudioManager.STREAM_MUSIC
		const val AUDIO_CHANNEL_MASK = AudioFormat.CHANNEL_OUT_MONO
		const val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
	}

	private var mAudioTrack: AudioTrack? = null
	private var mAudioPlayListener: AudioPlayListener? = null
	private var mAudioPlayRunnable: PlayRunnable? = null
	private val mAudioDataQueue: LinkedBlockingQueue<ByteArray> = LinkedBlockingQueue<ByteArray>()
	private var mAudioState = AudioPlayState.STATE_NO_READY
	private val mMinBuffSize = AudioTrack.getMinBufferSize(
		SAMPLE_RATE,
		AUDIO_CHANNEL_MASK,
		AUDIO_ENCODING
	)
	private var mExecutor: ExecutorService? = null
	private var mIsFinishSendData = false

	init {
		mAudioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			AudioTrack.Builder()
				.setAudioAttributes(
					AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_MEDIA)
						.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
						.setLegacyStreamType(AUDIO_STREAM_TYPE)
						.build()
				)
				.setAudioFormat(
					AudioFormat.Builder()
						.setEncoding(AUDIO_ENCODING)
						.setSampleRate(SAMPLE_RATE)
						.setChannelMask(AUDIO_CHANNEL_MASK)
						.build()
				)
				.setTransferMode(AudioTrack.MODE_STREAM)
				.setBufferSizeInBytes(mMinBuffSize)
				.build()
		} else {
			AudioTrack(
				AUDIO_STREAM_TYPE,
				SAMPLE_RATE,
				AUDIO_CHANNEL_MASK,
				AUDIO_ENCODING,
				mMinBuffSize * 10,
				AudioTrack.MODE_STREAM
			)
		}
		mExecutor = Executors.newSingleThreadExecutor()
		mAudioPlayRunnable = PlayRunnable(this)
		mAudioState = AudioPlayState.STATE_READY
		mExecutor?.execute(mAudioPlayRunnable)
	}

	fun setPlayListener(listener: AudioPlayListener) {
		this.mAudioPlayListener = listener
	}

	fun play(bytes: ByteArray) {
		mAudioDataQueue.also {
			if (it.isEmpty()) {
				playState()
			}
			Timber.d("receive bytes")
			it.offer(bytes)
		}
	}

	private fun playState() {
		mAudioState = AudioPlayState.STATE_PLAY
		mIsFinishSendData = false
		mAudioTrack?.play()
		mAudioPlayListener?.onStart()
		Timber.d("Audio is Playing")
	}

	fun play(filePath: String) {
		val audioFile = File(filePath)
		if (audioFile.exists()) {
			try {
				val bis = BufferedInputStream(FileInputStream(filePath))
				playState()
				val bytes = ByteArray(mMinBuffSize)
				while (bis.read(bytes) != -1) {
					mAudioDataQueue.offer(bytes)
				}
			} finally {
		  	stop()
			}
		}
	}

	fun finishSendData() {
		mIsFinishSendData = true
	}

	fun stop() {
		mAudioState = AudioPlayState.STATE_STOP
		mAudioPlayListener?.onStop()
		mIsFinishSendData = false
		mAudioTrack?.apply {
			flush()
			pause()
			stop()
		}
		mAudioDataQueue.clear()
	}

	fun release() {
		mAudioTrack = null
		mAudioPlayListener = null
		mAudioDataQueue.clear()
		mExecutor?.shutdownNow()
	}

	enum class AudioPlayState {
		STATE_NO_READY,
		STATE_READY,
		STATE_PLAY,
		STATE_STOP
	}

	interface AudioPlayListener {

		fun onStart()

		fun onStop()

		fun onError(error: String)
	}

	class PlayRunnable(player: AudioPlayer) : Runnable {

		private val playerWeakReference = WeakReference(player)

		override fun run() {
			playerWeakReference.get()?.also { player ->
				while (true) {
					if (player.mAudioState == AudioPlayState.STATE_PLAY) {
						if (player.mAudioDataQueue.isEmpty()) {
							if (player.mIsFinishSendData) {
								player.mAudioPlayListener?.onStop()
								player.mIsFinishSendData = false
							} else {
								try {
									Thread.sleep(10)
								} catch (e: Exception) {
									Timber.e(e)
								}
							}
							continue
						}
						var data: ByteArray? = null
						try {
							data = player.mAudioDataQueue.take()
						} catch (e: InterruptedException) {
							player.stop()
							Timber.e(e)
						}
						data?.let { player.mAudioTrack?.write(it, 0, data.size) }
					} else {
						try {
							Thread.sleep(20)
						} catch (e: Exception) {
							Timber.e(e)
						}
					}
				}
			}
		}
	}
}