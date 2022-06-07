package com.zup.nimbus.core.integration.operations

import com.zup.nimbus.core.EmptyNavigator
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
      httpClient = DefaultHttpClient(),
      logger = logger,
    )
  )

  @Test
  fun `should add 1 to the count everytime the button is pressed`() {
    val screen = nimbus.createNodeFromJson(FIRST_PAGE)
    val view = nimbus.createView({ EmptyNavigator() })

    view.renderer.paint(screen)

    var count = screen.state?.value as Double
    assertEquals(1.0, count)

    NodeUtils.pressButton(screen, "addToCount")

    count = screen.state?.value as Double
    assertEquals(2.0, count)

    NodeUtils.pressButton(screen, "addToCount")

    count = screen.state?.value as Double
    assertEquals(3.0, count)
  }
}
