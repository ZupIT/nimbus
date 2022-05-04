package com.zup.nimbus.core.unit

/**
 * Equivalent to the map operation of a List, but instead of creating a List as a result, it creates a MutableList.
 *
 * Note: this method is important for performance reasons, please don't replace it for less code, but higher cost.
 *
 * @param list the list to map.
 * @param iteratee the function to map each item of "list".
 * @return a mutable list with every item of "list" mapped to the result of "iteratee(item)".
 */
fun <T, U>mapValuesToMutableList(list: List<T>, iteratee: (item: T) -> U): MutableList<U> {
  val result = ArrayList<U>()
  list.forEach { result.add(iteratee(it)) }
  return result
}
