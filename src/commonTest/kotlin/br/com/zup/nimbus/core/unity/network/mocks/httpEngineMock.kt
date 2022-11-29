package br.com.zup.nimbus.core.unity.network.mocks

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode

val httpEngineMock = MockEngine { request ->
  return@MockEngine when(request.url.toString()) {
    "http://localhost/test" -> respond(
      content = "Hello World",
      status = HttpStatusCode.OK,
      headers = request.headers
    )
    "http://localhost/test-body" -> respond(
      content = (request.body as TextContent).text,
      status = HttpStatusCode.OK,
      headers = request.headers,
    )
    else -> respond(
      content = "Not Found",
      status = HttpStatusCode.NotFound,
    )
  }
}
