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
   * A function to get the navigator that spawned this view.
   *
   * Attention: this is a function so we can prevent a cyclical reference between Kotlin Native and Swift. Replacing
   * this with a direct reference will cause memory leaks.
   */
  val getNavigator: () -> ServerDrivenNavigator,
  /**
   * A description for this view. Suggestion: the URL used to load the content of this view or "json", if a local json
   * string was used to load it.
   */
  val description: String? = null,
) {
  /**
   * The currently rendered tree.
   */
  private var current: RenderNode? = null

  /**
   * The listener that observes changes in the current tree. This will be called only for changes that requires
   * re-rendering.
   */
  private var listener: Listener? = null

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
    listener?.let { it(current ?: throw EmptyViewError()) }
  }

  /**
   * Observes for changes in the current tree. Everytime a change that requires a re-render is made, the listener
   * passed as parameter will be called with the current tree.
   *
   * If there's already something rendered in this view, the listener is automatically called with it.
   *
   * The ServerDrivenView accepts only one observer. A second call to this method replaces the current listener. To
   * remove the listener call this method with null.
   *
   * Note:
   * The tree received as parameter by the listener is a ServerDrivenNode, which is immutable. It wouldn't take much for
   * the developer to realize that the concrete type of this tree is a RenderNode, which is mutable. We advise every
   * developer not to cast this to RenderNode and modify it, since this could cause unpredictable behavior.
   *
   * @param listener the function to call every time the tree needs to be re-rendered. This receives the current tree as
   * parameter.
   */
  fun onChange(listener: Listener?) {
    this.listener = listener
    if (current != null && listener != null) listener(current ?: return)
  }

  /**
   * Destroys this view by properly removing every reference that could become invalid.
   */
  fun destroy() {
    listener = null
    removeGlobalStateListener?.let { it() }
  }
}
