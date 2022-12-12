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

import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.tree.dynamic.node.RootNode
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.findNodeById

object NodeUtils {
  fun triggerEvent(node: ServerDrivenNode?, eventName: String, implicitStateValue: Any? = null) {
    if (node == null) throw IllegalArgumentException("The node is null, can't trigger event")
    val event = node.properties?.get(eventName)
    if (event is ServerDrivenEvent) event.run(implicitStateValue)
    else throw IllegalArgumentException("The event name \"$eventName\" does not correspond to an existing event.")
  }

  fun pressButton(screen: ServerDrivenNode?, buttonId: String) {
    if (screen == null) return
    val button = screen.findNodeById(buttonId) ?: throw Error("Could not find button with id $buttonId")
    triggerEvent(button, "onPress")
  }

  fun getContent(tree: RootNode): ServerDrivenNode = tree.children?.first()!!
}
