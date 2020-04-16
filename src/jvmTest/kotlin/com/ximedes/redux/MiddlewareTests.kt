package com.ximedes.redux

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

val LOGGER: Middleware<CounterState, CounterAction> = { store, action, next ->
    println("Before: ${store.getState()}")
    next(action)
    println("After: ${store.getState()}")
}

class MiddlewareTests {
    var middlewareStore = ReducerStore(CounterReducer, CounterState())
    private val logStore = applyMiddleware(middlewareStore, LOGGER)

    @Test
    fun `Test dispatch with Middleware`() {
        logStore.dispatch(Increment)
        assertEquals(1, logStore.getState().counter)
    }

    @Test
    fun `Test decrement with Middleware`() {
        logStore.dispatch(Decrement)
        assertEquals(-1, logStore.getState().counter)
    }

    @Test
    fun `Exception Middleware`() {
        val exception: Middleware<CounterState, CounterAction> = { store, action, next ->
            next(action)
            if (store.getState().counter < 0) {
                throw Exception("Cannot be a negative")
            }
        }
        val excStore = applyMiddleware(middlewareStore, exception)
        assertFailsWith<Exception> { excStore.dispatch(Decrement) }
    }

    @Nested
    inner class MultipleMiddlewaresTests {

        @Test
        fun `Test dispatch with Middlewares`() {
            val logStore = applyAllMiddleware(middlewareStore, LOGGER, LOGGER, LOGGER)
            logStore.dispatch(Increment)
            assertEquals(1, logStore.getState().counter)
        }

        @Test
        fun `Test proper wrapping of the store with multiple middlewares`() {
            // Test list
            val middlewareIDList = mutableListOf<Int>()
            // Middleware that adds its ID to the list
            val middlewareTest1: Middleware<CounterState, CounterAction> = { store, action, next ->
                middlewareIDList.add(1)
                next(action)
            }
            val middlewareTest2: Middleware<CounterState, CounterAction> = { store, action, next ->
                middlewareIDList.add(2)
                next(action)
            }
            val middlewareTest3: Middleware<CounterState, CounterAction> = { store, action, next ->
                middlewareIDList.add(3)
                next(action)
            }

            // A store with the above middleware
            val enhancedStore = applyAllMiddleware(middlewareStore, middlewareTest1, middlewareTest2, middlewareTest3)
            // An action to trigger the middlewares
            enhancedStore.dispatch(Increment)
            assertEquals(listOf(1, 2, 3), middlewareIDList)
        }

        @Test
        fun `Test middleware Exception catching`() {
            // Quick and dirty way to catch the exception
            val exception: Middleware<CounterState, CounterAction> = { store, action, next ->
                if (store.getState().counter == 0 && action is Decrement) {
                    throw Exception("Cannot be a negative")
                }
                next(action)
            }

            val exCatchStore = applyAllMiddleware(middlewareStore, LOGGER, exception, LOGGER)
            assertFailsWith<Exception> { exCatchStore.dispatch(Decrement) }
            assertEquals(0, exCatchStore.getState().counter)
        }
    }
}