package com.chinnsenn.libalitts.listeners

/**
 * @author: 陈川
 * @date: 2022/9/18
 */
interface ITokenProvider {

	fun getToken(callback: OnTokenResultCallback)

	interface OnTokenResultCallback {
		fun onSuccess(token: String)
		fun onFailed(msg: String)
	}
}