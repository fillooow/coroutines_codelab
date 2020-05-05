package com.example.android.advancedcoroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class SomeFlowTest {

    private fun `make flow`() = flow {
        println("sending first value")
        emit(1)
        println("first value collected, sending another value")
        emit(2)
        println("second value collected, sending a third value")
        emit(3)
        println("done")

    }

    private suspend fun `collect flow`() = coroutineScope{
        launch {
            `make flow`().collect {
                println("got $it")
            }
            println("flow is completed")
        }
    }

    @Test
    fun `collect all flows`() = runBlockingTest {

        `collect flow`()
    }

    @Test
    fun `take some flows`() = runBlockingTest {

        launch {
            val repeatableFlow = `make flow`().take(2)  // we only care about the first two elements
            println("first collection")
            repeatableFlow.collect()
            println("collecting again")
            repeatableFlow.collect()
            println("second collection completed")
        }
    }
}