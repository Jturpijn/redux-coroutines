package com.ximedes.todo

typealias TReducer<State, A> = (State, A) -> State
fun <State,Action> combineReducers(vararg reducers: TReducer<State, Action>) = { state: State, action: Action ->
    reducers.fold(state, { s, reducer -> reducer(s, action) })
}