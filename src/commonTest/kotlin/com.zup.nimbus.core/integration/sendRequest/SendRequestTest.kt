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
  private val logger = ObservableLogger()

  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = BASE_URL,
    platform = "test",
    httpClient = DefaultHttpClient(serverMock),
    logger = logger,
  ))

  private fun pressButtonToSendRequest(content: ServerDrivenNode) {
    val button = content.children?.get(0)!!
    val pressButton = valueOf<(value: Any?) -> Unit>(button.properties, "onPress")
    pressButton(null)
  }

  private fun runSendRequestTest(
    json: String,
    numberOfLogsToWaitFor: Int = 1,
    onLogEvent: (entry: LogEntry) -> Unit,
  ) = scope.runTest {
    var changed = 0
    val screen = nimbus.createNodeFromJson(json)
    val view = nimbus.createView(EmptyNavigator())
    logger.clear()
    logger.waitForLogEvents(numberOfLogsToWaitFor) { onLogEvent(it) }
    view.onChange {
      changed++
      pressButtonToSendRequest(it)
    }
    view.renderer.paint(screen)
    assertEquals(1, changed)
  }

  @Test
  fun shouldRunOnSuccess() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals("success", it.message)
    assertEquals(LogLevel.Info, it.level)
  }

  @Test
  fun shouldRunOnError() = runSendRequestTest(buildScreen("/user/2")) {
    assertEquals("error", it.message)
    assertEquals(LogLevel.Error, it.level)
  }

  @Test
  fun shouldRunOnFinish() = runSendRequestTest(buildScreen("/user/1"), 2) {
    assertEquals("finish", it.message)
    assertEquals(LogLevel.Info, it.level)
  }

  @Test
  fun shouldFailBeforeSendingRequest() = runSendRequestTest(buildScreen(null)) {
    assertContains(it.message, "Unexpected value type at \"url\". Expected \"String\", found \"null\".")
    assertEquals(LogLevel.Error, it.level)
  }

  @Test
  fun shouldRunOnSuccessIfOnErrorAndOnFinishAreMissing() = runSendRequestTest(
    buildScreen("/user/1", false),
  ) {
    assertEquals("success", it.message)
    assertEquals(LogLevel.Info, it.level)
  }
}
