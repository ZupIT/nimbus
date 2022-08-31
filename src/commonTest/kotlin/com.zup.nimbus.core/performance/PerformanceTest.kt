package com.zup.nimbus.core.performance

import com.zup.nimbus.core.EmptyNavigator
import com.zup.nimbus.core.JsonLoader
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.NodeUtils
import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.ViewObserver
import com.zup.nimbus.core.observe
import com.zup.nimbus.core.tree.ServerDrivenNode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val MAX_AVERAGE_UPDATE_TIME_MS = 60
private const val FOR_EACH_MAX_AVERAGE_UPDATE_TIME_MS = 80
private const val SHOULD_PRINT_TIMES = false

@OptIn(ExperimentalCoroutinesApi::class)
class PerformanceTest {
  private val scope = TestScope()

  private val operations = mapOf<String, OperationHandler>(
    "formatPrice" to { "US$ ${it[0]}" }
  )

  private suspend fun addToCart(observer: ViewObserver, productId: Int, totalRenders: Int): Long {
    val content = observer.history.last()
    // do not use NodeUtils.pressButton here, we can't measure the time it takes to find the button.
    val button = NodeUtils.findById(content, "add-to-cart:$productId") ?: throw Error("Could not find button")
    val started = Clock.System.now().toEpochMilliseconds()
    NodeUtils.triggerEvent(button, "onPress")
    observer.waitForChanges(totalRenders)
    val elapsed = Clock.System.now().toEpochMilliseconds() - started
    val newButton = NodeUtils.findById(content, "add-to-cart:$productId")
    val inCartText = NodeUtils.findById(content, "in-cart:$productId")
    assertNull(newButton)
    assertEquals("In cart ✓", inCartText?.properties?.get("text"))
    clean(content)
    return elapsed
  }

  private fun toFixed(num: Double, cases: Int = 2): String {
    val intValue = num.toInt()
    val base = 10.0
    val decimalValue = ((num - intValue) * base.pow(cases)).toInt()
    return "${intValue}.$decimalValue"
  }

  private fun clean(node: ServerDrivenNode) {
    node.dirty = false
    node.children?.forEach { clean(it) }
  }

  private suspend fun runPerformanceTest(jsonFileName: String, maxTimeMs: Int) {
    val json = JsonLoader.loadJson(jsonFileName)
    val nimbus = Nimbus(ServerDrivenConfig("", "test", operations = operations))
    val node = nimbus.createNodeFromJson(json)
    val page = nimbus.createView({ EmptyNavigator() })
    val observer = page.observe()
    val started = Clock.System.now().toEpochMilliseconds()
    page.renderer.paint(node)
    observer.waitForChanges()
    val times = mutableListOf(Clock.System.now().toEpochMilliseconds() - started)
    clean(observer.history.last())
    for (i in 1..20) {
      times.add(addToCart(observer, i, i + 1))
    }
    val updates = times.drop(1)

    if (SHOULD_PRINT_TIMES) {
      println("==========================")
      println("Times for $jsonFileName:")
      println("1st render: ${times[0]}ms")
      println("best update: ${updates.min()}ms")
      println("worst update: ${updates.max()}ms")
      println("average update: ${toFixed(updates.average())}ms")
      println("==========================")
    }

    assertTrue(updates.average() < maxTimeMs)
  }

  @Test
  fun `should add to cart in reasonable time - without forEach`() = scope.runTest {
    runPerformanceTest("products", MAX_AVERAGE_UPDATE_TIME_MS)
  }

  @Test
  fun `should add to cart in reasonable time - with forEach`() = scope.runTest {
    runPerformanceTest("products-forEach", FOR_EACH_MAX_AVERAGE_UPDATE_TIME_MS)
  }
}
