package com.zup.nimbus.core.deserialization

import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.dynamic.DynamicEvent
import com.zup.nimbus.core.utils.Null

/**
 * This class helps to deserialize data of unknown type. This is very useful for deserializing the data that comes from
 * the JSON of a Nimbus response.
 *
 * To get the wrapped value as a specific type, use the methods: `asString()`, `asStringOrNull()`, `asInt()`,
 * `asLong()`, `asListOrNull()`, `asEnum(enum: Array<Enum<*>>)`, etc.
 *
 * With the exception of the method `toJson()`, this class **will never throw errors**, instead, whenever a
 * deserialization error happens, it will fill its own array of errors with a new error string. To check if any error
 * happened during deserialization, call the method `hasError()`. To get the errors themselves, call `errorsAsString()`.
 *
 * If the value wrapped by this represents a map or a list, you can navigate through the structure using the methods
 * `get(key: String)` and `at(index: Int)`, which returns the child value wrapped in a new `AnyServerDrivenData`.
 */
class AnyServerDrivenData private constructor (
  /**
   * The actual value wrapped by this.
   */
  value: Any?,
  /**
   * The path for the current data access.
   *
   * Example:
   * ```
   * val data = AnyServerDrivenData(mapOf(
   *     "a" to 1,
   *     "b" to listOf(
   *         0,
   *         mapOf(
   *             "c" to "hello",
   *         ),
   *     ),
   * ))
   * val textData = data.get("b").at(1).get("c")
   * ```
   *
   * The path of `textData` will be "b[1].c"
   */
  val path: String,
  /**
   * The errors generated by this AnyServerDrivenData, any of its ancestors and any of its descendants.
   * The errors array is shared between the original AnyServerDrivenData and any AnyServerDrivenData spawned by it via
   * the methods "get", "at", "asList" and "asMap".
   */
  private val errors: MutableList<String>,
) {
  constructor(
    /**
     * The actual value wrapped by this.
     */
    value: Any?,
  ): this(value, "", mutableListOf())

  val value: Any?

  init {
    this.value = Null.sanitize(value)
  }

  /**
   * Holds the values to use when null is found on functions that can't return null.
   */
  companion object {
    val emptyAny = Any()
    val emptyEvent = DynamicEvent("Unknown")
    const val emptyString = ""
    const val emptyInt = 0
    const val emptyLong = 0L
    const val emptyFloat = 0F
    const val emptyDouble = 0.0
    const val emptyBoolean = false
    val emptyMap = emptyMap<String, AnyServerDrivenData>()
    val emptyList = emptyList<AnyServerDrivenData>()
  }

  // Private functions

  private fun addTypeError(expected: String, shouldUseValueInsteadOfType: Boolean = false) {
    val found = when {
      value == null -> "null"
      shouldUseValueInsteadOfType -> value
      value is Map<*, *> -> "Map"
      value is List<*> -> "List"
      value is ServerDrivenEvent -> "Event"
      else -> value::class.simpleName
    }
    errors.add("Expected $expected for property \"$path\", but found $found.")
  }

  private fun buildPath(key: String): String = if (path.isEmpty()) key else "$path.$key"
  private fun buildPath(index: Int): String = "$path[$index]"

  // Private type converters with the "nullable" parameter

  private fun asAny(nullable: Boolean): Any? {
    return value ?: if (nullable) null else {
      addTypeError("anything")
      emptyAny
    }
  }

  private fun asString(nullable: Boolean): String? {
    return value?.let { "$value" } ?: if (nullable) null else {
      addTypeError("a string")
      emptyString
    }
  }

  @Suppress("ReturnCount")
  private fun <T: Enum<*>>asEnum(enum: Array<T>, nullable: Boolean): T? {
    val stringValue = asString(true)
    if (nullable && stringValue == null) return null
    val data = enum.find { it.name.lowercase() == stringValue?.lowercase() }
    if (data != null) return data
    addTypeError(enum.joinToString(", ") { it.name }, true)
    return enum[0]
  }

  private fun asInt(nullable: Boolean): Int? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value
      is Double -> value.toInt()
      is Long -> {
        // the following conditions are needed because they're not the default behavior of `Long#toInt()`
        when {
          value <= Int.MIN_VALUE -> Int.MIN_VALUE
          value >= Int.MAX_VALUE -> Int.MAX_VALUE
          else -> value.toInt()
        }
      }
      is Float -> value.toInt()
      is String -> if (value.contains('.')) value.toDoubleOrNull()?.toInt() else value.toIntOrNull()
      else -> null
    } ?: run {
      addTypeError("a number")
      emptyInt
    }
  }

  private fun asLong(nullable: Boolean): Long? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value.toLong()
      is Double -> value.toLong()
      is Long -> value
      is Float -> value.toLong()
      is String -> if (value.contains('.')) value.toDoubleOrNull()?.toLong() else value.toLongOrNull()
      else -> null
    } ?: run {
      addTypeError("a number")
      emptyLong
    }
  }

  private fun asDouble(nullable: Boolean): Double? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value.toDouble()
      is Double -> value
      is Long -> value.toDouble()
      is Float -> value.toDouble()
      is String -> value.toDoubleOrNull()
      else -> null
    } ?: run {
      addTypeError("a number")
      emptyDouble
    }
  }

  private fun asFloat(nullable: Boolean): Float? {
    if (nullable && value == null) return null
    return when (value) {
      is Int -> value.toFloat()
      is Double -> value.toFloat()
      is Long -> value.toFloat()
      is Float -> value
      is String -> value.toFloatOrNull()
      else -> null
    } ?: run {
      addTypeError("a number")
      emptyFloat
    }
  }

  private fun asBoolean(nullable: Boolean): Boolean? {
    if (nullable && value == null) return null
    if (value is Boolean) return value
    addTypeError("a boolean")
    return emptyBoolean
  }

  private fun asList(nullable: Boolean): List<AnyServerDrivenData>? {
    if (nullable && value == null) return null
    if (value is List<*>) {
      return value.mapIndexed { index, item -> AnyServerDrivenData(item, buildPath(index), errors) }
    }
    addTypeError("a list")
    return emptyList
  }

  private fun asMap(nullable: Boolean): Map<String, AnyServerDrivenData>? {
    if (nullable && value == null) return null
    if (value is Map<*, *>) {
      @Suppress("UNCHECKED_CAST")
      return value.mapValues {
        AnyServerDrivenData(it.value, buildPath("${it.key}"), errors)
      } as Map<String, AnyServerDrivenData>
    }
    addTypeError("a map")
    return emptyMap
  }

  private fun asEvent(nullable: Boolean): ServerDrivenEvent? {
    if (nullable && value == null) return null
    if (value is ServerDrivenEvent) return value
    addTypeError("an event")
    return emptyEvent
  }

  // Non-nullable type converters: aliases to asType(false)!!

  /**
   * Unwraps the value of this AnyServerDrivenData. Creates an error, if the value is null.
   */
  fun asAny(): Any = asAny(false)!!
  /**
   * Returns the wrapped value as a String. The value is converted to a string using `"$value"`, i.e. any type can be
   * retrieved as a string.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asString(): String = asString(false)!!
  /**
   * Returns the wrapped value as one of the enum values passed as parameter. The comparison is case insensitive, so
   * `Animal.Dog` and "doG" refers to the same value.
   *
   * If the wrapped value isn't a string or if the string doesn't correspond to any of the enum values passes as,
   * parameter, an error is created.
   *
   * Example of call: `data.asEnum(Animal.values())`.
   *
   * This function can't return null. If the value is null, an error is created.
   *
   * @param enum the array of enum values that can be accepted.
   */
  fun <T: Enum<*>>asEnum(enum: Array<T>): T = asEnum(enum, false)!!
  /**
   * Returns the wrapped value as an Int. The following types can be read as Int in addition to Int: Long, Float,
   * Double and String. Strings can be read as Ints as long as they represent a number. Examples of valid numeric
   * strings are: "1", "5.52". If the string is not numeric, an error is created.
   *
   * Special cases:
   * - If the value is a floating point, the decimal part is removed, truncating the number.
   * - If the value is a string with a numeric value that doesn't fit an Int, an error is created.
   * - If the value is greater than Int.MAX_VALUE, Int.MAX_VALUE is returned.
   * - If the value is lower than Int.MIN_VALUE, Int.MIN_VALUE is returned.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asInt(): Int = asInt(false)!!
  /**
   * Returns the wrapped value as a Long. The following types can be read as Long in addition to Long: Int, Float,
   * Double and String. Strings can be read as Longs as long as they represent a number. Examples of valid numeric
   * strings are: "1", "5.52". If the string is not numeric, an error is created.
   *
   * Special cases:
   * - If the value is a floating point, the decimal part is removed, truncating the number.
   * - If the value is a string with a numeric value that doesn't fit a Long, an error is created.
   * - If the value is greater than Long.MAX_VALUE, Long.MAX_VALUE is returned.
   * - If the value is lower than Long.MIN_VALUE, Long.MIN_VALUE is returned.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asLong(): Long = asLong(false)!!
  /**
   * Returns the wrapped value as a Float. The following types can be read as Float in addition to Float: Int, Long,
   * Double and String. Strings can be read as Floats as long as they represent a number. Examples of valid numeric
   * strings are: "1", "5.52", "3.67E12". If the string is not numeric, an error is created.
   *
   * Special cases:
   * - If the value is a string with a numeric value that doesn't fit a Float, the decimal part is truncated.
   * - If the value is greater than Float.MAX_VALUE, Float.POSITIVE_INFINITY is returned.
   * - If the value is lower than Float.MIN_VALUE, 0F is returned.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asFloat(): Float = asFloat(false)!!
  /**
   * Returns the wrapped value as a Double. The following types can be read as Double in addition to Double: Int, Long,
   * Float and String. Strings can be read as Doubles as long as they represent a number. Examples of valid numeric
   * strings are: "1", "5.52", "3.67E12". If the string is not numeric, an error is created.
   *
   * Special cases:
   * - If the value is a string with a numeric value that doesn't fit a Double, the decimal part is truncated.
   * - If the value is greater than Double.MAX_VALUE, Double.POSITIVE_INFINITY is returned.
   * - If the value is lower than Double.MIN_VALUE, 0.0 is returned.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asDouble(): Double = asDouble(false)!!
  /**
   * Returns the wrapped value as a Boolean. If the value is not a Boolean, an error is created.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asBoolean(): Boolean = asBoolean(false)!!
  /**
   * Returns the wrapped value as a List where all items are wrapped in an `AnyServerDrivenData`. If the value is not
   * a List, an error is created.
   *
   * All `AnyServerDrivenData` created by this method will have the same error list as this. It will also be provided
   * a path according to its position in the list: "[0]" for the first element, "[1]" for the second, and so on.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asList(): List<AnyServerDrivenData> = asList(false)!!
  /**
   * Returns the wrapped value as a Map where all keys are strings and all values are wrapped in an
   * `AnyServerDrivenData`. If the value is not a Map, an error is created.
   *
   * All `AnyServerDrivenData` created by this method will have the same error list as this. It will also be provided
   * a path according to its key in the map. Example: if the key is "propertyA" and the path for this
   * AnyServerDrivenData is "", the path of the child will be "propertyA". If the path for this AnyServerDrivenData
   * is "root", the path of the child will be "root.propertyA".
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asMap(): Map<String, AnyServerDrivenData> = asMap(false)!!
  /**
   * Returns the wrapped value as a ServerDrivenEvent. If the value is not a ServerDrivenEvent, an error is created.
   *
   * This function can't return null. If the value is null, an error is created.
   */
  fun asEvent(): ServerDrivenEvent = asEvent(false)!!

  // Nullable type converters: aliases to asType(true)

  /**
   * Same as `asAny`, but if the value is null, null is returned and no error is created.
   */
  fun asAnyOrNull(): Any? = asAny(true)
  /**
   * Same as `asString`, but if the value is null, null is returned and no error is created.
   */
  fun asStringOrNull(): String? = asString(true)
  /**
   * Same as `asEnum`, but if the value is null, null is returned and no error is created.
   * @param enum the array of enum values that can be accepted.
   */
  fun <T: Enum<*>>asEnumOrNull(enum: Array<T>): T? = asEnum(enum, true)
  /**
   * Same as `asInt`, but if the value is null, null is returned and no error is created.
   */
  fun asIntOrNull(): Int? = asInt(true)
  /**
   * Same as `asLong`, but if the value is null, null is returned and no error is created.
   */
  fun asLongOrNull(): Long? = asLong(true)
  /**
   * Same as `asFloat`, but if the value is null, null is returned and no error is created.
   */
  fun asFloatOrNull(): Float? = asFloat(true)
  /**
   * Same as `asDouble`, but if the value is null, null is returned and no error is created.
   */
  fun asDoubleOrNull(): Double? = asDouble(true)
  /**
   * Same as `asBoolean`, but if the value is null, null is returned and no error is created.
   */
  fun asBooleanOrNull(): Boolean? = asBoolean(true)
  /**
   * Same as `asList`, but if the value is null, null is returned and no error is created.
   */
  fun asListOrNull(): List<AnyServerDrivenData>? = asList(true)
  /**
   * Same as `asMap`, but if the value is null, null is returned and no error is created.
   */
  fun asMapOrNull(): Map<String, AnyServerDrivenData>? = asMap(true)
  /**
   * Same as `asEvent`, but if the value is null, null is returned and no error is created.
   */
  fun asEventOrNull(): ServerDrivenEvent? = asEvent(true)

  // Type checkers

  /**
   * Checks if the value wrapped is a Map.
   */
  fun isMap(): Boolean = value is Map<*, *>
  /**
   * Checks if the value wrapped is a List.
   */
  fun isList(): Boolean = value is List<*>
  /**
   * Checks if the value wrapped is a String.
   */
  fun isString(): Boolean = value is String
  /**
   * Checks if the value wrapped is an Int.
   */
  fun isInt(): Boolean = value is Int
  /**
   * Checks if the value wrapped is a Long.
   */
  fun isLong(): Boolean = value is Long
  /**
   * Checks if the value wrapped is a Double.
   */
  fun isDouble(): Boolean = value is Double
  /**
   * Checks if the value wrapped is a Float.
   */
  fun isFloat(): Boolean = value is Float
  /**
   * Checks if the value wrapped is a Boolean.
   */
  fun isBoolean(): Boolean = value is Boolean
  /**
   * Checks if the value wrapped is null.
   */
  fun isNull(): Boolean = value == null
  /**
   * Checks if the value wrapped is a ServerDrivenEvent.
   */
  fun isEvent(): Boolean = value is ServerDrivenEvent

  // Map and List utilities
  /**
   * Checks if:
   * 1. The value is a Map.
   * 2. It contains the key passed as parameter.
   * 3. The value at the key `key` is not null (`value[key] != null`).
   *
   * @param key the key to look for in the map.
   */
  fun hasValueForKey(key: String): Boolean = value is Map<*, *> && value[key] != null
  /**
   * Verifies if the value is a map and if it contains at least one of the keys passed as parameter.
   *
   * @param keys the keys to look for in the map.
   */
  fun hasAnyOfKeys(keys: List<String>): Boolean = value is Map<*, *> && keys.find { value.keys.contains(it) } != null

  /**
   * Checks if:
   * 1. The value is a List.
   * 2. Its size is greater than `index`.
   * 3. The value at the position `index` is not null.
   *
   * @param index the index to check in the list.
   */
  fun hasValueForIndex(index: Int): Boolean = value is List<*> && value.getOrNull(index) != null
  /**
   * Returns the size of the list if the value is a list or zero otherwise.
   */
  fun listSize(): Int = if (value is List<*>) value.size else 0
  /**
   * Returns the size of the map if the value is a map or zero otherwise.
   */
  fun mapSize(): Int = if (value is Map<*, *>) value.size else 0

  // Error handling

  /**
   * Returns true if an error has been created for:
   * - this AnyServerDrivenData;
   * - the AnyServerDrivenData that spawned this one and its ancestors;
   * - any AnyServerDrivenData created by this one through the methods `get`, `at`, `asMap` and `asList`.
   */
  fun hasError(): Boolean = errors.isNotEmpty()
  /**
   * Collects all errors from the AnyServerDrivenData's hierarchy and present them as a string.
   *
   * @param prefix a string to prefix every error. Default is "\n\t".
   */
  fun errorsAsString(prefix: String = "\n\t"): String = "$prefix${errors.joinToString(prefix)}"

  // Accessors

  /**
   * Spawns a new `AnyServerDrivenData`.
   *
   * If the value of this `AnyServerDrivenData` is a map, the value of the new `AnyServerDrivenData` will be
   * `value[key]`. Otherwise, it will be null.
   *
   * The path of the new `AnyServerDrivenData` will be the path of this `AnyServerDrivenData` + "." + the key passed as
   * parameter. Unless the path of this `AnyServerDrivenData` is an empty string, in this case, the "." will not be
   * added.
   *
   * @param key the key to retrieve in the map.
   */
  fun get(key: String): AnyServerDrivenData {
    val actualValue = if (value is Map<*, *>) value[key] else null
    return AnyServerDrivenData(actualValue, buildPath(key), errors)
  }

  /**
   * Spawns a new `AnyServerDrivenData`.
   *
   * If the value of this `AnyServerDrivenData` is a list, the value of the new `AnyServerDrivenData` will be
   * `value.getOrNull(index)`. Otherwise, it will be null.
   *
   * The path of the new `AnyServerDrivenData` will be the path of this `AnyServerDrivenData` + `"["` + the index
   * passed as parameter + `"]"`.
   *
   * @param index the position to retrieve in the list.
   */
  fun at(index: Int): AnyServerDrivenData {
    val actualValue = if (value is List<*>) value.getOrNull(index) else null
    return AnyServerDrivenData(actualValue, buildPath(index), errors)
  }

  // other

  override fun equals(other: Any?): Boolean = other is AnyServerDrivenData && value == other.value

  override fun hashCode(): Int {
    return value?.hashCode() ?: 0
  }

  override fun toString() = "$value"

  /**
   * Returns the JSON representation of this data. If some part of it is not serializable, a SerializationError will be
   * thrown.
   *
   * @return the json representing this data structure
   * @throws SerializationError when something in the structure is not of the types String, Short, Int, Long, Float,
   * Double, Boolean, Map or List.
   */
  @Throws(SerializationError::class)
  fun toJson(): String = when {
    isNull() -> "null"
    isString() -> "\"${this.asString()}\""
    value is Short || isInt() || this.isLong() || this.isFloat() || this.isDouble() || this.isBoolean() ->
      this.asString()
    isList() -> "[${this.asList().joinToString(",") { it.toJson() }}]"
    isMap() -> "{${this.asMap().map { "\"${it.key}\":${it.value.toJson()}" }.joinToString(",") }}"
    else -> throw SerializationError(path, value)
  }
}
