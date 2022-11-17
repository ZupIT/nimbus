package com.zup.nimbus.core.deserialization

import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.dynamic.DynamicEvent

class AnyServerDrivenData private constructor (
  val value: Any?,
  val path: String,
  private val errors: MutableList<String>,
) {
  constructor(value: Any?): this(value, "", mutableListOf())

  companion object {
    val any = Any()
  }

  // Private functions

  private fun addTypeError(expected: String, shouldUseValueInsteadOfType: Boolean = false) {
    val found = if (value == null) "null" else if (shouldUseValueInsteadOfType) value else value::class.simpleName
    errors.add("Expected $expected for property \"$path\", but found $found.")
  }

  private fun buildPath(key: String): String = if (path.isEmpty()) key else "$path.$key"
  private fun buildPath(index: Int): String = "$path[$index]"

  // Type converters with "nullable" parameter

  fun asAny(nullable: Boolean): Any? {
    return value ?: if (nullable) null else {
      addTypeError("anything")
      any
    }
  }

  fun asString(nullable: Boolean): String? {
    return value?.let { "$value" } ?: if (nullable) null else {
      addTypeError("a string")
      ""
    }
  }

  @Suppress("ReturnCount")
  fun <T: Enum<*>>asEnum(enum: Array<T>, nullable: Boolean): T? {
    val stringValue = asString(true)
    if (nullable && stringValue == null) return null
    val data = enum.find { it.name.lowercase() == stringValue?.lowercase() }
    if (data != null) return data
    addTypeError(enum.joinToString(", ") { it.name }, true)
    return enum[0]
  }

  fun asInt(nullable: Boolean): Int? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value
      is Double -> value.toInt()
      is Number -> value.toInt()
      is Long -> value.toInt()
      is Float -> value.toInt()
      is String -> value.toDoubleOrNull()?.toInt()
      else -> null
    } ?: run {
      addTypeError("a number")
      0
    }
  }

  fun asLong(nullable: Boolean): Long? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value.toLong()
      is Double -> value.toLong()
      is Number -> value.toLong()
      is Long -> value
      is Float -> value.toLong()
      is String -> value.toDoubleOrNull()?.toLong()
      else -> null
    } ?: run {
      addTypeError("a number")
      0
    }
  }

  fun asDouble(nullable: Boolean): Double? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value.toDouble()
      is Double -> value
      is Number -> value.toDouble()
      is Long -> value.toDouble()
      is Float -> value.toDouble()
      is String -> value.toDoubleOrNull()
      else -> null
    } ?: run {
      addTypeError("a number")
      0.0
    }
  }

  fun asFloat(nullable: Boolean): Float? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value.toFloat()
      is Double -> value.toFloat()
      is Number -> value.toFloat()
      is Long -> value.toFloat()
      is Float -> value
      is String -> value.toFloatOrNull()
      else -> null
    } ?: run {
      addTypeError("a number")
      0.0F
    }
  }

  fun asBoolean(nullable: Boolean): Boolean? {
    if (nullable && value == null) return null
    if (value is Boolean) return value
    addTypeError("a boolean")
    return false
  }

  fun asList(nullable: Boolean): List<AnyServerDrivenData>? {
    if (nullable && value == null) return null
    if (value is List<*>) {
      return value.mapIndexed { index, item -> AnyServerDrivenData(item, buildPath(index), errors) }
    }
    addTypeError("an array")
    return emptyList()
  }

  fun asMap(nullable: Boolean): Map<String, AnyServerDrivenData>? {
    if (nullable && value == null) return null
    if (value is Map<*, *>) {
      @Suppress("UNCHECKED_CAST")
      return value.mapValues {
        AnyServerDrivenData(it.value, buildPath("${it.key}"), errors)
      } as Map<String, AnyServerDrivenData>
    }
    addTypeError("an object")
    return emptyMap()
  }

  fun asEvent(nullable: Boolean): ServerDrivenEvent? {
    if (nullable && value == null) return null
    if (value is ServerDrivenEvent) return value
    addTypeError("an event, i.e. an array of actions.")
    return DynamicEvent("Unknown")
  }

  // Non-nullable type converters: aliases to asType(false)!!

  fun asAny(): Any = asAny(false)!!
  fun asString(): String = asString(false)!!
  fun <T: Enum<*>>asEnum(enum: Array<T>): T = asEnum(enum, false)!!
  fun asInt(): Int = asInt(false)!!
  fun asLong(): Long = asLong(false)!!
  fun asDouble(): Double = asDouble(false)!!
  fun asFloat(): Float = asFloat(false)!!
  fun asBoolean(): Boolean = asBoolean(false)!!
  fun asList(): List<AnyServerDrivenData> = asList(false)!!
  fun asMap(): Map<String, AnyServerDrivenData> = asMap(false)!!
  fun asEvent(): ServerDrivenEvent = asEvent(false)!!

  // Nullable type converters: aliases to asType(true)

  fun asAnyOrNull(): Any? = asAny(true)
  fun asStringOrNull(): String? = asString(true)
  fun <T: Enum<*>>asEnumOrNull(enum: Array<T>): T? = asEnum(enum, true)
  fun asIntOrNull(): Int? = asInt(true)
  fun asLongOrNull(): Long? = asLong(true)
  fun asDoubleOrNull(): Double? = asDouble(true)
  fun asFloatOrNull(): Float? = asFloat(true)
  fun asBooleanOrNull(): Boolean? = asBoolean(true)
  fun asListOrNull(): List<AnyServerDrivenData>? = asList(true)
  fun asMapOrNull(): Map<String, AnyServerDrivenData>? = asMap(true)
  fun asEventOrNull(): ServerDrivenEvent? = asEvent(true)

  // Type checkers

  fun isMap(): Boolean = value is Map<*, *>
  fun isList(): Boolean = value is List<*>
  fun isString(): Boolean = value is String
  fun isInt(): Boolean = value is Int
  fun isLong(): Boolean = value is Long
  fun isDouble(): Boolean = value is Double
  fun isFloat(): Boolean = value is Float
  fun isBoolean(): Boolean = value is Boolean
  fun isNull(): Boolean = value == null

  // Map and List utilities
  fun containsKey(key: String): Boolean = value is Map<*, *> && value.containsKey(key)
  fun hasValueForKey(key: String): Boolean = value is Map<*, *> && value[key] != null
  fun hasAnyOfKeys(keys: List<String>): Boolean = value is Map<*, *> && keys.find { value.keys.contains(it) } != null
  fun containsElement(element: Any?): Boolean = value is List<*> && value.contains(element)
  fun hasValueForIndex(index: Int): Boolean = value is List<*> && value.getOrNull(index) != null
  fun listSize(): Int = if (value is List<*>) value.size else 0
  fun mapSize(): Int = if (value is Map<*, *>) value.size else 0

  // Error handling

  fun hasError(): Boolean = errors.isNotEmpty()
  fun errorsAsString(prefix: String = "\n\t"): String = "$prefix${errors.joinToString(prefix)}"

  // Accessors

  fun get(key: String): AnyServerDrivenData {
    val actualValue = if (value is Map<*, *>) value[key] else null
    return AnyServerDrivenData(actualValue, buildPath(key), errors)
  }

  fun at(index: Int): AnyServerDrivenData {
    val actualValue = if (value is List<*>) value.getOrNull(index) else null
    return AnyServerDrivenData(actualValue, buildPath(index), errors)
  }

  override fun equals(other: Any?): Boolean = other is AnyServerDrivenData && value == other.value
}
