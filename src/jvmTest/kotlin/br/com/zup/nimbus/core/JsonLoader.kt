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

package br.com.zup.nimbus.core

import java.io.File

// Attention: there are no errors here. This is an IntelliJ bug. I don't know which string to pass to @Suppress, so
// IntelliJ keeps complaining.

actual object JsonLoader {
  actual fun loadJson(fileName: String): String {
    val path = "./src/commonTest/resources/$fileName.json"
    val file = File(path)
    if (!file.exists()) throw IllegalArgumentException("No such file: $path")
    return file.readText()
  }
}
