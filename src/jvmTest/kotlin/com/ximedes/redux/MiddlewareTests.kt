package com.ximedes.redux

import org.junit.Test
import kotlin.test.assertEquals

val LOGGER: Middleware<CounterState, CounterAction> = { store, action, next ->
    println("Before: ${store.getState()}")
    next(action)
    println("After: ${store.getState()}")
}

class LoggerTestsJVM {


    private val store = ReducerStore(
        reducer,
        CounterState()
    )
    private val logStore =
        apply(store, LOGGER)

    @Test
    fun `Test dispatch with Middleware`() {
        logStore.dispatch(Increment)
        assertEquals(1, logStore.getState().counter)
    }
}

class MultipleLoggersTestsJVM {
    private val store = ReducerStore(
        reducer,
        CounterState()
    )
    private val logStore = applyAll(
        store,
        LOGGER,
        LOGGER,
        LOGGER
    )

    @Test
    fun `Test dispatch with Middlewares`() {
        logStore.dispatch(Increment)
        assertEquals(1, logStore.getState().counter)
    }
}