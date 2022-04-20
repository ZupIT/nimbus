package com.zup.nimbus.core.tree

import com.zup.nimbus.core.utils.deepCopyToMutableMap
import com.zup.nimbus.core.utils.mapValuesToMutableList
import com.zup.nimbus.core.utils.transformJsonElementToKotlinType
import com.zup.nimbus.core.utils.transformJsonObjectToMap
import kotlinx.serialization.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

data class RawNode(
  override val id: String,
  override val component: String,
  override val properties: MutableMap<String, Any?>?,
  override val children: MutableList<RawNode>?,
  val state: ServerDrivenState?,
): ServerDrivenNode {
  companion object Factory {
    fun fromJsonString(json: String, idManager: IdManager): RawNode {
      val jsonObject = Json.decodeFromString<JsonObject>(json)
      return fromJsonObject(jsonObject, idManager)
    }

    fun fromJsonObject(jsonObject: JsonObject, idManager: IdManager): RawNode {
      // state
      val stateMap = if (jsonObject.containsKey("state")) jsonObject["state"] else null
      val state = if (stateMap == null) null else ServerDrivenState(
        id = stateMap.jsonObject["id"]?.jsonPrimitive?.content ?: throw MalformedComponentError(),
        value = stateMap.jsonObject["value"]?.let { transformJsonElementToKotlinType(it) },
      )

      // children
      val children: ArrayList<RawNode> = ArrayList()
      if (jsonObject.containsKey("children")) {
        val childList = jsonObject["children"]?.jsonArray
        childList?.forEach() {
          children.add(fromJsonObject(it.jsonObject, idManager))
        }
      }

      return RawNode(
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

  fun deepCopy(): RawNode {
    val copiedChildren = if (children == null) null else mapValuesToMutableList(children) { it.deepCopy() }
    val copiedProperties = if (properties == null) null else deepCopyToMutableMap(properties)
    return copy(children = copiedChildren, properties = copiedProperties)
  }
}
