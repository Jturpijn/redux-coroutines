package com.ximedes.todo

import java.lang.Exception
import com.ximedes.todo.Action.*

fun main() {
    val lib = runApp()
    val container = lib.first
    val store = lib.second

    // Command Line Interface
    println("Welcome to the redux-coroutines CLI app, this is an interactive app as an introduction to redux-coroutines.")
    println("Please try typing 'help' to see the commands.")
    loop@ while (true) {
        when (readLine()!!) {
            "help" -> printHelp()
            "getstate" -> {
                println("Current state: ${store.getState().visibilityFilter}")
                printTodos(store.getState().visibleTodos)
            }
            "todos" -> {
                println("visible todos : ")
                printTodos(store.getState().visibleTodos)
            }
            "addTodo" -> {
                while (true) {
                    println("Please enter the description of your todo")
                    val description = readLine()!!
                    if(description == "cancel") {println("Cancelled addTodo proces" ) ; break }
                    println("Please confirm your description (y/n): $description")
                    if (confirm(readLine()!!)) {
                        try {
                            store.dispatch(RequestAddTodo(description))
                            println("Successfully added todo: $description")
                            break
                        } catch (e: Exception) {
                            println("An error has occurred.")
                        }
                    } else {
                        println("Let's try that again then.. (Type 'cancel' to exit this process)")
                    }
                }
            }
            "toggleTodo" -> {
                println("Please enter the ID of the todo you're trying to toggle.")
                val index:Int = readLine()!!.toInt()
                try {
                    println("Please confirm your toggle of todo: ${store.getState().todos[index]}")
                    if(confirm(readLine()!!)) {
                        try {
                            store.dispatch(RequestToggleTodo(index))
                            println("Successfully toggled todo: ${store.getState().todos[index]}")
                        } catch (e: Exception) {
                            println("Something went wrong: $e")
                        }
                    }
                } catch (e: Exception) {
                    println("Couldn't find a todo with that ID!")
                }
            }
            "removeTodo" -> {
                println("Please enter the id of the todo you're trying to remove.")
                val index:Int = readLine()!!.toInt()

                try {
                    val todo = store.getState().todos[index]
                    println("Please confirm the deletion of todo: ${todo.text}")
                    if(confirm(readLine()!!)) {
                        try {
                            store.dispatch(RequestRemoveTodo(index))
                            println("Successfully removed todo: ${todo.text}")
                        } catch (e: Exception) {
                            println("Something went wrong: $e")
                        }
                    }
                } catch (e: Exception) {
                    println("Couldn't find a todo with that ID!")
                }
            }
            "exit" -> break@loop
            else -> println("Can't understand what you're trying to do. Try typing help for some tips.")
        }
    }
}

// Helper functions
fun confirm(text: String): Boolean = text in arrayOf("y", "yes", "yea", "ok")
fun printHelp() {
    println("+----------------------------------------------------------------+")
    println("|   command :  |       description :                             |")
    println("+--------------+-------------------------------------------------+")
    println("| getstate     |  shows the complete state                       |")
    println("| todos        |  shows the todos based on the visibilityFilter  |")
    println("| addTodo      |  starts the process of adding   a todo          |")
    println("| toggleTodo   |  starts the process of toggling a todo          |")
    println("| removeTodo   |  starts the process of removing a todo          |")
    println("| exit         |  quits this CLI application                     |")
    println("+----------------------------------------------------------------+")
}
fun printTodos(todos: List<Todo>) {
    for(todo in todos) {
        println("| ${todo.id} |\t ${todo.completed} \t | \t ${todo.text}")
    }
}