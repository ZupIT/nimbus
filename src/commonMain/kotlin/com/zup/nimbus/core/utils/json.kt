package com.zup.nimbus.core.utils

import kotlinx.serialization.json.*

fun transformJsonObjectToMap(json: JsonObject): Map<String, Any?> {
  return json.mapValues { transformJsonElementToKotlinType(it.value) }
}

fun transformJsonArrayToList(json: JsonArray): List<Any?> {
  return json.map { transformJsonElementToKotlinType(it) }
}

fun transformJsonPrimitiveToPrimitive(json: JsonPrimitive): Any? {
  return json.booleanOrNull ?: json.intOrNull ?: json.longOrNull ?: json.doubleOrNull ?: json.contentOrNull
}

fun transformJsonElementToKotlinType(json: JsonElement): Any? {
  if (json is JsonObject) return transformJsonObjectToMap(json.jsonObject)
  if (json is JsonArray) return transformJsonArrayToList(json.jsonArray)
  return transformJsonPrimitiveToPrimitive(json.jsonPrimitive)
}
