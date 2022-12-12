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

class MatchGroups(
  val values: List<String>
) {
  val destructured: Destructured get() = Destructured(this)

  @Suppress("MagicNumber")
  inner class Destructured(val group: MatchGroups) {
    operator fun component1():  String = group.values[1]
    operator fun component2():  String = group.values[2]
    operator fun component3():  String = group.values[3]
    operator fun component4():  String = group.values[4]
    operator fun component5():  String = group.values[5]
    operator fun component6():  String = group.values[6]
    operator fun component7():  String = group.values[7]
    operator fun component8():  String = group.values[8]
    operator fun component9():  String = group.values[9]
    operator fun component10(): String = group.values[10]
  }
}
