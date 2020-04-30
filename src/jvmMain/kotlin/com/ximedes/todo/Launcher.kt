package com.ximedes.todo

import java.lang.Exception

val confirmResponses = arrayOf("y", "yes", "yea", "ok")
val denyResponses = arrayOf("n", "no", "nah", "nope")

fun main() {
    val lib = runApp()
    val container = lib.first
    val store = lib.second

    println("Welcome to the redux-coroutines CLI app, this is an interactive app as an introduction to redux-coroutines.")
    println("Please try typing 'help' to see the commands.")

    while (true) {
        val text = readLine()
        if (text == "help") {
            printHelp()
        }
        else if (text == "getstate") {
            println("Current state: ${store.getState()}")
        }
        else if (text == "todos") {
            println("visible todos : ${store.getState().visibleTodos}")
        }
        else if (text == "addTodo") {
            while (true) {
                println("Please enter the description of your todo")
                val description = readLine()!!
                if(description == "cancel") { break }

                println("Please confirm your description (y/n): $text")
                val confirm = readLine()

                if (confirm in confirmResponses) {
                    try {
                        val add: Action = Action.AddTodo(description)
                        store.dispatch(add)
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
        else if (text == "exit") {
            break
        } else {
            println("Can't understand what you're trying to do. Try typing help for some tips.")
        }
    }
}

fun printHelp() {
    println("+----------------------------------------------------------------+")
    println("|   command :  |       description :                             |")
    println("+--------------+-------------------------------------------------+")
    println("| getstate     |  shows the complete state                       |")
    println("| todos        |  shows the todos based on the visibilityFilter  |")
    println("| addTodo      |  starts the process of adding a todo            |")
    println("| toggleTodo   |  starts the process of toggling a todo          |")
    println("+----------------------------------------------------------------+")
}