package com.zup.nimbus.core.unity.tree

import com.zup.nimbus.core.tree.DefaultIdManager
import com.zup.nimbus.core.tree.MalformedJsonError
import com.zup.nimbus.core.tree.RenderNode
import kotlin.test.Test
import kotlin.test.assertTrue

class RenderNodeTest {
  @Test
  fun `should throw when json is invalid`() {
    var error: Throwable? = null
    try {
      RenderNode.fromJsonString("""{ "aa": 45, 85,""", DefaultIdManager())
    } catch (e: Throwable) {
      error = e
    }
    assertTrue(error is MalformedJsonError)
  }
}
