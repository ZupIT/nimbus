package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.*
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ResponseError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.scope.closestScopeWithType
import com.zup.nimbus.core.tree.dynamic.node.RootNode
import com.zup.nimbus.core.tree.findNodeById
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

  private suspend fun pushViews(numberOfViews: Int): RootNode {
    var current = 1
    navigator.push(ViewRequest("/screen1"))
    var result = navigator.awaitPushCompletion()
    while (current < numberOfViews) {
      NodeUtils.pressButton(result, "next")
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
  fun `should render the first view`() = scope.runTest {
    val page1 = pushViews(1)
    assertEquals(1, navigator.pages.size)
    verifyScreen1(NodeUtils.getContent(page1))
  }

  @Test
  fun `should not find the first view with a wrong url`() = scope.runTest {
    var error: Throwable? = null
    try {
      navigator.push(ViewRequest("/none"))
      navigator.awaitPushCompletion()
    } catch(e: CancellationException) {
      // this is super-weird, but on Android the error will be at e.cause.cause while on iOS it will be in e.cause
      error = e.cause?.cause ?: e.cause
    }
    print(error?.message)
    assertTrue(error is ResponseError)
    assertEquals(HttpStatusCode.NotFound.value, error.status)
  }

  @Test
  fun `should push the second view`() = scope.runTest {
    val page2 = pushViews(2)
    assertEquals(2, navigator.pages.size)
    verifyScreen2(NodeUtils.getContent(page2))
  }

  @Test
  fun `should push the third view`() = scope.runTest {
    val page3 = pushViews(3)
    assertEquals(3, navigator.pages.size)
    verifyScreen3(NodeUtils.getContent(page3))
  }

  @Test
  fun `should show the fallback when pushing the fourth view`() = scope.runTest {
    val fallbackPage = pushViews(4)
    assertEquals(4, navigator.pages.size)
    NodeUtils.getContent(fallbackPage)
  }

  @Test
  fun `should produce an error and not navigate when going to Screen4`() = scope.runTest {
    val page3 = pushViews(3)
    var error: Throwable? = null
    try {
      NodeUtils.pressButton(page3, "next-error")
      navigator.awaitPushCompletion()
    } catch(e: CancellationException) {
      // this is super-weird, but on Android the error will be at e.cause.cause while on iOS it will be in e.cause
      error = e.cause?.cause ?: e.cause
    }
    assertTrue(error is ResponseError)
    assertEquals(404, error.status)
  }

  @Test
  fun `should push the second view and pop`() = scope.runTest {
    val page2 = pushViews(2)
    NodeUtils.pressButton(page2, "previous")
    assertEquals(1, navigator.pages.size)
    val view: ServerDrivenView? = navigator.pages.last().closestScopeWithType()
    assertEquals("/screen1", view?.description)
  }

  @Test
  fun `should pop to root from fallback`() = scope.runTest {
    val fallbackPage = pushViews(4)

    // fallback to /screen3
    NodeUtils.pressButton(fallbackPage, "previous")
    assertEquals(3, navigator.pages.size)
    var lastPage = navigator.pages.last()
    var view: ServerDrivenView? = lastPage.closestScopeWithType()
    assertEquals("/screen3", view?.description)

    // /screen3 to /screen2
    NodeUtils.pressButton(lastPage, "previous")
    lastPage = navigator.pages.last()
    view = lastPage.closestScopeWithType()
    assertEquals(2, navigator.pages.size)
    assertEquals("/screen2", view?.description)

    // /screen2 to /screen1
    NodeUtils.pressButton(lastPage, "previous")
    assertEquals(1, navigator.pages.size)
    lastPage = navigator.pages.last()
    view = lastPage.closestScopeWithType()
    assertEquals("/screen1", view?.description)
  }

  @Test
  fun `should use a navigation state`() = scope.runTest {
    navigator.push(ViewRequest("/stateful-navigation-1"))
    val page1 = navigator.awaitPushCompletion()
    NodeUtils.pressButton(page1, "next")
    val page2 = navigator.awaitPushCompletion()
    val address = page2.findNodeById("address")?.properties?.get("text")
    assertEquals("Rua dos bobos, 0", address)
  }
}

