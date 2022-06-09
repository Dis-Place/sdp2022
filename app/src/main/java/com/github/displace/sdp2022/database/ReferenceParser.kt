package com.github.displace.sdp2022.database

/**
 * This helper object is used to solve references in the MockDatabase
 *
 * @see GoodDB
 * @see MockDB
 * @see com.github.displace.sdp2022.util.collections.ObservableMultiLevelMap
 */
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
        val ref = reference.removeSuffix("/")

        if(ref.isEmpty()) {
            return listOf()
        }
        val prefix = ref.takeWhile { it != ('/') }
        val stripped = ref.removePrefix("$prefix/")

        return if(prefix == stripped){
            listOf(prefix)
        } else {
            listOf(prefix,stripped)
        }
    }
}