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

@file:Suppress("ComplexCondition") // todo: verify
package br.com.zup.nimbus.core.expression.parser

import br.com.zup.nimbus.core.regex.FastRegex
import br.com.zup.nimbus.core.regex.toFastRegex

private const val MAX_MEMOIZED_KEYS = 1000

class Transition {
  val readString: String?
  val readRegex: FastRegex?
  val push: String?
  val pop: String?
  val next: String

  constructor(push: String?, pop: String?, next: String) {
    readString = null
    readRegex = null
    this.push = push
    this.pop = pop
    this.next = next
  }

  constructor(read: String?, isRegex: Boolean, push: String?, pop: String?, next: String) {
    if (isRegex) {
      readString = null
      readRegex = if (read == null) read else """^($read)""".toFastRegex()
    } else {
      readString = read
      readRegex = null
    }
    this.push = push
    this.pop = pop
    this.next = next
  }

  constructor(read: String?, push: String?, pop: String?, next: String) : this(read, false, push, pop, next)
}

/**
 * Creates a Deterministic Pushdown Automaton (DPA) according to states and transitions passed as
 * parameters.
 *
 * A DPA is often used to recognize patterns in a string. Although regular expressions also
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
 */
class DPA (
    private val initial: String,
    private val final: String,
    private val transitions: Map<String, List<Transition>>,
) {
  object Symbols {
    const val EMPTY = "∅"
  }

  /* A better idea than to memoize this result is to compile the expression in the first render and never care about
  it again. When we implement this new strategy, we must remove this memoization to avoid wasting memory. */
  private val memoized = mutableMapOf<String, String>()

  @Suppress("ComplexMethod")
  fun match(input: String): String? {
    if (memoized.containsKey(input)) return memoized[input]

    val stack: MutableList<String> = mutableListOf()
    var currentState = initial
    var remainingInput = input
    var matchedValue = ""

    fun matchTransition(transition: Transition): Boolean {
      if (transition.pop != null &&
        (stack.isNotEmpty() && stack.last() != transition.pop) &&
        (transition.pop == Symbols.EMPTY && stack.isNotEmpty())) {
        return false
      }

      if (transition.readString == null && transition.readRegex == null) return true
      if (transition.readString != null) {
        matchedValue = transition.readString
        return remainingInput.startsWith(transition.readString)
      }

      val value = transition.readRegex?.find(remainingInput) ?: return false
      matchedValue = value
      return true
    }

    while (currentState != final) {
      val availableTransitions = transitions[currentState]
      matchedValue = ""
      val transition = availableTransitions?.find { matchTransition(it) } ?: return null

      remainingInput = remainingInput.substring(matchedValue.length)
      currentState = transition.next
      if (transition.pop != null && stack.isNotEmpty()) stack.removeLast()
      if (transition.push != null) stack.add(transition.push)
    }

    val result = input.substring(0, input.length - remainingInput.length)
    if (memoized.size > MAX_MEMOIZED_KEYS) memoized.remove(memoized.keys.first())
    memoized[input] = result
    return result
  }
}
