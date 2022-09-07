package com.zup.nimbus.core.integration.sendRequest

import com.zup.nimbus.core.*
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.tree.stateful.ServerDrivenNode
import com.zup.nimbus.core.utils.valueOfKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
/*
@OptIn(ExperimentalCoroutinesApi::class)
class SendRequestTest {
  private val scope = TestScope()
  private val logger = ObservableLogger()

  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = BASE_URL,
    platform = "test",
    httpClient = DefaultHttpClient(serverMock),
    logger = logger,
  ))

  private fun pressButtonToSendRequest(content: ServerDrivenNode) {
    val button = content.children?.get(0)!!
    val pressButton = valueOfKey<(value: Any?) -> Unit>(button.properties, "onPress")
    pressButton(null)
  }

  private fun runSendRequestTest(
    json: String,
    numberOfLogsToWaitFor: Int = 2,
    onLogEvent: () -> Unit,
  ) = scope.runTest {
    var changed = 0
    val screen = nimbus.createNodeFromJson(json)
    val view = nimbus.createView({ EmptyNavigator() })
    logger.clear()
    view.onChange {
      changed++
      pressButtonToSendRequest(it)
    }
    view.renderer.paint(screen)
    assertEquals(1, changed)
    AsyncUtils.waitUntil { logger.entries.size >= numberOfLogsToWaitFor }
    onLogEvent()
  }

  @Test
  fun `should run on success`() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals(2, logger.entries.size)
    val firstLog = logger.entries.first()
    assertEquals("success", firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }

  @Test
  fun `should run on error`() = runSendRequestTest(buildScreen("/user/2"), 3) {
    assertEquals(3, logger.entries.size)
    val firstLog = logger.entries.first()
    val secondLog = logger.entries[1]
    assertEquals(LogLevel.Error, firstLog.level)
    assertEquals("error", secondLog.message)
    assertEquals(LogLevel.Error, secondLog.level)
  }

  @Test
  fun `should run on finish`() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals(2, logger.entries.size)
    val lastLog = logger.entries.last()
    assertEquals("finish", lastLog.message)
    assertEquals(LogLevel.Info, lastLog.level)
  }

  @Test
  fun `should fail before sending request`() = runSendRequestTest(buildScreen(null), 1) {
    assertEquals(1, logger.entries.size)
    val firstLog = logger.entries.first()
    assertContains(firstLog.message, "Unexpected value type at \"url\". Expected \"String\", found \"null\".")
    assertEquals(LogLevel.Error, firstLog.level)
  }

  @Test
  fun `should run on success if on error and on finish are missing`() = runSendRequestTest(
    buildScreen("/user/1", false),
    1,
  ) {
    assertEquals(1, logger.entries.size)
    val firstLog = logger.entries.first()
    assertEquals("success", firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }
}
*/
