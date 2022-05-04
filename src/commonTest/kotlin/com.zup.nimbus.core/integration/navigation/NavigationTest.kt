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
  ) = scope.runTest {
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
  fun shouldRenderFirstView() = runNavigationTest(onLoad = { pages ->
    assertEquals(1, pages.size)
    verifyScreen1(pages.last().content)
  })

  @Test
  fun shouldNotFindFirstViewWithWrongUrl() = runNavigationTest(
    firstScreen = "/none",
    onError = { error ->
      assertEquals(true, error is ResponseError)
      error as ResponseError
      assertEquals(HttpStatusCode.NotFound.value, error.status)
    },
  )

  @Test
  fun shouldPushSecondView() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1") {
      pressNextButton(pages.last().content)
    } else {
      assertEquals(2, pages.size)
      verifyScreen2(pages.last().content)
    }
  })

  @Test
  fun shouldPushThirdView() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1" || pages.last().id == "/screen2") {
      pressNextButton(pages.last().content)
    } else {
      assertEquals(3, pages.size)
      verifyScreen3(pages.last().content)
    }
  })

  @Test
  fun shouldShowFallbackWhenPushingFourthView() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1" || pages.last().id == "/screen2" || pages.last().id == "/screen3") {
      pressNextButton(pages.last().content)
    } else {
      assertEquals(4, pages.size)
      verifyFallbackScreen(pages.last().content)
    }
  })

  @Test
  fun shouldProduceErrorAndNotNavigateWhenGoingToScreen4() = runNavigationTest(
    onLoad = { pages ->
      if (pages.last().id == "/screen4") fail("we didn't expect to able to load /screen4.")
      // for "/screen3", there will be two next buttons, we want to press the second
      val nextButtonIndex = if (pages.last().id == "/screen3") 2 else 1
      pressNextButton(pages.last().content, nextButtonIndex)
    },
    onError = { error ->
      assertEquals(true, error is ResponseError)
      error as ResponseError
      assertEquals(404, error.status)
    }
  )

  @Test
  fun shouldPushSecondViewAndPop() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1") {
      pressNextButton(pages.last().content)
    } else {
      pressPreviousButton(pages.last().content)
      assertEquals(true, pages.size == 1)
      assertEquals("/screen1", pages.last().id)
    }
  })

  @Test
  fun shouldPopToRootFromFallback() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1" || pages.last().id == "/screen2" || pages.last().id == "/screen3") {
      pressNextButton(pages.last().content)
    } else {
      // fallback to /screen3
      pressPreviousButton(pages.last().content)
      assertEquals(true, pages.size == 3)
      assertEquals("/screen3", pages.last().id)
      // /screen3 to /screen2
      pressPreviousButton(pages.last().content)
      assertEquals(true, pages.size == 2)
      assertEquals("/screen2", pages.last().id)
      // /screen2 to /screen1
      pressPreviousButton(pages.last().content)
      assertEquals(true, pages.size == 1)
      assertEquals("/screen1", pages.last().id)
    }
  })
}
