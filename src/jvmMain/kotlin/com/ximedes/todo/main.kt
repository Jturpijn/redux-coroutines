package com.ximedes.todo


import com.fasterxml.jackson.databind.*
import com.ximedes.redux.ReducerStore
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.put

fun main() {
    val root = combineReducers(
        todoReducer,
        visibilityFilterReducer,
        counterReducer
    )
    val store = ReducerStore(root, State())

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
            get("/") {
                call.respond(store.getState())
            }
            static("/static") {
                resource("redux-coroutines.js")
            }
        }
    }.start(wait = true)
}