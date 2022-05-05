package com.zup.nimbus.core.action

import com.zup.nimbus.core.ActionHandler

val coreActions = mapOf<String, ActionHandler>(
  "push" to { push(it) },
  "pop" to { pop(it) },
  "popTo" to { popTo(it) },
  "present" to { present(it) },
  "dismiss" to { dismiss(it) },
)
