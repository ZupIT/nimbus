package com.zup.nimbus.core

import com.zup.nimbus.core.log.LogLevel
import com.zup.nimbus.core.log.Logger
import kotlin.jvm.Volatile

data class LogEntry(
  val message: String,
  val level: LogLevel,
)

class ObservableLogger(): Logger {
  override fun enable() {}
  override fun disable() {}
  @Volatile
  var entries = ArrayList<LogEntry>()

  fun clear() {
    entries = ArrayList()
  }

  override fun log(message: String, level: LogLevel) {
    entries.add(LogEntry(message, level))
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
}
