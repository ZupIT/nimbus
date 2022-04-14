package com.zup.nimbus.core.log

interface Logger {
  fun enable()
  fun disable()
  fun log(vararg messages: String)
  fun info(vararg messages: String)
  fun warn(vararg messages: String)
  fun error(vararg messages: String)
  fun success(vararg messages: String)
  fun lifecycle(vararg messages: String)
  fun expression(vararg messages: String)
}
