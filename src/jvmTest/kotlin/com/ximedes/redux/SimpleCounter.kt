package com.ximedes.redux

sealed class CounterAction
object Increment : CounterAction()
object Decrement : CounterAction()

data class CounterState(val counter: Int = 0)

val CounterReducer = { s: CounterState, a: CounterAction ->
    when (a) {
        is Increment -> CounterState(s.counter + 1)
        is Decrement -> CounterState(s.counter - 1)
    }
}

var counterStore = ReducerStore(CounterReducer, CounterState())