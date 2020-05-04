package com.ximedes.todo


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

val MAX_TODOS = 20
val todoMap = mutableMapOf<Int, Todo>(
    0 to Todo("Your very first todo!", false, 0),
    1 to Todo("Being cool.", true, 1),
    2 to Todo("Afstuderen", false, 2)
)

fun main() {
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
            get("/todos") {
                call.respond(todoMap.values)
            }
            get("/todo") {
                val payload = call.receive<todoKey>()
                if (payload.id in todoMap.keys) {
                    val todo = todoMap.getValue(payload.id)
                    call.respond(todo)
                } else {
                    call.respond("Couldn't find ID")
                }
            }
            post("/todo") {
                val payload = call.receive<addTodo>()
                for(i in 0..MAX_TODOS) {
                    if(i !in todoMap.keys) {
                        todoMap[i] = Todo(payload.text, false, i)
                        call.respondText(payload.text)
                        break
                    }
                    if(i >= MAX_TODOS) {
                        call.respond("You've reached your maximum of todo's. Delete some to make room for new todo's")
                    }
                }
            }
            post("/toggleTodo") {
                val payload = call.receive<todoKey>()
                if(payload.id in todoMap.keys) {
                    val oldTodo = todoMap.getValue(payload.id)
                    val newTodo = Todo(oldTodo.text, oldTodo.completed.not(), oldTodo.id)
                    todoMap.replace(payload.id, oldTodo, newTodo)
                    call.respond(newTodo.id)
                } else {
                    call.respond("Couldn't find todo with id: ${payload.id}")
                }
            }
            delete("/todo") {
                val payload = call.receive<todoKey>()
                if(payload.id in todoMap.keys) {
                    val todo = todoMap.getValue(payload.id)
                    todoMap.remove(payload.id)
                    call.respond(todo.id)
                } else {
                    call.respond("Couldn't find todo with id: ${payload.id}")
                }
            }
            static("/static") {
                resource("redux-coroutines.js")
            }
        }
    }.start(wait = true)
}