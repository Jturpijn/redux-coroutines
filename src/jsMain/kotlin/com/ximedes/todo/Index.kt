package com.ximedes.todo

import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.dom.create
import kotlinx.html.js.p
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear


fun main() {
    val lib = runApp()
    val container = lib.first
    val store = lib.second

    container.runSaga {
        takeEvery({ true }, {
            listTodos(store)
        })
    }

    document.addEventListener("DOMContentLoaded", {
        document.getElementById("title")!!.appendChild(document.create.p {
            +"Description : "
            input {
                id = "todoDescription"
                type = InputType.text
                placeholder = "Add your own todo!"
            }
            button {
                +"Add todo"
                id = "addTodoButton"
                type = ButtonType.button
                onClickFunction = { _ ->
                    val description = document.getElementById("todoDescription") as HTMLInputElement
                    if (description.value.isBlank()) {
                        window.alert("You must enter a description!")
                    } else {
                        store.dispatch(Action.RequestAddTodo(description.value))
                        description.placeholder = ""
                    }
                }
            }
        })
    })
}
