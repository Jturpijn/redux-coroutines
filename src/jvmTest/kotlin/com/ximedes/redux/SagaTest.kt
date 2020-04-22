package com.ximedes.redux

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

const val sagaStartUpMillis: Long = 5

class SagaTest {
    val container = SagaContainer<CounterState, CounterAction>()
    val sagaStore = applyMiddleware(ReducerStore(CounterReducer, CounterState()), container.createMiddleWare())

    @Test
    fun `actions are dispatched synchronously`() {
        sagaStore.dispatch(Increment)
        assertEquals(1, container.select().counter)
    }

    @Test
    fun `put call from saga dispatches action`() {
        sagaStore.dispatch(Decrement) // initializes put() to store::dispatch
        val sagaJob = container.runSaga { put(Increment) }
        runBlocking {
            sagaJob.join()
        }
        assertEquals(0, sagaStore.getState().counter)
    }

    @Nested
    inner class TakeTests {

        @Test
        fun `take consumes first dispatched value`() {
            var action: CounterAction = Decrement
            container.runSaga { action = take() }
            Thread.sleep(sagaStartUpMillis)
            sagaStore.dispatch(Increment)
            sagaStore.dispatch(Decrement)
            Thread.sleep(sagaStartUpMillis)
            assertEquals(Increment, action)
        }

        @Test
        fun `take consumes specific value`() {
            var action: CounterAction = Decrement
            container.runSaga { action = take { action -> action === Increment} }
            Thread.sleep(sagaStartUpMillis)
            container.runSaga { sagaStore.dispatch(Decrement) ; sagaStore.dispatch(Increment) }
            Thread.sleep(sagaStartUpMillis)
            assertEquals(Increment, action)
        }
    }

    @Nested
    inner class TakeEveryTests {
        @Test
        fun `TakeEvery consumes 3 dispatched actions`() {
            val latch = CountDownLatch(3)
            val aList = mutableListOf<CounterAction>()
            container.runSaga {
                takeEvery({ true }) {
                    aList.add(it)
                    latch.countDown()
                }
            }
            Thread.sleep(5)
            repeat (3) { sagaStore.dispatch(Increment) }
            assertTrue(latch.await(100, TimeUnit.MILLISECONDS))
        }

        @Test
        fun `TakeEvery consumes only Increments`() {
            val latch = CountDownLatch(3)
            val aList = mutableListOf<CounterAction>()
            container.runSaga {
                takeEvery({ action -> action == Increment }) {
                    aList.add(it)
                    latch.countDown()
                }
            }
            Thread.sleep(sagaStartUpMillis)
            repeat (3) { sagaStore.dispatch(Decrement) }
            repeat (3) { sagaStore.dispatch(Increment) }
            assertTrue(latch.await(100, TimeUnit.MILLISECONDS))
            assertEquals(listOf<CounterAction>(Increment, Increment, Increment), aList)
        }

        @Test
        fun `TakeEvery consumes ordered actions`() {
            val latch = CountDownLatch(3)
            val aList = mutableListOf<CounterAction>()
            container.runSaga {
                takeEvery({ true }) {
                    aList.add(it)
                    latch.countDown()
                }
            }
            Thread.sleep(sagaStartUpMillis)

            sagaStore.dispatch(Increment)
            sagaStore.dispatch(Decrement)
            sagaStore.dispatch(Increment)
            assertTrue(latch.await(100, TimeUnit.MILLISECONDS))
            assertEquals(listOf(Increment, Decrement, Increment), aList)
        }
    }
}
