package com.zup.nimbus.core.unit

infix fun <T> Boolean.then(param: T): T? = if (this) param else null
