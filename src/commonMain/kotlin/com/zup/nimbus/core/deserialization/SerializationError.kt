package com.zup.nimbus.core.deserialization

class SerializationError(propertyName: String, value: Any?): Error() {
  override val message = "Can't serialize \"$propertyName\". Only the types String, Short, Int, Long, Float, Double, " +
    "Boolean, Map and List are serializable.\nThe value of \"$propertyName\" is ${valueString(value)}."

  private fun valueString(value: Any?) = value?.let {
    "of type ${value::class.qualifiedName ?: value::class.simpleName ?: value::class}, its string representation " +
      "is $value"
  } ?: "null"
}
