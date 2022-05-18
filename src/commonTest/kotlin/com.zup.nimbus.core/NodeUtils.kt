package com.zup.nimbus.core

import com.zup.nimbus.core.tree.ServerDrivenNode

object NodeUtils {
  fun triggerEvent(node: ServerDrivenNode?, event: String, implicitStateValue: Any? = null) {
    val action = node?.properties?.get(event)
    if (action is Function<*>) (action as (implicitState: Any?) -> Unit)(implicitStateValue)
  }

  fun pressButton(pageContent: ServerDrivenNode?, buttonIndex: Int) {
    val button = pageContent?.children?.get(buttonIndex)
    triggerEvent(button, "onPress")
  }
}
