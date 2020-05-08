package com.ximedes.todo

import io.ktor.html.Template
import kotlinx.html.*

class WorkshopPage : Template<HTML> {
    override fun HTML.apply() {
        head {
            title("Workshop Redux Saga in Kotlin")
        }
        body {
            div {
                id = "container"
                div {
                    id = "title"
                    h1 { +"Welcome to the Redux Saga in Kotlin workshop."}
                }
                div {
                    id = "todos"
                    h3{ +"Todos:" }
                }
            }
            script(src = "/static/redux-coroutines.js") {}
        }
    }
}