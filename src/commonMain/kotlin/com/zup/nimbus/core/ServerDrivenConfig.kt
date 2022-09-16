package com.zup.nimbus.core

import com.zup.nimbus.core.ui.UILibrary
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.UrlBuilder
import com.zup.nimbus.core.network.ViewClient
import com.zup.nimbus.core.tree.IdManager

data class ServerDrivenConfig(
  /**
   * The base url to use by this project when it encounters relative urls.
   */
  val baseUrl: String,
  /**
   * The platform running the Server Driven Lib. Examples: "Android", "iOS".
   */
  val platform: String,
  val ui: List<UILibrary>? = null,
  val coreUILibrary: UILibrary? = null,
  /**
   * The logger to call when printing errors, warning and information messages. By default, Nimbus will use its
   * DefaultLogger that just prints the messages to the console.
   */
  val logger: Logger? = null,
  /**
   * A logic to create full URLs from relative paths. By default, Nimbus checks if the path starts with "/". If it does,
   * the full URL is the baseUrl provided in this config concatenated with the path. Otherwise, it's the path itself.
   */
  val urlBuilder: ((baseUrl: String) -> UrlBuilder)? = null,
  /**
   * The lowest level API for making requests in Nimbus. By default, Nimbus will use its DefaultHttpClient, which just
   * calls kotlin's ktor lib with the provided requests.
   *
   * Replacing the HttpClient is required for most projects. By implementing your own, you can create authentication
   * logic, additional headers, retrial polices, etc.
   */
  val httpClient: HttpClient? = null,
  /**
   * The ViewClient is one layer above the HttpClient, it is used to fetch JSON screens from the backend. By default,
   * Nimbus use the DefaultViewClient, which relies on the HttpClient to fetch the JSON and on the RenderNode object
   * factory to deserialize the responses.
   *
   * A ViewClient is also responsible for implementing the prefetch logic, indicated by the property "prefetch" of a
   * navigation action. Check the DefaultViewClient documentation to know more about how it deals with prefetching.
   */
  val viewClient: ((nimbus: Nimbus) -> ViewClient)? = null,
  /**
   * An id generator for creating unique ids for nodes in a UI tree when one is not provided by the JSON. By default,
   * the ids are incremental, starting at 0 and prefixed with "nimbus:".
   */
  val idManager: IdManager? = null,
  val states: List<ServerDrivenState>? = null,
)
