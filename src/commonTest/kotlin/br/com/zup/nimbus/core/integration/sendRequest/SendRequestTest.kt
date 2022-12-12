/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.integration.sendRequest

import br.com.zup.nimbus.core.AsyncUtils
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.ObservableLogger
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.log.LogLevel
import br.com.zup.nimbus.core.network.DefaultHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SendRequestTest {
  private val scope = TestScope()
  private val logger = ObservableLogger()

  @BeforeTest
  fun setup() {
    Dispatchers.setMain(Dispatchers.Unconfined)
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  private val nimbus = Nimbus(
    ServerDrivenConfig(
      baseUrl = BASE_URL,
      platform = "test",
      httpClient = DefaultHttpClient(serverMock),
      logger = logger,
    )
  )

  private fun runSendRequestTest(
    json: String,
    numberOfLogsToWaitFor: Int = 2,
    onLogEvent: () -> Unit,
  ) = scope.runTest {
    logger.clear()
    val tree = nimbus.nodeBuilder.buildFromJsonString(json)
    tree.initialize(nimbus)
    NodeUtils.pressButton(tree, "send-request-btn")
    AsyncUtils.waitUntil { logger.entries.size >= numberOfLogsToWaitFor }
    onLogEvent()
  }

  @Test
  fun `should run on success`() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals(2, logger.entries.size)
    val firstLog = logger.entries.first()
    assertEquals("success", firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }

  @Test
  fun `should run on error`() = runSendRequestTest(buildScreen("/user/2"), 3) {
    assertEquals(3, logger.entries.size)
    val firstLog = logger.entries.first()
    val secondLog = logger.entries[1]
    assertEquals(LogLevel.Error, firstLog.level)
    assertEquals("error", secondLog.message)
    assertEquals(LogLevel.Error, secondLog.level)
  }

  @Test
  fun `should run on finish`() = runSendRequestTest(buildScreen("/user/1")) {
    assertEquals(2, logger.entries.size)
    val lastLog = logger.entries.last()
    assertEquals("finish", lastLog.message)
    assertEquals(LogLevel.Info, lastLog.level)
  }

  @Test
  fun `should fail before sending request`() = runSendRequestTest(buildScreen(null), 1) {
    assertEquals(1, logger.entries.size)
    val firstLog = logger.entries.first()
    assertContains(firstLog.message, "Expected a string for property \"url\", but found null")
    assertEquals(LogLevel.Error, firstLog.level)
  }

  @Test
  fun `should run on success if on error and on finish are missing`() = runSendRequestTest(
    buildScreen("/user/1", false),
    1,
  ) {
    assertEquals(1, logger.entries.size)
    val firstLog = logger.entries.first()
    assertEquals("success", firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }

  private fun runPostTest(
    data: String,
    expressionToLog: String,
    expectedLog: String,
  ) = runSendRequestTest(createPostScreen(data, expressionToLog), 1) {
    assertEquals(1, logger.entries.size)
    val firstLog = logger.entries.first()
    assertEquals(expectedLog, firstLog.message)
    assertEquals(LogLevel.Info, firstLog.level)
  }

  @Test
  fun `should post string`() = runPostTest("\"my data\"", "@{onSuccess.data}", "my data")

  @Test
  fun `should post boolean`() = runPostTest("true", "@{onSuccess.data}", "true")

  @Test
  fun `should post int`() = runPostTest("123", "@{onSuccess.data}", "123")

  @Test
  fun `should post long`() = runPostTest("2147483649", "@{onSuccess.data}", "2147483649")

  @Test
  fun `should post double`() = runPostTest("123.456789", "@{onSuccess.data}", "123.456789")

  @Test
  fun `should post List`() = runPostTest(
    "[1, \"test\", true, null, 50.3, [1], { \"hello\": \"world\" }]",
    "@{onSuccess.data[0]} @{onSuccess.data[1]} @{onSuccess.data[2]} @{onSuccess.data[3]} @{onSuccess.data[4]} " +
      "@{onSuccess.data[5][0]} @{onSuccess.data[6].hello}",
    "1 test true  50.3 1 world",
  )

  @Test
  fun `should post Map`() = runPostTest(
    """{ "a": 1, "b": "test", "c": true, "d": null, "e": 50.3, "f": [1], "g": { "hello": "world" } }""",
    "@{onSuccess.data.a} @{onSuccess.data.b} @{onSuccess.data.c} @{onSuccess.data.d} @{onSuccess.data.e} " +
      "@{onSuccess.data.f[0]} @{onSuccess.data.g.hello}",
    "1 test true  50.3 1 world",
  )
}
