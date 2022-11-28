package com.zup.nimbus.core.deserialization

class SerializationError(val propertyName: String): Error() {
  override val message = "Can't serialize \"$propertyName\". Only the types String, Int, Long, Float, Double, " +
    "Boolean, Map and List are serializable."
}
