package com.zup.nimbus.core.integration.sendRequest

import ObservableLogger
import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ResponseError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.tree.ServerDrivenNode
import com.zup.nimbus.core.utils.valueOf
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
class SendRequestTest {
  private val scope = TestScope()
  private val logger = ObservableLogger(scope)

  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = BASE_URL,
    platform = "test",
    httpClient = DefaultHttpClient(serverMock),
    logger = logger,
  ))

  /*@Test
  fun log() {
    scope.runTest {
      val logger = ObservableLogger(scope)
      logger.waitForLogEvent {
        assertEquals(1, logger.logEntries.size)
      }
      delay(1500L)
      logger.info("hey")
    }
  }*/

  private fun pressButtonToSendRequest(content: ServerDrivenNode) {
    val button = content.children?.get(0)!!
    val pressButton = valueOf<(value: Any?) -> Unit>(button.properties, "onPress")
    pressButton(null)
  }

  @Test
  fun shouldRunOnSuccess() = scope.runTest {
    var changed = 0
    val screen = nimbus.createNodeFromJson(buildScreen("/user/1"))
    val view = nimbus.createView(EmptyNavigator())
    logger.waitForLogEvent {
      assertEquals("success", it.message)
      assertEquals(LogLevel.Info, it.level)
    }
    view.onChange {
      changed++
      pressButtonToSendRequest(it)
    }
    view.renderer.paint(screen)
    assertEquals(1, changed)
  }

  /*@Test
  fun shouldRunOnError() {

  }

  @Test
  fun shouldRunOnFinish() {

  }

  @Test
  fun shouldFailBeforeSendingRequest() {

  }*/
}
