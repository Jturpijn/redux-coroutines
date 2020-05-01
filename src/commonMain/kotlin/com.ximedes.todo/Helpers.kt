package com.ximedes.todo

import kotlinx.serialization.Serializable

typealias TReducer<State, A> = (State, A) -> State
fun <State,Action> combineReducers(vararg reducers: TReducer<State, Action>) = { state: State, action: Action ->
    reducers.fold(state, { s, reducer -> reducer(s, action) })
}

// rest call data classes
@Serializable
data class addTodo(val text: String)
@Serializable
data class todoKey(val id: Int)