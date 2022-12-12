/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.expression.parser

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.expression.Expression
import br.com.zup.nimbus.core.expression.Literal
import br.com.zup.nimbus.core.expression.StateReference
import br.com.zup.nimbus.core.regex.matches
import br.com.zup.nimbus.core.regex.toFastRegex
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.getPathToScope

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
