package com.ximedes.redux

data class State(
    val visibilityFilter: VisibilityFilter = VisibilityFilter.SHOW_ALL,
    val todos: List<Todo> = listOf(),
    val counter: Int = 0
)

fun main() {
    val root = combineReducers(todoReducer, visibilityFilterReducer, counterReducer)
    val store = ReducerStore(root, State())
    store.dispatch(Increment)
    println("Added first to-do ${store.getState()}")
    store.dispatch(AddTodo("Finish Combine Reducer function"))
    store.dispatch(ToggleTodo(0))
    println("Toggled to-do to be completed ${store.getState()}")
}

typealias TReducer<State, A> = (State, A) -> State
fun <State,Action> combineReducers(vararg reducers: TReducer<State, Action>) = { state: State, action: Action ->
    reducers.fold(state, {s, reducer -> reducer(s, action)})
}