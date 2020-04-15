package com.ximedes.redux

typealias Reducer<S, A> = (S, A) -> S
typealias Listener<S> = (state: S) -> Unit

interface Store<S, A> {
    fun getState(): S
    fun subscribe(listener: Listener<S>)
    fun dispatch(action: A)
}

class ReducerStore<S, A>(private val reducer: Reducer<S, A>, initialState: S) :
    Store<S, A> {
    private var currentState = initialState
    private var listeners: MutableList<Listener<S>> = mutableListOf()

    override fun getState(): S {
        return currentState
    }

    override fun subscribe(listener: Listener<S>) {
        listeners.apply { this.add(listener) }
    }

    override fun dispatch(action: A) {
        currentState = reducer(currentState, action)
        listeners.forEach { it(currentState) }
    }
}

