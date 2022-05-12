import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.log.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

data class LogEntry(
  val message: String,
  val level: LogLevel,
)

class ObservableLogger(private val scope: CoroutineScope): Logger {
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
   * Use `clear` to reset the log count.
   *
   * @param numberOfLogs the number of logs to wait for.
   */
  suspend fun waitForLogEvents(numberOfLogs: Int = 1) {
    if (routine != null) throw Error("Concurrent log observations are not supported.")
    val mutex = Mutex()
    // withLock makes the block atomic (thread-safe)
    mutex.withLock {
      if (entries.size < numberOfLogs) {
        routine = scope.async { awaitCancellation() }
        logListener = {
          if (entries.size == numberOfLogs) { stopLogListening() }
        }
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
