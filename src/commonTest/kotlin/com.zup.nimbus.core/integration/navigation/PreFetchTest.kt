package com.zup.nimbus.core.integration.navigation

import com.zup.nimbus.core.*
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PreFetchTest {
  private val scope = TestScope()
  private val httpClient = ObservableHttpClient(DefaultHttpClient(serverMock))
  private val nimbus = Nimbus(ServerDrivenConfig(
    baseUrl = BASE_URL,
    platform = "test",
    httpClient = httpClient,
  ))
  private val navigator = ObservableNavigator(scope, nimbus)

  @BeforeTest
  fun clear() {
    httpClient.clear()
    navigator.clear()
  }

  @Test
  fun `should behave correctly in the first prefetch screen`()  = scope.runTest {
    // WHEN /prefetch1 is rendered
    navigator.push(ViewRequest("/prefetch1"))
    navigator.awaitPushCompletion()
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered
    AsyncUtils.flush()
    // THEN it should have called the httpClient 6 times (should not prefetch same url twice)
    assertEquals(6, httpClient.entries.size)
    // THEN it should have called the httpClient for /prefetch1.json (required fetch for the current view)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/prefetch1"))
    // THEN it should have called the httpClient for /prefetch2.json
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/prefetch2"))
    // THEN it should have called the httpClient for /screen1.json (single prefetch for multiple calls to the same url)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/screen1"))
    // THEN it should have called the httpClient for /screen2.json (two prefetches on the same node)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/screen2"))
    // THEN it should have called the httpClient for /screen3.json (two prefetches on the same action)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/screen3"))
    // THEN it should have called the httpClient for /bad.json
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/bad"))
  }

  @Test
  fun `should behave correctly when navigating to the second prefetch screen`() = scope.runTest(100) {
    // WHEN /prefetch1 is rendered
    navigator.push(ViewRequest("/prefetch1"))
    val prefetch1 = navigator.awaitPushCompletion()
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered
    AsyncUtils.flush()
    // WHEN all the prefetch requests finish
    httpClient.awaitAllCurrentRequestsToFinish()
    httpClient.clear()
    // WHEN a request to /prefetch2 takes 1 second to complete
    httpClient.delayMsPerUrl["${BASE_URL}/prefetch2"] = 1000
    // WHEN the user navigates to /prefetch2.json
    NodeUtils.pressButton(prefetch1.content, 1)
    // THEN it should use the prefetched result to render /prefetch2. Since this test fails after 100ms and the request
    // will take 1s, it fails if the prefetched result is not used.
    navigator.awaitPushCompletion()
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered
    AsyncUtils.flush()
    // THEN should have called the httpClient twice
    assertEquals(2, httpClient.entries.size)
    // THEN should have called the httpClient for /prefetch2.json (prefetched result should have been consumed)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/prefetch2"))
    // THEN should have called the httpClient for /screen1.json (prefetched result should be replaced)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/screen1"))
  }

  @Test
  fun `should behave correctly when navigating to bad url`() = scope.runTest {
    // WHEN /prefetch1 is rendered
    navigator.push(ViewRequest("/prefetch1"))
    val prefetch1 = navigator.awaitPushCompletion()
    // WHEN all fetch and pre-fetch requests complete
    AsyncUtils.waitUntil { httpClient.entries.size == 6 }
    httpClient.awaitAllCurrentRequestsToFinish()
    httpClient.clear()
    // When the user navigates to /bad.json
    NodeUtils.pressButton(prefetch1.content, 4)
    // THEN it should ignore the failed prefetched result and make a new network call
    AsyncUtils.flush()
    assertEquals(1, httpClient.entries.size)
    assertTrue(httpClient.hasFetchedUrl("${BASE_URL}/bad"))
  }

  @Test
  fun `should await an existing request`() = scope.runTest {
    // WHEN a request to /prefetch2 takes 1 second to complete
    httpClient.delayMsPerUrl["${BASE_URL}/prefetch2"] = 1000
    // WHEN /prefetch1 is rendered
    navigator.push(ViewRequest("/prefetch1"))
    val prefetch1 = navigator.awaitPushCompletion()
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered (less than 1 second)
    AsyncUtils.flush()
    httpClient.clear()
    // When the user navigates to /prefetch2.json
    NodeUtils.pressButton(prefetch1.content, 1)
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered (less than 1 second)
    AsyncUtils.flush()
    // THEN it should await the existing request instead of making another one
    assertTrue(httpClient.entries.isEmpty())
  }

  @Test
  fun `should not prefetch`() = scope.runTest {
    // WHEN /screen1 is rendered
    navigator.push(ViewRequest("/screen1"))
    navigator.awaitPushCompletion()
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered
    AsyncUtils.flush()
    // THEN it should have called the httpClient only once (for the current view)
    assertEquals(1, httpClient.entries.size)
  }

  @Test
  fun `should not prefetch if the node has already been rendered`() = scope.runTest {
    // WHEN /prefetch1 is rendered
    navigator.push(ViewRequest("/prefetch1"))
    navigator.awaitPushCompletion()
    // WHEN all prefetch requests have finished
    AsyncUtils.waitUntil { httpClient.entries.size == 6 }
    httpClient.awaitAllCurrentRequestsToFinish()
    httpClient.clear()
    // WHEN the global state is updated and every node in the page is forced to refresh
    nimbus.globalState.set("test")
    // WHEN we give enough time for every asynchronous pre-fetch to be triggered
    AsyncUtils.flush()
    // THEN no prefetch should have been triggered again
    assertTrue(httpClient.entries.isEmpty())
  }
}
