package com.zup.nimbus.core.tree

open class MalformedJson(override var message: String): Error("$message Please check your JSON file.")

class MalformedComponentError(
  message: String? = null,
): MalformedJson("Malformed Component.${if (message == null) "" else "\n$message"}")

class MalformedActionListError: MalformedJson("The list of actions is mal-formed.")
