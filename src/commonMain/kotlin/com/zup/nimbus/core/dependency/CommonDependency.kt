package com.zup.nimbus.core.dependency


abstract class CommonDependency: Dependency {
  override val dependents = mutableSetOf<Dependent>()

  override var hasChanged = false

  override fun addDependent(dependent: Dependent) {
    dependents.add(dependent)
  }

  override fun removeDependent(dependent: Dependent) {
    dependents.remove(dependent)
  }
}
