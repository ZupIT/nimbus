package com.zup.nimbus.core

import com.zup.nimbus.core.render.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlinx.coroutines.CompletableDeferred

class ViewObserver(view: ServerDrivenView) {
  var history = ArrayList<ServerDrivenNode>()
  private var changeCallback: (() -> Unit)? = null

  init {
    view.onChange {
      history.add(it)
      changeCallback?.let { it() }
    }
  }

  suspend fun waitForChanges(totalNumberOfChanges: Int = 1): ArrayList<ServerDrivenNode> {
    if (history.size >= totalNumberOfChanges) return history
    val deferred = CompletableDeferred<ArrayList<ServerDrivenNode>>()
    changeCallback = {
      if (history.size >= totalNumberOfChanges) deferred.complete(history)
    }
    return deferred.await()
  }

  fun clear() {
    history = ArrayList()
    changeCallback = null
  }
}

fun ServerDrivenView.observe(): ViewObserver {
  return ViewObserver(this)
}
