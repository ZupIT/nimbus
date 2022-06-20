package com.zup.nimbus.core.component

open class ComponentStructureError(message: String): IllegalArgumentException(message)

class UnexpectedComponentError(message: String): ComponentStructureError(message)

class MissingComponentError(message: String): ComponentStructureError(message)

