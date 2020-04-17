package com.ximedes.redux

typealias Middleware<S, A> = (store: Store<S, A>, action: A, next: (A) -> Unit) -> Unit

fun <S, A> applyMiddleware(s: Store<S, A>, m: Middleware<S, A>): Store<S, A> = object : Store<S, A> by s {
    override fun dispatch(action: A) {
        m(this, action, s::dispatch)
    }
}

fun <S, A> applyAll(store: Store<S, A>, vararg middleware: Middleware<S, A>): Store<S, A> {
    return middleware.foldRight(store) { m, s -> applyMiddleware(s, m) }
}