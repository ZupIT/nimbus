
package com.zup.nimbus.core.render

import com.zup.nimbus.core.ServerDrivenNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.ServerDrivenNode

typealias Listener = (tree: ServerDrivenNode) -> Unit

class ServerDrivenView(
  /**
   * The instance of Nimbus that created this ServerDrivenView.
   */
  val nimbusInstance: Nimbus,
  /**
   * The navigator that created this view.
   */
  val parentNavigator: ServerDrivenNavigator,
) {
  /**
   * The currently rendered tree.
   */
  private var current: RenderNode? = null

  /**
   * The set of listeners looking for changes in the current tree. These listeners must be called only for changes that
   * requires re-rendering.
   */
  private val listeners: ArrayList<Listener> = ArrayList()

  /**
   * Stores the function to make this view stop listening to the Global State.
   */
  private var removeGlobalStateListener: (() -> Unit)? = null

  /**
   * The Renderer for this view. Use it to change the UI tree or a view state.
   */
  val renderer = Renderer(
    view = this,
    detachedStates = listOf(nimbusInstance.globalState),
    getCurrentTree = { current },
    replaceCurrentTree = { current = it },
    onFinish = { runListeners() },
  )

  init {
    removeGlobalStateListener = nimbusInstance.globalState.onChange {
      if (current != null) renderer.refresh()
    }
  }

  private fun runListeners() {
    listeners.forEach { it(current ?: throw EmptyViewError()) }
  }

  /**
   * Observes for changes in the current tree. Everytime a change that requires a re-render is made, the listener
   * passed as parameter will be called with the current tree.
   *
   * Note:
   * The tree received as parameter by the listener is a ServerDrivenNode, which is immutable. It wouldn't take much for
   * the developer to realize that the concrete type of this tree is a RenderNode, which is mutable. We advise every
   * developer not to cast this to RenderNode and modify it, since this could cause unpredictable behavior.
   *
   * @param listener the function to call every time the tree needs to be re-rendered. This receives the current tree as
   * parameter.
   */
  fun onChange(listener: Listener): () -> Unit {
    listeners.add(listener)
    return { listeners.remove(listener) }
  }

  /**
   * Destroys this view by properly removing every reference that could become invalid.
   */
  fun destroy() {
    removeGlobalStateListener?.let { it() }
  }
}
