package com.ximedes.todo

import com.ximedes.redux.ReducerStore
import com.ximedes.redux.SagaContainer
import com.ximedes.redux.applyMiddleware
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get

fun runApp() {
    val rootReducer = combineReducers(todoReducer, visibilityFilterReducer)
    val container = SagaContainer<State, Action>()
    val mStore = applyMiddleware(ReducerStore(rootReducer, State()), container.createMiddleWare())
    val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    println("Checking initial store ${mStore.getState()}")
    mStore.dispatch(AddTodo("Finish POC"))
    var todos = listOf<Todo>()
    container.runSaga {
        val response = client.get<List<Todo>>("http://127.0.0.1:8080/getTodos")
        println(response)
    }
    println("Retrieving off site todos : ${todos}")
}