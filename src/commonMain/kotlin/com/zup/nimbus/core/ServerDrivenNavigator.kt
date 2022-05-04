package com.zup.nimbus.core

import com.zup.nimbus.core.network.ViewRequest

interface ServerDrivenNavigator {
  fun push(request: ViewRequest)
  fun pop()
}
