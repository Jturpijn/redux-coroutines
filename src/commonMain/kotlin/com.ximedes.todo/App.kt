package com.ximedes.todo

import com.ximedes.redux.ReducerStore
import com.ximedes.redux.SagaContainer
import com.ximedes.redux.Store
import com.ximedes.redux.applyMiddleware
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.native.concurrent.ThreadLocal

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
        val todos:List<Todo> = client.get("http://127.0.0.1:8080/todos")
        for(todo in todos) {
            store.dispatch(Action.AddTodo(todo.text))
        }
    }

    return Pair(container, store)
}