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
 * Removes a prefix from the string.
 *
 * @param str the string to have the `prefix` removed from
 * @param prefix the prefix to remove from the string
 * @returns the string without the prefix
 */
fun removePrefix(str: String, prefix: String): String {
  return if (str.startsWith(prefix)) str.drop(prefix.length) else str
}

/**
 * Adds a single character to start of the string if the string doesn't yet start with this
 * character.
 *
 * @param str the string to add the prefix character to
 * @param prefix the single character to add as prefix
 * @returns the resulting string
 */
fun addPrefix(str: String, prefix: String): String {
  return ((str.isEmpty() || str[0].toString() != prefix) then "${prefix}${str}") ?: str
}

/**
 * Removes a suffix from the string.
 *
 * @param str the string to have the `suffix` removed from
 * @param suffix the suffix to remove from the string
 * @returns the string without the suffix
 */
fun removeSuffix(str: String, suffix: String): String {
  return if (str.endsWith(suffix)) str.dropLast(suffix.length) else str
}
