package com.zup.nimbus.core.ui

import com.zup.nimbus.core.ui.action.condition
import com.zup.nimbus.core.ui.action.dismiss
import com.zup.nimbus.core.ui.action.log
import com.zup.nimbus.core.ui.action.onPushOrPresentInitialized
import com.zup.nimbus.core.ui.action.pop
import com.zup.nimbus.core.ui.action.popTo
import com.zup.nimbus.core.ui.action.present
import com.zup.nimbus.core.ui.action.push
import com.zup.nimbus.core.ui.action.sendRequest
import com.zup.nimbus.core.ui.action.setState
import com.zup.nimbus.core.ui.operations.registerArrayOperations
import com.zup.nimbus.core.ui.operations.registerLogicOperations
import com.zup.nimbus.core.ui.operations.registerNumberOperations
import com.zup.nimbus.core.ui.operations.registerOtherOperations
import com.zup.nimbus.core.ui.operations.registerStringOperations

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
