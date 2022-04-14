package com.zup.nimbus.core.network

import com.zup.nimbus.core.utils.UriUtils
import com.zup.nimbus.core.utils.removeSuffix
import com.zup.nimbus.core.utils.then

class DefaultUrlBuilder(private val baseUrl: String) : UrlBuilder {
  override fun build(path: String): String {
    val base = removeSuffix(baseUrl, "/")
    val url = path.matches(Regex("^\\/+(\\b|$)")) then "${base}${path}" ?: path
    return (UriUtils.decode(url) == url) then UriUtils.encode(url) ?: url
  }
}
