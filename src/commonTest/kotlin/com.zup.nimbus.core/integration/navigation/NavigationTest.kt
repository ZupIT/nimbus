package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ResponseError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.tree.ServerDrivenNode
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationTest {
  private val scope = TestScope()

  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = BASE_URL,
    platform = "test",
    httpClient = DefaultHttpClient(serverMock),
  ))

  private fun runNavigationTest(
    firstScreen: String = "/screen1",
    onLoad: (pages: ArrayList<Page>) -> Unit = { fail("An error was expected.") },
    onError: ((e: Error) -> Unit) = { fail("Unexpected error: ${it.message}") },
  ) {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = onError,
      onLoad = onLoad,
    )
    navigator.push(ViewRequest(firstScreen))
  }

  private fun pressNextButton(pageContent: ServerDrivenNode?, buttonIndex: Int = 1) {
    val nextButton = pageContent?.children?.get(buttonIndex)
    val onPress = nextButton?.properties?.get("onPress")
    if (onPress is Function0<*>) onPress.invoke()
  }

  private fun pressPreviousButton(pageContent: ServerDrivenNode?) {
    val previousButton = pageContent?.children?.last()
    val onPress = previousButton?.properties?.get("onPress")
    if (onPress is Function0<*>) onPress.invoke()
  }

  @Test
  fun shouldRenderFirstView() = scope.runTest {
    runNavigationTest(onLoad = {
      assertEquals(1, it.size)
      verifyScreen1(it.last().content)
    })
  }

  @Test
  fun shouldNotFindFirstViewWithWrongUrl() = scope.runTest {
    runNavigationTest(
      firstScreen = "/none",
      onError = {
        assertEquals(true, it is ResponseError)
        it as ResponseError
        assertEquals(HttpStatusCode.NotFound.value, it.status)
      },
    )
  }

  @Test
  fun shouldPushSecondView() = scope.runTest {
    runNavigationTest(onLoad = {
      if(it.last().id == "/screen1") {
        pressNextButton(it.last().content)
      } else {
        assertEquals(2, it.size)
        verifyScreen2(it.last().content)
      }
    })
  }

  @Test
  fun shouldPushThirdView() = scope.runTest {
    runNavigationTest(onLoad = {
      if(it.last().id == "/screen1" || it.last().id == "/screen2") {
        pressNextButton(it.last().content)
      } else {
        assertEquals(3, it.size)
        verifyScreen3(it.last().content)
      }
    })
  }

  @Test
  fun shouldShowFallbackWhenPushingFourthView() = scope.runTest {
    runNavigationTest(onLoad = {
      if(it.last().id == "/screen1" || it.last().id == "/screen2" || it.last().id == "/screen3") {
        pressNextButton(it.last().content)
      } else {
        assertEquals(4, it.size)
        verifyFallbackScreen(it.last().content)
      }
    })
  }

  @Test
  fun shouldProduceErrorAndNotNavigateWhenGoingToScreen4() = scope.runTest {
    runNavigationTest(
      onLoad = {
        if (it.last().id == "/screen4") fail("we didn't expect to able to load /screen4.")
        // for "/screen3", there will be two next buttons, we want to press the second
        val nextButtonIndex = if (it.last().id == "/screen3") 2 else 1
        pressNextButton(it.last().content, nextButtonIndex)
      },
      onError = {
        assertEquals(true, it is ResponseError)
        it as ResponseError
        assertEquals(404, it.status)
      }
    )
  }

  @Test
  fun shouldPushSecondViewAndPop() = scope.runTest {
    runNavigationTest(onLoad = {
      if(it.last().id == "/screen1") {
        pressNextButton(it.last().content)
      } else {
        pressPreviousButton(it.last().content)
        assertEquals(true, it.size == 1)
        assertEquals("/screen1", it.last().id)
      }
    })
  }

  @Test
  fun shouldPopToRootFromFallback() = scope.runTest {
    runNavigationTest(onLoad = {
      if(it.last().id == "/screen1" || it.last().id == "/screen2" || it.last().id == "/screen3") {
        pressNextButton(it.last().content)
      } else {
        // fallback to /screen3
        pressPreviousButton(it.last().content)
        assertEquals(true, it.size == 3)
        assertEquals("/screen3", it.last().id)
        // /screen3 to /screen2
        pressPreviousButton(it.last().content)
        assertEquals(true, it.size == 2)
        assertEquals("/screen2", it.last().id)
        // /screen2 to /screen1
        pressPreviousButton(it.last().content)
        assertEquals(true, it.size == 1)
        assertEquals("/screen1", it.last().id)
      }
    })
  }
}
