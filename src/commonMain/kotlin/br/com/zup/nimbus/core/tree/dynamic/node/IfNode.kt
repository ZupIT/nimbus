package br.com.zup.nimbus.core.tree.dynamic.node

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenState
import br.com.zup.nimbus.core.scope.closestScopeWithType
import br.com.zup.nimbus.core.utils.UnexpectedDataTypeError
import br.com.zup.nimbus.core.utils.valueOfKey

// fixme(1): normally, in UI frameworks, if-else blocks completely remove the other branch from the tree and rebuilds it
//  when the condition changes. This is not being done here. On the positive side, states will never be lost when
//  switching from true to false. On the other hand, we won't free up the memory for the if-else branch not currently
//  rendered until the associated RootNode is unmounted. If we decide this to be a feature and not a bug, remove this
//  commentary.
//
// fixme(2): we shouldn't try to run the components of Else if the condition is true; or the components of Then when
//  the condition is false. Example: we may use an operation that expects something not to be null and this can only be
//  guaranteed by the if's condition. By initializing both Then and Else no matter the condition value, we'll end up
//  processing way more than we need and risk running operations that expect values different than the ones we have.
//  PS: fixing the first issue automatically fixes this.
/**
 * IfNode is a polymorphic DynamicNode that chooses only one of its original subtree (json) to render at a time. The
 * chosen subtree depends on the value of the property "condition". When "condition" is true and "Then" is a child of
 * "If", the children of "Then" is rendered. When "condition" is false and "Else" is a child of "If", the children of
 * "Else" is rendered.
 *
 * Reminder: a polymorphic node is a special type of dynamic node that is always skipped by the NodeContainer when
 * calculating the children of a node. Only the non-polymorphic children of a polymorphic node ends up in the UI tree.
 * To know more about polymorphic nodes, read the documentation for "polymorphic" in "DynamicNode".
 *
 * A, IfNode node in its json form is represented by the following type definition (Typescript):
 * interface If {
 *   condition: boolean,
 *   children: Then | Else | [Then, Else],
 * }
 * interface Then {
 *   children: Component[],
 * }
 * interface Else {
 *   children: Component[],
 * }
 */
class IfNode(
  id: String,
  states: List<ServerDrivenState>?,
) : DynamicNode(id, "if", states, true) {
  override fun update() {
    val condition: Boolean = try {
      valueOfKey(propertyContainer?.read(), "condition")
    } catch (e: UnexpectedDataTypeError) {
        closestScopeWithType<Nimbus>()?.logger?.error(e.message)
        return
    }
    val fromContainer = childrenContainer?.read()
    val thenNode = fromContainer?.find { it.component == "then" }
    val elseNode = fromContainer?.find { it.component == "else" }
    val previousChildrenStructure = children?.map { it.id }
    children = if (condition) thenNode?.children else elseNode?.children
    hasChanged = previousChildrenStructure != children?.map { it.id }
  }

  override fun clone(idSuffix: String): DynamicNode = clone(idSuffix) { id, states -> IfNode(id, states) }
}
