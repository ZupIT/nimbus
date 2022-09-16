package com.zup.nimbus.core.performance

import com.zup.nimbus.core.JsonLoader
import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.NodeUtils
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.tree.node.ServerDrivenNode
import com.zup.nimbus.core.ui.UILibrary
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
private const val SHOULD_PRINT_TIMES = true

@OptIn(ExperimentalCoroutinesApi::class)
class PerformanceTest {
  private val scope = TestScope()

  private val uiLibrary = UILibrary()
    .addOperation("formatPrice") { "US$ ${it[0]}" }

  private fun addToCart(content: ServerDrivenNode, productId: Int): Long {
    // do not use NodeUtils.pressButton here, we can't measure the time it takes to find the button.
    val button = content.findNodeById("add-to-cart:$productId") ?: throw Error("Could not find button")
    val started = Clock.System.now().toEpochMilliseconds()
    NodeUtils.triggerEvent(button, "onPress")
    val elapsed = Clock.System.now().toEpochMilliseconds() - started
    val newButton = content.findNodeById("add-to-cart:$productId")
    val inCartText = content.findNodeById("in-cart:$productId")
    assertNull(newButton)
    assertEquals("In cart ✓", inCartText?.properties?.get("text"))
    return elapsed
  }

  private fun toFixed(num: Double, cases: Int = 2): String {
    val intValue = num.toInt()
    val base = 10.0
    val decimalValue = ((num - intValue) * base.pow(cases)).toInt()
    return "${intValue}.$decimalValue"
  }

  private fun runPerformanceTest(jsonFileName: String, maxTimeMs: Int) {
    val json = JsonLoader.loadJson(jsonFileName)
    val nimbus = Nimbus(ServerDrivenConfig("", "test", ui = listOf(uiLibrary)))
    val started = Clock.System.now().toEpochMilliseconds()
    val content = nimbus.nodeBuilder.buildFromJsonString(json)
    content.initialize(nimbus)
    val times = mutableListOf(Clock.System.now().toEpochMilliseconds() - started)
    for (i in 1..20) {
      times.add(addToCart(content, i))
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

