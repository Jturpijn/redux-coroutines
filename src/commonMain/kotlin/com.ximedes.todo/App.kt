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
import io.ktor.http.cio.Response
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun runApp(): Pair<SagaContainer<State, Action>, Store<State, Action>> {
    val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    // Whipe local todos and populate state with retrieved todos
    GlobalScope.launch() {
        runCatching {
            client.get<List<Todo>>("http://127.0.0.1:8080/todos")
        }.onSuccess {
            State(todos = it)
            println("this has been a success $it")
        }.onFailure {
            println("Retrieving the state has been unsuccessful.")
        }
    }

    val rootReducer = combineReducers(todoReducer, visibilityFilterReducer)
    val container = SagaContainer<State, Action>()
    val store = applyMiddleware(ReducerStore(rootReducer, State()), container.createMiddleWare())

    container.runSaga {
        val json = defaultSerializer()
        while (true) {
            takeEvery({ true }, {
                when (it) {
                    is CLIAddTodo -> try {
                        val response = client.post<Todo>("http://127.0.0.1:8080/todo") {
                            body = json.write(addTodo(it.text))
                        }
                        store.dispatch(AddTodo(response.text))
                    } catch (e: Exception) {
                        println("Something went wrong sending the action: $e")
                        println("Todo has not been added.")
                    }
                    is CLIToggleTodo ->  try {
                        val response = client.post<Todo>("http://127.0.0.1:8080/toggleTodo") {
                        body = json.write(todoKey(it.index))
                    }
                        store.dispatch(ToggleTodo(response.id))
                    } catch (e: Exception) {
                        println("Something went wrong sending the action: $e")
                        println("Todo has not been toggled.")
                    }
                    is CLIRemoveTodo -> try {
                        val response = client.post<Todo>("http://127.0.0.1:8080/removeTodo") {
                            body = json.write(todoKey(it.index))
                        }
                        store.dispatch(RemoveTodo(response.id))
                    } catch (e: Exception) {
                        println("Something went wrong sending the action: $e")
                        println("Todo has not been removed.")
                    }
                }
            })
        }

    }

    return Pair(container, store)
}