package com.zup.nimbus.core.log

class DefaultLogger: Logger {
  private var isEnabled = true

  private enum class LoggerColor(val code: String) {
    RESET("\u001B[0m"),
    BLUE("\u001B[34m"),
    CYAN("\u001B[36m"),
    GREEN("\u001B[32m"),
    PURPLE("\u001B[35m"),
    RED("\u001B[31m"),
    WHITE("\u001B[37m"),
    YELLOW("\u001B[33m"),
  }

  override fun enable() {
    isEnabled = true
  }

  override fun disable() {
    isEnabled = false
  }

  override fun log(vararg messages: String) {
    print(LoggerColor.WHITE, *messages)
  }

  override fun info(vararg messages: String) {
    print(LoggerColor.BLUE, *messages)
  }

  override fun warn(vararg messages: String) {
    print(LoggerColor.YELLOW, *messages)
  }

  override fun success(vararg messages: String) {
    print(LoggerColor.GREEN, *messages)
  }

  override fun error(vararg messages: String) {
    print(LoggerColor.RED, *messages)
  }

  override fun lifecycle(vararg messages: String) {
    print(LoggerColor.PURPLE, *messages)
  }

  override fun expression(vararg messages: String) {
    print(LoggerColor.CYAN, *messages)
  }

  private fun print(color: LoggerColor, vararg messages: String) {
    if (isEnabled) {
      messages.forEach {
        println("${color.code}${it}${LoggerColor.RESET.code}")
      }
    }
  }
}
