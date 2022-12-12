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

package br.com.zup.nimbus.core.regex

actual class FastRegex actual constructor(actual val pattern: String) {
  private val regex = pattern.toRegex()

  actual fun find(input: String): String? {
    return regex.find(input)?.value
  }

  actual fun findWithGroups(input: String): MatchGroups? {
    val values = regex.find(input)?.groupValues ?: return null
    return MatchGroups(values)
  }

  actual fun findAll(input: String): List<String> {
    val matched = regex.findAll(input)
    return matched.map { it.value }.toList()
  }

  actual fun findAllWithGroups(input: String): List<MatchGroups> {
    val matched = regex.findAll(input)
    return matched.map { MatchGroups(it.groupValues) }.toList()
  }

  actual fun matches(input: String): Boolean {
    return regex.matches(input)
  }

  actual fun containsMatchIn(input: String): Boolean {
    return regex.containsMatchIn(input)
  }

  actual fun replace(input: String, replacement: String): String {
    return regex.replace(input, replacement)
  }

  actual fun replace(input: String, transform: (MatchGroups) -> String): String {
    return regex.replace(input) {
      transform(MatchGroups(it.groupValues))
    }
  }

  actual fun <T>transform(
    input: String,
    transformUnmatching: (String) -> T,
    transformMatching: (MatchGroups) -> T,
  ): List<T> {
    val matches = regex.findAll(input)
    val parts = mutableListOf<T>()
    var next = 0
    matches.forEach {
      parts.add(transformUnmatching(input.substring(next, it.range.first)))
      parts.add(transformMatching(MatchGroups(it.groupValues)))
      next = it.range.last + 1
    }
    parts.add(transformUnmatching(input.substring(next)))
    return parts
  }
}
