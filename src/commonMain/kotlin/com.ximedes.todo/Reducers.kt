package com.ximedes.todo

val todoReducer = { s: State, a: Action ->
    when (a) {
        is Action.AddTodo -> State(s.visibilityFilter, s.todos + Todo(
            a.text,
            id = s.todos.size
        )
        )
        is Action.ToggleTodo -> State(s.visibilityFilter, s.todos.mapIndexed { index, todo ->
            if (index == a.index) {
                todo.copy(completed = !todo.completed)
            } else {
                todo
            }
        })
        else -> State(s.visibilityFilter, s.todos)
    }
}

val visibilityFilterReducer = { s: State, a: Action ->
    when (a) {
        is Action.SetVisibilityFilter -> State(a.filter, s.todos)
        else -> State(s.visibilityFilter, s.todos)
    }
}