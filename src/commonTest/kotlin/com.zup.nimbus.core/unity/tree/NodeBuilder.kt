package com.zup.nimbus.core.unity.tree

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.tree.MalformedJsonError
import kotlin.test.Test
import kotlin.test.assertTrue

class NodeBuilderTest {
  val nimbus = Nimbus(ServerDrivenConfig(baseUrl = "", platform = "test"))

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
