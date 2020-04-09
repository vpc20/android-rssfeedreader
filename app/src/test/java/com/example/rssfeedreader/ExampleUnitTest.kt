package com.example.rssfeedreader

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_doubleIt(){
        val x = doubleIt(10)
        println(x)
    }

    private fun doubleIt(n: Int): Int {
        return n * 2
    }

}
