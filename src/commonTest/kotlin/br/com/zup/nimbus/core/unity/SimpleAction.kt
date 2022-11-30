package br.com.zup.nimbus.core.unity

import br.com.zup.nimbus.core.ActionHandler
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.tree.ServerDrivenAction

class SimpleAction(
  override val name: String,
  override val handler: ActionHandler,
  override val properties: Map<String, Any?>? = null,
  override val metadata: Map<String, Any?>? = null,
) : ServerDrivenAction {
  override fun update() = throw NotImplementedError()
}
