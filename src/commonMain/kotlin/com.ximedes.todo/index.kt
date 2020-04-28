package com.ximedes.todo

typealias TReducer<State, A> = (State, A) -> State
fun <State,Action> combineReducers(vararg reducers: TReducer<State, Action>) = { state: State, action: Action ->
    reducers.fold(state, { s, reducer -> reducer(s, action) })
}

data class State(
    val visibilityFilter: VisibilityFilter = VisibilityFilter.SHOW_ALL,
    val todos: List<Todo> = listOf(),
    val counter: Int = 0
){
    val visibleTodos: List<Todo>
        get() = getVisibleTodos(visibilityFilter)

    private fun getVisibleTodos(visibilityFilter: VisibilityFilter) = when (visibilityFilter) {
        VisibilityFilter.SHOW_ALL -> todos
        VisibilityFilter.SHOW_ACTIVE -> todos.filter { !it.completed }
        VisibilityFilter.SHOW_COMPLETED -> todos.filter { it.completed }
    }
}