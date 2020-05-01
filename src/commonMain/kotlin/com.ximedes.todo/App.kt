package com.ximedes.todo

import com.ximedes.redux.*
import com.ximedes.todo.Action.*
import io.ktor.client.HttpClient
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.delay

fun runApp(): Pair<SagaContainer<State, Action>, Store<State, Action>> {
    val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    val rootReducer = combineReducers(todoReducer, visibilityFilterReducer)
    val container = SagaContainer<State, Action>()
    val store = applyMiddleware(ReducerStore(rootReducer, State()), container.createMiddleWare())

    // init
    container.runSaga {
        val todos: List<Todo> = client.get("http://127.0.0.1:8080/todos")
        for (todo in todos) {
            store.dispatch(AddTodo(todo.text))
            if (todo.completed) {
                store.dispatch(ToggleTodo(todo.id))
            }
        }
    }

    container.runSaga {
        val json = defaultSerializer()
        delay(1000)
        while (true) {
            takeEvery({ true }, {
                when (it) {
                    is AddTodo -> client.post("http://127.0.0.1:8080/todo") {
                        body = json.write(addTodo(it.text))
                    }
                    is ToggleTodo -> client.post("http://127.0.0.1:8080/toggleTodo") {
                        body = json.write(todoKey(it.index))
                    }
                    is RemoveTodo -> client.delete("http://127.0.0.1:8080/todo") {
                        body = json.write(todoKey(it.index))
                    }
                }
            })
        }

    }

    return Pair(container, store)
}