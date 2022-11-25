package com.zup.nimbus.core.dependency

/**
 * Manages a dependency graph by updating the nodes that need to be updated.
 */
object DependencyUpdateManager {
  private fun traverseAndUpdateLevels(
    dependency: Dependency,
    levels: MutableMap<Dependent, Int>,
    dependencyMap: MutableMap<Dependent, MutableSet<Dependency>>,
    currentLevel: Int,
  ) {
    dependency.dependents.forEach {
      val level = levels[it]
      if (level == null || level < currentLevel) {
        levels[it] = currentLevel
        dependencyMap[it] = dependencyMap[it] ?: mutableSetOf()
        dependencyMap[it]?.add(dependency)
      }
      if (it is Dependency) traverseAndUpdateLevels(it, levels, dependencyMap, currentLevel + 1)
    }
  }

  private fun createGroupsAndDependencyMap(
    dependencies: Set<Dependency>,
  ): Pair<List<List<Dependent>>, Map<Dependent, Set<Dependency>>> {
    val levels = mutableMapOf<Dependent, Int>()
    val dependencyMap = mutableMapOf<Dependent, MutableSet<Dependency>>()
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

  @Suppress("NestedBlockDepth")
  private fun updateDependents(
    groups: List<List<Dependent>>,
    dependencyMap: Map<Dependent, Set<Dependency>>,
  ): Set<Dependency> {
    val errors = mutableListOf<Throwable>()
    val updated = mutableSetOf<Dependency>()
    groups.forEach { group ->
      group.forEach { dependent ->
        if (dependencyMap[dependent]?.find { it.hasChanged } != null) {
          try { dependent.update() }
          catch(t: Throwable) { errors.add(t) }
          dependencyMap[dependent]?.let { updated.addAll(it) }
        }
      }
    }
    if (errors.isNotEmpty()) throw UpdateError(errors)
    return updated
  }

  /**
   * Propagates an update in the dependency graph.
   *
   * This algorithm takes a list of dependencies that should have its dependents updated and update them. If a dependent
   * changes and it is a also a dependency, the update is propagated recursively.
   */
  fun updateDependentsOf(dependencies: Set<Dependency>) {
    val (groups, dependencyMap) = createGroupsAndDependencyMap(dependencies)
    val updated = updateDependents(groups, dependencyMap)
    updated.forEach { it.hasChanged = false }
  }

  /**
   * Alias for updateDependentsOf(listOf(dependency))
   */
  fun updateDependentsOf(dependency: Dependency) = updateDependentsOf(setOf(dependency))
}
