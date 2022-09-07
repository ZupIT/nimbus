package com.zup.nimbus.core.ast

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
    state.addDependent(this)
  }

  override fun getValue(): Any? {
    return value
  }

  override fun update() {
    value = valueOfPath(state.value, path)
  }
}

class Operation(
  private val handler: OperationHandler,
  private val arguments: List<Expression>,
): Expression, Dependency(), Dependent {
  private var value: Any? = null

  init {
    update()
    arguments.forEach {
      if (it is Dependency) it.addDependent(this)
    }
  }

  override fun update() {
    val argValues = arguments.map { getValue() }
    value = handler(argValues)
  }

  override fun getValue(): Any? {
    return value
  }
}

class StringTemplate(private val composition: List<Expression>): Expression, Dependency(), Dependent {
  private var value: String = ""

  init {
    update()
    composition.forEach {
      if (it is Dependency) it.addDependent(this)
    }
  }

  override fun getValue(): String {
    return value
  }

  override fun update() {
    value = composition.joinToString { "${it.getValue()}" }
  }
}
