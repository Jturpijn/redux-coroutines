package redux_coroutines

import org.junit.Test
import kotlin.test.assertEquals

class LoggerTestsJVM {

    private val store = ReducerStore(reducer, State())
    private val logStore = apply(store, logger)

    @Test
    fun `Test dispatch with Middleware`() {
        logStore.dispatch(Increment)
        assertEquals(1, logStore.getState().counter)
    }
}

class MultipleLoggersTestsJVM {
    private val store = ReducerStore(reducer, State())
    private val logStore = applyAll(store, logger, logger, logger)

    @Test
    fun `Test dispatch with Middlewares`() {
        logStore.dispatch(Increment)
        assertEquals(1, logStore.getState().counter)
    }
}