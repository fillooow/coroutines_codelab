package com.example.android.advancedcoroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

internal class TestFLow {

    val ints: Flow<Int> by lazy {
        flow {
            for (i in 1 .. 10) {
                delay(10000)
                emit(i)
            }
        }
    }

    suspend fun someTest() {

        val time = measureTimeMillis {
            ints.collect { println(it) }
        }
        println("Collected in $time ms")
    }

    @Test
    fun test() = runBlockingTest {

        someTest()
    }
}