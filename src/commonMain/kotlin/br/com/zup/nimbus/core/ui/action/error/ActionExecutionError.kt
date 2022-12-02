package br.com.zup.nimbus.core.ui.action.error

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.scope.getPathToScope

class ActionExecutionError private constructor (
  event: ActionEvent,
  override val cause: Throwable?,
  message: String,
): Error() {
  constructor(event: ActionEvent, cause: Throwable): this(
    event,
    cause,
    cause.message ?: cause::class.qualifiedName ?: "unknown",
  )

  constructor(event: ActionEvent, message: String): this(event, null, message)

  override val message: String = "Error while running the action \"${event.action.name}\".\nAt: " +
    "${event.scope.getPathToScope()}\nCaused by: $message"
}
