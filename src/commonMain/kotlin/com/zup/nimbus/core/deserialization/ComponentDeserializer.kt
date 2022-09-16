package com.zup.nimbus.core.deserialization

import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.tree.node.ServerDrivenNode

class ComponentDeserializer(val logger: Logger, val node: ServerDrivenNode) {
  private val deserializer = MapDeserializer()

  fun start() {
    deserializer.start(node.properties)
  }

  fun end(): Boolean {
    val errors = deserializer.end()
    if (errors.isEmpty()) return true
    val errorText = errors.joinToString("\n  ")
    logger.error("Error while deserializing component \"${node.component}\" with id \"${node.id}\":\n  $errorText")
    return false
  }

  fun enter(key: String, nullable: Boolean): Boolean {
    return deserializer.enter(key, nullable)
  }

  fun leave(): Boolean {
    return deserializer.leave()
  }

  fun addTypeError(expected: String, key: String, value: Any?, shouldUseValueInsteadOfType: Boolean = false) {
    deserializer.addTypeError(expected, key, value, shouldUseValueInsteadOfType)
  }

  fun asString(key: String): String {
    return deserializer.asString(key)
  }

  fun <T: Enum<*>>asEnum(key: String, enum: Array<T>): T {
    return deserializer.asEnum(key, enum)
  }

  fun asInt(key: String): Int {
    return deserializer.asInt(key)
  }

  fun asDouble(key: String): Double {
    return deserializer.asDouble(key)
  }

  fun asBoolean(key: String): Boolean {
    return deserializer.asBoolean(key)
  }

  fun asList(key: String): List<Any?> {
    return deserializer.asList(key)
  }

  fun asMap(key: String): Map<String, Any?> {
    return deserializer.asMap(key)
  }

  fun asAction(key: String): (Any?) -> Unit {
    return deserializer.asAction(key)
  }

  fun asStringOrNull(key: String): String? {
    return deserializer.asStringOrNull(key)
  }

  fun <T: Enum<*>>asEnumOrNull(key: String, enum: Array<T>): T? {
    return deserializer.asEnumOrNull(key, enum)
  }

  fun asIntOrNull(key: String): Int? {
    return deserializer.asIntOrNull(key)
  }

  fun asDoubleOrNull(key: String): Double? {
    return deserializer.asDoubleOrNull(key)
  }

  fun asBooleanOrNull(key: String): Boolean? {
    return deserializer.asBooleanOrNull(key)
  }

  fun asListOrNull(key: String): List<Any?>? {
    return deserializer.asListOrNull(key)
  }

  fun asMapOrNull(key: String): Map<String, Any?>? {
    return deserializer.asMapOrNull(key)
  }

  fun asActionOrNull(key: String): ((Any?) -> Unit)? {
    return deserializer.asActionOrNull(key)
  }
}
