package com.zup.nimbus.core

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.network.HttpClient
import com.zup.nimbus.core.network.UrlBuilder
import com.zup.nimbus.core.network.ViewClient
import com.zup.nimbus.core.render.ActionTriggeredEvent
import com.zup.nimbus.core.tree.IdManager

typealias ActionHandler = (event: ActionTriggeredEvent) -> Unit
typealias OperationHandler = (arguments: List<Any>) -> Any

data class ServerDrivenConfig(
  /**
   * The base url to use by this project when it encounters relative urls.
   */
  val baseUrl: String,
  /**
   * The platform running the Server Driven Lib. Examples: "Android", "iOS".
   */
  val platform: String,
  /**
   * A map of ActionHandlers. Use this to provide customized actions to your server driven UIs.
   *
   * Each key in this map must be the action name with its namespace. Examples: "material:button", "layout:column".
   *
   * The value for each key `k` must the function to run once the action with name `k` is triggered. This function
   * receives the ActionEvent and must return nothing. The ActionEvent has all the data needed to run the action.
   *
   * You can also add an action handler to an instance of `Nimbus` via the method `addActions`
   */
  val actions: Map<String, ActionHandler>? = null,
  /**
   * A list of action handlers to run when any action is triggered. This runs after the handler in `action` and is
   * used to implement a behavior that should run for every action, no matter its name.
   *
   * This can be useful for implementing Analytics.
   */
  val actionObservers: List<ActionHandler>? = null,
  /**
   * A map of OperationHandlers. Use this to provide customized operations to your server driven UIs.
   *
   * Each key in this map must be the operation name. Examples: "isDocumentValid", "formatPhoneNumber".
   *
   * The value for each key `k` must the function to run once the operation with name `k` is called. This function
   * receives a list with the operation parameters and must return its result.
   *
   * You can also add an operation handler to an instance of `Nimbus` via the method `addOperations`
   */
  val operations: Map<String, OperationHandler>? = null,
  /**
   * The logger to call when printing errors, warning and information messages. By default, Nimbus will use its
   * DefaultLogger that just prints the messages to the console.
   */
  val logger: Logger? = null,
  /**
   * A logic to create full URLs from relative paths. By default, Nimbus checks if the path starts with "/". If it does,
   * the full URL is the baseUrl provided in this config concatenated with the path. Otherwise, it's the path itself.
   */
  val urlBuilder: UrlBuilder? = null,
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
  val viewClient: ViewClient? = null,
  /**
   * An id generator for creating unique ids for nodes in a UI tree when one is not provided by the JSON. By default,
   * the ids are incremental, starting at 0 and prefixed with "nimbus:".
   */
  val idManager: IdManager? = null,
)
