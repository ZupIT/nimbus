package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.Page
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
    if (onPress is Function<*>) (onPress as (implicitState: Any?) -> Unit)(null)
  }

  private fun pressPreviousButton(pageContent: ServerDrivenNode?) {
    val previousButton = pageContent?.children?.last()
    val onPress = previousButton?.properties?.get("onPress")
    if (onPress is Function<*>) (onPress as (implicitState: Any?) -> Unit)(null)
  }

  @Test
  fun `should render the first view`() = runNavigationTest(onLoad = { pages ->
    assertEquals(1, pages.size)
    verifyScreen1(pages.last().content)
  })

  @Test
  fun `should not find the first view with a wrong url`() = runNavigationTest(
    firstScreen = "/none",
    onError = { error ->
      assertEquals(true, error is ResponseError)
      error as ResponseError
      assertEquals(HttpStatusCode.NotFound.value, error.status)
    },
  )

  @Test
  fun `should push the second view`() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1") {
      pressNextButton(pages.last().content)
      assertEquals(2, pages.size)
    } else {
      verifyScreen2(pages.last().content)
    }
  })

  @Test
  fun `should push the third view`() {
    var expectedNumberOfPages = 1
    runNavigationTest(onLoad = { pages ->
      if(pages.last().id == "/screen1" || pages.last().id == "/screen2") {
        pressNextButton(pages.last().content)
        assertEquals(++expectedNumberOfPages, pages.size)
      } else {
        verifyScreen3(pages.last().content)
      }
    })
  }

  @Test
  fun `should show the fallback when pushing the fourth view`() {
    var expectedNumberOfPages = 1
    runNavigationTest(onLoad = { pages ->
      if (pages.last().id == "/screen1" || pages.last().id == "/screen2" || pages.last().id == "/screen3") {
        pressNextButton(pages.last().content)
        assertEquals(++expectedNumberOfPages, pages.size)
      } else {
        assertEquals(4, pages.size)
        verifyFallbackScreen(pages.last().content)
      }
    })
  }

  @Test
  fun `should produce an error and not navigate when going to screen 4`() {
    var expectedNumberOfPages = 1
    runNavigationTest(
      onLoad = { pages ->
        if (pages.last().id == "/screen4") fail("we didn't expect to able to load /screen4.")
        // for "/screen3", there will be two next buttons, we want to press the second
        val nextButtonIndex = if (pages.last().id == "/screen3") 2 else 1
        pressNextButton(pages.last().content, nextButtonIndex)
        assertEquals(++expectedNumberOfPages, pages.size)
      },
      onError = { error ->
        assertEquals(true, error is ResponseError)
        error as ResponseError
        assertEquals(404, error.status)
      }
    )
  }

  @Test
  fun `should push the second view and pop`() = runNavigationTest(onLoad = { pages ->
    if(pages.last().id == "/screen1") {
      pressNextButton(pages.last().content)
      assertEquals(2, pages.size)
    } else {
      pressPreviousButton(pages.last().content)
      assertEquals(true, pages.size == 1)
      assertEquals("/screen1", pages.last().id)
    }
  })

  @Test
  fun `should pop to root from fallback`() {
    var expectedNumberOfPages = 1
    runNavigationTest(onLoad = { pages ->
      if(pages.last().id == "/screen1" || pages.last().id == "/screen2" || pages.last().id == "/screen3") {
        pressNextButton(pages.last().content)
        assertEquals(++expectedNumberOfPages, pages.size)
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
}
