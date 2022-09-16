package com.zup.nimbus.core.scope

interface LazilyScoped<T> {
  fun initialize(scope: Scope)
  fun clone(): T
}

class DoubleInitializationError: IllegalStateException(
  "Can't initialize this LazilyScoped instance because it has already been initialized!"
)

class CloneAfterInitializationError: IllegalStateException(
  "Can't clone this LazilyScoped instance because it has already been initialized!!"
)
