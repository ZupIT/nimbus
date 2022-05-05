package com.zup.nimbus.core

import com.zup.nimbus.core.network.ViewRequest

interface ServerDrivenNavigator {
  fun push(request: ViewRequest)
  fun pop()
  fun popTo(url: String)
  fun present(request: ViewRequest)
  fun dismiss()
}
