import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.log.Logger
import kotlinx.coroutines.*

data class LogEntry(
  val message: String,
  val level: LogLevel,
)

class LogTimeoutError: Error("Timeout while waiting for log event!")

class ObservableLogger: Logger {
  override fun enable() {}
  override fun disable() {}
  var entries = ArrayList<LogEntry>()
  private var routine: Deferred<Unit>? = null
  private var logListener: (() -> Unit)? = null

  private fun stopLogListening() {
    logListener = null
    routine?.cancel()
    routine = null
  }

  fun clear() {
    entries = ArrayList()
    stopLogListening()
  }

  /**
   * Wait until the number of logs created by this logger is the number of logs expected. If this logger has already
   * created the expected number of logs, this suspended function completes automatically. Otherwise, it completes once
   * the expected number of logs is created.
   *
   * If `timeoutMs` milliseconds passes and the expected amount of logs is not created. LogTimeoutError is thrown.
   *
   * Use `clear` to reset the log count.
   *
   * Attention: since this throws an exception when it times out, you might want to increase the timeout for debugging
   * the code that comes before it.
   *
   * @param numberOfLogs the number of logs to wait for.
   * @param timeoutMs the maximum amount of time to wait for (in ms).
   * @throws LogTimeoutError if the expected number of logs is not reached within `timeoutMs` milliseconds.
   */
  suspend fun waitForLogEvents(numberOfLogs: Int = 1, timeoutMs: Long = 20000) {
    if (routine != null) throw Error("Concurrent log observations are not supported.")
    if (entries.size == numberOfLogs) return
    routine = CoroutineScope(Dispatchers.Default).async {
      delay(timeoutMs)
      stopLogListening()
      throw LogTimeoutError()
    }
    logListener = {
      if (entries.size == numberOfLogs) {
        stopLogListening()
      }
    }
    try {
      routine?.await()
    } catch (e: CancellationException) {}
  }

  override fun log(message: String, level: LogLevel) {
    entries.add(LogEntry(message, level))
    if (logListener != null) logListener!!()
  }

  override fun info(message: String) {
    log(message, LogLevel.Info)
  }

  override fun warn(message: String) {
    log(message, LogLevel.Warning)
  }

  override fun error(message: String) {
    log(message, LogLevel.Error)
  }

  override fun success(message: String) {
    TODO("Not yet implemented")
  }
}
