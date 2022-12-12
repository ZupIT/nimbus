/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core

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
