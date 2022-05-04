package com.zup.nimbus.core.network

import com.zup.nimbus.core.utils.removeSuffix

class DefaultUrlBuilder(private val baseUrl: String) : UrlBuilder {
  override fun build(path: String): String {
    val base = removeSuffix(baseUrl, "/")
    return if (path.startsWith("/")) "${base}${path}" else path
  }
}
