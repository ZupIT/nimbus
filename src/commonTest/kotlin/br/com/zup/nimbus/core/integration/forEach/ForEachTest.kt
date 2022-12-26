/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.integration.forEach

import br.com.zup.nimbus.core.EmptyHttpClient
import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.OperationHandler
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.findNodeById
import br.com.zup.nimbus.core.ui.UILibrary
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
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(GENERAL_FOR_EACH)
    tree.initialize(nimbus)
    assertFalse(getComponentsInTree(tree).contains("forEach"))
    val column = NodeUtils.getContent(tree)
    // THEN it should have 6 children on the root: 3 original components + 1 column for the premium users + 1
    // component per basic user (2). The forEach with items = null should be just removed.
    assertEquals(6, column.children?.size)
    // THEN the column of premium users should have 3 components per user = 9 components
    val premiumColumn = column.children?.get(1)
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
    assertEquals("nimbus:10:0", column.children?.get(3)?.id)
    assertEquals("nimbus:10:1", column.children?.get(4)?.id)
    // THEN the 2 components before the last (second forEach) should represent the two basic users
    assertEquals("0. Rose 21", column.children?.get(3)?.properties?.get("text")) // 0: index. name age
    assertEquals("1. Paul 54", column.children?.get(4)?.properties?.get("text")) // 1: index. name age
  }

  @Test
  fun shouldCorrectlyProcessForEachWithStatesScreen() {
    // WHEN the FOR_EACH_WITH_STATES screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(STATEFUL_FOR_EACH)
    tree.initialize(nimbus)
    // THEN it should have replaced the forEach
    assertFalse(getComponentsInTree(tree).contains("forEach"))
    // THEN the column (root) should have three rows
    val rows = NodeUtils.getContent(tree).children
    assertEquals(3, rows?.size)
    // THEN the first row should be correctly resolved
    var rowContent = rows?.get(0)?.children
    assertEquals("John", rowContent?.get(0)?.properties?.get("text"))
    assertEquals("Increment list counter: 0", rowContent?.get(1)?.properties?.get("text"))
    assertEquals("Increment item counter: 0", rowContent?.get(2)?.properties?.get("text"))
    // THEN the second row should be correctly resolved
    rowContent = rows?.get(1)?.children
    assertEquals("Mary", rowContent?.get(0)?.properties?.get("text"))
    assertEquals("Increment list counter: 0", rowContent?.get(1)?.properties?.get("text"))
    assertEquals("Increment item counter: 0", rowContent?.get(2)?.properties?.get("text"))
    // THEN the third row should be correctly resolved
    rowContent = rows?.get(2)?.children
    assertEquals("Anthony", rowContent?.get(0)?.properties?.get("text"))
    assertEquals("Increment list counter: 0", rowContent?.get(1)?.properties?.get("text"))
    assertEquals("Increment item counter: 0", rowContent?.get(2)?.properties?.get("text"))
    // WHEN the button to increment the item counter of the first row is pressed
    val firstIncrementItemButton = tree.findNodeById("increment-item:0")
    val secondIncrementItemButton = tree.findNodeById("increment-item:1")
    val thirdIncrementItemButton = tree.findNodeById("increment-item:2")
    NodeUtils.triggerEvent(firstIncrementItemButton, "onPress")
    // THEN this button should change its text to "Increment item counter: 1", but the other buttons should not change
    assertEquals("Increment item counter: 1", firstIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 0", secondIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 0", thirdIncrementItemButton?.properties?.get("text"))
    // WHEN the button to increment the item counter of the second row is pressed
    NodeUtils.triggerEvent(secondIncrementItemButton, "onPress")
    // THEN this button should change its text to "Increment item counter: 1", but the other buttons should not change
    assertEquals("Increment item counter: 1", firstIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 1", secondIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 0", thirdIncrementItemButton?.properties?.get("text"))
    // WHEN the button to increment the item counter of the third row is pressed
    NodeUtils.triggerEvent(thirdIncrementItemButton, "onPress")
    // THEN this button should change its text to "Increment item counter: 1", but the other buttons should not change
    assertEquals("Increment item counter: 1", firstIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 1", secondIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 1", thirdIncrementItemButton?.properties?.get("text"))
    // WHEN the first button to increase the listCounter is pressed
    val firstIncrementListButton = tree.findNodeById("increment-list:0")
    val secondIncrementListButton = tree.findNodeById("increment-list:1")
    val thirdIncrementListButton = tree.findNodeById("increment-list:2")
    NodeUtils.triggerEvent(firstIncrementListButton, "onPress")
    // THEN every button to increase the listCounter should read "Increment list counter: 1"
    assertEquals("Increment list counter: 1", firstIncrementListButton?.properties?.get("text"))
    assertEquals("Increment list counter: 1", secondIncrementListButton?.properties?.get("text"))
    assertEquals("Increment list counter: 1", thirdIncrementListButton?.properties?.get("text"))
    // AND the item counter buttons should remain unchanged ("Increment item counter: 1")
    assertEquals("Increment item counter: 1", firstIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 1", secondIncrementItemButton?.properties?.get("text"))
    assertEquals("Increment item counter: 1", thirdIncrementItemButton?.properties?.get("text"))
  }

  @Test
  fun shouldCorrectlyProcessForEachWithKeyScreen() {
    // WHEN the FOR_EACH_WITH_KEY screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(FOR_EACH_WITH_KEY)
    tree.initialize(nimbus)
    // THEN it should have replaced the forEach
    assertFalse(getComponentsInTree(tree).contains("forEach"))
    // THEN it should render three text components
    val column = NodeUtils.getContent(tree)
    assertEquals(3, column.children?.size)
    // THEN the first text component should have the correct id
    assertEquals("person:John", column.children?.get(0)?.id)
    // THEN the second text component should have the correct id
    assertEquals("person:Mary", column.children?.get(1)?.id)
    // THEN the third text component should have the correct id
    assertEquals("person:Anthony", column.children?.get(2)?.id)
    // THEN the first text component should have the correct content
    assertEquals("John: 30", column.children?.get(0)?.properties?.get("text"))
    // THEN the second text component should have the correct content
    assertEquals("Mary: 22", column.children?.get(1)?.properties?.get("text"))
    // THEN the third text component should have the correct content
    assertEquals("Anthony: 5", column.children?.get(2)?.properties?.get("text"))
  }

  @Test
  fun shouldCorrectlyProcessNestedEmptyForEachScreen() {
    // WHEN the NESTED_EMPTY_FOR_EACH screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(NESTED_EMPTY_FOR_EACH)
    tree.initialize(nimbus)
    val column = NodeUtils.getContent(tree)
    // THEN the column should have no children
    assertTrue(column.children?.isEmpty() != false)
  }

  private fun assertThatPlanColumnIsCorrect(
    column: ServerDrivenNode?,
    expectedHeaderId: String,
    expectedHeaderContent: String,
    expectedTextIds: List<String>,
    expectedTextContent: List<String>,
  ) {
    // THEN the column (plan container) should have 1 child for the header and 2 children for each client
    assertEquals(1 + expectedTextIds.size, column?.children?.size)
    // THEN the header of the column should have the correct id
    val header = column?.children?.get(0)
    assertEquals(expectedHeaderId, header?.id)
    // THEN the header of the column should have the correct content
    assertEquals(expectedHeaderContent, header?.properties?.get("text"))
    // THEN the components corresponding to the clients of the this plan should have the correct id
    expectedTextIds.forEachIndexed { index, id ->
      assertEquals(id, column?.children?.get(index + 1)?.id)
    }
    // THEN the components corresponding to the clients of this plan should have the correct content
    expectedTextContent.forEachIndexed { index, content ->
      assertEquals(content, column?.children?.get(index + 1)?.properties?.get("text"))
    }
  }

  @Test
  fun shouldCorrectlyProcessNestedForEachScreen() {
    // WHEN the NESTED_FOR_EACH screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(NESTED_FOR_EACH)
    tree.initialize(nimbus)
    // THEN it should have replaced the forEach
    assertFalse(getComponentsInTree(tree).contains("forEach"))
    val column = NodeUtils.getContent(tree)
    // THEN it should have 3 column components as children of the root (one for each plan)
    assertEquals(3, column.children?.size)

    // THEN the column for the premium plan (first) should be correct
    assertThatPlanColumnIsCorrect(
      column = column.children?.get(0),
      expectedHeaderId = "header:0",
      expectedHeaderContent = "Documents of clients for the premium plan (59.9):",
      expectedTextIds = listOf("document:0:0:0", "document:0:0:1", "document:0:1:0", "document:0:1:1",
        "document:0:2:0", "document:0:2:1"),
      expectedTextContent = listOf("045.445.875-96 (belonging to John)", "MG14785987 (belonging to John)",
        "854.112.745-98 (belonging to Mary)", "SP51476321 (belonging to Mary)",
        "856.334.857-85 (belonging to Anthony)", "PR14786320 (belonging to Anthony)"),
    )

    // THEN the column for the super plan (second) should be correct
    assertThatPlanColumnIsCorrect(
      column = column.children?.get(1),
      expectedHeaderId = "header:1",
      expectedHeaderContent = "Documents of clients for the super plan (39.9):",
      expectedTextIds = listOf("document:1:0:0", "document:1:0:1"),
      expectedTextContent = listOf("555.412.744-88 (belonging to Helen)", "MG45127889 (belonging to Helen)"),
    )

    // THEN the column for the basic plan (third) should be correct
    assertThatPlanColumnIsCorrect(
      column = column.children?.get(2),
      expectedHeaderId = "header:2",
      expectedHeaderContent = "Documents of clients for the basic plan (19.9):",
      expectedTextIds = listOf("document:2:0:0", "document:2:0:1", "document:2:1:0", "document:2:1:1"),
      expectedTextContent = listOf("124.111.458-44 (belonging to Rose)", "RJ41775652 (belonging to Rose)",
        "122.225.974-87 (belonging to Paul)", "SC41257896 (belonging to Paul)"),
    )
  }

  @Test
  fun `should add and remove elements in the dataset`() {
    // WHEN the FOR_EACH_MUTABLE_DATASET screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(FOR_EACH_MUTABLE_DATASET)
    tree.initialize(nimbus)
    val column = NodeUtils.getContent(tree).children?.first()!!
    // THEN 3 texts + 2 buttons should be rendered
    assertEquals(5, column.children?.size)
    // WHEN the button to add one more item is pressed
    NodeUtils.pressButton(column, "add")
    // THEN 4 texts + 2 buttons should be rendered
    assertEquals(6, column.children?.size)
    // AND the last text must be "Paul"
    assertEquals(column.children?.get(3)?.properties?.get("text"), "Paul")
    // WHEN the button to remove the second item is pressed
    NodeUtils.pressButton(column, "remove")
    // THEN 3 texts + 2 buttons should be rendered
    assertEquals(5, column.children?.size)
    // AND the second text must be "Anthony"
    assertEquals(column.children?.get(1)?.properties?.get("text"), "Anthony")
  }

  @Test
  fun `should update property of item when source list changes`() {
    // WHEN the FOR_EACH_DYNAMIC_ITEM screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(FOR_EACH_DYNAMIC_ITEM)
    tree.initialize(nimbus)
    assertEquals("hello", tree.findNodeById("message:1")?.properties?.get("text"))
    NodeUtils.pressButton(tree, "update")
    assertEquals("bye", tree.findNodeById("message:1")?.properties?.get("text"))
  }

  @Test
  fun `should filter items in list`() {
    // Given a filtering operation
    val filterNotes: OperationHandler = { arguments ->
      val notes = arguments.first() as List<Map<String, Any>>
      val term = arguments[1] as String
      if (term.isBlank()) notes
      else notes.filter { (it["title"] as String).contains(term) || (it["description"] as String).contains(term) }
    }
    val ui = UILibrary("test").addOperation("filterNotes", filterNotes)
    // WHEN the FOR_EACH_FILTERING screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", ui = listOf(ui), httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(FOR_EACH_FILTERING)
    tree.initialize(nimbus)
    // THEN it should render 2 components plus an empty list
    assertEquals(2, tree.findNodeById("container")?.children?.size)
    // WHEN we press the button to fill up the list
    NodeUtils.pressButton(tree, "start")
    // THEN it should render 2 components plus every note in the list (13)
    assertEquals(15, tree.findNodeById("container")?.children?.size)
    // WHEN we filter the list by the term "cereal"
    NodeUtils.triggerEvent(tree.findNodeById("filter"), "onChange", "cereal")
    // THEN it should render 2 components plus 1 note in the list: "Buy cereal for the kids"
    assertEquals(3, tree.findNodeById("container")?.children?.size)
    assertEquals(
      "11: Buy cereal for the kids: 5 boxes",
      tree.findNodeById("container")?.children?.last()?.properties?.get("text"),
    )
    // WHEN we remove the filter
    NodeUtils.triggerEvent(tree.findNodeById("filter"), "onChange", "")
    // THEN it should show every note again (2 components + 13 notes = 15)
    assertEquals(15, tree.findNodeById("container")?.children?.size)
  }

  @Test
  fun `should filter items in Map of String to List`() {
    // Given a filtering operation
    val filterNotes: OperationHandler = { arguments ->
      val notesByDate = arguments.first() as Map<String, List<Map<String, Any>>>
      val term = arguments[1] as String
      if (term.isBlank()) notesByDate
      else {
        val result = mutableMapOf<String, List<Map<String, Any>>>()
        notesByDate.forEach { entry ->
          val filtered = entry.value.filter {
            (it["title"] as String).contains(term) || (it["description"] as String).contains(term)
          }
          if (filtered.isNotEmpty()) result[entry.key] = filtered
        }
        result
      }
    }
    val ui = UILibrary("test").addOperation("filterNotes", filterNotes)
    // WHEN the FOR_EACH_MAP_FILTERING screen is rendered
    val nimbus = Nimbus(ServerDrivenConfig("", "test", ui = listOf(ui), httpClient = EmptyHttpClient))
    val tree = nimbus.nodeBuilder.buildFromJsonString(FOR_EACH_MAP_FILTERING)
    tree.initialize(nimbus)
    // THEN it should render only 2 components: the button and the text input
    assertEquals(2, tree.findNodeById("container")?.children?.size)
    // WHEN we press the button to fill up the list
    NodeUtils.pressButton(tree, "start")
    // THEN it should render 2 components plus every section (3)
    assertEquals(5, tree.findNodeById("container")?.children?.size)
    // AND it should render the title and all notes for each section
    assertEquals(7, tree.findNodeById("section:1671408000000")?.children?.size)
    assertEquals(4, tree.findNodeById("section:1671148800000")?.children?.size)
    assertEquals(5, tree.findNodeById("section:1670976000000")?.children?.size)
    // WHEN we filter the list by the term "cereal", typing letter by letter in the keyboard
    fun type(str: String) {
      NodeUtils.triggerEvent(tree.findNodeById("filter"), "onChange", str)
    }
    type("c")
    type("ce")
    type("cer")
    type("cere")
    type("cerea")
    type("cereal")
    // THEN it should render 2 components plus 1 section
    assertEquals(3, tree.findNodeById("container")?.children?.size)
    // AND this section (1671408000000) should have a title plus a single note: "Buy cereal for the kids"
    assertEquals(2, tree.findNodeById("section:1671408000000")?.children?.size)
    assertEquals(
      "11: Buy cereal for the kids: 5 boxes",
      tree.findNodeById("section:1671408000000")?.children?.last()?.properties?.get("text"),
    )
    // WHEN we remove the filter, using backspace to remove letter by letter from the text input
    type("cerea")
    type("cere")
    type("cer")
    type("ce")
    type("c")
    type("")
    // THEN it should show every section again
    assertEquals(5, tree.findNodeById("container")?.children?.size)
    // AND, in every section, the title and every note
    assertEquals(7, tree.findNodeById("section:1671408000000")?.children?.size)
    assertEquals(4, tree.findNodeById("section:1671148800000")?.children?.size)
    assertEquals(5, tree.findNodeById("section:1670976000000")?.children?.size)
  }
}

