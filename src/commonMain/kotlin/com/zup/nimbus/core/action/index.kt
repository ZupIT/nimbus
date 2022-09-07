package com.zup.nimbus.core.action

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializedHandler

internal fun getCoreActions(): Map<String, ActionHandler> {
  return mapOf(
    "push" to { push(it) },
    "pop" to { pop(it) },
    "popTo" to { popTo(it) },
    "present" to { present(it) },
    "dismiss" to { dismiss(it) },
    "log" to { log(it) },
    "sendRequest" to { sendRequest(it) },
    "setState" to { setState(it) },
    "condition" to { condition(it) },
  )
}


internal fun getInitHandlersForCoreActions(): Map<String, ActionInitializedHandler> {
  return mapOf(
    "push" to { onPushOrPresentRendered(it) },
    "present" to { onPushOrPresentRendered(it) },
  )
}
