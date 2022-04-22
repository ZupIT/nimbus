package com.zup.nimbus.core.render

open class RenderingError(override val message: String): Error("Aborting rendering process. $message")

class AnchorNotFoundError(anchor: String): RenderingError("""The current tree has no node identified by "$anchor".""")

class EmptyViewError(): RenderingError("The current tree was null while trying to update one of its branches. This " +
  "is a bug within the Server Driven Lib. Please, report it to the developer team.")
