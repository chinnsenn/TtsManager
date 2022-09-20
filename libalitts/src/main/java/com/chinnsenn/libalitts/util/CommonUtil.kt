package com.chinnsenn.libalitts.util

import android.os.Looper

/**
 * @author: 陈川
 * @date: 2022/9/16
 */

fun Thread.isMainThread() = Looper.getMainLooper().thread == this