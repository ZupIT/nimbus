package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.*
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ResponseError
import com.zup.nimbus.core.network.ViewRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationTest {
  private val scope = TestScope()
  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = BASE_URL,
    platform = "test",
    httpClient = DefaultHttpClient(serverMock),
  ))
  private val navigator = ObservableNavigator(scope, nimbus)

  private suspend fun pushViews(numberOfViews: Int): Page {
    var current = 1
    navigator.push(ViewRequest("/screen1"))
    var result = navigator.awaitPushCompletion()
    while (current < numberOfViews) {
      NodeUtils.pressButton(result.content, "next")
      result = navigator.awaitPushCompletion()
      current++
    }
    return result
  }

  @BeforeTest
  fun clear() {
    navigator.clear()
  }

  @Test
  fun shouldRenderFirstView() = scope.runTest {
    val page1 = pushViews(1)
    assertEquals(1, navigator.pages.size)
    verifyScreen1(page1.content)
  }

  @Test
  fun shouldNotFindFirstViewWithWrongUrl() = scope.runTest {
    var error: Throwable? = null
    try {
      navigator.push(ViewRequest("/none"))
      navigator.awaitPushCompletion()
    } catch(e: CancellationException) {
      error = e.cause?.cause
    }
    assertTrue(error is ResponseError)
    assertEquals(HttpStatusCode.NotFound.value, error.status)
  }

  @Test
  fun shouldPushSecondView() = scope.runTest {
    val page2 = pushViews(2)
    assertEquals(2, navigator.pages.size)
    verifyScreen2(page2.content)
  }

  @Test
  fun shouldPushThirdView() = scope.runTest {
    val page3 = pushViews(3)
    assertEquals(3, navigator.pages.size)
    verifyScreen3(page3.content)
  }

  @Test
  fun shouldShowFallbackWhenPushingFourthView() = scope.runTest {
    val fallbackPage = pushViews(4)
    assertEquals(4, navigator.pages.size)
    verifyFallbackScreen(fallbackPage.content)
  }

  @Test
  fun shouldProduceErrorAndNotNavigateWhenGoingToScreen4() = scope.runTest {
    val page3 = pushViews(3)
    var error: Throwable? = null
    try {
      NodeUtils.pressButton(page3.content, "next-error")
      navigator.awaitPushCompletion()
    } catch(e: CancellationException) {
      error = e.cause?.cause
    }
    assertTrue(error is ResponseError)
    assertEquals(404, error.status)
  }

  @Test
  fun shouldPushSecondViewAndPop() = scope.runTest {
    val page2 = pushViews(2)
    NodeUtils.pressButton(page2.content, "previous")
    assertEquals(1, navigator.pages.size)
    assertEquals("/screen1", navigator.pages.last().id)
  }

  @Test
  fun shouldPopToRootFromFallback() = scope.runTest {
    val fallbackPage = pushViews(4)
    // fallback to /screen3
    NodeUtils.pressButton(fallbackPage.content, "previous")
    assertEquals(3, navigator.pages.size)
    assertEquals("/screen3", navigator.pages.last().id)
    // /screen3 to /screen2
    NodeUtils.pressButton(navigator.pages.last().content, "previous")
    assertEquals(2, navigator.pages.size)
    assertEquals("/screen2", navigator.pages.last().id)
    // /screen2 to /screen1
    NodeUtils.pressButton(navigator.pages.last().content, "previous")
    assertEquals(1, navigator.pages.size)
    assertEquals("/screen1", navigator.pages.last().id)
  }
}
