package com.zup.nimbus.core.utils

expect object Null {
  /**
   * Verifies if the value is null considering all the null values of the platform.
   *
   * Example: NSNull on iOS.
   */
  fun isNull(value: Any?): Boolean

  /**
   * If the value passed as parameter corresponds to null in the current platform, this function returns the Kotlin
   * Null (`null`). Otherwise, it returns the value.
   */
  fun <T>sanitize(value: T): T?
}
