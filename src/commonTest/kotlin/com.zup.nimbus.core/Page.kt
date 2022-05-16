package com.zup.nimbus.core

import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenNode

class Page(val id: String, view: ServerDrivenView) {
  var content: ServerDrivenNode? = null

  init {
    view.onChange { content = it }
  }
}
