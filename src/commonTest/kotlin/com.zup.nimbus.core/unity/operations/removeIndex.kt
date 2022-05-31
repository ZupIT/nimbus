package com.zup.nimbus.core.unity.operations

import com.zup.nimbus.core.operations.removeIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoveIndexOperationTest {
  @Test
  fun `should remove an element from a list`() {
    val items = arrayOf(
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

    list = removeIndex(list, 2)

    assertEquals(3, list.size)
    assertEquals(items[3], list[2])

    list = removeIndex(list, 2)

    assertEquals(2, list.size)
    assertEquals(items[1], list[1])
  }
}
