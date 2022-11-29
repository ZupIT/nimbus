package com.zup.nimbus.core.utils

actual object Null {
  actual fun isNull(value: Any?) = value == null

  actual fun <T> sanitize(value: T): T? = if (isNull(value)) null else value
}
