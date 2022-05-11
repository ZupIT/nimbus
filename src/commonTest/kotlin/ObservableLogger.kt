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
  var logEntries = ArrayList<LogEntry>()
  private var routine: Job? = null
  private var logListener: ((log: LogEntry) -> Unit)? = null

  private fun stopLogListening() {
    logListener = null
    routine?.cancel()
    routine = null
  }

  fun clear() {
    logEntries = ArrayList()
    stopLogListening()
  }

  /**
   * Wait for a number of log events. Once all expected events are fired, the callback function is called with the last
   * log entry.
   *
   * If `timeoutMs` passes and the log events don't fire. LogTimeoutError is thrown.
   *
   * @param numberOfEvents the number of events to wait for.
   * @param timeoutMs the maximum amount of time to wait for the events to fire.
   * @param callback the callback to execute once the expected number of events is fired.
   * @throws LogTimeoutError if the expected number of events is not fired within `timeoutMs` milliseconds.
   */
  fun waitForLogEvents(numberOfEvents: Int = 1, timeoutMs: Long = 500, callback: (log: LogEntry) -> Unit) {
    if (routine != null) throw Error("Concurrent log observations are not supported.")
    var events = 0
    routine = CoroutineScope(Dispatchers.Default).launch {
      delay(timeoutMs)
      stopLogListening()
      throw LogTimeoutError()
    }
    logListener = {
      if (++events == numberOfEvents) {
        callback(it)
        stopLogListening()
      }
    }
  }

  override fun log(message: String, level: LogLevel) {
    val entry = LogEntry(message, level)
    logEntries.add(entry)
    if (logListener != null) logListener!!(entry)
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
