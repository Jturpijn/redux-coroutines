package com.ximedes.redux

sealed class CounterAction
object Increment : CounterAction()
object Decrement : CounterAction()
object Multiply : CounterAction()
object Divide : CounterAction()

data class CounterState(val counter: Int = 0)

val reducer = { s: CounterState, a: CounterAction ->
    when (a) {
        is Increment -> CounterState(s.counter + 1)
        is Decrement -> CounterState(s.counter - 1)
        is Multiply -> CounterState(s.counter * 2)
        is Divide -> CounterState(s.counter / 2)
    }
}