package com.zup.nimbus.core.dependency

private fun traverseAndUpdateLevels(
  dependency: CommonDependency,
  levels: MutableMap<Dependent, Int>,
  dependencyMap: MutableMap<Dependent, MutableSet<CommonDependency>>,
  currentLevel: Int,
) {
  dependency.dependents.forEach {
    val level = levels[it]
    if (level == null || level < currentLevel) {
      levels[it] = currentLevel
      dependencyMap[it] = dependencyMap[it] ?: mutableSetOf()
      dependencyMap[it]?.add(dependency)
    }
    if (it is CommonDependency) traverseAndUpdateLevels(it, levels, dependencyMap, currentLevel + 1)
  }
}

private fun createGroupsAndDependencyMap(
  dependencies: Set<CommonDependency>,
): Pair<List<List<Dependent>>, Map<Dependent, Set<CommonDependency>>> {
  val levels = mutableMapOf<Dependent, Int>()
  val dependencyMap = mutableMapOf<Dependent, MutableSet<CommonDependency>>()
  dependencies.forEach { traverseAndUpdateLevels(it, levels, dependencyMap, 0) }
  val groups = mutableListOf<MutableList<Dependent>>()
  levels.forEach {
    val dependent = it.key
    val level = it.value
    if (groups.getOrNull(level) == null) groups.add(level, mutableListOf())
    groups[level].add(dependent)
  }
  return Pair(groups, dependencyMap)
}

private fun updateDependents(
  groups: List<List<Dependent>>,
  dependencyMap: Map<Dependent, Set<CommonDependency>>,
): Set<CommonDependency> {
  val updated = mutableSetOf<CommonDependency>()
  groups.forEach { group ->
    group.forEach { dependent ->
      if (dependencyMap[dependent]?.find { it.hasChanged } != null) {
        dependent.update()
        dependencyMap[dependent]?.let { updated.addAll(it) }
      }
    }
  }
  return updated
}

fun updateDependentsOf(dependencies: Set<CommonDependency>) {
  val (groups, dependencyMap) = createGroupsAndDependencyMap(dependencies)
  val updated = updateDependents(groups, dependencyMap)
  updated.forEach { it.hasChanged = false }
}

fun updateDependentsOf(dependency: CommonDependency) = updateDependentsOf(setOf(dependency))
