package com.zup.nimbus.core.ui.operations

import com.zup.nimbus.core.ui.UILibrary

internal fun registerArrayOperations(library: UILibrary) {
  library
    .addOperation("insert") {
      val list = if (it[0] is List<*>) (it[0] as List<*>).toMutableList() else ArrayList()
      val item = it[1]
      val index = it.getOrNull(2) as Int?
      if (index == null) list.add(item) else list.add(index, item)
      list
    }
    .addOperation("remove") {
      val list = if (it[0] is List<*>) (it[0] as List<*>).toMutableList() else ArrayList()
      val item = it[1]
      list.remove(item)
      list
    }
    .addOperation("removeIndex") {
      val list = if (it[0] is List<*>) (it[0] as List<*>).toMutableList() else ArrayList()
      val index = it.getOrNull(1) as Int?
      if (index == null) list.removeLast() else list.removeAt(index)
      list
    }
}
