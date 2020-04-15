package redux_coroutines

sealed class Action
object Increment: Action()
object Decrement: Action()