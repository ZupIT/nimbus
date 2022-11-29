package br.com.zup.nimbus.core.regex

class MatchGroups(
  val values: List<String>
) {
  val destructured: Destructured get() = Destructured(this)

  @Suppress("MagicNumber")
  inner class Destructured(val group: MatchGroups) {
    operator fun component1():  String = group.values[1]
    operator fun component2():  String = group.values[2]
    operator fun component3():  String = group.values[3]
    operator fun component4():  String = group.values[4]
    operator fun component5():  String = group.values[5]
    operator fun component6():  String = group.values[6]
    operator fun component7():  String = group.values[7]
    operator fun component8():  String = group.values[8]
    operator fun component9():  String = group.values[9]
    operator fun component10(): String = group.values[10]
  }
}
