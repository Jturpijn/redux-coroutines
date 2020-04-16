package com.ximedes.redux

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class ReducerStoreTest {
    var reducerStore = ReducerStore(CounterReducer, CounterState())

    @Test
    fun `Testing the initial getState`() = assertEquals(CounterState(), reducerStore.getState())

    @Test
    fun `Testing dispatching an Increment`() {
        reducerStore.dispatch(Increment)
        assertEquals(1, reducerStore.getState().counter)
    }

    @ParameterizedTest(name = "Dispatched with input: {0}")
    @CsvSource("1", "100", "1000")
    fun `Testing 1000 dispatches of Increment`(input: Int) {
        for (i in 1..input) {
            reducerStore.dispatch(Increment)
        }
        assertEquals(input, reducerStore.getState().counter)
    }

    @Test
    fun `Testing dispatching a Decrement`() {
        reducerStore.dispatch(Decrement)
        assertEquals(-1, reducerStore.getState().counter)
    }

    @ParameterizedTest(name = "Listened this many times: {0}")
    @CsvSource("1","100","1000")
    fun `Testing listeners`(lInput: Int) {
        reducerStore.subscribe { state -> println("$lInput New state is : $state")}
        for(i in 1..lInput) {
            reducerStore.dispatch(Increment)
        }
        assertEquals(lInput, reducerStore.getState().counter)
    }

    @ParameterizedTest(name = "Tested with pair {0}, {1}")
    @CsvSource("1, 2", "2, 1")
    fun `Testing order of listeners`(first: Int, second: Int) {
        var listenerID = 0
        reducerStore.subscribe { _ -> listenerID = first } ; reducerStore.subscribe { _ -> listenerID = second }
        reducerStore.dispatch(Increment)
        assertEquals(second, listenerID)
    }
}