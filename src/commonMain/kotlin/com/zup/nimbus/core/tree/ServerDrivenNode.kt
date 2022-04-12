package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.transformJsonElementToKotlinType
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import kotlinx.serialization.json.*
import kotlinx.serialization.decodeFromString

data class ServerDrivenNode(
  val id: String?,
  val component: String,
  val state: ServerDrivenState?,
  val properties: Map<String, Any?>?,
  val children: List<ServerDrivenNode>?,
) {
  companion object Factory {
    fun fromJsonString(json: String, idManager: IdManager): ServerDrivenNode {
      val jsonObject = Json.decodeFromString<JsonObject>(json)
      return fromJsonObject(jsonObject, idManager)
    }

    fun fromJsonObject(jsonObject: JsonObject, idManager: IdManager): ServerDrivenNode {
      // state
      val stateMap = if (jsonObject.containsKey("state")) jsonObject["state"] else null
      val state = if (stateMap == null) null else ServerDrivenState(
        id = stateMap.jsonObject["id"]?.jsonPrimitive?.content ?: throw MalformedComponentError(),
        value = stateMap.jsonObject["value"]?.let { transformJsonElementToKotlinType(it) },
      )

      // children
      val children: ArrayList<ServerDrivenNode> = ArrayList()
      if (jsonObject.containsKey("children")) {
        val childList = jsonObject["children"]?.jsonArray
        childList?.forEach() {
          children.add(fromJsonObject(it.jsonObject, idManager))
        }
      }

      return ServerDrivenNode(
        id = jsonObject["id"]?.jsonPrimitive?.content ?: idManager.next(),
        component = jsonObject["component"]?.jsonPrimitive?.content ?: throw MalformedComponentError(),
        state = state,
        // fixme: validate json structure
        properties = if (jsonObject.containsKey("properties")) transformJsonObjectToMap(
          jsonObject["properties"]?.jsonObject ?: throw MalformedComponentError()
        ) else null,
        children = if (children.isEmpty()) null else children,
      )
    }
  }
}
