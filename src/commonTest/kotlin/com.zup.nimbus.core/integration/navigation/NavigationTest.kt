package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ResponseError
import com.zup.nimbus.core.network.ViewRequest
import io.ktor.http.*
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

  @Test
  fun shouldRenderFirstView() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = { fail("Unexpected error: ${it.message}") },
      onLoad = {
        assertEquals(1, it.size)
        verifyScreen1(it.last().content)
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }

  @Test
  fun shouldNotFindFirstViewWithWrongUrl() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = {
        assertEquals(true, it is ResponseError)
        it as ResponseError
        assertEquals(HttpStatusCode.NotFound.value, it.status)
      },
      onLoad = { fail("An error was expected.") },
    )
    navigator.push(ViewRequest("/none"))
  }

  @Test
  fun shouldPushSecondView() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = { fail("Unexpected error: ${it.message}") },
      onLoad = {
        if(it.last().id == "/screen1") {
          val nextButton = it.last().content?.children?.get(1)
          val onPress = nextButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
        } else {
          assertEquals(2, it.size)
          verifyScreen2(it.last().content)
        }
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }

  @Test
  fun shouldPushThirdView() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = { fail("Unexpected error: ${it.message}") },
      onLoad = {
        if(it.last().id == "/screen1" || it.last().id == "/screen2") {
          val nextButton = it.last().content?.children?.get(1)
          val onPress = nextButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
        } else {
          assertEquals(3, it.size)
          verifyScreen3(it.last().content)
        }
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }

  @Test
  fun shouldShowFallbackWhenPushingFourthView() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = { fail("Unexpected error: ${it.message}") },
      onLoad = {
        if(it.last().id == "/screen1" || it.last().id == "/screen2" || it.last().id == "/screen3") {
          val nextButton = it.last().content?.children?.get(1)
          val onPress = nextButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
        } else {
          assertEquals(4, it.size)
          verifyFallbackScreen(it.last().content)
        }
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }

  @Test
  fun shouldProduceErrorAndNotNavigateWhenGoingToScreen4() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = {
        assertEquals(true, it is ResponseError)
        it as ResponseError
        assertEquals(404, it.status)
      },
      onLoad = {
        val index = if (it.last().id == "/screen3") 2 else 1
        val nextButton = it.last().content?.children?.get(index)
        val onPress = nextButton?.properties?.get("onPress")
        if (onPress is Function0<*>) onPress.invoke()
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }

  @Test
  fun shouldPushSecondViewAndPop() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = { fail("Unexpected error: ${it.message}") },
      onLoad = {
        if(it.last().id == "/screen1") {
          val nextButton = it.last().content?.children?.get(1)
          val onPress = nextButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
        } else {
          val previousButton = it.last().content?.children?.get(2)
          val onPress = previousButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
          assertEquals(true, it.size == 1)
          assertEquals("/screen1", it.last().id)
        }
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }

  @Test
  fun shouldPopToRootFromFallback() = scope.runTest {
    val navigator = Navigator(
      scope = scope,
      nimbus = nimbus,
      onError = { fail("Unexpected error: ${it.message}") },
      onLoad = {
        if(it.last().id == "/screen1" || it.last().id == "/screen2" || it.last().id == "/screen3") {
          val nextButton = it.last().content?.children?.get(1)
          val onPress = nextButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
        } else {
          // fallback to /screen3
          var previousButton = it.last().content?.children?.get(1)
          var onPress = previousButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
          assertEquals(true, it.size == 3)
          assertEquals("/screen3", it.last().id)
          // /screen3 to /screen2
          previousButton = it.last().content?.children?.get(3)
          onPress = previousButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
          assertEquals(true, it.size == 2)
          assertEquals("/screen2", it.last().id)
          // /screen2 to /screen1
          previousButton = it.last().content?.children?.get(2)
          onPress = previousButton?.properties?.get("onPress")
          if (onPress is Function0<*>) onPress.invoke()
          assertEquals(true, it.size == 1)
          assertEquals("/screen1", it.last().id)
        }
      },
    )
    navigator.push(ViewRequest("/screen1"))
  }
}
