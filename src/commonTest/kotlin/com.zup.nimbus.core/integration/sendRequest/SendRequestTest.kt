package com.zup.nimbus.core.integration.sendRequest

import LogEntry
import ObservableLogger
import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.tree.ServerDrivenNode
import com.zup.nimbus.core.utils.valueOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SendRequestTest {
  private val scope = TestScope()

  private fun pressButtonToSendRequest(content: ServerDrivenNode) {
    val button = content.children?.get(0)!!
    val pressButton = valueOf<(value: Any?) -> Unit>(button.properties, "onPress")
    pressButton(null)
  }

  private fun runSendRequestTest(
    json: String,
    numberOfLogsToWaitFor: Int = 2,
    onLogEvent: (entries: List<LogEntry>) -> Unit,
  ) = scope.runTest {
    val logger = ObservableLogger()
    val nimbus = Nimbus(ServerDrivenConfig(
      baseUrl = BASE_URL,
      platform = "test",
      httpClient = DefaultHttpClient(serverMock),
      logger = logger,
    ))
    var changed = 0
    val screen = nimbus.createNodeFromJson(json)
    val view = nimbus.createView(EmptyNavigator())
    view.onChange {
      changed++
      pressButtonToSendRequest(it)
    }
    view.renderer.paint(screen)
    assertEquals(1, changed)
    logger.waitForLogEvents(numberOfLogsToWaitFor)
    onLogEvent(logger.entries)
  }

  @Test
  fun shouldRunOnSuccess() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals(2, it.size)
    val firstLog = it.first()
    assertEquals("success", firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }

  @Test
  fun shouldRunOnError() = runSendRequestTest(buildScreen("/user/2")) {
    assertEquals(3, it.size)
    val firstLog = it.first()
    val secondLog = it[1]
    assertEquals(LogLevel.Error, firstLog.level)
    assertEquals("error", secondLog.message)
    assertEquals(LogLevel.Error, secondLog.level)
  }

  @Test
  fun shouldRunOnFinish() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals(2, it.size)
    val lastLog = it.last()
    assertEquals("finish", lastLog.message)
    assertEquals(LogLevel.Info, lastLog.level)
  }

  @Test
  fun shouldFailBeforeSendingRequest() = runSendRequestTest(buildScreen(null), 1) {
    assertEquals(1, it.size)
    val firstLog = it.first()
    assertContains(firstLog.message, "Unexpected value type at \"url\". Expected \"String\", found \"null\".")
    assertEquals(LogLevel.Error, firstLog.level)
  }

  @Test
  fun shouldRunOnSuccessIfOnErrorAndOnFinishAreMissing() = runSendRequestTest(
    buildScreen("/user/1", false),
    1,
  ) {
    assertEquals(1, it.size)
    val firstLog = it.first()
    assertEquals("success", firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }
}
