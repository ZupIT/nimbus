package com.zup.nimbus.core.network

data class ViewRequest(
  /**
   * The URL to send the request to. When it starts with "/", it's relative to the BaseUrl.
   */
  val url: String,
  /**
   * The request method. Default is "Get".
   */
  val method: ServerDrivenHttpMethod = ServerDrivenHttpMethod.Get,
  /**
   * The headers for the request.
   */
  val headers: Map<String, String>? = null,
  /**
   * The request body. Invalid for "Get" requests.
   */
  val body: Any? = null,
  /**
   * UI tree to show if an error occurs and the view can't be fetched.
   */
  val fallback: Map<String, Any?>? = null,
  /**
   * The map of state ids and its values that will be used on the next page.
   */
  val params: Map<String, Any?>? = null,
)
