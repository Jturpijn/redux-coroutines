package com.ximedes.todo

import com.ximedes.redux.Store
import kotlinx.html.*
import kotlin.browser.document

import kotlinx.html.dom.create
import kotlinx.html.js.*

fun listTodos(store: Store<State,Action>) {
    document.getElementById("todos")!!.outerHTML = " "
    document.getElementById("container")!!.appendChild(document.create.div {
        id = "todos"
        h3 { +"Todos:" }
    })

    document.getElementById("todos")!!.appendChild(
        document.create.div {
            table {
                tr {
                 th { +"ID" }
                 th { +"Description"  }
                 th { +"Completed" }
                 th { +"Actions" }
                }

                for (todo in store.getState().todos) {
                    tr {
                        td { +"#${todo.id}" }
                        td {  +todo.text }
                        td {  +todo.completed.toString() }
                        td {
                            button {
                                id = "toggle"
                                +"Toggle"
                                onClickFunction = { _ ->
                                    store.dispatch(Action.RequestToggleTodo(todo.id))
                                }
                            }
                        }
                        td {
                            button {
                                id = "delete"
                                +"Delete"
                                onClickFunction = { _ ->
                                    store.dispatch(Action.RequestRemoveTodo(todo.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}