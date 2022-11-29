package br.com.zup.nimbus.core.log

class DefaultLogger: Logger {
  private var isEnabled = true

  private enum class LoggerColor(val code: String) {
    RESET("\u001B[0m"),
    BLUE("\u001B[34m"),
    RED("\u001B[31m"),
    YELLOW("\u001B[33m"),
  }

  override fun enable() {
    isEnabled = true
  }

  override fun disable() {
    isEnabled = false
  }

  override fun log(message: String, level: LogLevel) {
    when (level) {
      LogLevel.Error -> error(message)
      LogLevel.Info -> info(message)
      LogLevel.Warning -> warn(message)
    }
  }

  override fun info(message: String) {
    print(LoggerColor.BLUE, message)
  }

  override fun warn(message: String) {
    print(LoggerColor.YELLOW, message)
  }

  override fun error(message: String) {
    print(LoggerColor.RED, message)
  }

  private fun print(color: LoggerColor, message: String) {
    if (isEnabled) {
      println("${color.code}${message}${LoggerColor.RESET.code}")
    }
  }
}
