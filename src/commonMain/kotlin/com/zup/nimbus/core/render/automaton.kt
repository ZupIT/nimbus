package com.zup.nimbus.core.render

class Transition (
  val read: Any?,
  val push: String?,
  val pop: String?,
  val next: String
)

class DPAParams (
  val initial: String,
  val final: String,
  val transitions: Map<String, List<Transition>>,
)

interface DPA {
  /**
   * Uses the automaton to identify its patterns inside the string passed as parameter. If a pattern
   * is identified, the identified substring is returned. Otherwise, null is returned.
   *
   * @param input the string to match against the automaton
   * @returns the substring matching the pattern
   */
  fun match(input: String): String?
}

class Automaton {
  companion object Factory {
    /**
     * The empty symbol for transitions
     */
    const val empty = "∅"
    /**
     * Creates a Deterministic Pushdown Automaton (DPA) according to states and transitions passed as
     * parameters.
     *
     * A DPA is oftenly used to recognize patterns in a string. Although regular expressions also
     * recognizes patterns in strings, a DPA is more powerful and can recognize more complex structures.
     * A regular expression can only recognize regular languages, while a DPA can also recognize
     * free context languages.
     *
     * This function creates an automaton with a single function: `match`. `match` is always run for
     * a string and, if a match is found, the string matching the pattern is returned. If no match is
     * found, `null` is returned.
     *
     * Every time `match` is called, the automaton starts at the state `param.initial` and if it ever
     * reaches `param.final`, it stops with a match. If the input ends and the current automaton state
     * is not the final, there's no match. Also, if there's no transition for the current input, the
     * automaton stops without a match.
     *
     * To represent end of stack, use the constant `Automaton.empty`.
     *
     * Note about reading an input: to make the representation easier, we can read multiple characters
     * at once from the input. This means that, in a state transition, `read` can be something like
     * `An entire paragraph.`, instead of sequence of transitions for each letter. Moreover, to simplify
     * the state machine, we can also use a regular expression in the property `read` of the transition.
     *
     * @param param an object with the initial state (string), final state (string) and the state
     * transitions. The state transitions are a map where the key is the state and the value is an array
     * of transitions for the state. A transition is composed by `read` (the string or regex to read
     * from the input string); `push` (the value to push to the stack); `pop` (the value to pop from
     * the stack); and `next` (the next state). The only required property for a transition is `next`.
     */
    fun createDPA(params: DPAParams): DPA {
      val dpa = object: DPA {
        override fun match(input: String): String? {
          val stack: MutableList<String> = mutableListOf()
          var currentState = params.initial
          var remainingInput = input
          var matchedValue = ""

          fun availableTransitionsPredicate(transition: Transition): Boolean {
            if (transition.pop != null &&
              stack.last() != transition.pop &&
              (transition.pop == empty && stack.isNotEmpty())) {
              return false
            }

            if (transition.read == null) return true
            if (transition.read is String) {
              matchedValue = transition.read as String
              return remainingInput.startsWith(transition.read as String)
            }

            if (transition.read is Regex) {
              val match = """^(${(transition.read as Regex).pattern})""".toRegex().find(remainingInput) ?: return false
              val (value) = match.destructured
              matchedValue = value
              return true
            }

            return false
          }

          while (currentState != params.final) {
            val availableTransitions = params.transitions[currentState]
            matchedValue = ""
            val transition = availableTransitions?.find { availableTransitionsPredicate(it) } ?: return null

            remainingInput = remainingInput.substring(matchedValue.length)
            currentState = transition.next
            if (transition.pop != null) stack.removeLast()
            if (transition.push != null) stack.add(transition.push!!)
          }

          return input.substring(0, input.length - remainingInput.length)
        }
      }

      return dpa
    }
  }
}
