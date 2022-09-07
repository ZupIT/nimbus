package com.zup.nimbus.core.tree.stateful

import com.zup.nimbus.core.ServerDrivenState

class RootNode(
  id: String,
  states: List<ServerDrivenState>?,
) : UINode(id, "fragment", states, null)
