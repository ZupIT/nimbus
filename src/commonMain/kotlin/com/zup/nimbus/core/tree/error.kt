package com.zup.nimbus.core.tree

open class MalformedJson(override var message: String): Error("$message Please check your JSON file.")

class MalformedComponentError: MalformedJson("Malformed Component.")

class MalformedActionListError: MalformedJson("The list of actions is mal-formed.")
