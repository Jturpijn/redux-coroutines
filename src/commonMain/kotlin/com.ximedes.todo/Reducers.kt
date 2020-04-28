package com.ximedes.todo

val todoReducer = { s: State, a: Action ->
    when (a) {
        is AddTodo -> State(s.visibilityFilter, s.todos + Todo(
            a.text,
            id = s.todos.size
        )
        )
        is ToggleTodo -> State(s.visibilityFilter, s.todos.mapIndexed { index, todo ->
            if (index == a.index) {
                todo.copy(completed = !todo.completed)
            } else {
                todo
            }
        })
        else -> State(s.visibilityFilter, s.todos, s.counter)
    }
}

val visibilityFilterReducer = { s: State, a: Action ->
    when (a) {
        is SetVisibilityFilter -> State(a.filter, s.todos)
        else -> State(s.visibilityFilter, s.todos, s.counter)
    }
}

val counterReducer = { s: State, a: Action ->
    when (a) {
        is Increment -> State(s.visibilityFilter, s.todos, s.counter + 1)
        else -> State(s.visibilityFilter, s.todos, s.counter)
    }
}