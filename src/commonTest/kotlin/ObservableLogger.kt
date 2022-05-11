import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.log.Logger
import kotlinx.coroutines.*

data class LogEntry(
  val message: String,
  val level: LogLevel,
)

class LogTimeoutError: Error("Timeout while waiting for log event!")

class ObservableLogger(private val scope: CoroutineScope): Logger {
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

  fun waitForLogEvent(maximumWaitTimeMs: Long = 500, callback: (log: LogEntry) -> Unit) {
    if (routine != null) throw Error("Concurrent log observations are not supported.")
    routine = scope.launch {
      delay(maximumWaitTimeMs)
      stopLogListening()
      throw LogTimeoutError()
    }
    logListener = {
      callback(it)
      stopLogListening()
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
