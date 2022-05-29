package com.zup.nimbus.core.render

open class RenderingError(override val message: String, illegal: Boolean = false)
  : Error("Aborting rendering process. $message" +
  if (illegal) " This is probably a bug within the Server Driven Lib. Please, report it to the developer team." else "")

class AnchorNotFoundError(anchor: String)
  : RenderingError("""The current tree has no node identified by "$anchor".""")

class EmptyViewError
  : RenderingError("The current tree was null while trying to update one of its branches.", true)

class InvalidTreeError: RenderingError("The current UI tree reached an illegal state.", true)

class InvalidStatePathError(path: String): RenderingError("""The path "$path" is not a valid state path.""")

class StateNotFoundError(stateId: String, componentId: String)
  : RenderingError("""Could not find state "$stateId" from the component with id "$componentId"""")

class NoStateHierarchyError: RenderingError("Expected node to have a state hierarchy.", true)

class UnexpectedRootStructuralComponent: RenderingError("A structural component can't be the root of a UI " +
  "tree. Please wrap it under a UI component node.")
