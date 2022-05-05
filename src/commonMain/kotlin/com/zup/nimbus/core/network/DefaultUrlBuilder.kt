package com.zup.nimbus.core.network

import com.zup.nimbus.core.utils.removeSuffix

class DefaultUrlBuilder(
  /**
   * The baseUrl to use when building the URLs. If the baseUrl contains special characters, it should be encoded before
   * passed to this constructor.
   */
  private val baseUrl: String,
) : UrlBuilder {
  override fun build(path: String): String {
    val base = removeSuffix(baseUrl, "/")
    return if (path.startsWith("/")) "${base}${path}" else path
  }
}
