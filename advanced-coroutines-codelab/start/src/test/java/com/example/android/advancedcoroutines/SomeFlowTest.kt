package com.example.android.advancedcoroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    data class Jopa(val kal: String = "", val anus: String ="")

    @Test
    fun `flow testing`() {

        val flow1 = flow<List<Jopa>> {

            coroutineScope {


                delay(500)
                emit(listOf(Jopa("a", "b")))
                delay(1500)
                emit(listOf(Jopa("a", "c")))
                error("LOL")
                delay(500)
                emit(listOf(Jopa("c")))
            }
        }.catch { emit(emptyList()) }

        val flow2 = flow {

            coroutineScope {

                delay(200)
                emit(listOf("1", "2"))
                delay(3000)
                emit(listOf("2", "3"))
                delay(1000)
                emit(listOf("4"))
                delay(1000)
                emit(listOf("4"))
            }
        }.catch { emit(emptyList()) }

        runBlockingTest {

//            val result = flow1.combine(flow2) { first, second ->
//
//                first + second
//            }
//
//            result.collect {
//                println(it)
//            }
            listOf(flow1, flow2).collectAllSafe {

                println(it)
            }
        }
    }

    private suspend fun <T> List<Flow<List<T>>>.collectAllSafe(
        collector: suspend (List<T>) -> Unit = {}
    ) = coroutineScope {

        val flows = this@collectAllSafe.map { it.catch { emit(emptyList()) } }
        val result: MutableMap<Int, List<T>> = mutableMapOf()

        flows.forEachIndexed { index, flow ->
            launch {
                flow.collect {

                    result[index] = it
                    collector.invoke(result.values.flatten())
                }
            }
        }
    }

    fun <T> Flow<T>.handleErrors(): Flow<T> = catch {  }
}