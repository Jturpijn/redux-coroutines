package com.ximedes.todo


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.ximedes.redux.ReducerStore
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class PostSetVisibilityFilter(val filter: VisibilityFilter)
data class PostAddTodo(val text: String)
data class PostToggleTodo(val id: Int)

fun main() {
    val rootReducer = combineReducers(todoReducer, visibilityFilterReducer)
    val store = ReducerStore(rootReducer, State())

    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
                enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            }
        }
        routing {
            // basic functionalities for the To-do example
            get("/") {
                call.respond(store.getState())
            }
            get("/todos") {
                call.respond(store.getState().visibleTodos)
            }
            post("/setVisibilityFilter") {
                val payload = call.receive<PostSetVisibilityFilter>()
                store.dispatch(SetVisibilityFilter(payload.filter))
                call.respond("VisibilityFilter has been updated to ${store.getState().visibilityFilter}")
            }
            post("/addTodo") {
                val payload = call.receive<PostAddTodo>()
                store.dispatch(AddTodo(payload.text))
                call.respond("Successfully added todo to the list")
            }
            post("/toggleTodo") {
                val payload = call.receive<PostToggleTodo>()
                store.dispatch(ToggleTodo(payload.id))
                call.respond("Successfully toggled todo")
            }

            // Some faker calls to insert some asynchronicity
            get("/getTodos") {
                val todos: List<Todo> = listOf(
                    Todo("First Todo item", false, 0),
                    Todo("Completed Todo item", true, 1),
                    Todo("Third not so completed Todo item", false, 2)
                )
                call.respond(todos)
            }
            static("/static") {
                resource("redux-coroutines.js")
            }
        }
    }.start(wait = true)
}