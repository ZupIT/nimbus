package br.com.zup.nimbus.core.ui

import br.com.zup.nimbus.core.ui.action.condition
import br.com.zup.nimbus.core.ui.action.dismiss
import br.com.zup.nimbus.core.ui.action.log
import br.com.zup.nimbus.core.ui.action.onPushOrPresentInitialized
import br.com.zup.nimbus.core.ui.action.pop
import br.com.zup.nimbus.core.ui.action.popTo
import br.com.zup.nimbus.core.ui.action.present
import br.com.zup.nimbus.core.ui.action.push
import br.com.zup.nimbus.core.ui.action.sendRequest
import br.com.zup.nimbus.core.ui.action.setState
import br.com.zup.nimbus.core.ui.operations.registerArrayOperations
import br.com.zup.nimbus.core.ui.operations.registerLogicOperations
import br.com.zup.nimbus.core.ui.operations.registerNumberOperations
import br.com.zup.nimbus.core.ui.operations.registerOtherOperations
import br.com.zup.nimbus.core.ui.operations.registerStringOperations

/**
 * The action handlers, action initializers, action observers and operations of the core Nimbus library.
 */
val coreUILibrary = UILibrary("")
  // Actions
  .addAction("push") { push(it) }
  .addAction("pop") { pop(it) }
  .addAction("popTo") { popTo(it) }
  .addAction("present") { present(it) }
  .addAction("dismiss") { dismiss(it) }
  .addAction("log") { log(it) }
  .addAction("sendRequest") { sendRequest(it) }
  .addAction("setState") { setState(it) }
  .addAction("condition") { condition(it) }
  // Action initializers
  .addActionInitializer("push") { onPushOrPresentInitialized(it) }
  .addActionInitializer("present") { onPushOrPresentInitialized(it) }
  // operations
  .run {
    registerArrayOperations(this)
    registerLogicOperations(this)
    registerNumberOperations(this)
    registerOtherOperations(this)
    registerStringOperations(this)
    this
  }
