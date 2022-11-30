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
