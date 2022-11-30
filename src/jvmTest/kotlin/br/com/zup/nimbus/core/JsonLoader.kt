package br.com.zup.nimbus.core

import java.io.File

// Attention: there are no errors here. This is an IntelliJ bug. I don't know which string to pass to @Suppress, so
// IntelliJ keeps complaining.

actual object JsonLoader {
  actual fun loadJson(fileName: String): String {
    val path = "./src/commonTest/resources/$fileName.json"
    val file = File(path)
    if (!file.exists()) throw IllegalArgumentException("No such file: $path")
    return file.readText()
  }
}
