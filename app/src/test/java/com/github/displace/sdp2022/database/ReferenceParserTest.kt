package com.github.displace.sdp2022.database

import org.junit.Assert.assertEquals
import org.junit.Test

class ReferenceParserTest {

    private fun checkList(expected: List<String>, actual: List<String>) {
        assertEquals(expected.size, actual.size)
        for(i in expected.indices) {
            assertEquals(expected[i],actual[i])
        }
    }

    @Test
    fun emptyListOnEmptyString() {
        checkList(listOf(),ReferenceParser.parse(""))
    }

    @Test
    fun emptyListOnSlash() {
        checkList(listOf(),ReferenceParser.parse("/"))
    }

    @Test
    fun correctOnOne() {
        val first = "first"
        checkList(listOf(first),ReferenceParser.parse(first))
    }

    @Test
    fun correctOnOneWithSlash() {
        val first = "first"
        print(ReferenceParser.parse("$first/"))
        checkList(listOf(first),ReferenceParser.parse("$first/"))
    }

    @Test
    fun correctOnTwo() {
        val first = "first"
        val second = "second"
        checkList(listOf(first,second),ReferenceParser.parse("$first/$second"))
    }

    @Test
    fun correctOnThree() {
        val first = "first"
        val second = "second"
        val third = "third"
        checkList(listOf(first,"$second/$third"),ReferenceParser.parse("$first/$second/$third"))
    }
}