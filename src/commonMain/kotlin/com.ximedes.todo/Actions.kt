package com.ximedes.todo

import kotlinx.serialization.Serializable

/*
 * Actions for to-do example
 */
sealed class Action {
    data class SyncState(val todos: List<Todo>) : Action()

    // Redux actions
    data class AddTodo(val text: String) : Action()
    data class ToggleTodo(val index: Int) : Action()
    data class RemoveTodo(val index: Int) : Action()
    data class SetVisibilityFilter(val filter: VisibilityFilter) : Action()

    // Command line Actions
    data class CLIAddTodo(val text: String) : Action()
    data class CLIToggleTodo(val index: Int) : Action()
    data class CLIRemoveTodo(val index: Int) : Action()
}

/*
 * More data such as the custom To-do class and VisibilityFilter
 */
@Serializable
data class Todo(
    val text: String,
    val completed: Boolean = false,
    val id: Int
)
enum class VisibilityFilter {
    SHOW_ALL,
    SHOW_COMPLETED,
    SHOW_ACTIVE
}