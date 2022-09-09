package com.zup.nimbus.core.expression

import com.zup.nimbus.core.regex.matches
import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.scope.NimbusScope
import com.zup.nimbus.core.tree.stateful.Stateful
import com.zup.nimbus.core.tree.stateful.find

private val stateReferenceRegex = """^[\w\d_]+(\[\d+\])*(\.([\w\d_]+(\[\d+\])*))*$""".toFastRegex()
private val pathRegex = """^([^\.\[\]]+)\.?(.*)""".toFastRegex()
private val keyWords = setOf("true", "false", "null")

class StateReferenceParser(private val scope: NimbusScope) {
  private fun pathError(path: String): Literal {
    scope.getLogger().error("invalid path \"$path\". Please, make sure your variable names contain only letters, " +
      "numbers and the symbol \"_\". To access substructures use \".\" and to access array indexes use " +
      "\"[index]\". Using null in the place of this expression.")
    return Literal(null)
  }

  private fun stateIdError(id: String): Literal {
    scope.getLogger().error("The referred state is invalid because it uses a key word as its id: $id. Using null in its place.")
    return Literal(null)
  }

  private fun stateNotFoundError(id: String): Literal {
    scope.getLogger().error("Couldn't find state with id \"$id\". Using null in its place.")
    return Literal(null)
  }

  fun parse(path: String, origin: Stateful): Expression {
    if (!path.matches(stateReferenceRegex)) return pathError(path)
    val pathMatch = pathRegex.findWithGroups(path) ?: return pathError(path)
    val (stateId, statePath) = pathMatch.destructured
    if (keyWords.contains(stateId)) return stateIdError(stateId)
    val state = origin.find(stateId) ?: return stateNotFoundError(stateId)
    return StateReference(state, statePath)
  }
}
