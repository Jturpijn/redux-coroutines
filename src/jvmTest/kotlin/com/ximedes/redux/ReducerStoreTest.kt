package com.ximedes.redux

import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class ReducerStoreTest {
    private val store = ReducerStore(reducer, CounterState())

    @Test
    fun `Testing the initial getState`() = assertEquals(CounterState(), store.getState())

    @Test
    fun `Testing dispatching an Increment`() {
        store.dispatch(Increment)
        assertEquals(1, store.getState().counter)
    }

    @ParameterizedTest(name = "Dispatched with input: {0}")
    @CsvSource("1", "100", "1000")
    fun `Testing 1000 dispatches of Increment`(input: Int) {
        for (i in 1..input) {
            store.dispatch(Increment)
        }
        assertEquals(input, store.getState().counter)
    }

    @Test
    fun `Testing dispatching a Decrement`() {
        store.dispatch(Increment); store.dispatch(Decrement)
        assertEquals(0, store.getState().counter)
    }

    @ParameterizedTest(name = "Listened this many times: {0}")
    @CsvSource("1","100","1000")
    fun `Testing listeners`(input: Int) {
        store.subscribe { state -> println("New state is : $state")}
        for(i in 1..input) {
            store.dispatch(Increment)
        }
        assertEquals(input, store.getState().counter)
    }

    @ParameterizedTest(name = "Tested with pair {0}, {1}")
    @CsvSource("1, 2", "2, 1")
    fun `Testing order of listeners`(first: Int, second: Int) {
        var listenerID = 0
        store.subscribe { _ -> listenerID = first }
        store.subscribe { _ -> listenerID = second }
        store.dispatch(Increment)
        assertEquals(second, listenerID)
    }
}