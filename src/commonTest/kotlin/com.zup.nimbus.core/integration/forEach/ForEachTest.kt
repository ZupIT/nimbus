package com.zup.nimbus.core.integration.forEach

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun getComponentsInTree(tree: ServerDrivenNode): List<String> {
  val result = ArrayList<String>()
  result.add(tree.component)
  tree.children?.forEach { result.addAll(getComponentsInTree(it)) }
  return result
}

class ForEachTest {
  @Test
  fun shouldCorrectlyProcessGeneralForEachScreen() {
    // WHEN the GENERAL_FOR_EACH screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(GENERAL_FOR_EACH)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      // THEN it should have replaced every forEach
      assertFalse(getComponentsInTree(it).contains("forEach"))
      // THEN it should have 6 children on the root: 3 original components + 1 column for the premium users + 1
      // component per basic user (2). The forEach with items = null should be just removed.
      assertEquals(6, it.children?.size)
      // THEN the column of premium users should have 3 components per user = 9 components
      val premiumColumn = it.children?.get(1)
      assertEquals(9, premiumColumn?.children?.size)
      // THEN each component in the column of premium users should have the correct id
      assertEquals("nimbus:5:0", premiumColumn?.children?.get(0)?.id)
      assertEquals("nimbus:6:0", premiumColumn?.children?.get(1)?.id)
      assertEquals("nimbus:7:0", premiumColumn?.children?.get(2)?.id)
      assertEquals("nimbus:5:1", premiumColumn?.children?.get(3)?.id)
      assertEquals("nimbus:6:1", premiumColumn?.children?.get(4)?.id)
      assertEquals("nimbus:7:1", premiumColumn?.children?.get(5)?.id)
      assertEquals("nimbus:5:2", premiumColumn?.children?.get(6)?.id)
      assertEquals("nimbus:6:2", premiumColumn?.children?.get(7)?.id)
      assertEquals("nimbus:7:2", premiumColumn?.children?.get(8)?.id)
      // THEN each component in the column of premium users should have the correct text content
      assertEquals(0, premiumColumn?.children?.get(0)?.properties?.get("text")) // 0: index
      assertEquals("John", premiumColumn?.children?.get(1)?.properties?.get("text")) // 0: name
      assertEquals(30, premiumColumn?.children?.get(2)?.properties?.get("text")) // 0: age
      assertEquals(1, premiumColumn?.children?.get(3)?.properties?.get("text")) // 1: index
      assertEquals("Mary", premiumColumn?.children?.get(4)?.properties?.get("text")) // 1: name
      assertEquals(22, premiumColumn?.children?.get(5)?.properties?.get("text")) // 1: age
      assertEquals(2, premiumColumn?.children?.get(6)?.properties?.get("text")) // 2: index
      assertEquals("Anthony", premiumColumn?.children?.get(7)?.properties?.get("text")) // 2: name
      assertEquals(5, premiumColumn?.children?.get(8)?.properties?.get("text")) // 2: age
      // THEN the 2 components before the last (second forEach) should have the correct ids
      assertEquals("nimbus:10:0", it.children?.get(3)?.id)
      assertEquals("nimbus:10:1", it.children?.get(4)?.id)
      // THEN the 2 components before the last (second forEach) should represent the two basic users
      assertEquals("0. Rose 21", it.children?.get(3)?.properties?.get("text")) // 0: index. name age
      assertEquals("1. Paul 54", it.children?.get(4)?.properties?.get("text")) // 1: index. name age
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun shouldCorrectlyProcessForEachWithStatesScreen() {
    // WHEN the FOR_EACH_WITH_STATES screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(FOR_EACH_WITH_STATES)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      // THEN it should have replaced the forEach
      assertFalse(getComponentsInTree(it).contains("forEach"))
      // THEN the column (root) should have three rows
      val rows = it.children
      assertEquals(3, rows?.size)
      // THEN the first row should be correctly resolved
      var rowContent = rows?.get(0)?.children
      assertEquals("John", rowContent?.get(0)?.properties?.get("text"))
      assertEquals("Increment listCounter: 0", rowContent?.get(1)?.properties?.get("text"))
      assertEquals("Increment item counter: 0", rowContent?.get(2)?.properties?.get("text"))
      // THEN the second row should be correctly resolved
      rowContent = rows?.get(1)?.children
      assertEquals("Mary", rowContent?.get(0)?.properties?.get("text"))
      assertEquals("Increment listCounter: 0", rowContent?.get(1)?.properties?.get("text"))
      assertEquals("Increment item counter: 0", rowContent?.get(2)?.properties?.get("text"))
      // THEN the third row should be correctly resolved
      rowContent = rows?.get(2)?.children
      assertEquals("Anthony", rowContent?.get(0)?.properties?.get("text"))
      assertEquals("Increment listCounter: 0", rowContent?.get(1)?.properties?.get("text"))
      assertEquals("Increment item counter: 0", rowContent?.get(2)?.properties?.get("text"))
      // todo: implement this as soon as we have both the setState and operations working
      // WHEN the button to increment the item counter of the first row is pressed
      // THEN this button should change its text to "Increment item counter: 1", but the other buttons should not change
      // WHEN the button to increment the item counter of the second row is pressed
      // THEN this button should change its text to "Increment item counter: 1", but the other buttons should not change
      // WHEN the button to increment the item counter of the third row is pressed
      // THEN this button should change its text to "Increment item counter: 1", but the other buttons should not change
      // todo: implement this as soon as we have a solution for not replacing the nodes in the previous result of the
      //  forEach. If this test is implemented right now, it will reset the item counters to zero.
      // WHEN the first button to increase the listCounter is pressed
      // THEN every button to increase the listCounter should read "Increment listCounter: 1"
      // THEN the item counter buttons should remain unchanged ("Increment item counter: 1")
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun shouldCorrectlyProcessForEachWithKeyScreen() {
    // WHEN the FOR_EACH_WITH_KEY screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(FOR_EACH_WITH_KEY)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      // THEN it should have replaced the forEach
      assertFalse(getComponentsInTree(it).contains("forEach"))
      // THEN it should render three text components
      assertEquals(3, it.children?.size)
      // THEN the first text component should have the correct id
      assertEquals("person:John", it.children?.get(0)?.id)
      // THEN the second text component should have the correct id
      assertEquals("person:Mary", it.children?.get(1)?.id)
      // THEN the third text component should have the correct id
      assertEquals("person:Anthony", it.children?.get(2)?.id)
      // THEN the first text component should have the correct content
      assertEquals("John: 30", it.children?.get(0)?.properties?.get("text"))
      // THEN the second text component should have the correct content
      assertEquals("Mary: 22", it.children?.get(1)?.properties?.get("text"))
      // THEN the third text component should have the correct content
      assertEquals("Anthony: 5", it.children?.get(2)?.properties?.get("text"))
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun shouldCorrectlyProcessNestedEmptyForEachScreen() {
    // WHEN the NESTED_EMPTY_FOR_EACH screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(NESTED_EMPTY_FOR_EACH)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      // THEN the root should have no children
      assertTrue(it.children?.isEmpty() != false)
      hasRendered = true
    }
    assertTrue(hasRendered)
  }

  @Test
  fun shouldCorrectlyProcessNestedForEachScreen() {
    // WHEN the NESTED_FOR_EACH screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test"))
    val node = nimbus.createNodeFromJson(NESTED_FOR_EACH)
    val page = nimbus.createView({ EmptyNavigator() })
    var hasRendered = false
    page.renderer.paint(node)
    page.onChange {
      // THEN it should have replaced the forEach
      assertFalse(getComponentsInTree(it).contains("forEach"))
      // THEN it should have 3 column components as children of the root (one for each plan)
      assertEquals(3, it.children?.size)

      // ASSERTIONS FOR THE PREMIUM PLAN (FIRST COLUMN)
      // THEN the first column (premium) should have 7 text components: 1 for the header and 2 for each client (3)
      var column = it.children?.get(0)
      assertEquals(7, column?.children?.size)
      // THEN the header of the premium column (first) should have the correct id
      var header = column?.children?.get(0)
      assertEquals("header:0", header?.id)
      // THEN the header of the premium column (first) should have the correct content
      assertEquals("Documents of clients for the premium plan (59.9):", header?.properties?.get("text"))
      // THEN the components corresponding to the clients of the premium plan should have the correct id
      var documents = listOf(column?.children?.get(1), column?.children?.get(2), column?.children?.get(3),
        column?.children?.get(4), column?.children?.get(5), column?.children?.get(6))
      assertEquals("document:0:0:0", documents[0]?.id)
      assertEquals("document:0:0:1", documents[1]?.id)
      assertEquals("document:0:1:0", documents[2]?.id)
      assertEquals("document:0:1:1", documents[3]?.id)
      assertEquals("document:0:2:0", documents[4]?.id)
      assertEquals("document:0:2:1", documents[5]?.id)
      // THEN the components corresponding to the clients of the premium plan should have the correct content
      assertEquals("045.445.875-96 (belonging to John)", documents[0]?.properties?.get("text"))
      assertEquals("MG14785987 (belonging to John)", documents[1]?.properties?.get("text"))
      assertEquals("854.112.745-98 (belonging to Mary)", documents[2]?.properties?.get("text"))
      assertEquals("SP51476321 (belonging to Mary)", documents[3]?.properties?.get("text"))
      assertEquals("856.334.857-85 (belonging to Anthony)", documents[4]?.properties?.get("text"))
      assertEquals("PR14786320 (belonging to Anthony)", documents[5]?.properties?.get("text"))
      hasRendered = true
    }
    assertTrue(hasRendered)
  }
}
