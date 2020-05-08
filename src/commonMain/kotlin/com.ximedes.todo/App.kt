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

fun runApp(): Pair<SagaContainer<State, Action>, Store<State, Action>> {
    val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    val rootReducer = combineReducers(todoReducer, visibilityFilterReducer)
    val container = SagaContainer<State, Action>()
    val store = applyMiddleware(ReducerStore(rootReducer, State()), container.createMiddleWare())

    // Whipe local todos and populate state with retrieved todos
    container.runSaga {
        runCatching {
            client.get<List<Todo>>("http://127.0.0.1:8080/todos")
        }.onSuccess {
            store.dispatch(SyncState(it))
        }.onFailure {
            println("Retrieving the state has been unsuccessful.")
        }
    }

    container.runSaga {
        val json = defaultSerializer()
        while (true) {
            takeEvery({ true }, {
                when (it) {
                    is RequestAddTodo -> runCatching {
                        client.post<String>("http://127.0.0.1:8080/todo") {
                            body = json.write(addTodo(it.text))
                        }
                    }.onSuccess {
                        store.dispatch(AddTodo(it))
                    }.onFailure {
                        println("Something went wrong sending the action: ${it.message}")
                        println("Todo has not been added.")
                    }
                    is RequestToggleTodo -> runCatching {
                        client.post<Int>("http://127.0.0.1:8080/toggleTodo") {
                            body = json.write(todoKey(it.index))
                        }
                    }.onSuccess {
                        val id:Int = it
                        store.dispatch(ToggleTodo(id))
                    }.onFailure {
                        println("Something went wrong sending the action: $it")
                        println("Todo has not been toggled.")
                    }
                    is RequestRemoveTodo -> runCatching {
                        client.delete<Int>("http://127.0.0.1:8080/todo") {
                            body = json.write(todoKey(it.index))
                        }
                    }.onSuccess {
                        val id:Int = it
                        store.dispatch(RemoveTodo(id))
                    }.onFailure {
                        println("Something went wrong sending the action: $it")
                        println("Todo has not been removed.")
                    }
                }
            })
        }

    }

    return Pair(container, store)
}