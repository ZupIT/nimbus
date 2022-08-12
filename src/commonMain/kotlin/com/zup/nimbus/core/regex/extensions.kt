package com.zup.nimbus.core.regex

fun String.toFastRegex() = FastRegex(this)

fun String.replace(regex: FastRegex, replacement: String) = regex.replace(this, replacement)

fun String.replace(regex: FastRegex, transform: (MatchGroups) -> String) = regex.replace(this, transform)

fun String.matches(regex: FastRegex) = regex.matches(this)
