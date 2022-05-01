package com.github.displace.sdp2022.database

object ReferenceParser {

    /**
     * for example,
     * if reference = "abc/de/fg"
     * then List("abc","de/fg")
     * is returned
     *
     * if reference = "abc" or "abc/"
     * then List("abc")
     * is returned
     *
     * @param reference
     * @return prefix and suffix (if present)
     */
    fun parse(reference: String): List<String> {
        if(reference.isEmpty()) {
            return listOf()
        }
        val prefix = reference.takeWhile { it != ('/') }
        val stripped = reference.removePrefix("$prefix/")

        return if(prefix == stripped || stripped.isEmpty()){
            listOf(prefix)
        } else {
            listOf(prefix,stripped)
        }
    }
}