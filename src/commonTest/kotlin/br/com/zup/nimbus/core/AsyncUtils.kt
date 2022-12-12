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

package br.com.zup.nimbus.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val POLL_MS = 20L
private const val TIMEOUT = 500L

object AsyncUtils {
  /**
   * Suspend the current thread until the condition expressed by `validator` is met.
   * If timeout is reached, an error is thrown and the coroutine is cancelled.
   */
  suspend fun waitUntil(timeout: Long = TIMEOUT, validator: () -> Boolean) {
    var timePassed = 0L
    while (!validator() && timePassed < timeout) {
      withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
        delay(POLL_MS)
        timePassed += POLL_MS
      }
    }
    if (timePassed >= timeout) throw Error("Timed out while waiting for condition to be true.")
  }

  /**
   * Suspend the current thread for a minimal amount of time, just to make sure every thread have been given enough time
   * to run, i.e. "flush" its content.
   */
  suspend fun flush(timeMs: Long = 10) {
    withContext(CoroutineScope(Dispatchers.Default).coroutineContext) { delay(timeMs) }
  }
}
