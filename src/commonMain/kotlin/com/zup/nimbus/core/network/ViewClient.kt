package com.zup.nimbus.core.network

import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.RenderNode

interface ViewClient {
  /**
   * Fetches a view (UI tree) from the server.
   *
   * @param request the data for the request to make.
   * @return the UI tree fetched.
   * @throws RequestError if an error happens while creating the request object.
   * @throws ResponseError if an error happens while obtaining the response from the server.
   * @throws MalformedComponentError if an error happens while deserializing the response into a RenderNode.
   */
  suspend fun fetch(request: ViewRequest): RenderNode
}
