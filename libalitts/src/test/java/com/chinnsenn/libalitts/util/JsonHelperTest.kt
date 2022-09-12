package com.chinnsenn.libalitts.util

import com.chinnsenn.libalitts.entity.InitializationProfile
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * @author: 陈川
 * @date: 2022/9/9
 */
class JsonHelperTest {

	private lateinit var json: String

	private lateinit var bean: InitializationProfile

	@Before
	fun setUp() {
		json = """{"workspace": "/sdcard/","token": "fjasdoif-0321849012jklf-0","app_key": "3300781","device_id": "3300781"}"""
		bean = InitializationProfile()
		bean.workspace = "/sdcard/"
		bean.token = "fjasdoif-0321849012jklf-0"
		bean.appKey = "3300781"
		bean.deviceId = "3300781"
	}

	@After
	fun tearDown() {

	}

	@Test
	fun string2Bean() {
		val bean = JsonHelper.string2Bean(json, InitializationProfile::class.java)
		assertThat(bean?.appKey).isEqualTo("3300781")
	}

	@Test
	fun string2BeanNullSafe() {
		val bean = JsonHelper.string2Bean("", InitializationProfile::class.java)
		assertThat(bean).isEqualTo(null)
	}

	@Test
	fun bean2Json() {
		val ret = JsonHelper.bean2Json(bean)
		assertThat(ret).isEqualTo(ret)
	}

	@Test
	fun string2Map() {
		val map = JsonHelper.string2Map(json)
		assertThat(map?.get("workspace")).isEqualTo("/sdcard/")
		assertThat(map?.get("device_id")).isEqualTo("3300781")
	}

	@Test
	fun bean2Map() {
		val map = JsonHelper.bean2Map(bean)
		assertThat(map?.get("workspace")).isEqualTo("/sdcard/")
		assertThat(map?.get("device_id")).isEqualTo("3300781")
	}
}