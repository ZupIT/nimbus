package com.zup.nimbus.core.tree

open class MalformedJson(override val message: String): Error("$message Please check your json string.")

class MalformedComponentError(
  id: String?,
  jsonPath: String,
  cause: String? = null,
): MalformedJson("") {
  private val idText = if (id == null) "" else """ id "$id" and"""
  private val causeText = if (cause == null) "" else "\nCause: $cause"
  override val message = "Error while trying to deserialize component with$idText JSONPath \"$jsonPath\".$causeText"
}

class MalformedActionListError(cause: String): MalformedJson("The list of actions is mal-formed.\nCause: $cause")
