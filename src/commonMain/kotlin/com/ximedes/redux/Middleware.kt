package com.ximedes.redux

typealias Middleware<S, A> = (store: Store<S, A>, action: A, next: (A) -> Unit) -> Unit

fun <S, A> applyMiddleware(store: Store<S, A>, m: Middleware<S, A>): Store<S, A> {
    return object : Store<S, A> {
        override fun getState(): S = store.getState()
        override fun subscribe(listener: Listener<S>) = store.subscribe(listener)
        override fun dispatch(action: A) {
            m(store, action, store::dispatch)
        }
    }
}

fun <S, A> applyAllMiddleware(store: Store<S, A>, vararg middleware: Middleware<S, A>): Store<S, A> {
    return middleware.foldRight(store) { m, s -> applyMiddleware(s, m) }
}