package com.ximedes.todo

import com.ximedes.todo.Action.*

val todoReducer = { s: State, a: Action ->
    when (a) {
        is AddTodo -> State(s.visibilityFilter, s.todos.plusElement(addTodoWithLowestIdAvailable(s.todos, a.text)))
        is RemoveTodo -> State(s.visibilityFilter, s.todos.minusElement(s.todos.single { it.id == a.index }))
        is ToggleTodo -> State(s.visibilityFilter, s.todos.mapIndexed { index, todo ->
            if (index == a.index) {
                todo.copy(completed = !todo.completed)
            } else {
                todo
            }
        })
        is SyncState -> State(todos = a.todos)
        else -> State(s.visibilityFilter, s.todos)
    }
}

val visibilityFilterReducer = { s: State, a: Action ->
    when (a) {
        is SetVisibilityFilter -> State(a.filter, s.todos)
        else -> State(s.visibilityFilter, s.todos)
    }
}