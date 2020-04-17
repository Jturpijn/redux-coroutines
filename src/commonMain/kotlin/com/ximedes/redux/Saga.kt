package com.ximedes.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch


interface SagaRuntime<S, A> {
    suspend fun take(): A

    // suspend fun take(matcher: (A) -> Boolean): A

    // suspend fun takeEvery(...)
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


}


