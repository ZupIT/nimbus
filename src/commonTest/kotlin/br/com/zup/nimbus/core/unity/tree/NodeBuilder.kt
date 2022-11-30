package br.com.zup.nimbus.core.unity.tree

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.tree.dynamic.builder.MalformedJsonError
import kotlin.test.Test
import kotlin.test.assertTrue

class NodeBuilderTest {
  val nimbus = Nimbus(ServerDrivenConfig(baseUrl = "", platform = "test", httpClient = EmptyHttpClient))

  @Test
  fun `should throw when json is invalid`() {
    var error: Throwable? = null
    try {
      nimbus.nodeBuilder.buildFromJsonString("""{ "aa": 45, 85,""")
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is MalformedJsonError)
  }
}
