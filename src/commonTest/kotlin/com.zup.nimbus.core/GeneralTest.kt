package com.zup.nimbus.core

import com.zup.nimbus.core.action.ServerDrivenNavigator
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlin.test.Test
import kotlin.test.assertEquals

class MyNavigator: ServerDrivenNavigator {

}

class GeneralTest {
    private val config = ServerDrivenConfig(baseUrl = "", platform = "")
    private val tree = """{"id":"1","component":"layout:container","children":[{"id":"2","component":"material:text","properties":{"text":"Nimbus App @{counter}"}},{"id":"3","component":"material:text","properties":{"text":"Hi There"}},{"id":"4","component":"custom:personCard","properties":{"person":{"name":"Fulano da Silva","age":28,"company":"ZUP","document":"014.778.547-56"},"address":{"street":"Rua dos bobos","number":0,"zip":"47478-745"}}},{"id":"5","component":"material:button","properties":{"text":"Increment counter","onPress":"[[ACTION:INC_COUNTER]]"}}]}"""

    @Test
    fun testView() {
        val nimbus = Nimbus(config)
        val parsedTree = nimbus.createNodeFromJson(tree)
        val view = nimbus.createView(MyNavigator())
        val calledWithTree = ArrayList<ServerDrivenNode>()
        view.onChange { calledWithTree.add(it) }

        view.renderer.paint(parsedTree)
        assertEquals(1, calledWithTree.size)
        assertEquals("layout:container", calledWithTree[0].component)
        assertEquals(4, calledWithTree[0].children?.size)
        assertEquals("material:text", calledWithTree[0].children?.get(0)?.component)
        assertEquals("Nimbus App 1", calledWithTree[0].children?.get(0)?.properties?.get("text"))
        assertEquals("material:text", calledWithTree[0].children?.get(1)?.component)
        assertEquals("Hi There", calledWithTree[0].children?.get(1)?.properties?.get("text"))
        assertEquals("custom:personCard", calledWithTree[0].children?.get(2)?.component)
        assertEquals(true, calledWithTree[0].children?.get(2)?.properties?.get("person") is Map<*, *>)
        val person = calledWithTree[0].children?.get(2)?.properties?.get("person") as Map<*, *>
        assertEquals("Fulano da Silva", person["name"])
        assertEquals(28, person["age"])
        assertEquals("ZUP", person["company"])
        assertEquals("014.778.547-56", person["document"])
        assertEquals(true, calledWithTree[0].children?.get(2)?.properties?.get("address") is Map<*, *>)
        val address = calledWithTree[0].children?.get(2)?.properties?.get("address") as Map<*, *>
        assertEquals("Rua dos bobos", address["street"])
        assertEquals(0, address["number"])
        assertEquals("47478-745", address["zip"])
        assertEquals("material:button", calledWithTree[0].children?.get(3)?.component)
        assertEquals("Increment counter", calledWithTree[0].children?.get(3)?.properties?.get("text"))
        assertEquals(true, calledWithTree[0].children?.get(3)?.properties?.get("onPress") is Function<*>)

        (calledWithTree[0].children?.get(3)?.properties?.get("onPress") as () -> Unit)()
        assertEquals(2, calledWithTree.size)
        assertEquals("layout:container", calledWithTree[1].component)
        assertEquals(4, calledWithTree[1].children?.size)
        assertEquals("material:text", calledWithTree[1].children?.get(0)?.component)
        assertEquals("Nimbus App 2", calledWithTree[1].children?.get(0)?.properties?.get("text"))
        assertEquals("material:text", calledWithTree[1].children?.get(1)?.component)
        assertEquals("Hi There", calledWithTree[1].children?.get(1)?.properties?.get("text"))
        assertEquals("custom:personCard", calledWithTree[1].children?.get(2)?.component)
        assertEquals(true, calledWithTree[1].children?.get(2)?.properties?.get("person") is Map<*, *>)
        val person2 = calledWithTree[1].children?.get(2)?.properties?.get("person") as Map<*, *>
        assertEquals("Fulano da Silva", person2["name"])
        assertEquals(28, person2["age"])
        assertEquals("ZUP", person2["company"])
        assertEquals("014.778.547-56", person2["document"])
        assertEquals(true, calledWithTree[1].children?.get(2)?.properties?.get("address") is Map<*, *>)
        val address2 = calledWithTree[1].children?.get(2)?.properties?.get("address") as Map<*, *>
        assertEquals("Rua dos bobos", address2["street"])
        assertEquals(0, address2["number"])
        assertEquals("47478-745", address2["zip"])
        assertEquals("material:button", calledWithTree[0].children?.get(3)?.component)
        assertEquals("Increment counter", calledWithTree[0].children?.get(3)?.properties?.get("text"))
        assertEquals(true, calledWithTree[0].children?.get(3)?.properties?.get("onPress") is Function<*>)
    }
}