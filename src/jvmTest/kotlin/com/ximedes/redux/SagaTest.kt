package com.ximedes.redux

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

val container = SagaContainer<CounterState, CounterAction>()
val sagaStore = applyMiddleware(ReducerStore(CounterReducer, CounterState()), container.createMiddleWare())

class SagaTest {
    @Test
    fun `Selector test`() {
        sagaStore.dispatch(Increment)
        assertEquals(container.select(), sagaStore.getState())
    }

    @Test
    fun `Put test`() {
        assertEquals(container.select(), sagaStore.getState())
        container.runSaga { assertEquals(Increment, put(Increment)) }
        assertEquals(sagaStore.getState(), container.select())
    }

    @Nested
    inner class TakeTests {
        @Test
        fun `Take test`() {

        }
    }

    @Nested
    inner class TakeEveryTests {
        @Test
        fun `TakeEvery test`() {
            
        }
    }
}