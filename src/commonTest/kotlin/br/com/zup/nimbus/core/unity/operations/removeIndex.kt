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

package br.com.zup.nimbus.core.unity.operations

import br.com.zup.nimbus.core.ui.coreUILibrary
import kotlin.test.Test
import kotlin.test.assertEquals

private val removeIndex = coreUILibrary.getOperation("removeIndex")!!

class RemoveIndexOperationTest {
  @Test
  fun `should remove an element from a list`() {
    val items = listOf(
      object { val world = "hello"  },
      object { val hello = "hello"  },
      object { val hello = "world"  },
      object { val world = "world"  }
    )
    var list = mutableListOf(
      items[0],
      items[1],
      items[2],
      items[3],
    )

    assertEquals(4, list.size)
    assertEquals(items[2], list[2])

    list = removeIndex(listOf(list, 2)) as MutableList<Any>

    assertEquals(3, list.size)
    assertEquals(items[3], list[2])

    list = removeIndex(listOf(list, 2)) as MutableList<Any>

    assertEquals(2, list.size)
    assertEquals(items[1], list[1])
  }
}
