package com.zup.nimbus.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val POLL_MS = 20L
private const val TIMEOUT = 500L

object AsyncUtils {
  /**
   * Suspend the current thread until the condition expressed by `validator` is met or `timeout` is reached.
   * If timeout is reached, and error is thrown.
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
  suspend fun flush() {
    withContext(CoroutineScope(Dispatchers.Default).coroutineContext) { delay(10) }
  }
}
