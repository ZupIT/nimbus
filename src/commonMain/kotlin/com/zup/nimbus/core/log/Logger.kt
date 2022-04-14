package com.zup.nimbus.core.log

interface Logger {
  fun enable()
  fun disable()
  fun log(message: String)
  fun info(message: String)
  fun warn(message: String)
  fun error(message: String)
  fun success(message: String)
}
