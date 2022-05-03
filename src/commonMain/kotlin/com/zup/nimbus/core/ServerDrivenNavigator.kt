package com.zup.nimbus.core

interface ServerDrivenNavigator {
  fun push(url: String)
  fun pop()
}
