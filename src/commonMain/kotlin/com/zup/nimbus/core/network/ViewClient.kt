package com.zup.nimbus.core.network

import com.zup.nimbus.core.tree.dynamic.builder.MalformedComponentError
import com.zup.nimbus.core.tree.dynamic.builder.MalformedJsonError
import com.zup.nimbus.core.tree.dynamic.node.RootNode
import kotlin.coroutines.cancellation.CancellationException

interface ViewClient {
  /**
   * Fetches a view (UI tree) from the server.
   *
   * @param request the data for the request to make.
   * @return the UI tree fetched.
   * @throws RequestError if an error happens while creating the request object.
   * @throws ResponseError if an error happens while obtaining the response from the server.
   * @throws MalformedJsonError if the string is not a valid json.
   * @throws MalformedComponentError if an error happens while deserializing the response into a RenderNode.
   */
  @Throws(RequestError::class, ResponseError::class, MalformedJsonError::class, MalformedComponentError::class,
    CancellationException::class)
  suspend fun fetch(request: ViewRequest): RootNode

  /**
   * Pre-fetches a view (UI tree) from the server and stores it. The next fetch will get the stored value instead of
   * performing a network request.
   *
   * Each implementation of the ViewClient decides how its pre-fetch logic works. See `DefaultViewClient` for the
   * default logic.
   *
   * @param request the data for the request to make.
   */
  fun preFetch(request: ViewRequest)
}
