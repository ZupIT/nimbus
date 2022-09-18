package com.zup.nimbus.core.expression.parser

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.expression.Expression
import com.zup.nimbus.core.expression.Literal
import com.zup.nimbus.core.expression.StateReference
import com.zup.nimbus.core.regex.matches
import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.scope.getPathToScope
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.node.ServerDrivenNode

private val stateReferenceRegex = """^[\w\d_]+(\[\d+\])*(\.([\w\d_]+(\[\d+\])*))*$""".toFastRegex()
private val pathRegex = """^([^\.\[\]]+)\.?(.*)""".toFastRegex()
private val keyWords = setOf("true", "false", "null")

class StateReferenceParser(private val nimbus: Nimbus) {
  private fun pathError(path: String): Literal {
    nimbus.logger.error("invalid path \"$path\". Please, make sure your variable names contain only letters, " +
      "numbers and the symbol \"_\". To access substructures use \".\" and to access array indexes use " +
      "\"[index]\". Using null in the place of this expression.")
    return Literal(null)
  }

  private fun stateIdError(id: String): Literal {
    nimbus.logger.error(
      "The referred state is invalid because it uses a key word as its id: $id. Using null in its place."
    )
    return Literal(null)
  }

  private val stateNotFoundError: (String, Scope) -> Unit = { stateId, scope ->
    val location = "At: ${scope.getPathToScope()}"
    nimbus.logger.error("Couldn't find state with id \"$stateId\". Using null in its place.\n$location")
  }

  fun parse(path: String): Expression {
    if (!path.matches(stateReferenceRegex)) return pathError(path)
    val pathMatch = pathRegex.findWithGroups(path) ?: return pathError(path)
    val (stateId, statePath) = pathMatch.destructured
    if (keyWords.contains(stateId)) return stateIdError(stateId)
    return StateReference(stateId, statePath, stateNotFoundError)
  }
}
