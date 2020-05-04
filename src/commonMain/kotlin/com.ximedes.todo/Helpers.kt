package com.ximedes.todo

import kotlinx.serialization.Serializable

typealias TReducer<State, A> = (State, A) -> State

fun <State, Action> combineReducers(vararg reducers: TReducer<State, Action>) = { state: State, action: Action ->
    reducers.fold(state, { s, reducer -> reducer(s, action) })
}

// rest call data classes
@Serializable
data class addTodo(val text: String)

@Serializable
data class todoKey(val id: Int)

// adding the lowest id possible to avoid ID conflicts with the backend
fun addTodoWithLowestIdAvailable(todos: List<Todo>, text: String): Todo {
    val ids = mutableListOf<Int>()
    for(todo in todos ) { ids.add(todo.id) }

    for (i in todos.indices) {
        if (i !in ids) {
            return Todo(text, false, i)
        }
    }
    return (Todo(text, false, todos.size))
}