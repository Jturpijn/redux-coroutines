package com.ximedes.redux

import org.junit.Before
import org.junit.Test
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals


class ReducerStoreTest {
    @Before
    fun initStore() {
        counterStore = ReducerStore(CounterReducer, CounterState(counter = 0))
    }

    @Test
    fun `Testing the initial getState`() = assertEquals(CounterState(), counterStore.getState())

    @Test
    fun `Testing dispatching an Increment`() {
        counterStore.dispatch(Increment)
        assertEquals(1, counterStore.getState().counter)
    }

    @ParameterizedTest(name = "Dispatched with input: {0}")
    @CsvSource("1", "100", "1000")
    fun `Testing 1000 dispatches of Increment`(input: Int) {
        // Calling initStore() in order to reset the store counter. @ParameterizedTest should, but doesnt trigger @Before
        initStore()
        for (i in 1..input) {
            counterStore.dispatch(Increment)
        }
        assertEquals(input, counterStore.getState().counter)
    }

    @Test
    fun `Testing dispatching a Decrement`() {
        counterStore.dispatch(Decrement)
        assertEquals(-1, counterStore.getState().counter)
    }

    @ParameterizedTest(name = "Listened this many times: {0}")
    @CsvSource("1","100","1000")
    fun `Testing listeners`(lInput: Int) {
        // Calling initStore() in order to reset the store counter. @ParameterizedTest should, but doesnt trigger @Before
        initStore()
        counterStore.subscribe { state -> println("$lInput New state is : $state")}
        for(i in 1..lInput) {
            counterStore.dispatch(Increment)
        }
        assertEquals(lInput, counterStore.getState().counter)
    }

    @ParameterizedTest(name = "Tested with pair {0}, {1}")
    @CsvSource("1, 2", "2, 1")
    fun `Testing order of listeners`(first: Int, second: Int) {
        var listenerID = 0
        counterStore.subscribe { _ -> listenerID = first }
        counterStore.subscribe { _ -> listenerID = second }
        counterStore.dispatch(Increment)
        assertEquals(second, listenerID)
    }
}