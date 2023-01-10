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

package br.com.zup.nimbus.core.ui

import br.com.zup.nimbus.core.ui.action.condition
import br.com.zup.nimbus.core.ui.action.dismiss
import br.com.zup.nimbus.core.ui.action.log
import br.com.zup.nimbus.core.ui.action.onPushOrPresentInitialized
import br.com.zup.nimbus.core.ui.action.pop
import br.com.zup.nimbus.core.ui.action.popTo
import br.com.zup.nimbus.core.ui.action.present
import br.com.zup.nimbus.core.ui.action.push
import br.com.zup.nimbus.core.ui.action.sendRequest
import br.com.zup.nimbus.core.ui.action.setState
import br.com.zup.nimbus.core.ui.action.triggerViewEvent
import br.com.zup.nimbus.core.ui.operations.registerArrayOperations
import br.com.zup.nimbus.core.ui.operations.registerLogicOperations
import br.com.zup.nimbus.core.ui.operations.registerNumberOperations
import br.com.zup.nimbus.core.ui.operations.registerObjectOperations
import br.com.zup.nimbus.core.ui.operations.registerOtherOperations
import br.com.zup.nimbus.core.ui.operations.registerStringOperations

/**
 * The action handlers, action initializers, action observers and operations of the core Nimbus library.
 */
val coreUILibrary = UILibrary("")
  // Actions
  .addAction("push") { push(it) }
  .addAction("pop") { pop(it) }
  .addAction("popTo") { popTo(it) }
  .addAction("present") { present(it) }
  .addAction("dismiss") { dismiss(it) }
  .addAction("triggerViewEvent") { triggerViewEvent(it) }
  .addAction("log") { log(it) }
  .addAction("sendRequest") { sendRequest(it) }
  .addAction("setState") { setState(it) }
  .addAction("condition") { condition(it) }
  // Action initializers
  .addActionInitializer("push") { onPushOrPresentInitialized(it) }
  .addActionInitializer("present") { onPushOrPresentInitialized(it) }
  // operations
  .run {
    registerArrayOperations(this)
    registerLogicOperations(this)
    registerNumberOperations(this)
    registerOtherOperations(this)
    registerStringOperations(this)
    registerObjectOperations(this)
    this
  }
