package redux_coroutines

import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class StoreTestsJvm {
    private val store = ReducerStore(reducer, State())

    @Test
    fun `Testing the initial getState`() = assertEquals(State(), store.getState())

    @Test
    fun `Testing dispatching an Increment`() {
        store.dispatch(Increment)
        assertEquals(1, store.getState().counter)
    }

    @Test
    fun `Testing 1000 dispatches of Increment`() {
        for(i in 0..1000) {
            store.dispatch(Increment)
        }
        assertEquals(1000, store.getState().counter)
    }

    @Test
    fun `Testing dispatching a Decrement`() {
        store.dispatch(Increment) ; store.dispatch(Decrement)
        assertEquals(0, store.getState().counter)
    }
}