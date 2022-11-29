package br.com.zup.nimbus.core

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object JsonLoader {
  fun loadJson(fileName: String): String
}
