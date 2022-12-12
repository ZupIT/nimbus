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

import br.com.zup.nimbus.core.log.LogLevel
import br.com.zup.nimbus.core.log.Logger

data class LogEntry(
  val message: String,
  val level: LogLevel,
)

class ObservableLogger: Logger {
  override fun enable() {}
  override fun disable() {}
  var entries = ArrayList<LogEntry>()

  fun clear() {
    entries = ArrayList()
  }

  override fun log(message: String, level: LogLevel) {
    entries.add(LogEntry(message, level))
  }

  override fun info(message: String) {
    log(message, LogLevel.Info)
  }

  override fun warn(message: String) {
    log(message, LogLevel.Warning)
  }

  override fun error(message: String) {
    log(message, LogLevel.Error)
  }
}
