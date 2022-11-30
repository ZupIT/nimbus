package br.com.zup.nimbus.core.ui.operations

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.ui.UILibrary

internal fun registerArrayOperations(library: UILibrary) {
  library
    .addOperation("insert") {
      val arguments = AnyServerDrivenData(it)
      val list = if (arguments.at(0).isList()) (arguments.at(0).value as List<*>).toMutableList()
      else ArrayList()
      val item = arguments.at(1).asAnyOrNull()
      val index = arguments.at(2).asIntOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      if (index == null) list.add(item) else list.add(index, item)
      list
    }
    .addOperation("remove") {
      val arguments = AnyServerDrivenData(it)
      val list = if (arguments.at(0).isList()) (arguments.at(0).value as List<*>).toMutableList()
      else ArrayList()
      val item = arguments.at(1).asAnyOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      list.remove(item)
      list
    }
    .addOperation("removeIndex") {
      val arguments = AnyServerDrivenData(it)
      val list = if (arguments.at(0).isList()) (arguments.at(0).value as List<*>).toMutableList()
      else ArrayList()
      val index = arguments.at(1).asIntOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      if (index == null) list.removeLast() else list.removeAt(index)
      list
    }
}
