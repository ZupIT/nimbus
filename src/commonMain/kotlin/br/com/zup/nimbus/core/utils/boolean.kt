package br.com.zup.nimbus.core.utils

infix fun <T> Boolean.then(param: T): T? = if (this) param else null
