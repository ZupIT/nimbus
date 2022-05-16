package com.zup.nimbus.core

import com.zup.nimbus.core.tree.ServerDrivenNode

object NodeUtils {
  fun pressButton(pageContent: ServerDrivenNode?, buttonIndex: Int) {
    val button = pageContent?.children?.get(buttonIndex)
    val onPress = button?.properties?.get("onPress")
    if (onPress is Function<*>) (onPress as (implicitState: Any?) -> Unit)(null)
  }
}
