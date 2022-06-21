package com.zup.nimbus.core.action

import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.render.UnexpectedRootStructuralComponent
import com.zup.nimbus.core.tree.MalformedComponentError
import com.zup.nimbus.core.tree.RenderNode
import com.zup.nimbus.core.tree.TreeUpdateMode
import com.zup.nimbus.core.utils.UnexpectedDataTypeError
import com.zup.nimbus.core.utils.valueOf
import com.zup.nimbus.core.utils.valueOfEnum

fun setContent(event: ActionEvent) {
  val logger = event.view.nimbusInstance.logger
  val properties = event.action.properties
  try {
    val id: String = valueOf(properties, "id")
    val value: Map<String, Any?> = valueOf(properties, "value")
    val mode: TreeUpdateMode = valueOfEnum(properties, "mode", TreeUpdateMode.Append)
    val valueAsTree = RenderNode.fromMap(value, event.view.nimbusInstance.idManager)
    event.view.renderer.paint(valueAsTree, id, mode)
  } catch (e: UnexpectedDataTypeError) {
    logger.error("Error while executing the action \"setContent\".\n${e.message}")
  } catch (e: MalformedComponentError) {
    logger.error("Could not set content because the provided value is not a valid Server Driven Node.\n${e.message}")
  } catch (e: UnexpectedRootStructuralComponent) {
    logger.error("Could not set content because the provided element is a structural component. Please wrap it " +
      "under another component.\n${e.message}")
  }
}
