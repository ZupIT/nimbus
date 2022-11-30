package br.com.zup.nimbus.core.unity.network

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.network.DefaultUrlBuilder
import br.com.zup.nimbus.core.unity.network.mocks.CustomUrlBuilderTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UrlBuilderTest {
  private val builder = DefaultUrlBuilder("/base-url/")

  @Test
  fun `should remove the last slash from the base url when it ends with one`() {
    val result = builder.build("/new-path")
    assertNotEquals("/base-url//new-path", result)
    assertEquals("/base-url/new-path", result)
  }

  @Test
  fun `should return only the path when the path to build doesn't start with a slash`() {
    val result = builder.build("new-path")
    assertNotEquals("/base-url/new-path", result)
    assertEquals("new-path", result)
  }

  @Test
  fun `should use the custom url builder when nimbus was instantiated with one`() {
    val nimbus = Nimbus(
      ServerDrivenConfig(
        baseUrl = "/",
        platform = "test",
        urlBuilder = { CustomUrlBuilderTest() },
        httpClient = EmptyHttpClient,
      )
    )
    val result = nimbus.urlBuilder.build("new-path")
    assertEquals("/custom-builder/new-path", result)
  }
}
