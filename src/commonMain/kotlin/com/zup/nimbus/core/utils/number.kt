package com.zup.nimbus.core.utils

private fun bothAreIntegers(a: Number, b: Number): Boolean {
  return (a is Int && b is Int)
}

operator fun Number.minus(number: Number): Number {
  if (bothAreIntegers(this, number)) return (this.toInt() - number.toInt())
  return (this.toDouble() - number.toDouble())
}

operator fun Number.plus(number: Number): Number {
  if (bothAreIntegers(this, number)) return (this.toInt() + number.toInt())
  return (this.toDouble() + number.toDouble())
}

operator fun Number.times(number: Number): Number {
  if (bothAreIntegers(this, number)) return (this.toInt() * number.toInt())
  return (this.toDouble() * number.toDouble())
}

operator fun Number.div(number: Number): Number {
  if (bothAreIntegers(this, number)) return (this.toInt() / number.toInt())
  return (this.toDouble() / number.toDouble())
}

operator fun Number.rem(number: Number): Number {
  if (bothAreIntegers(this, number)) return (this.toInt() % number.toInt())
  return (this.toDouble() % number.toDouble())
}

operator fun Number.compareTo(number: Number): Int {
  if (bothAreIntegers(this, number)) return (this.toInt().compareTo(number.toInt()))
  return (this.toDouble().compareTo(number.toDouble()))
}

operator fun Number.plusAssign(number: Number) { this += number }
operator fun Number.minusAssign(number: Number) { this -= number }
operator fun Number.timesAssign(number: Number) { this *= number }
operator fun Number.divAssign(number: Number) { this /= number }
operator fun Number.remAssign(number: Number) { this %= number }
