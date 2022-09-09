package com.zup.nimbus.core.expression

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.dependencyGraph.Dependency
import com.zup.nimbus.core.dependencyGraph.Dependent
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.utils.valueOfPath

/**
 * The compiled version of an expression string, i.e., the Abstract Syntax Tree (AST).
 */
interface Expression {
  fun getValue(): Any?
}

class Literal(private val value: Any?): Expression {
  override fun getValue(): Any? {
    return value
  }
}

class StateReference(
  private val state: ServerDrivenState,
  private val path: String,
): Expression, Dependency(), Dependent {
  private var value: Any? = null

  init {
    update()
    hasChanged = false
    state.addDependent(this)
  }

  override fun getValue(): Any? {
    return value
  }

  override fun update() {
    val newValue: Any? = valueOfPath(state.value, path)
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }
}

class Operation(
  private val handler: OperationHandler,
  private val arguments: List<Expression>,
): Expression, Dependency(), Dependent {
  private var value: Any? = null

  init {
    update()
    hasChanged = false
    arguments.forEach {
      if (it is Dependency) it.addDependent(this)
    }
  }

  override fun update() {
    val argValues = arguments.map { it.getValue() }
    val newValue = handler(argValues)
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }

  override fun getValue(): Any? {
    return value
  }
}

class StringTemplate(private val composition: List<Expression>): Expression, Dependency(), Dependent {
  private var value: String = ""

  init {
    update()
    hasChanged = false
    composition.forEach {
      if (it is Dependency) it.addDependent(this)
    }
  }

  override fun getValue(): String {
    return value
  }

  override fun update() {
    val newValue = composition.joinToString("") { "${it.getValue() ?: ""}" }
    if (value != newValue) {
      value = newValue
      hasChanged = true
    }
  }
}
