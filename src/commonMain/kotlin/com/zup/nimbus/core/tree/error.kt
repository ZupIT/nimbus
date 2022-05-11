package com.zup.nimbus.core.tree

open class MalformedJsonError(override val message: String): Error("$message Please check your json string.")

class MalformedComponentError(
  id: String?,
  jsonPath: String,
  cause: String? = null,
): MalformedJsonError("") {
  private val idText = if (id == null) "" else """ id "$id" and"""
  private val causeText = if (cause == null) "" else "\nCause: $cause"
  override val message = "Error while trying to deserialize component with$idText JSONPath \"$jsonPath\".$causeText"
}

class MalformedActionListError(cause: String): MalformedJsonError("The list of actions is mal-formed.\nCause: $cause")
