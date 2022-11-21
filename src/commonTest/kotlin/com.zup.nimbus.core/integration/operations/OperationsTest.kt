package com.zup.nimbus.core.integration.operations

import com.zup.nimbus.core.EmptyHttpClient
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.NodeUtils
import com.zup.nimbus.core.ObservableLogger
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.integration.sendRequest.BASE_URL
import com.zup.nimbus.core.network.DefaultHttpClient
import kotlin.test.Test
import kotlin.test.assertEquals

class OperationsTest {
  private val logger = ObservableLogger()

  private val nimbus = Nimbus(
      ServerDrivenConfig(
      baseUrl = BASE_URL,
      platform = "test",
      httpClient = EmptyHttpClient,
      logger = logger,
    )
  )

  @Test
  fun `should add 1 to the count everytime the button is pressed`() {
    val tree = nimbus.nodeBuilder.buildFromJsonString(FIRST_PAGE)
    tree.initialize(nimbus)
    val content = NodeUtils.getContent(tree)
    var count = content.states?.first()?.value as Number
    assertEquals(1, count)

    NodeUtils.pressButton(content, "addToCount")

    count = content.states?.first()?.value as Number
    assertEquals(2, count)

    NodeUtils.pressButton(content, "addToCount")

    count = content.states?.first()?.value as Number
    assertEquals(3, count)
  }
}
