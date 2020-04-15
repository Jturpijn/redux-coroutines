package redux_coroutines

typealias Reducer<S,A> = (S,A) -> S
typealias Listener = () -> Unit
typealias Middleware<S, A> = (store: Store<S, A>, action: A, next: (A) -> Unit) -> Unit

interface Store<S,A> {
    fun getState(): S
    fun subscribe(listener: Listener)
    fun dispatch(action: A)
}

class ReducerStore<S,A>(reducer: Reducer<S,A>, initialState: S) : Store<S, A> {
    var currentReducer = reducer
    var currentState = initialState
    var isDispatching = false
    var listeners: MutableList<Listener> = mutableListOf()

    override fun getState(): S { return currentState }
    override fun subscribe(listener: Listener) {
        if (isDispatching) {
            throw Error("You may not subscribe when the store is dispatching.")
        }
        listeners.apply { this.add(listener) }
    }

    override fun dispatch(action: A) {
        try {
            isDispatching = true
            currentState = currentReducer(currentState, action)
        } finally {
            isDispatching = false
        }
    }
}

fun <S,A> apply(store: Store<S, A>, m: Middleware<S,A>): Store<S,A> {
    return object : Store<S,A> {
        override fun getState(): S = store.getState()
        override fun subscribe(listener: Listener) = store.subscribe(listener)
        override fun dispatch(action: A) {
            m(store, action, store::dispatch)
        }
    }
}

fun <S,A> applyAll(store: Store<S, A>, vararg middleware: Middleware<S,A>): Store<S, A> {
    return middleware.foldRight(store) { m, s -> apply(s,m)}
}