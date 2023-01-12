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

package br.com.zup.nimbus.core.integration.navigation

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.NodeUtils
import br.com.zup.nimbus.core.ObservableNavigator
import br.com.zup.nimbus.core.ServerDrivenConfig
import br.com.zup.nimbus.core.network.DefaultHttpClient
import br.com.zup.nimbus.core.network.ViewRequest
import br.com.zup.nimbus.core.scope.closestState
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.findNodeById
import br.com.zup.nimbus.core.ui.UILibrary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationCallbackTest {
  private val scope = TestScope()
  private var savedNote: Any? = null
  private val ui = UILibrary("test").addAction("saveNote") {
    savedNote = it.action.properties?.get("note")
  }
  private val nimbus = Nimbus(
      ServerDrivenConfig(
        baseUrl = BASE_URL,
        platform = "test",
        httpClient = DefaultHttpClient(callbackServerMock),
        ui = listOf(ui)
    )
  )
  private val navigator = ObservableNavigator(scope, nimbus)
  private val noteId = 1
  private val noteTitle = "My first note"
  private val noteDescription = "Description of my first note"
  private val newTitle = "My edited note"
  private val newDescription = "Description of my edited note"

  @BeforeTest
  fun clear() {
    navigator.clear()
    savedNote = null
  }

  private suspend fun listNotesAndEditFirst(): ServerDrivenNode {
    // WHEN we load the screen that lists all notes
    navigator.push(ViewRequest("/list"))
    val listScreen = navigator.awaitPushCompletion()
    // AND we click the first note in order to edit it
    NodeUtils.pressButton(listScreen, "edit:1")
    // AND we wait for the edition screen to load
    val editScreen = navigator.awaitPushCompletion()
    //NodeUtils.triggerEvent(editScreen.findNodeById("form"), "onInit")
    // THEN the form should be pre-filled with the values of the first note
    val titleInput = editScreen.findNodeById("title")
    val descriptionInput = editScreen.findNodeById("description")
    assertEquals(noteTitle, titleInput?.properties?.get("value"))
    assertEquals(noteDescription, descriptionInput?.properties?.get("value"))
    // WHEN we edit each text field
    NodeUtils.triggerEvent(titleInput, "onChange", newTitle)
    NodeUtils.triggerEvent(descriptionInput, "onChange", newDescription)
    return editScreen
  }

  @Test
  fun shouldSaveEditedNoteOnSecondPage() = scope.runTest {
    // WHEN we list all notes, navigate to edit page of the first note and change the values in the form
    val editionScreen = listNotesAndEditFirst()
    // AND press "save"
    NodeUtils.pressButton(editionScreen, "save")
    // THEN it should be showing the list screen
    val currentScreen = navigator.pages.last()
    assertTrue(currentScreen.findNodeById("edit:1") != null)
    assertTrue(currentScreen.findNodeById("edit:2") != null)
    // AND the callback of the first page (saveNote) should have been called with the edited note
    assertEquals(
      mapOf("id" to noteId, "title" to newTitle, "description" to newDescription),
      savedNote,
    )
    // AND the textual (ui) representation of the first note should have been updated
    assertEquals("$newTitle: $newDescription", currentScreen.findNodeById("text:1")?.properties?.get("text"))
    // AND the first note should have been updated (actual list state)
    val notes = currentScreen.children?.first()?.closestState("notes")
    assertEquals(
      mapOf("id" to noteId, "title" to newTitle, "description" to newDescription),
      (notes?.get() as List<Any?>).firstOrNull(),
    )
  }

  @Test
  fun shouldCancelEditedNoteOnSecondPage() = scope.runTest {
    // WHEN we list all notes, navigate to edit page of the first note and change the values in the form
    val editionScreen = listNotesAndEditFirst()
    // AND press "cancel"
    NodeUtils.pressButton(editionScreen, "cancel")
    // THEN the list screen should be showing
    val currentScreen = navigator.pages.last()
    assertTrue(currentScreen.findNodeById("edit:1") != null)
    assertTrue(currentScreen.findNodeById("edit:2") != null)
    // AND the textual (ui) representation of the first note should not have changed
    assertEquals("$noteTitle: $noteDescription", currentScreen.findNodeById("text:1")?.properties?.get("text"))
    // AND the value of the first item in the forEach should not have changed (for each item state)
    val item = currentScreen.findNodeById("edit:1")?.closestState("item")
    assertEquals(
      mapOf("id" to noteId, "title" to noteTitle, "description" to noteDescription),
      item?.get(),
    )
    // AND the first note should not have changed (actual list state)
    val notes = currentScreen.children?.first()?.closestState("notes")
    assertEquals(
      mapOf("id" to noteId, "title" to noteTitle, "description" to noteDescription),
      (notes?.get() as List<Any?>).firstOrNull(),
    )
    // AND the first page callback should not have been called
    assertEquals(null, savedNote)
  }
}
