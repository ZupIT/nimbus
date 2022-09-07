package com.zup.nimbus.core.dependencyGraph

private fun traverseAndUpdateLevels(dependency: Dependency, levels: HashMap<Dependent, Int>, currentLevel: Int) {
  if (!dependency.hasChanged) return
  dependency.dependents.forEach {
    val level = levels[it]
    if (level == null || level < currentLevel) {
      levels[it] = currentLevel
    }
    if (it is Dependency) traverseAndUpdateLevels(it, levels, currentLevel + 1)
  }
}

private fun markUnchanged(dependency: Dependency) {
  dependency.hasChanged = false
  dependency.dependents.forEach {
    if (it is Dependency) markUnchanged(it)
  }
}

fun updateDependentsOf(dependencies: Set<Dependency>) {
  val levels = HashMap<Dependent, Int>()
  dependencies.forEach { traverseAndUpdateLevels(it, levels, 0) }
  val groups = mutableListOf<MutableList<Dependent>>()
  levels.forEach {
    groups[it.value] = groups.getOrNull(it.value) ?: mutableListOf()
    groups[it.value].add(it.key)
  }
  groups.forEach { group ->
    group.forEach { it.update() }
  }
  // set hasChanged to false for each dependency that had its dependents updated
  dependencies.forEach { markUnchanged(it) }
}
