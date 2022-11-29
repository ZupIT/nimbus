package br.com.zup.nimbus.core.ui.action.error

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.scope.getPathToScope

class ActionExecutionError(event: ActionEvent, override val cause: Throwable): Error() {
  override val message: String = "Error while running the action \"${event.action.name}\".\nAt: " +
    "${event.scope.getPathToScope()}\nCaused by: ${cause.message ?: cause::class.qualifiedName}"
}
