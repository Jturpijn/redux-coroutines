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

    container.runSaga {
        val b = take { action -> action == Increment }
        println(
            when (b) {
                is Increment -> "Succes"
                else -> "Failed"
            }
        )

    }

    container.runSaga {
        val c = takeEvery { action -> action == Increment }
        c
    }

    store.dispatch(Decrement)
    Thread.sleep(1000)
    store.dispatch(Increment)
    store.dispatch(Increment)
    Thread.sleep(2000)
}