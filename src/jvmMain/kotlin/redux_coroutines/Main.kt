package redux_coroutines

val reducer = { s: State, a: Action ->
    when(a) {
        is Increment -> State(s.counter + 1)
        is Decrement -> State(s.counter -1)
    }
}
data class State(val counter: Int = 0)
val logger: Middleware<State, Action> = { store, action, next ->
    println("Before: ${store.getState()}")
    next(action)
    println("After: ${store.getState()}")
}

fun main() {
    val store = ReducerStore(reducer, State())
    println("current state ${store.getState()}")

    // apply middleware
    val mstore = apply(store, logger)
//    val mstore = applyAll(store, logger, logger, logger)

    store.dispatch(Increment)
    mstore.dispatch(Increment)

}