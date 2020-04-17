package com.ximedes.redux

fun main() {
    val container = SagaContainer<CounterState, CounterAction>()
    val store = applyMiddleware(ReducerStore(CounterReducer, CounterState()), container.createMiddleWare())

    container.runSaga {
        while (true) {
            println("Waiting for action")
            val a = take()
            println(
                when (a) {
                    is Increment -> "+1"
                    is Decrement -> "-1"
                }
            )
        }
    }

    store.dispatch(Increment)
    Thread.sleep(100)
}