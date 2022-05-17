package com.zup.nimbus.core.integration

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.Page
import com.zup.nimbus.core.ServerDrivenConfig
import com.zup.nimbus.core.network.DefaultHttpClient
import com.zup.nimbus.core.network.ResponseError
import com.zup.nimbus.core.network.ViewRequest
import com.zup.nimbus.core.render.ActionEvent
import com.zup.nimbus.core.tree.ServerDrivenAction
import com.zup.nimbus.core.tree.ServerDrivenNode
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AnalyticsRecord(
  val platform: String,
  val action: ServerDrivenAction,
  val node: ServerDrivenNode,
  val event: String,
  val screen: String,
  val timestamp: Long,
)

object MyAnalyticsService {
  val records = ArrayList<AnalyticsRecord>()

  fun createRecord(event: ActionEvent) {
    records.add(AnalyticsRecord(
      platform = "Test",
      action = event.action,
      node = event.element,
      //event = event.
      screen = event.view.description ?: "unknown",
      timestamp = 98844454548L, // in a real implementation, get the current unix time
    )
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ActionObserverTest {
  @Test
  fun shouldCreateAnalyticsRecord() {

  }

  @Test
  fun shouldNotCreateAnalyticsRecord() {

  }
}
