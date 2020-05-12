package com.ximedes.redux

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach


interface SagaRuntime<S, A> {

    suspend fun take(): A

    suspend fun take(matcher: (A) -> Boolean): A

    suspend fun takeEvery(matcher: (A) -> Boolean, saga: ActionSaga<S, A>)

    fun put(action: A)

    fun select(): S
}

typealias Saga<S, A> = suspend SagaRuntime<S, A>.() -> Unit
typealias ActionSaga<S, A> = suspend SagaRuntime<S, A>.(action: A) -> Unit

class SagaContainer<S, A> : CoroutineScope, SagaRuntime<S, A> {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Default

    private val actionChannel = BroadcastChannel<A>(300)
    private lateinit var getState: () -> S
    private lateinit var dispatch: (A) -> Unit

    // TODO nadenken of UNDISPATCHED een goed idee is...
    fun runSaga(saga: Saga<S, A>) = launch(start = CoroutineStart.UNDISPATCHED) { saga() }

    // TODO betere manier verzinnen om middleware te apply-en
    fun createMiddleWare(): Middleware<S, A> = { store, action, next ->
        getState = store::getState
        dispatch = { a: A -> store.dispatch(a) }
        next(action)
        launch {
            actionChannel.offer(action)
        }
    }

    override suspend fun take(): A {
        return take { true }
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

    override suspend fun takeEvery(matcher: (A) -> Boolean, saga: ActionSaga<S, A>) {
        val receiveChannel = actionChannel.openSubscription()
        launch {
            receiveChannel.consumeEach {
                if (matcher(it)) {
                    launch(start = CoroutineStart.UNDISPATCHED) { saga(it) }
                }
            }

        }
    }

    override fun select(): S {
        return getState()
    }

    override fun put(action: A) {
        dispatch(action)
    }
}


