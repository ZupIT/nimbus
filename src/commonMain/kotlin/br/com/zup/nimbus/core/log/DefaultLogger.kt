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

package br.com.zup.nimbus.core.log

class DefaultLogger: Logger {
  private var isEnabled = true

  override fun enable() {
    isEnabled = true
  }

  override fun disable() {
    isEnabled = false
  }

  override fun log(message: String, level: LogLevel) {
    when (level) {
      LogLevel.Error -> error(message)
      LogLevel.Info -> info(message)
      LogLevel.Warning -> warn(message)
    }
  }

  override fun info(message: String) {
    print(LogLevel.Info, message)
  }

  override fun warn(message: String) {
    print(LogLevel.Warning, message)
  }

  override fun error(message: String) {
    print(LogLevel.Error, message)
  }

  private fun print(level: LogLevel, message: String) {
    if (isEnabled) {
      println("${level.name}: $message")
    }
  }
}
