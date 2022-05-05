package com.zup.nimbus.core.utils

import kotlinx.serialization.json.*

/**
 * Transforms a JsonObject into a Kotlin Mutable Map recursively, i.e. this method will transform every JsonElement
 * inside the map into its Kotlin equivalent.
 *
 * @param json the JsonObject to be used as source to create the mutable map.
 * @return the mutable map equivalent to "json".
 */
fun transformJsonObjectToMap(json: JsonObject): MutableMap<String, Any?> {
  return mapValuesToMutableMap(json) { transformJsonElementToKotlinType(it.value) }
}

/**
 * Transforms a JsonArray into a Kotlin Mutable List recursively, i.e. this method will transform every JsonElement
 * inside the list into its Kotlin equivalent.
 *
 * @param json the JsonArray to be used as source to create the mutable list.
 * @return the mutable list equivalent to "json".
 */
fun transformJsonArrayToList(json: JsonArray): List<Any?> {
  return mapValuesToMutableList(json) { transformJsonElementToKotlinType(it) }
}

/**
 * Transforms a JsonPrimitive into its Kotlin equivalent.
 *
 * @param json the JsonPrimitive to be used as source to create the Kotlin primitive.
 * @return the Kotlin primitive equivalent to "json".
 */
fun transformJsonPrimitiveToPrimitive(json: JsonPrimitive): Any? {
  if (json.isString) return json.content
  return json.booleanOrNull ?: json.intOrNull ?: json.longOrNull ?: json.doubleOrNull
}

/**
 * Transforms a JsonElement into its Kotlin equivalent recursively, i.e. if "json" is a JsonArray or JsonMap, this
 * method will transform every JsonElement inside the array or map into its Kotlin equivalent.
 *
 * @param json the JsonElement to be used as source to create the Kotlin type.
 * @return a mutable map, a mutable list or a primitive type equivalent to "json".
 */
fun transformJsonElementToKotlinType(json: JsonElement): Any? {
  if (json is JsonObject) return transformJsonObjectToMap(json.jsonObject)
  if (json is JsonArray) return transformJsonArrayToList(json.jsonArray)
  return transformJsonPrimitiveToPrimitive(json.jsonPrimitive)
}
