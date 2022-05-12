package com.zup.nimbus.core

import com.zup.nimbus.core.network.ViewRequest

class EmptyNavigator: ServerDrivenNavigator {
  override fun push(request: ViewRequest) {}
  override fun pop() {}
  override fun popTo(url: String) {}
  override fun present(request: ViewRequest) {}
  override fun dismiss() {}
}
