package com.zup.nimbus.core.action

import com.zup.nimbus.core.ActionHandler

val coreActions = mapOf<String, ActionHandler>(
  "pushView" to { pushView(it) },
  "popView" to { popView(it) },
)
