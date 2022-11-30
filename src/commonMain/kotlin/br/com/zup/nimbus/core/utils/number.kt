package br.com.zup.nimbus.core.utils

private enum class NumberType { Short, Int, Long, Float, Double }

private fun decideType(left: Number, right: Number): NumberType {
  return when {
    left is Double || right is Double -> NumberType.Double
    left is Float || right is Float -> NumberType.Float
    left is Long || right is Long -> NumberType.Long
    left is Int || right is Int -> NumberType.Int
    left is Short || right is Short -> NumberType.Short
    else -> NumberType.Double
  }
}

fun Number.isDividable(other: Number): Boolean = when(decideType(this, other)) {
  NumberType.Short -> this.toShort() % other.toShort() == 0
  NumberType.Int -> this.toInt() % other.toInt() == 0
  NumberType.Long -> this.toLong() % other.toLong() == 0L
  NumberType.Float -> this.toFloat() % other.toFloat() == 0F
  NumberType.Double -> this.toDouble() % other.toDouble() == 0.0
}

operator fun Number.minus(other: Number): Number = when(decideType(this, other)) {
  NumberType.Short -> this.toShort() - other.toShort()
  NumberType.Int -> this.toInt() - other.toInt()
  NumberType.Long -> this.toLong() - other.toLong()
  NumberType.Float -> this.toFloat() - other.toFloat()
  NumberType.Double -> this.toDouble() - other.toDouble()
}

operator fun Number.plus(other: Number): Number = when(decideType(this, other)) {
  NumberType.Short -> this.toShort() + other.toShort()
  NumberType.Int -> this.toInt() + other.toInt()
  NumberType.Long -> this.toLong() + other.toLong()
  NumberType.Float -> this.toFloat() + other.toFloat()
  NumberType.Double -> this.toDouble() + other.toDouble()
}

operator fun Number.times(other: Number): Number = when(decideType(this, other)) {
  NumberType.Short -> this.toShort() * other.toShort()
  NumberType.Int -> this.toInt() * other.toInt()
  NumberType.Long -> this.toLong() * other.toLong()
  NumberType.Float -> this.toFloat() * other.toFloat()
  NumberType.Double -> this.toDouble() * other.toDouble()
}

operator fun Number.div(other: Number): Number = when(decideType(this, other)) {
  NumberType.Short -> if (isDividable(other)) this.toShort() / other.toShort() else this.toDouble() / other.toDouble()
  NumberType.Int -> if (isDividable(other)) this.toInt() / other.toInt() else this.toDouble() / other.toDouble()
  NumberType.Long -> if (isDividable(other)) this.toLong() / other.toLong() else this.toDouble() / other.toDouble()
  NumberType.Float -> this.toFloat() / other.toFloat()
  NumberType.Double -> this.toDouble() / other.toDouble()
}

operator fun Number.rem(other: Number): Number = when(decideType(this, other)) {
  NumberType.Short -> this.toShort() % other.toShort()
  NumberType.Int -> this.toInt() % other.toInt()
  NumberType.Long -> this.toLong() % other.toLong()
  NumberType.Float -> this.toFloat() % other.toFloat()
  NumberType.Double -> this.toDouble() % other.toDouble()
}

operator fun Number.compareTo(other: Number) = when(decideType(this, other)) {
  NumberType.Short -> this.toShort().compareTo(other.toShort())
  NumberType.Int -> this.toInt().compareTo(other.toInt())
  NumberType.Long -> this.toLong().compareTo(other.toLong())
  NumberType.Float -> this.toFloat().compareTo(other.toFloat())
  NumberType.Double -> this.toDouble().compareTo(other.toDouble())
}

operator fun Number.plusAssign(number: Number) { this += number }
operator fun Number.minusAssign(number: Number) { this -= number }
operator fun Number.timesAssign(number: Number) { this *= number }
operator fun Number.divAssign(number: Number) { this /= number }
operator fun Number.remAssign(number: Number) { this %= number }

fun toNumberOrNull(value: Any?) = when(value) {
  is String -> if (value.contains('.')) value.toDoubleOrNull() else value.toIntOrNull() ?: value.toLongOrNull()
  is Number -> value
  else -> null
}
