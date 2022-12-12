/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.utils

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
