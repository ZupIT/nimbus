package com.zup.nimbus.core.utils

import platform.Foundation.NSNull

actual object Null {
  actual fun isNull(value: Any?) = value == null || value is NSNull

  actual fun <T> sanitize(value: T): T? = if (isNull(value)) null else value
}
