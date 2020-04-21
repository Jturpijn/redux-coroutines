package com.ximedes.redux

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class SagaTest {

    val container = SagaContainer<CounterState, CounterAction>()
    val sagaStore = applyMiddleware(ReducerStore(CounterReducer, CounterState()), container.createMiddleWare())

    @Test
    fun `actions are dispatched synchronously`() {
        sagaStore.dispatch(Increment)
        // Now the state should be updated
        assertEquals(container.select(), sagaStore.getState())
    }

    @Test
    fun `put call from saga dispatches action`() {
        sagaStore.dispatch(Decrement)
        val sagaJob = container.runSaga { assertEquals(Increment, put(Increment)) }
        runBlocking {
            sagaJob.join()
        }
        assertEquals(0, sagaStore.getState().counter)
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
            val latch = CountDownLatch(3)
            container.runSaga {
                takeEvery({ true }) {
                    latch.countDown()
                }
            }
//            Thread.sleep(10)
            sagaStore.dispatch(Increment)
            sagaStore.dispatch(Increment)
            sagaStore.dispatch(Increment)
            assertTrue(latch.await(100, TimeUnit.MILLISECONDS))

        }
    }
}
