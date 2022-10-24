package com.zup.nimbus.core.deserialization

import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.dynamic.DynamicEvent

class MapDeserializer {
  private var errors = ArrayList<String>()
  private var current: Map<String, Any?>? = null
  private var path = ArrayList<String>()
  private var previous = ArrayList<Map<String, Any?>>()

  fun addTypeError(expected: String, key: String, value: Any?, shouldUseValueInsteadOfType: Boolean = false) {
    val found = if (value == null) "null" else if (shouldUseValueInsteadOfType) value else value::class.simpleName
    val propertyPath = if (path.isEmpty()) key else "${path.joinToString(".")}.$key"
    errors.add("Expected $expected for property \"$propertyPath\", but found $found.")
  }

  private fun getString(key: String, nullable: Boolean): String? {
    val value = current?.get(key) ?: return if (nullable) null else ""
    return "$value"
  }

  private fun <T: Enum<*>>getEnum(key: String, enum: Array<T>, nullable: Boolean): T? {
    val stringValue = getString(key, true)
    if (nullable && stringValue == null) return null
    val value = enum.find { it.name.lowercase() == stringValue!!.lowercase() }
    if (value != null || nullable) return value
    addTypeError(enum.joinToString(", ") { it.name }, key, value, true)
    return enum[0]
  }

  private fun getInt(key: String, nullable: Boolean): Int? {
    val value = current?.get(key)
    if (nullable && value == null) return null
    if (value is Int) return value
    if (value is Double) return value.toInt()
    if (value is Number) return value.toInt()
    addTypeError("a number", key, value)
    return 0
  }

  private fun getDouble(key: String, nullable: Boolean): Double? {
    val value = current?.get(key)
    if (nullable && value == null) return null
    if (value is Int) return value.toDouble()
    if (value is Double) return value
    if (value is Number) return value.toDouble()
    addTypeError("a number", key, value)
    return 0.0
  }

  private fun getBoolean(key: String, nullable: Boolean): Boolean? {
    val value = current?.get(key)
    if (nullable && value == null) return null
    if (value is Boolean) return value
    addTypeError("a boolean", key, value)
    return false
  }

  private fun <T>getList(key: String, nullable: Boolean): List<T>? {
    val value = current?.get(key)
    if (nullable && value == null) return null
    @Suppress("UNCHECKED_CAST")
    if (value is List<*>) return value as List<T>
    addTypeError("an array", key, value)
    return emptyList()
  }

  private fun <T>getMap(key: String, nullable: Boolean): Map<String, T>? {
    val value = current?.get(key)
    if (nullable && value == null) return null
    @Suppress("UNCHECKED_CAST")
    if (value is Map<*, *>) return value as Map<String, T>
    addTypeError("an object", key, value)
    return emptyMap()
  }

  private fun getEvent(key: String, nullable: Boolean): ServerDrivenEvent? {
    val value = current?.get(key)
    if (nullable && value == null) return null
    if (value is ServerDrivenEvent) return value
    addTypeError("an event, i.e. an array of actions.", key, value)
    return DynamicEvent("Unknown")
  }

  fun start(map: Map<String, Any?>?) {
    current = map
    errors = ArrayList()
    path = ArrayList()
  }

  fun enter(key: String, nullable: Boolean): Boolean {
    val map = getMap<Any?>(key, nullable) ?: return false
    previous.add(current ?: return false)
    current = map
    path.add(key)
    return true
  }

  fun leave(): Boolean {
    if (previous.isEmpty()) return false
    current = previous.removeLast()
    path.removeLast()
    return true
  }

  fun end(): List<String> {
    return errors
  }

  fun asString(key: String): String {
    return getString(key, false)!!
  }

  fun <T: Enum<*>>asEnum(key: String, enum: Array<T>): T {
    return getEnum(key, enum, false)!!
  }

  fun asInt(key: String): Int {
    return getInt(key, false)!!
  }

  fun asDouble(key: String): Double {
    return getDouble(key, false)!!
  }

  fun asBoolean(key: String): Boolean {
    return getBoolean(key, false)!!
  }

  fun <T>asList(key: String): List<T> {
    return getList(key, false)!!
  }

  fun <T>asMap(key: String): Map<String, T> {
    return getMap(key, false)!!
  }

  fun asEvent(key: String): ServerDrivenEvent {
    return getEvent(key, false)!!
  }

  fun asStringOrNull(key: String): String? {
    return getString(key, true)
  }

  fun <T: Enum<*>>asEnumOrNull(key: String, enum: Array<T>): T? {
    return getEnum(key, enum, true)
  }

  fun asIntOrNull(key: String): Int? {
    return getInt(key, true)
  }

  fun asDoubleOrNull(key: String): Double? {
    return getDouble(key, true)
  }

  fun asBooleanOrNull(key: String): Boolean? {
    return getBoolean(key, true)
  }

  fun <T>asListOrNull(key: String): List<T>? {
    return getList(key, true)
  }

  fun <T>asMapOrNull(key: String): Map<String, T>? {
    return getMap(key, true)
  }

  fun asEventOrNull(key: String): ServerDrivenEvent? {
    return getEvent(key, true)
  }
}
