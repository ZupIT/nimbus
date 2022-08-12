package com.zup.nimbus.core

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.FILE
import platform.posix.SEEK_END
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.rewind

actual object JsonLoader {
  actual fun loadJson(fileName: String): String {
    val path = NSBundle.mainBundle.pathForResource("resources/$fileName", "json")
      ?: throw IllegalArgumentException("No such file: resources/$fileName.json")
    val file: CPointer<FILE> = fopen(path, "r") ?: throw IllegalArgumentException("Can't open: $path")
    fseek(file, 0, SEEK_END)
    val size = ftell(file)
    rewind(file)

    return memScoped {
      val tmp = allocArray<ByteVar>(size)
      fread(tmp, sizeOf<ByteVar>().convert(), size.convert(), file)
      tmp.toKString()
    }
  }
}
