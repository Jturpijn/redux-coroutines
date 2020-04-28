package com.ximedes.todo

/*
 * Actions for to-do example
 */
sealed class Action()
class AddTodo(val text: String): Action()
object Increment: Action()
class ToggleTodo(val index: Int): Action()
class SetVisibilityFilter(val filter: VisibilityFilter): Action()

/*
 * More data such as the custom To-do class and VisibilityFilter
 */
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