package com.ximedes.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch


interface SagaRuntime<S, A> {

    suspend fun take(): A

    suspend fun take(matcher: (A) -> Boolean): A

    suspend fun takeEvery(matcher: (A) -> Boolean)

    // suspend (?) fun select() getstate
}

typealias Saga<S, A> = suspend SagaRuntime<S, A>.() -> Unit

class SagaContainer<S, A> : CoroutineScope, SagaRuntime<S, A> {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Default

    private val actionChannel = BroadcastChannel<A>(300)

    fun runSaga(saga: Saga<S, A>) = launch { saga() }

    fun createMiddleWare(): Middleware<S, A> = { store, action, next ->
        next(action)
        launch {
            actionChannel.send(action)
        }

    }

    override suspend fun take(): A {
        val receiveChannel = actionChannel.openSubscription()
        val action = receiveChannel.receive()
        receiveChannel.cancel()
        return action
    }

    override suspend fun take(matcher: (A) -> Boolean): A {
        val receiveChannel = actionChannel.openSubscription()
        var action = receiveChannel.receive()
        while (true) {
            if (matcher(action)) {
                receiveChannel.cancel()
                return action
            } else {
                action = receiveChannel.receive()
            }
        }

    }

    override suspend fun takeEvery(matcher: (A) -> Boolean) {
        val receiveChannel = actionChannel.openSubscription()
        receiveChannel.consumeEach {
            if (matcher(it)) {
                println("Succesfully consumed Action $it")
            }
        }
    }


}


